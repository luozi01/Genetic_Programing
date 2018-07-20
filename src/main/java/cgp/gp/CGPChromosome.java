package cgp.gp;

import cgp.interfaces.Function;
import cgp.program.Node;
import cgp.solver.CartesianGP;
import genetics.chromosome.Chromosome;
import lombok.Cleanup;
import lombok.Getter;
import org.eclipse.collections.api.list.MutableList;

import java.util.Scanner;
import java.util.stream.IntStream;

import static cgp.gp.CGPCore.initialiseChromosome;
import static cgp.gp.CGPCore.setChromosomeActiveNodes;
import static cgp.solver.CGPSolver.initialiseParameters;

@Getter
public class CGPChromosome extends Chromosome {
    int numInputs;
    int numOutputs;
    int numNodes;
    int numActiveNodes;
    int arity;
    Node[] nodes;
    int[] outputNodes;
    int[] activeNodes;
    double[] outputValues;
    MutableList<Function> funcSet;
    double[] nodeInputsHold;
    int generation;

    public static CGPChromosome deserialization(String serialize) {
        String[] line;

        /* open the chromosome file */
        @Cleanup Scanner scanner = new Scanner(serialize);

        /* get num inputs */
        line = scanner.nextLine().split(",");
        int numInputs = Integer.parseInt(line[1]);

        /* get num nodes */
        line = scanner.nextLine().split(",");
        int numNodes = Integer.parseInt(line[1]);

        /* get num outputs */
        line = scanner.nextLine().split(",");
        int numOutputs = Integer.parseInt(line[1]);

        /* get arity */
        line = scanner.nextLine().split(",");
        int arity = Integer.parseInt(line[1]);

        /* initialise parameters  */
        CartesianGP params = initialiseParameters(numInputs, numNodes, numOutputs, arity);

        /* for each function name */
        line = scanner.nextLine().split(",");
        for (int k = 0; k < line.length - 1; k++) {
            if (!params.addPresetFunctionToFunctionSet(line[k + 1])) {
                throw new IllegalArgumentException("Error: cannot load chromosome which contains custom node functions.");
            }
        }

        /* initialise a chromosome based on the parameters associated with given chromosome */
        CGPChromosome chromosome = initialiseChromosome(params);

        /* set the node parameters */
        for (int i = 0; i < numNodes; i++) {
            /* get the function gene */
            chromosome.nodes[i].function = scanner.nextInt();

            scanner.nextLine();
            for (int j = 0; j < arity; j++) {
                line = scanner.nextLine().split(",");
                chromosome.nodes[i].inputs[j] = Integer.parseInt(line[0]);
                chromosome.nodes[i].weights[j] = Double.parseDouble(line[1]);
            }
        }

        line = scanner.nextLine().split(",");
        /* set the outputs */
        for (int i = 0; i < numOutputs; i++) {
            chromosome.outputNodes[i] = Integer.parseInt(line[i]);
        }
        /* set the active nodes in the copied chromosome */
        setChromosomeActiveNodes(chromosome);

        return chromosome;
    }

    @Override
    public String serialization() {
        StringBuilder sb = new StringBuilder();

        /* save meta information */
        sb.append(String.format("numInputs,%d\n", numInputs));
        sb.append(String.format("numNodes,%d\n", numNodes));
        sb.append(String.format("numOutputs,%d\n", numOutputs));
        sb.append(String.format("arity,%d\n", arity));

        sb.append("FunctionSet");

        for (int i = 0; i < funcSet.size(); i++) {
            sb.append(String.format(",%s", funcSet.get(i).getName()));
        }
        sb.append("\n");

        /* save the chromosome structure */
        for (int i = 0; i < numNodes; i++) {
            sb.append(String.format("%d\n", nodes[i].function));
            for (int j = 0; j < arity; j++) {
                sb.append(String.format("%d,%f\n", nodes[i].inputs[j], nodes[i].weights[j]));
            }
        }

        for (int i = 0; i < numOutputs; i++) {
            sb.append(String.format("%d,", outputNodes[i]));
        }
        return sb.toString();
    }

    public void copyChromosome(CGPChromosome chromoSrc) {
        /* error checking  */
        if (this.numInputs != chromoSrc.numInputs) {
            throw new IllegalArgumentException("Error: cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome inputs do not match.");
        }

        if (this.numNodes != chromoSrc.numNodes) {
            throw new IllegalArgumentException("Error: cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome nodes do not match.");
        }

        if (this.numOutputs != chromoSrc.numOutputs) {
            throw new IllegalArgumentException("Error: cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome outputs do not match.");
        }

        if (this.arity != chromoSrc.arity) {
            throw new IllegalArgumentException("Error: cannot copy a chromosome to a chromosome of different dimensions. The arity of the chromosome nodes do not match.");
        }

        /* copy nodes and which are active */
        for (int i = 0; i < chromoSrc.numNodes; i++) {
            this.nodes[i].copyNode(chromoSrc.nodes[i]);
        }

        System.arraycopy(chromoSrc.activeNodes, 0, this.activeNodes, 0, chromoSrc.numNodes);

        /* copy functionSet */
        this.funcSet = chromoSrc.funcSet.clone();

        /* copy each of the chromosomes outputs */
        System.arraycopy(chromoSrc.outputNodes, 0, this.outputNodes, 0, chromoSrc.numOutputs);

        /* copy the number of active node */
        this.numActiveNodes = chromoSrc.numActiveNodes;

        /* copy the fitness */
        this.fitness = chromoSrc.fitness;

        /* copy generation */
        this.generation = chromoSrc.generation;
    }

    /**
     * reset the outputNodes values of all chromosome nodes to zero
     */
    public void resetChromosome() {
        IntStream.range(0, this.numNodes).forEach(i -> this.nodes[i].output = 0);
    }

    /**
     * used to access the chromosome outputs after executeChromosome
     * has been called
     */
    public double getChromosomeOutput(int output) {

        if (output < 0 || output > this.numOutputs) {
            System.out.print("Error: outputNodes less than or greater than the number of chromosome outputs. Called from getChromosomeOutput.\n");
            System.exit(0);
        }

        return this.outputValues[output];
    }

    /**
     * used to access the chromosome node values after executeChromosome
     * has been called
     */
    public double getChromosomeNodeValue(int node) {
        if (node < 0 || node > this.numNodes) {
            System.out.print("Error: node less than or greater than the number of nodes  in chromosome. Called from getChromosomeNodeValue.\n");
            System.exit(0);
        }
        return this.nodes[node].output;
    }

    /**
     * returns whether the specified node is active in the given chromosome
     */
    public boolean isNodeActive(int node) {

        if (node < 0 || node > this.numNodes) {
            System.out.print("Error: node less than or greater than the number of nodes  in chromosome. Called from isNodeActive.\n");
            System.exit(0);
        }

        return this.nodes[node].active;
    }

    /**
     * Gets the chromosome node arity
     */
    public int getChromosomeNodeArity(int index) {

        int chromoArity = this.arity;
        int maxArity = this.funcSet.get(this.nodes[index].function).arity();

        if (maxArity == -1) {
            return chromoArity;
        } else if (maxArity < chromoArity) {
            return maxArity;
        } else {
            return chromoArity;
        }
    }

    /**
     * Gets the number of active connections in the given chromosome
     */
    public int getNumChromosomeActiveConnections() {
        return IntStream.range(0, this.numActiveNodes).map(i -> this.nodes[this.activeNodes[i]].actArity).sum();
    }
}
