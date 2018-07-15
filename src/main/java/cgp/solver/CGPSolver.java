package cgp.solver;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPCore;
import cgp.interfaces.CGPFitness;
import cgp.interfaces.CGPReproductionStrategy;
import cgp.interfaces.CGPSelectionStrategy;
import cgp.program.DataSet;
import cgp.program.FunctionSet;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

import static cgp.gp.CGPCore.*;
import static cgp.gp.CGPCore.fitnessCalc.supervisedLearning;
import static cgp.gp.CGPCore.mutationStrategy.*;
import static cgp.gp.CGPCore.reproduction.mutateRandomParent;
import static cgp.gp.CGPCore.selection.selectFittest;

public class CGPSolver {
    private CartesianGP params;
    private DataSet data;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<CGPChromosome> globalBest = Optional.empty();

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
    private static CartesianGP initialiseParameters(int numInputs, int numNodes, int numOutputs, int arity) {

        CartesianGP params = new CartesianGP();

        /* Set default values */
        params.mu = 1;
        params.lambda = 4;
        params.evolutionaryStrategy = '+';
        params.mutationRate = 0.05;
        params.recurrentConnectionProbability = 0.0;
        params.connectionWeightRange = 1;
        params.shortcutConnections = 1;

        params.targetFitness = 0;

        params.updateFrequency = 1;

        setNumInputs(params, numInputs);
        setNumNodes(params, numNodes);
        setNumOutputs(params, numOutputs);
        setArity(params, arity);

        params.mutationType = probabilisticMutation;
        params.mutationTypeName = "probabilistic";

        params.funcSet = new FunctionSet();
        params.funcSet.numFunctions = 0;

        params.fitnessFunction = supervisedLearning;
        params.fitnessFunctionName = "supervisedLearning";

        params.selectionScheme = selectFittest;
        params.selectionSchemeName = "selectFittest";

        params.reproductionScheme = mutateRandomParent;
        params.reproductionSchemeName = "mutateRandomParent";

        return params;
    }

    /**
     * sets num chromosome inputs in parameters
     */
    private static void setNumInputs(CartesianGP params, int numInputs) {

        /* error checking */
        if (numInputs <= 0) {
            System.out.printf("Error: number of chromosome inputs cannot be less than one; %d is invalid.\nTerminating CGP-Library.\n", numInputs);
            System.exit(0);
        }

        params.numInputs = numInputs;
    }

    /**
     * sets num chromosome nodes in parameters
     */
    private static void setNumNodes(CartesianGP params, int numNodes) {

        /* error checking */
        if (numNodes < 0) {
            System.out.printf("Warning: number of chromosome nodes cannot be negative; %d is invalid.\nTerminating CGP-Library.\n", numNodes);
            System.exit(0);
        }

        params.numNodes = numNodes;
    }

    /**
     * sets num chromosome outputs in parameters
     */
    private static void setNumOutputs(CartesianGP params, int numOutputs) {

        /* error checking */
        if (numOutputs < 0) {
            System.out.printf("Warning: number of chromosome outputs cannot be less than one; %d is invalid.\nTerminating CGP-Library.\n", numOutputs);
            System.exit(0);
        }

        params.numOutputs = numOutputs;
    }

    /**
     * sets chromosome arity in parameters
     */
    private static void setArity(CartesianGP params, int arity) {

        /* error checking */
        if (arity < 0) {
            System.out.printf("Warning: node arity cannot be less than one; %d is invalid.\nTerminating CGP-Library.\n", arity);
            System.exit(0);
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
            System.out.printf("\nWarning: mu value '%d' is invalid. Mu value must have a value of one or greater. Mu value left unchanged as '%d'.\n", mu, params.mu);
        }
    }

    /**
     * Sets the lambda value in given parameters to the new given value.
     * If lambda value is invalid a warning is displayed and the lambda value
     * is left unchanged.
     */
    public void setLambda(CartesianGP params, int lambda) {

        if (lambda > 0) {
            params.lambda = lambda;
        } else {
            System.out.printf("\nWarning: lambda value '%d' is invalid. Lambda value must have a value of one or greater. Lambda value left unchanged as '%d'.\n", lambda, params.lambda);
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
            System.out.printf("\nWarning: the evolutionary strategy '%c' is invalid. The evolutionary strategy must be '+' or ','. The evolutionary strategy has been left unchanged as '%c'.\n", evolutionaryStrategy, params.evolutionaryStrategy);
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
            System.out.printf("\nWarning: mutation rate '%f' is invalid. The mutation rate must be in the range [0,1]. The mutation rate has been left unchanged as '%f'.\n", mutationRate, params.mutationRate);
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
            System.out.printf("\nWarning: recurrent connection probability '%f' is invalid. The recurrent connection probability must be in the range [0,1]. The recurrent connection probability has been left unchanged as '%f'.\n", recurrentConnectionProbability, params.recurrentConnectionProbability);
        }
    }

    /**
     * Sets the whether shortcut connections are used. If an invalid
     * value is given a warning is displayed and the value is left	unchanged.
     */
    public void setShortcutConnections(int shortcutConnections) {

        if (shortcutConnections == 0 || shortcutConnections == 1) {
            params.shortcutConnections = shortcutConnections;
        } else {
            System.out.printf("\nWarning: shortcut connection '%d' is invalid. The shortcut connections takes values 0 or 1. The shortcut connection has been left unchanged as '%d'.\n", shortcutConnections, params.shortcutConnections);
        }
    }


