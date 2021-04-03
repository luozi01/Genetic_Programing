package cgp.fitness;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import cgp.interfaces.CGPFitness;
import cgp.program.DataSet;

public class SupervisedLearning implements CGPFitness {
    @Override
    public double calc(CGPChromosome chromosome) {
        return 0;
    }

    @Override
    public double calc(CGPParams params, CGPChromosome chromosome, DataSet dataSet) {
        double error = 0;
        if (chromosome.getNumInputs() != dataSet.getNumInputs()) {
            throw new IllegalArgumentException("The number of chromosome inputs must match the number of inputs specified in the dataSet.");
        }
        if (chromosome.getNumOutputs() != dataSet.getNumOutputs()) {
            throw new IllegalArgumentException("The number of chromosome outputs must match the number of outputs specified in the dataSet.");
        }
        for (int i = 0; i < dataSet.getNumSamples(); i++) {
            chromosome.evaluate(dataSet.getDataSetSampleInputs(i));
            for (int j = 0; j < chromosome.getNumOutputs(); j++) {
                error += Math.abs(chromosome.getChromosomeOutput(j) - dataSet.getDataSetSampleOutput(i, j));
            }
        }
        return error;
    }
}
