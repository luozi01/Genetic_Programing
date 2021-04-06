package cgp.gp;

import cgp.emum.CGPFitnessStrategy;
import cgp.emum.CGPMutationStrategy;
import cgp.emum.CGPReproductionStrategy;
import cgp.emum.CGPSelectionStrategy;
import cgp.interfaces.CGPFunction;
import cgp.interfaces.CGPMutation;
import cgp.mutation.PointANNMutation;
import cgp.mutation.PointMutation;
import cgp.mutation.ProbabilisticMutation;
import cgp.mutation.ProbabilisticOnlyActiveMutation;
import cgp.mutation.SingleMutation;
import cgp.program.DataSet;
import lombok.Data;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import static cgp.emum.CGPFitnessStrategy.SUPERVISED_LEARNING;
import static cgp.emum.CGPMutationStrategy.POINT;
import static cgp.emum.CGPMutationStrategy.POINT_ANN;
import static cgp.emum.CGPMutationStrategy.PROBABILISTIC;
import static cgp.emum.CGPMutationStrategy.PROBABILISTIC_ONLY_ACTIVE;
import static cgp.emum.CGPMutationStrategy.SINGLE;
import static cgp.emum.CGPReproductionStrategy.MUTATE_RANDOM_PARENT;
import static cgp.emum.CGPSelectionStrategy.SELECT_FITTEST;
import static cgp.program.Operators.abs;
import static cgp.program.Operators.add;
import static cgp.program.Operators.and;
import static cgp.program.Operators.cos;
import static cgp.program.Operators.cube;
import static cgp.program.Operators.div;
import static cgp.program.Operators.exp;
import static cgp.program.Operators.gauss;
import static cgp.program.Operators.mul;
import static cgp.program.Operators.nand;
import static cgp.program.Operators.nor;
import static cgp.program.Operators.not;
import static cgp.program.Operators.one;
import static cgp.program.Operators.or;
import static cgp.program.Operators.pi;
import static cgp.program.Operators.pow;
import static cgp.program.Operators.rand;
import static cgp.program.Operators.sig;
import static cgp.program.Operators.sin;
import static cgp.program.Operators.softsign;
import static cgp.program.Operators.sq;
import static cgp.program.Operators.sqrt;
import static cgp.program.Operators.step;
import static cgp.program.Operators.sub;
import static cgp.program.Operators.tan;
import static cgp.program.Operators.tanh;
import static cgp.program.Operators.wire;
import static cgp.program.Operators.xnor;
import static cgp.program.Operators.xor;
import static cgp.program.Operators.zero;

@Data
public class CGPParams {
    private final Random random = new Random();
    private int mu;
    private int lambda;
    private char evolutionaryStrategy;
    private double mutationRate;
    private double recurrentConnectionProbability;
    private double connectionWeightRange;
    private int numInputs;
    private int numNodes;
    private int numOutputs;
    private int arity;
    private double targetFitness;
    private int updateFrequency;
    private boolean shortcutConnections;
    private CGPMutationStrategy mutationType;
    private CGPMutation mutationPolicy;
    private CGPFitnessStrategy fitnessFunction;
    private CGPSelectionStrategy selectionScheme;
    private CGPReproductionStrategy reproductionScheme;
    private MutableList<CGPFunction> functions;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<DataSet> data = Optional.empty();

