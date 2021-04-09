package cgp.fitness;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.data.DataSet;
import genetics.interfaces.FitnessCalc;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SupervisedLearning implements FitnessCalc<CGPChromosome> {
  private final CGPParams params;

  /**
   * Compute the fitness based on dataset
   *
   * @param chromosome CGP chromosome
   * @return fitness
   */
  @Override
  public double calc(CGPChromosome chromosome) {
    final DataSet dataSet = this.params.getData().orElseThrow();
    double error = 0;
    if (chromosome.getNumInputs() != dataSet.getNumInputs()) {
      throw new IllegalArgumentException(
          "The number of chromosome inputs must match the number of inputs specified in the dataSet.");
    }
    if (chromosome.getNumOutputs() != dataSet.getNumOutputs()) {
      throw new IllegalArgumentException(
          "The number of chromosome outputs must match the number of outputs specified in the dataSet.");
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
