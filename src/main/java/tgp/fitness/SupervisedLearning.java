package tgp.fitness;

import genetics.data.DataSet;
import genetics.interfaces.FitnessCalc;
import lombok.AllArgsConstructor;
import tgp.gp.TGPChromosome;
import tgp.gp.TGPParams;

@AllArgsConstructor
public class SupervisedLearning implements FitnessCalc<TGPChromosome> {
  private final TGPParams params;

  /**
   * Compute the fitness based on dataset
   *
   * @param chromosome CGP chromosome
   * @return fitness
   */
  @Override
  public double calc(TGPChromosome chromosome) {
    final DataSet dataSet = this.params.getData().orElseThrow();
    double error = 0;
    if (this.params.getNumInputs() != dataSet.getNumInputs()) {
      throw new IllegalArgumentException(
          "The number of chromosome inputs must match the number of inputs specified in the dataSet.");
    }
    for (int i = 0; i < dataSet.getNumSamples(); i++) {
      double result = chromosome.eval(dataSet.getDataSetSampleInputs(i));
      error += Math.abs(result - dataSet.getDataSetSampleOutput(i, 0));
    }
    return error;
  }
}
