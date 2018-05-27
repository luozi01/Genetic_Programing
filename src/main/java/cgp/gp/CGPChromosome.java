package cgp.gp;

import cgp.Solver.CartesianGP;
import cgp.program.Function;
import genetics.utils.Observation;
import lombok.Getter;
import org.apache.commons.math3.genetics.Chromosome;

import java.util.Arrays;

@Getter
public class CGPChromosome extends Chromosome {

    private final CartesianGP manager;

    private int[][] genotype;       //function input output
    private int[] outputs;          //source index
    private Object[] varMap;        //value mapping
    private boolean[] toEvaluate;

    CGPChromosome(CartesianGP manager) {
        this.manager = manager;
        genotype = new int[manager.getNodes()][3];
        outputs = new int[manager.getOutput()];
        varMap = new Object[manager.getNodes() + manager.getInput()];
        toEvaluate = new boolean[varMap.length];
    }

    void initialize() {
        int arity = manager.getArity();
        for (int i = 0; i < genotype.length; i++) {
            genotype[i][0] = manager.getRandomFunction();
            for (int j = 1; j <= arity; j++) {
                genotype[i][j] = getRandomSource(i / manager.getRows());
            }
        }

        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = getRandomSource();
        }
    }

    int getRandomSource() {
        return manager.getRandomInt(manager.getInput() + manager.getNodes());
    }

    int getRandomSource(int column) {
        int allowedColumns = column >= manager.getLevelsBack() ? manager.getLevelsBack() : column;
        return manager.getRandomInt(manager.getInput() + (manager.getRows() * allowedColumns));
    }

    public CGPChromosome makeCopy() {
        CGPChromosome clone = new CGPChromosome(manager);
        clone.genotype = new int[genotype.length][];
        for (int i = 0; i < genotype.length; i++) {
            clone.genotype[i] = genotype[i].clone();
        }
        clone.outputs = outputs.clone();
        clone.varMap = varMap.clone();
        return clone;
    }

    public void mutate() {
        int index = manager.getRandomInt(outputs.length + (manager.getRows() * manager.getCols()));

        if (index < outputs.length) { // outputs
            outputs[index] = getRandomSource();
        } else { // node
            index -= outputs.length;
            int col = index / manager.getRows();
            int geneType = manager.getRandomInt(1 + manager.getArity());

            if (geneType < 1) {
                genotype[index][0] = manager.getRandomFunction();
            } else {
                genotype[index][geneType] = getRandomSource(col);
            }
        }
    }

    public void eval(Observation o) {
        execute(o);
    }

    private void nodesToProcess() {
        toEvaluate = new boolean[varMap.length];
        for (int output : outputs) {
            toEvaluate[output] = true;
        }

        for (int i = genotype.length - 1; i >= 0; i--) {
            if (toEvaluate[i]) {
                toEvaluate[genotype[i][1]] = true;
                toEvaluate[genotype[i][2]] = true;
            }
        }
    }

    private void execute(Observation observation) {
        int inputRegisterCount = manager.getInput();
        for (int i = 0; i < inputRegisterCount; ++i) {
            varMap[i] = observation.getInput(i);
        }
//        nodesToProcess();
        for (int i = 0; i < genotype.length; i++) {
//            if (!toEvaluate[i]) continue;
            int[] gene = genotype[i];
            Function function = manager.getFunctions().get(gene[0]);
            Object[] args = new Object[function.arity()];
            for (int j = 0; j < function.arity(); j++) {
                args[j] = varMap[gene[j + 1]];
            }
            varMap[i + manager.getInput()] = function.eval(args);
        }

        int outputRegisterCount = Math.min(outputs.length, observation.outputCount());
        for (int i = 0; i < outputRegisterCount; i++) {
            observation.setPredictedOutput(i, (Double) varMap[outputs[i]]);
        }
    }

    @Override
    public double fitness() {
        double diff = 0;
        for (Observation o : manager.getTargets()) {
            for (int i = 0; i < o.outputCount(); i++) {
                eval(o);
                diff += Math.pow(o.getOutput(i) - o.getPredictedOutput(i), 2);
            }
        }
        return diff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] gene : genotype) {
            sb.append(Arrays.toString(gene)).append("\n");
        }
        sb.append(Arrays.toString(outputs)).append("\n");
        return sb.toString();
    }
}