    /**
     * Sets the update frequency in generations
     */
    public void setUpdateFrequency(int updateFrequency) {

        if (updateFrequency < 0) {
            System.out.printf("Warning: update frequency of %d is invalid. Update frequency must be >= 0. Update frequency is left unchanged as %d.\n", updateFrequency, params.updateFrequency);
        } else {
            params.updateFrequency = updateFrequency;
        }
    }

    /**
     * clears the given function set of functions
     */
    public void clearFunctionSet() {
        params.funcSet.numFunctions = 0;
    }


    /**
     * sets the fitness function to the fitnessFuction passed. If the fitnessFuction is NULL
     * then the default supervisedLearning fitness function is used.
     */
    public void setCustomFitnessFunction(CGPFitness fitnessFunction, String fitnessFunctionName) {

        if (fitnessFunction == null) {
            params.fitnessFunction = supervisedLearning;
            params.fitnessFunctionName = "supervisedLearning";
        } else {
            params.fitnessFunction = fitnessFunction;
            params.fitnessFunctionName = fitnessFunctionName;
        }
    }

    /**
     * sets the selection scheme used to select the parents from the candidate chromosomes. If the selectionScheme is NULL
     * then the default selectFittest selection scheme is used.
     */
    public void setCustomSelectionScheme(CGPSelectionStrategy selectionScheme, String selectionSchemeName) {
        if (selectionScheme == null) {
            params.selectionScheme = selectFittest;
            params.selectionSchemeName = "selectFittest";
        } else {
            params.selectionScheme = selectionScheme;
            params.selectionSchemeName = selectionSchemeName;
        }
    }

    public void setCustomReproductionScheme(CGPReproductionStrategy reproductionScheme, String reproductionSchemeName) {
        if (reproductionScheme == null) {
            params.reproductionScheme = mutateRandomParent;
            params.reproductionSchemeName = "mutateRandomParent";
        } else {
            params.reproductionScheme = reproductionScheme;
            params.reproductionSchemeName = reproductionSchemeName;
        }
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
                params.mutationType = probabilisticMutation;
                params.mutationTypeName = "probabilistic";
                break;
            case "point":
                params.mutationType = pointMutation;
                params.mutationTypeName = "point";
                break;
            case "pointANN":
                params.mutationType = pointMutationANN;
                params.mutationTypeName = "pointANN";
                break;
            case "onlyActive":
                params.mutationType = probabilisticMutationOnlyActive;
                params.mutationTypeName = "onlyActive";
                break;
            case "single":
                params.mutationType = singleMutation;
                params.mutationTypeName = "single";
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("\nWarning: mutation type '%s' is invalid. " +
                                        "The mutation type must be 'probabilistic' or 'point'. " +
                                        "The mutation type has been left unchanged as '%s'.\n",
                                mutationType, params.mutationTypeName));
        }
    }

    /**
     * Initialises data ure and assigns values of given file
     */
    public void initialiseDataSetFromFile(String file) {

        int i;
        data = new DataSet();
        InputStream inputStream = CGPCore.class.getClassLoader().getResourceAsStream(file);
        Scanner scanner = new Scanner(inputStream);

        int lineNum = -1;

        /* for every line in the given file */
        while (scanner.hasNext()) {

            /* deal with the first line containing meta data */
            if (lineNum == -1) {

                String[] dataset = scanner.nextLine().split(",");

                data.numInputs = Integer.parseInt(dataset[0]);
                data.numOutputs = Integer.parseInt(dataset[1]);
                data.numSamples = Integer.parseInt(dataset[dataset.length - 1]);

                data.inputData = new double[data.numSamples][];
                data.outputData = new double[data.numSamples][];

                for (i = 0; i < data.numSamples; i++) {
                    data.inputData[i] = new double[data.numInputs];
                    data.outputData[i] = new double[data.numOutputs];
                }
            }
            /* the other lines contain input outputNodes pairs */
            else {
                /* get the first value on the given line */
                String[] dataset = scanner.nextLine().split(",");

                for (int j = 0; j < dataset.length; j++) {
                    if (j < data.numInputs) {
                        data.inputData[lineNum][j] = Double.parseDouble(dataset[j]);
                    } else {
                        data.outputData[lineNum][j - data.numInputs] = Double.parseDouble(dataset[j]);
                    }
                }
            }

            /* increment the current line index */
            lineNum++;
        }
        scanner.close();
    }

    public void evolve(int iteration) {
        globalBest = Optional.of(runCGP(params, data, iteration));
    }

    public CGPChromosome getBestGene() {
        return globalBest.orElse(null);
    }

    /**
     * Adds the give pre-defined functions to the given function set. The
     * functions must be given in the char array. The function names must
     * be comma separated and contain no spaces i.e. "and,or".
     */
    public void addNodeFunction(String functionNames) {
        String[] func = functionNames.split(",");
        for (String aFunc : func) {
            addPresetFuctionToFunctionSet(params, aFunc);
        }

        /* if the function set is empty give warning */
        if (params.funcSet.numFunctions == 0) {
            System.out.print("Warning: No Functions added to function set.\n");
        }
    }

    public void printParams() {
        printParameters(params);
    }
}