    public static CGPParams initialiseParameters(int numInputs, int numNodes, int numOutputs, int arity) {
        if (numInputs <= 0) {
            throw new IllegalArgumentException(String.format("Number of chromosome inputs cannot be less than one; " +
                    "%d is invalid.", numInputs));
        }

        if (numNodes < 0) {
            throw new IllegalArgumentException(String.format("Number of chromosome nodes cannot be negative; " +
                    "%d is invalid.\n", numNodes));
        }

        if (numOutputs < 0) {
            throw new IllegalArgumentException(String.format("Number of chromosome outputs cannot be less than one; " +
                    "%d is invalid.", numOutputs));
        }

        if (arity < 0) {
            throw new IllegalArgumentException(String.format("Node arity cannot be less than one; %d is invalid.", arity));
        }
        CGPParams params = new CGPParams();

        /* Set default values */
        params.mu = 1;
        params.lambda = 4;
        params.evolutionaryStrategy = '+';
        params.mutationRate = 0.05;
        params.recurrentConnectionProbability = 0.0;
        params.connectionWeightRange = 1;
        params.shortcutConnections = true;

        params.targetFitness = 0;

        params.updateFrequency = 1;

        params.numInputs = numInputs;
        params.numNodes = numNodes;
        params.numOutputs = numOutputs;
        params.arity = arity;

        params.setMutation(PROBABILISTIC);
        params.functions = Lists.mutable.of(add, sub, mul, div);
        params.fitnessFunction = SUPERVISED_LEARNING;
        params.selectionScheme = SELECT_FITTEST;
        params.reproductionScheme = MUTATE_RANDOM_PARENT;

        return params;
    }

    public int nextInt(int numGenes) {
        return random.nextInt(numGenes);
    }

    public double uniform() {
        return random.nextDouble();
    }

    public void setMutation(CGPMutationStrategy strategy) {
        switch (strategy) {
            case PROBABILISTIC:
                this.setMutationType(PROBABILISTIC);
                this.setMutationPolicy(new ProbabilisticMutation());
                break;
            case POINT:
                this.setMutationType(POINT);
                this.setMutationPolicy(new PointMutation());
                break;
            case POINT_ANN:
                this.setMutationType(POINT_ANN);
                this.setMutationPolicy(new PointANNMutation());
                break;
            case PROBABILISTIC_ONLY_ACTIVE:
                this.setMutationType(PROBABILISTIC_ONLY_ACTIVE);
                this.setMutationPolicy(new ProbabilisticOnlyActiveMutation());
                break;
            case SINGLE:
                this.setMutationType(SINGLE);
                this.setMutationPolicy(new SingleMutation());
                break;
            default:
                throw new IllegalArgumentException(String.format("\nWarning: mutation type '%s' is invalid. " +
                                "The mutation type must be 'probabilistic' or 'point'. " +
                                "The mutation type has been left unchanged as '%s'.\n",
                        mutationType, this.mutationType));
        }
    }

    /**
     * returns a random chromosome outputNodes
     */
    public int getRandomChromosomeOutput(int numInputs, int numNodes) {
        return this.shortcutConnections ?
                random.nextInt(numInputs + numNodes) :
                random.nextInt(numNodes) + numInputs;
    }

    /**
     * returns a random connection weight value
     */
    public double getRandomConnectionWeight() {
        return (random.nextDouble() * 2 * connectionWeightRange) - connectionWeightRange;
    }

    /**
     * returns a random function index
     */
    public int getRandomFunction(int functionSize) {
        /* check that funcSet contains functions */
        if (functionSize < 1) {
            throw new IllegalArgumentException("Cannot assign the function gene a value as the Function Set is empty.");
        }
        return random.nextInt(functionSize);
    }

    /**
     * returns a random input for the given node
     */
    public int getRandomNodeInput(int numInputs,
                                  int numNodes,
                                  int nodePosition) {
        return random.nextDouble() < recurrentConnectionProbability ?
                random.nextInt(numNodes - nodePosition) + nodePosition + 1 : /* pick any ahead nodes or the node itself */
                random.nextInt(numInputs + nodePosition);  /* pick any previous node including inputs */
    }

    /**
     * Adds given node function to given function set with given name.
     * Disallows exceeding the function set size.
     */
    public void addCustomNodeFunction(CGPFunction function) {
        if (!this.functions.contains(function)) {
            this.functions.add(function);
        }
    }

    /**
     * used as an interface to adding pre-set node functions.
     * returns one if successful, zero otherwise.
     */
    public void addPresetFunctionToFunctionSet(String functionName) {

        /* Symbolic functions */
        switch (functionName.toLowerCase(Locale.ROOT)) {
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
                throw new IllegalArgumentException(String.format("Warning: function '%s' is not known which can be custom functions.", functionName));
        }
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
        sb.append("-----------------------------------------------------------\n");
        return sb.toString();
    }
}
