package cgp.program;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DataSet {
    @Getter
    private int numSamples;
    @Getter
    private int numInputs;
    @Getter
    private int numOutputs;
    private double[][] inputData;
    private double[][] outputData;

    public DataSet(final int numSamples, int numInputs, int numOutputs) {
        this.numSamples = numSamples;
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;

        this.inputData = new double[numSamples][numInputs];
        this.outputData = new double[numSamples][numOutputs];
    }

    /**
     * returns the inputs of the given sample of the given dataSet
     */
    public double[] getDataSetSampleInputs(int sample) {
        return inputData[sample];
    }

    /**
     * returns the given input of the given sample of the given dataSet
     */
    public double getDataSetSampleInput(int sample, int input) {
        return inputData[sample][input];
    }

    /**
     * returns the outputs of the given sample of the given dataSet
     */
    public double[] getDataSetSampleOutputs(int sample) {
        return outputData[sample];
    }

    /**
     * returns the given outputNodes of the given sample of the given dataSet
     */
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
}
