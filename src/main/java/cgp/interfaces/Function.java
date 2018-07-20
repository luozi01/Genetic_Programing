package cgp.interfaces;

public interface Function {
    int arity();

    double calc(int numInputs, double[] inputs, double[] connectionWeights);

    String getName();
}
