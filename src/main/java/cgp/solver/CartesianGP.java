package cgp.solver;

import cgp.interfaces.*;
import org.eclipse.collections.api.list.MutableList;

import static cgp.program.Operators.*;

public class CartesianGP {
    public int mu;
    public int lambda;
    public char evolutionaryStrategy;
    public double mutationRate;
    public double recurrentConnectionProbability;
    public double connectionWeightRange;
    public int numInputs;
    public int numNodes;
    public int numOutputs;
    public int arity;
    public double targetFitness;
    public int updateFrequency;
    public boolean shortcutConnections;
    public CGPMutationStrategy mutationType;
    public CGPFitness fitnessFunction;
    public CGPSelectionStrategy selectionScheme;
    public CGPReproductionStrategy reproductionScheme;
    public MutableList<CGPFunction> functions;

    /**
     * Adds given node function to given function set with given name.
     * Disallows exceeding the function set size.
     */
    void addCustomNodeFunction(CGPFunction function) {
        this.functions.add(function);
    }

    /**
     * used as an interface to adding pre-set node functions.
     * returns one if successful, zero otherwise.
     */
    public boolean addPresetFunctionToFunctionSet(String functionName) {

        /* Symbolic functions */

        switch (functionName) {
            case "add":
                addCustomNodeFunction(add);
                break;
            case "sub":
                addCustomNodeFunction(sub);
                break;
            case "mul":
                addCustomNodeFunction(mul);
                break;
            case "div":
                addCustomNodeFunction(div);
                break;
            case "abs":
                addCustomNodeFunction(abs);
                break;
            case "sqrt":
                addCustomNodeFunction(sqrt);
                break;
            case "sq":
                addCustomNodeFunction(sq);
                break;
            case "cube":
                addCustomNodeFunction(cube);
                break;
            case "pow":
                addCustomNodeFunction(pow);
                break;
            case "exp":
                addCustomNodeFunction(exp);
                break;
            case "sin":
                addCustomNodeFunction(sin);
                break;
            case "cos":
                addCustomNodeFunction(cos);
                break;
            case "tan":
                addCustomNodeFunction(tan);
                break;


            /* Boolean logic gates */
            case "and":
                addCustomNodeFunction(and);
                break;
            case "nand":
                addCustomNodeFunction(nand);
                break;
            case "or":
                addCustomNodeFunction(or);
                break;
            case "nor":
                addCustomNodeFunction(nor);
                break;
            case "xor":
                addCustomNodeFunction(xor);
                break;
            case "xnor":
                addCustomNodeFunction(xnor);
                break;
            case "not":
                addCustomNodeFunction(not);
                break;

            /* Neuron functions */
            case "sig":
                addCustomNodeFunction(sig);
                break;
            case "gauss":
                addCustomNodeFunction(gauss);
                break;
            case "step":
                addCustomNodeFunction(step);
                break;
            case "softsign":
                addCustomNodeFunction(soft);
                break;
            case "tanh":
                addCustomNodeFunction(tanh);
                break;

            /* other */
            case "rand":
                addCustomNodeFunction(rand);
                break;
            case "one":
                addCustomNodeFunction(one);
                break;
            case "zero":
                addCustomNodeFunction(zero);
                break;
            case "pi":
                addCustomNodeFunction(pi);
                break;
            case "wire":
                addCustomNodeFunction(wire);
                break;
            default:
                System.err.printf("Warning: function '%s' is not known and was not added.\n", functionName);
                return false;
        }
        return true;
    }
}
