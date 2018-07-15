package cgp.program;

import cgp.interfaces.Function;

public class FunctionSet {
    private final static int FUNCTION_SET_SIZE = 50;
    public int numFunctions;
    public String[] functionNames = new String[FUNCTION_SET_SIZE];
    public int[] maxNumInputs = new int[FUNCTION_SET_SIZE];
    public Function[] functions = new Function[FUNCTION_SET_SIZE];
}
