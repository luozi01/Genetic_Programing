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
                addCustomNodeFunction(softsign);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------------------\n");
        sb.append("                       Parameters                          \n");
        sb.append("-----------------------------------------------------------\n");
        sb.append(String.format("Evolutionary Strategy:\t\t\t(%d%c%d)-ES\n", mu, evolutionaryStrategy, lambda));
        sb.append(String.format("Inputs:\t\t\t\t\t%d\n", numInputs));
        sb.append(String.format("nodes:\t\t\t\t\t%d\n", numNodes));
        sb.append(String.format("Outputs:\t\t\t\t%d\n", numOutputs));
        sb.append(String.format("Node Arity:\t\t\t\t%d\n", arity));
        sb.append(String.format("Connection weights range:\t\t+/- %f\n", connectionWeightRange));
        sb.append(String.format("Mutation Type:\t\t\t\t%s\n", mutationType));
        sb.append(String.format("Mutation rate:\t\t\t\t%f\n", mutationRate));
        sb.append(String.format("Recurrent Connection Probability:\t%f\n", recurrentConnectionProbability));
        sb.append(String.format("Shortcut Connections:\t\t\t%s\n", shortcutConnections));
        sb.append(String.format("Fitness Function:\t\t\t%s\n", fitnessFunction));
        sb.append(String.format("Target Fitness:\t\t\t\t%f\n", targetFitness));
        sb.append(String.format("Selection scheme:\t\t\t%s\n", selectionScheme));
        sb.append(String.format("Reproduction scheme:\t\t\t%s\n", reproductionScheme));
        sb.append(String.format("Update frequency:\t\t\t%d\n", updateFrequency));
        sb.append("Function Set:");
        for (int i = 0; i < this.functions.size(); i++) {
            sb.append(String.format(" %s", this.functions.get(i)));
        }
        sb.append(String.format(" (%d)\n", this.functions.size()));
        sb.append("-----------------------------------------------------------\n\n");
        return sb.toString();
    }
}
