package genetics.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.primitive.IntInterval;
import org.eclipse.collections.impl.tuple.Tuples;

@NoArgsConstructor
public class DataSet {
  @Getter private int numSamples;

  @Getter private int numInputs;

  @Getter private int numOutputs;

  private double[][] inputData;
  private double[][] outputData;

  @Builder
  public DataSet(final int numSamples, int numInputs, int numOutputs) {
    this.numSamples = numSamples;
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;

    this.inputData = new double[numSamples][numInputs];
    this.outputData = new double[numSamples][numOutputs];
  }

  /** returns the inputs of the given sample of the given dataSet */
  public double[] getDataSetSampleInputs(int sample) {
    return inputData[sample];
  }

  /** returns the outputs of the given sample of the given dataSet */
  public double[] getDataSetSampleOutputs(int sample) {
    return outputData[sample];
  }

  /** returns the given input of the given sample of the given dataSet */
  public double getDataSetSampleInput(int sample, int input) {
    return inputData[sample][input];
  }

  /** returns the given outputNodes of the given sample of the given dataSet */
  public double getDataSetSampleOutput(int sample, int output) {
    return outputData[sample][output];
  }

  public void setInputData(int index, double[] value) {
    this.inputData[index] = value.clone();
  }

  public void setOutputData(int index, double[] value) {
    this.outputData[index] = value.clone();
  }

  public void setInputData(int inputIndex, int labelIndex, double value) {
    this.inputData[inputIndex][labelIndex] = value;
  }

  public void setOutputData(int inputIndex, int labelIndex, double value) {
    this.outputData[inputIndex][labelIndex] = value;
  }

  /**
   * Split dataset into two portion with percentage
   *
   * @param percent split percentage
   * @return training dataset, testing dataset
   */
  public Pair<DataSet, DataSet> split(double percent) {
    if (percent < 0 || percent > 1) {
      throw new IllegalArgumentException("Split percentage should be within [0, 1]");
    }
    int trainingSize = (int) Math.ceil(this.numSamples * percent);
    DataSet trainingSet =
        DataSet.builder()
            .numSamples(trainingSize)
            .numInputs(this.numInputs)
            .numOutputs(this.numOutputs)
            .build();
    DataSet testingSet =
        DataSet.builder()
            .numSamples(this.numSamples - trainingSize)
            .numInputs(this.numInputs)
            .numOutputs(this.numOutputs)
            .build();

    MutableIntList randomSample = IntInterval.zeroTo(this.numSamples - 1).toList().shuffleThis();
    for (int i = 0; i < randomSample.size(); i++) {
      final int sampleIndex = randomSample.get(i);
      if (i < trainingSize) {
        trainingSet.setInputData(i, this.inputData[sampleIndex]);
        trainingSet.setOutputData(i, this.outputData[sampleIndex]);
      } else {
        testingSet.setInputData(i - trainingSize, this.inputData[sampleIndex]);
        testingSet.setOutputData(i - trainingSize, this.outputData[sampleIndex]);
      }
    }
    return Tuples.pair(trainingSet, testingSet);
  }
}
