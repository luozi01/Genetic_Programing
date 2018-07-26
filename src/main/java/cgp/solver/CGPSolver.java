package cgp.solver;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPCore;
import cgp.interfaces.CGPFitness;
import cgp.interfaces.CGPReproductionStrategy;
import cgp.interfaces.CGPSelectionStrategy;
import cgp.interfaces.CGPFunction;
import cgp.program.DataSet;
import cgp.program.Results;
import org.eclipse.collections.impl.factory.Lists;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

import static cgp.gp.CGPCore.MutationStrategy.*;
import static cgp.gp.CGPCore.fitnessCalc.supervisedLearning;
import static cgp.gp.CGPCore.*;
import static cgp.gp.CGPCore.reproduction.mutateRandomParent;
import static cgp.gp.CGPCore.selection.selectFittest;

public class CGPSolver {
    private final CartesianGP params;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<DataSet> data = Optional.empty();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<CGPChromosome> globalBest = Optional.empty();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Results> results = Optional.empty();

    public CGPSolver(final int numInputs,
                     final int numNodes,
                     final int numOutputs,
                     final int nodeArity) {
        params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);
    }

    /**
     * Initialises a parameter  with default values. These
     * values can be individually changed via set functions.
     */
    public static CartesianGP initialiseParameters(int numInputs, int numNodes, int numOutputs, int arity) {

        CartesianGP params = new CartesianGP();

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

        setNumInputs(params, numInputs);
        setNumNodes(params, numNodes);
        setNumOutputs(params, numOutputs);
        setArity(params, arity);

        params.mutationType = probabilistic;

        params.functions = Lists.mutable.empty();

        params.fitnessFunction = supervisedLearning;

        params.selectionScheme = selectFittest;

        params.reproductionScheme = mutateRandomParent;

        return params;
    }

    /**
     * sets num chromosome inputs in parameters
     */
    private static void setNumInputs(CartesianGP params, int numInputs) {
        /* error checking */
        if (numInputs <= 0) {
            throw new IllegalArgumentException(String.format("Number of chromosome inputs cannot be less than one; " +
                    "%d is invalid.", numInputs));
        }

        params.numInputs = numInputs;
    }

    /**
     * sets num chromosome nodes in parameters
     */
    private static void setNumNodes(CartesianGP params, int numNodes) {
        /* error checking */
        if (numNodes < 0) {
            throw new IllegalArgumentException(String.format("Number of chromosome nodes cannot be negative; " +
                    "%d is invalid.\n", numNodes));
        }

        params.numNodes = numNodes;
    }

    /**
     * sets num chromosome outputs in parameters
     */
    private static void setNumOutputs(CartesianGP params, int numOutputs) {
        /* error checking */
        if (numOutputs < 0) {
            throw new IllegalArgumentException(String.format("Number of chromosome outputs cannot be less than one; " +
                    "%d is invalid.", numOutputs));
        }

        params.numOutputs = numOutputs;
    }

    /**
     * sets chromosome arity in parameters
     */
    private static void setArity(CartesianGP params, int arity) {
        /* error checking */
        if (arity < 0) {
            throw new IllegalArgumentException(String.format("Node arity cannot be less than one; %d is invalid.", arity));
        }
        params.arity = arity;
    }

    /**
     * Sets the connection weight range given in parameters.
     */
    public void setConnectionWeightRange(double weightRange) {
        params.connectionWeightRange = weightRange;
    }

    /**
     * Sets the mu value in given parameters to the new given value. If mu value
     * is invalid a warning is displayed and the mu value is left unchanged.
     */
    public void setMu(int mu) {
        if (mu > 0) {
            params.mu = mu;
        } else {
            System.err.printf("Mu value '%d' is invalid. Mu value must have a value of one or greater. " +
                    "Mu value left unchanged as '%d'.\n", mu, params.mu);
        }
    }

    /**
     * Sets the lambda value in given parameters to the new given value.
     * If lambda value is invalid a warning is displayed and the lambda value
     * is left unchanged.
     */
    public void setLambda(int lambda) {
        if (lambda > 0) {
            params.lambda = lambda;
        } else {
            System.err.printf("Lambda value '%d' is invalid. Lambda value must have a value of one or greater. " +
                    "Lambda value left unchanged as '%d'.\n", lambda, params.lambda);
        }
    }

    /**
     * Sets the evolutionary strategy given in parameters to '+' or ','.
     * If an invalid option is given a warning is displayed and the evolutionary
     * strategy is left unchanged.
     */
    public void setEvolutionaryStrategy(char evolutionaryStrategy) {
        if (evolutionaryStrategy == '+' || evolutionaryStrategy == ',') {
            params.evolutionaryStrategy = evolutionaryStrategy;
        } else {
            System.err.printf("\nWarning: the evolutionary strategy '%c' is invalid. " +
                            "The evolutionary strategy must be '+' or ','. " +
                            "The evolutionary strategy has been left unchanged as '%c'.\n",
                    evolutionaryStrategy, params.evolutionaryStrategy);
        }
    }

    /**
     * Sets the mutation rate given in parameters. If an invalid mutation
     * rate is given a warning is displayed and the mutation rate is left
     * unchanged.
     */
    public void setMutationRate(double mutationRate) {
        if (mutationRate >= 0 && mutationRate <= 1) {
            params.mutationRate = mutationRate;
        } else {
            System.err.printf("\nWarning: mutation rate '%f' is invalid. " +
                    "The mutation rate must be in the range [0,1]. " +
                    "The mutation rate has been left unchanged as '%f'.\n", mutationRate, params.mutationRate);
        }
    }

    /**
     * Sets the recurrent connection probability given in parameters. If an invalid
     * value is given a warning is displayed and the value is left	unchanged.
     */
    public void setRecurrentConnectionProbability(double recurrentConnectionProbability) {
        if (recurrentConnectionProbability >= 0 && recurrentConnectionProbability <= 1) {
            params.recurrentConnectionProbability = recurrentConnectionProbability;
        } else {
            System.err.print(String.format("\nWarning: recurrent connection probability '%f' is invalid. " +
                            "The recurrent connection probability must be in the range [0,1]. " +
                            "The recurrent connection probability has been left unchanged as '%f'.\n",
                    recurrentConnectionProbability, params.recurrentConnectionProbability));
        }
    }

    /**
     * Sets the whether shortcut connections are used. If an invalid
     * value is given a warning is displayed and the value is left	unchanged.
     */
    public void setShortcutConnections(boolean shortcutConnections) {
        params.shortcutConnections = shortcutConnections;
    }

    /**
     * Sets the update frequency in generations
     */
    public void setUpdateFrequency(int updateFrequency) {
        if (updateFrequency < 0) {
            System.err.printf("Warning: update frequency of %d is invalid. Update frequency must be >= 0. " +
                    "Update frequency is left unchanged as %d.\n", updateFrequency, params.updateFrequency);
        } else {
            params.updateFrequency = updateFrequency;
        }
    }

    /**
     * clears the given function set of functions
     */
    public void clearFunctionSet() {
        params.functions.clear();
    }

    /**
     * sets the fitness function to the fitnessFuction passed. If the fitnessFunction is NULL
     * then the default supervisedLearning fitness function is used.
     */
    public void setCustomFitnessFunction(CGPFitness fitnessFunction) {
        params.fitnessFunction = fitnessFunction == null ? supervisedLearning : fitnessFunction;
    }

    /**
     * sets the selection scheme used to select the parents from the candidate chromosomes.
     * If the selectionScheme is NULL then the default selectFittest selection scheme is used.
     */
    public void setCustomSelectionScheme(CGPSelectionStrategy selectionScheme) {
        params.selectionScheme = selectionScheme == null ? selectFittest : selectionScheme;
    }

    public void setCustomReproductionScheme(CGPReproductionStrategy reproductionScheme) {
        params.reproductionScheme = reproductionScheme == null ? mutateRandomParent : reproductionScheme;
    }

    /**
     * Sets the target fitness
     */
    public void setTargetFitness(double targetFitness) {
        params.targetFitness = targetFitness;
    }

    /**
     * sets the mutation type in params
     */
    public void setMutationType(String mutationType) {
        switch (mutationType) {
            case "probabilistic":
                params.mutationType = probabilistic;
                break;
            case "point":
                params.mutationType = point;
                break;
            case "pointANN":
                params.mutationType = pointANN;
                break;
            case "probabilisticOnlyActive":
                params.mutationType = probabilisticOnlyActive;
                break;
            case "single":
                params.mutationType = single;
                break;
            default:
                System.err.printf("\nWarning: mutation type '%s' is invalid. " +
                                "The mutation type must be 'probabilistic' or 'point'. " +
                                "The mutation type has been left unchanged as '%s'.\n",
                        mutationType, params.mutationType);
        }
    }

    public void initialiseDataSet(double[][] input, double[][] output, int numSamples, int numInput, int numOutput) {
        if (input == null || output == null) {
            throw new NullPointerException("Input or output cannot be null");
        }
        DataSet data = new DataSet();
        data.numInputs = numInput;
        data.numOutputs = numOutput;

        data.numSamples = numSamples;
        data.inputData = new double[data.numSamples][];
        data.outputData = new double[data.numSamples][];

        for (int i = 0; i < input.length; i++) {
            data.inputData[i] = input[i].clone();
            data.outputData[i] = output[i].clone();
        }
        this.data = Optional.of(data);
    }

    /**
     * Initialises data ure and assigns values of given file
     */
    public void initialiseDataSetFromFile(String file) {
        DataSet data = new DataSet();
        InputStream inputStream = CGPCore.class.getClassLoader().getResourceAsStream(file);
        Scanner scanner = new Scanner(inputStream);

        int lineNum = -1;
        String[] dataSet;
        /* for every line in the given file */
        while (scanner.hasNext()) {

            /* deal with the first line containing meta data */
            if (lineNum == -1) {

                dataSet = scanner.nextLine().split(",");

                data.numInputs = Integer.parseInt(dataSet[0]);
                data.numOutputs = Integer.parseInt(dataSet[1]);
                data.numSamples = Integer.parseInt(dataSet[dataSet.length - 1]);

                data.inputData = new double[data.numSamples][];
                data.outputData = new double[data.numSamples][];

                for (int i = 0; i < data.numSamples; i++) {
                    data.inputData[i] = new double[data.numInputs];
                    data.outputData[i] = new double[data.numOutputs];
                }
            }
            /* the other lines contain input outputNodes pairs */
            else {
                /* get the first value on the given line */
                dataSet = scanner.nextLine().split(",");

                for (int j = 0; j < dataSet.length; j++) {
                    if (j < data.numInputs) {
                        data.inputData[lineNum][j] = Double.parseDouble(dataSet[j]);
                    } else {
                        data.outputData[lineNum][j - data.numInputs] = Double.parseDouble(dataSet[j]);
                    }
                }
            }
            /* increment the current line index */
            lineNum++;
        }
        scanner.close();
        this.data = Optional.of(data);
    }

    public void evolve(int iteration, CGPChromosome... chromosomes) {
        globalBest = Optional.of(runCGP(params, data.orElse(null), iteration, chromosomes));
    }

    public void repeatEvolve(int numGens, int numRuns, CGPChromosome... chromosomes) {
        results = Optional.of(repeatCGP(params, data.orElse(null), numGens, numRuns, chromosomes));
        results.ifPresent(o -> globalBest = Optional.of(o.getBestChromosome()));
    }

    /**
     * if node needs for continues training, then should not
     * remove inactive nodes
     *
     * @param concise if true, will remove inactive nodes
     * @return the best gene
     */
    public CGPChromosome getBestGene(boolean concise) {
        if (concise)
            globalBest.ifPresent(CGPCore::removeInactiveNodes);
        return globalBest.orElse(null);
    }

    public Results getResults() {
        return results.orElse(null);
    }

    public CGPChromosome getChromosome(int index) {
        return results.map(o -> o.bestCGPChromosomes.get(index)).orElse(null);
    }

    /**
     * Adds the give pre-defined functions to the given function set. The
     * functions must be given in the char array. The function names must
     * be comma separated and contain no spaces i.e. "and,or".
     */
    public void addNodeFunction(String functionNames) {
        String[] func = functionNames.split(",");
        for (String aFunc : func) {
            params.addPresetFunctionToFunctionSet(aFunc);
        }

        /* if the function set is empty give warning */
        if (params.functions.isEmpty()) {
            System.err.print("Warning: No Functions added to function set.\n");
        }
    }

    public void addSelfDefineFunction(CGPFunction function) {
        params.addCustomNodeFunction(function);
    }

    public void printParams() {
        printParameters(params);
    }
}
