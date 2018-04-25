package Symbolic_engine;

import com.zluo.ga.utils.Observation;
import com.zluo.tgp.gp.TGPChromosome;
import com.zluo.tgp.solver.FitnessCalc;

import java.util.LinkedList;
import java.util.List;

public class TabulatedFunctionFitness implements FitnessCalc {

    private List<Observation> targets = new LinkedList<>();

    TabulatedFunctionFitness(List<Observation> targets) {
        this.targets.addAll(targets);
    }

    @Override
    public double fitness(TGPChromosome expression) {
        double diff = 0;

        for (Observation o : this.targets) {
            for (int i = 0; i < o.inputCount(); i++) {
                expression.getManager().setVariable(o.getTextInput(i), o.getInput(i));
            }
            double targetValue = o.getOutput(0);
            double calculatedValue = expression.eval();
            diff += Math.pow(targetValue - calculatedValue, 2);
        }
        return diff;
    }
}
