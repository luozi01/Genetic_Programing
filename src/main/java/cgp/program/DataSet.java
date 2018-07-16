package cgp.program;

public class DataSet {
    public int numSamples;
    public int numInputs;
    public int numOutputs;
    public double[][] inputData;
    public double[][] outputData;

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

    /*
        returns the given outputNodes of the given sample of the given dataSet
    */
    public double getDataSetSampleOutput(int sample, int output) {
        return outputData[sample][output];
    }
}
