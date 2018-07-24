package cgp.solver;

import cgp.interfaces.*;
import org.eclipse.collections.api.list.MutableList;

import static cgp.gp.CGPCore.operations.*;

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
    public int shortcutConnections;
    public CGPMutationStrategy mutationType;
    public String mutationTypeName;
    public CGPFitness fitnessFunction;
    public String fitnessFunctionName;
    public CGPSelectionStrategy selectionScheme;
    public String selectionSchemeName;
    public CGPReproductionStrategy reproductionScheme;
    public String reproductionSchemeName;
    public MutableList<Function> functions;

    /**
     * Adds given node function to given function set with given name.
     * Disallows exceeding the function set size.
     */
    void addCustomNodeFunction(Function function) {
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
                addCustomNodeFunction(_add);
                break;
            case "sub":
                addCustomNodeFunction(_sub);
                break;
            case "mul":
                addCustomNodeFunction(_mul);
                break;
            case "div":
                addCustomNodeFunction(_divide);
                break;
            case "abs":
                addCustomNodeFunction(_absolute);
                break;
            case "sqrt":
                addCustomNodeFunction(_squareRoot);
                break;
            case "sq":
                addCustomNodeFunction(_square);
                break;
            case "cube":
                addCustomNodeFunction(_cube);
                break;
            case "pow":
                addCustomNodeFunction(_power);
                break;
            case "exp":
                addCustomNodeFunction(_exponential);
                break;
            case "sin":
                addCustomNodeFunction(_sine);
                break;
            case "cos":
                addCustomNodeFunction(_cosine);
                break;
            case "tan":
                addCustomNodeFunction(_tangent);
                break;


            /* Boolean logic gates */
            case "and":
                addCustomNodeFunction(_and);
                break;
            case "nand":
                addCustomNodeFunction(_nand);
                break;
            case "or":
                addCustomNodeFunction(_or);
                break;
            case "nor":
                addCustomNodeFunction(_nor);
                break;
            case "xor":
                addCustomNodeFunction(_xor);
                break;
            case "xnor":
                addCustomNodeFunction(_xnor);
                break;
            case "not":
                addCustomNodeFunction(_not);
                break;

            /* Neuron functions */
            case "sig":
                addCustomNodeFunction(_sigmoid);
                break;
            case "gauss":
                addCustomNodeFunction(_gaussian);
                break;
            case "step":
                addCustomNodeFunction(_step);
                break;
            case "softsign":
                addCustomNodeFunction(_softsign);
                break;
            case "tanh":
                addCustomNodeFunction(_hyperbolicTangent);
                break;

            /* other */
            case "rand":
                addCustomNodeFunction(_randFloat);
                break;
            case "1":
                addCustomNodeFunction(_One);
                break;
            case "0":
                addCustomNodeFunction(_Zero);
                break;
            case "pi":
                addCustomNodeFunction(_PI);
                break;
            case "wire":
                addCustomNodeFunction(_wire);
                break;
            default:
                System.err.printf("Warning: function '%s' is not known and was not added.\n", functionName);
                return false;
        }
        return true;
    }
}
