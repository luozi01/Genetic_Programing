package cgp.interfaces;

public interface CGPFunction {
    int arity();

    double calc(int numInputs, double[] inputs, double[] connectionWeights);
}
