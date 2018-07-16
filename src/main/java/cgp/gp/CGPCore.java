package cgp.gp;

import cgp.interfaces.*;
import cgp.program.DataSet;
import cgp.program.FunctionSet;
import cgp.program.Node;
import cgp.program.Results;
import cgp.solver.CartesianGP;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;

import java.util.Arrays;
import java.util.stream.IntStream;

import static cgp.gp.CGPCore.operations.*;
import static java.lang.Math.*;

public class CGPCore {
    private final static int FUNCTION_SET_SIZE = 50;
    private final static RandEngine randEngine = new SimpleRandEngine();

    /**
     * Adds given node function to given function set with given name.
     * Disallows exceeding the function set size.
     */
    private static void addCustomNodeFunction(CartesianGP params, Function function, String functionName, int maxNumInputs) {

        if (params.funcSet.numFunctions >= FUNCTION_SET_SIZE) {
            System.out.printf("Warning: functions set has reached maximum capacity (%d). Function '%s' not added.\n", FUNCTION_SET_SIZE, functionName);
            return;
        }

        /* set the function name as the given function name */
        params.funcSet.functionNames[params.funcSet.numFunctions] = functionName;

        /* set the number of function inputs as the given number of function inputs */
        params.funcSet.maxNumInputs[params.funcSet.numFunctions] = maxNumInputs;

        /* add the given function to the function set */
        params.funcSet.functions[params.funcSet.numFunctions] = function;

        params.funcSet.numFunctions++;
    }

    /**
     * used as an interface to adding pre-set node functions.
     * returns one if successful, zero otherwise.
     */
    public static boolean addPresetFuctionToFunctionSet(CartesianGP params, String functionName) {

        /* Symbolic functions */

        switch (functionName) {
            case "add":
                addCustomNodeFunction(params, _add, "add", -1);
                break;
            case "sub":
                addCustomNodeFunction(params, _sub, "sub", -1);
                break;
            case "mul":
                addCustomNodeFunction(params, _mul, "mul", -1);
                break;
            case "div":
                addCustomNodeFunction(params, _divide, "div", -1);
                break;
            case "abs":
                addCustomNodeFunction(params, _absolute, "abs", 1);
                break;
            case "sqrt":
                addCustomNodeFunction(params, _squareRoot, "sqrt", 1);
                break;
            case "sq":
                addCustomNodeFunction(params, _square, "sq", 1);
                break;
            case "cube":
                addCustomNodeFunction(params, _cube, "cube", 1);
                break;
            case "pow":
                addCustomNodeFunction(params, _power, "pow", 2);
                break;
            case "exp":
                addCustomNodeFunction(params, _exponential, "exp", 1);
                break;
            case "sin":
                addCustomNodeFunction(params, _sine, "sin", 1);
                break;
            case "cos":
                addCustomNodeFunction(params, _cosine, "cos", 1);
                break;
            case "tan":
                addCustomNodeFunction(params, _tangent, "tan", 1);
                break;


            /* Boolean logic gates */
            case "and":
                addCustomNodeFunction(params, _and, "and", -1);
                break;
            case "nand":
                addCustomNodeFunction(params, _nand, "nand", -1);
                break;
            case "or":
                addCustomNodeFunction(params, _or, "or", -1);
                break;
            case "nor":
                addCustomNodeFunction(params, _nor, "nor", -1);
                break;
            case "xor":
                addCustomNodeFunction(params, _xor, "xor", -1);
                break;
            case "xnor":
                addCustomNodeFunction(params, _xnor, "xnor", -1);
                break;
            case "not":
                addCustomNodeFunction(params, _not, "not", 1);
                break;

            /* Neuron functions */
            case "sig":
                addCustomNodeFunction(params, _sigmoid, "sig", -1);
                break;
            case "gauss":
                addCustomNodeFunction(params, _gaussian, "gauss", -1);
                break;
            case "step":
                addCustomNodeFunction(params, _step, "step", -1);
                break;
            case "softsign":
                addCustomNodeFunction(params, _softsign, "soft", -1);
                break;
            case "tanh":
                addCustomNodeFunction(params, _hyperbolicTangent, "tanh", -1);
                break;

            /* other */
            case "rand":
                addCustomNodeFunction(params, _randFloat, "rand", 0);
                break;
            case "1":
                addCustomNodeFunction(params, _One, "1", 0);
                break;
            case "0":
                addCustomNodeFunction(params, _Zero, "0", 0);
                break;
            case "pi":
                addCustomNodeFunction(params, _PI, "pi", 0);
                break;
            case "wire":
                addCustomNodeFunction(params, _wire, "wire", 1);
                break;
            default:
                System.out.printf("Warning: function '%s' is not known and was not added.\n", functionName);
                return false;
        }
        return true;
    }

    /**
     * Returns a pointer to an initialised chromosome with values obeying the given parameters.
     */
    static CGPChromosome initialiseChromosome(CartesianGP params) {
        CGPChromosome chromo = new CGPChromosome();
        /* check that funcSet contains functions */
        if (params.funcSet.numFunctions < 1) {
            throw new IllegalArgumentException("Error: chromosome not initialised due to empty functionSet.\nTerminating CGP-Library.\n");
        }

        /* allocate memory for nodes */
        chromo.nodes = new Node[params.numNodes];

        /* allocate memory for outputNodes matrix */
        chromo.outputNodes = new int[params.numOutputs];

        /* allocate memory for active nodes matrix */
        chromo.activeNodes = new int[params.numNodes];

        /* allocate memory for chromosome outputValues */
        chromo.outputValues = new double[params.numOutputs];

        /* Initialise each of the chromosomes nodes */
        for (int i = 0; i < params.numNodes; i++) {
            chromo.nodes[i] = initialiseNode(params.numInputs, params.numNodes,
                    params.arity, params.funcSet.numFunctions,
                    params.connectionWeightRange, params.recurrentConnectionProbability, i);
        }

        /* set each of the chromosomes outputs */
        for (int i = 0; i < params.numOutputs; i++) {
            chromo.outputNodes[i] = getRandomChromosomeOutput(params.numInputs, params.numNodes, params.shortcutConnections);
        }

        /* set the number of inputs, nodes and outputs */
        chromo.numInputs = params.numInputs;
        chromo.numNodes = params.numNodes;
        chromo.numOutputs = params.numOutputs;
        chromo.arity = params.arity;

        /* set the number of active node to the number of nodes (all active) */
        chromo.numActiveNodes = params.numNodes;

        /* set the fitness to initial value */
        chromo.fitness = -1;

        /* copy the function set from the parameters to the chromosome */
        chromo.funcSet = new FunctionSet();
        copyFunctionSet(chromo.funcSet, params.funcSet);

        /* set the active nodes in the newly generated chromosome */
        setChromosomeActiveNodes(chromo);

        /* used interally when exicuting chromosome */
        chromo.nodeInputsHold = new double[params.arity];

        return chromo;
    }

    /**
     * Executes the given chromosome
     */
    public static void executeChromosome(CGPChromosome chromo, double[] inputs) {
        int numInputs = chromo.numInputs;
        int numActiveNodes = chromo.numActiveNodes;
        int numOutputs = chromo.numOutputs;

        /* for all of the active nodes */
        for (int i = 0; i < numActiveNodes; i++) {

            /* get the index of the current active node */
            int currentActiveNode = chromo.activeNodes[i];

            /* get the arity of the current node */
            int nodeArity = chromo.nodes[currentActiveNode].actArity;

            /* for each of the active nodes inputs */
            for (int j = 0; j < nodeArity; j++) {

                /* gather the nodes input locations */
                int nodeInputLocation = chromo.nodes[currentActiveNode].inputs[j];

                if (nodeInputLocation < numInputs) {
                    chromo.nodeInputsHold[j] = inputs[nodeInputLocation];
                } else {
                    chromo.nodeInputsHold[j] = chromo.nodes[nodeInputLocation - numInputs].output;
                }
            }

            /* get the functionality of the active node under evaluation */
            int currentActiveNodeFuction = chromo.nodes[currentActiveNode].function;

            /* calculate the outputNodes of the active node under evaluation */
            chromo.nodes[currentActiveNode].output = chromo.funcSet.functions[currentActiveNodeFuction]
                    .calc(nodeArity, chromo.nodeInputsHold, chromo.nodes[currentActiveNode].weights);


            /* deal with doubles becoming NAN */
            if (Double.isNaN(chromo.nodes[currentActiveNode].output)) {
                chromo.nodes[currentActiveNode].output = 0;
            }

            /* prevent double form going to inf and -inf */
            else if (Double.isInfinite(chromo.nodes[currentActiveNode].output)) {

                if (chromo.nodes[currentActiveNode].output > 0) {
                    chromo.nodes[currentActiveNode].output = Double.MAX_VALUE;
                } else {
                    chromo.nodes[currentActiveNode].output = Double.MIN_VALUE;
                }
            }
        }

        /* Set the chromosome outputs */
        for (int i = 0; i < numOutputs; i++) {
            if (chromo.outputNodes[i] < numInputs) {
                chromo.outputValues[i] = inputs[chromo.outputNodes[i]];
            } else {
                chromo.outputValues[i] = chromo.nodes[chromo.outputNodes[i] - numInputs].output;
            }
        }
    }

    /**
     * used to access the chromosome outputs after executeChromosome
     * has been called
     */
    public static double getChromosomeOutput(CGPChromosome chromo, int output) {

        if (output < 0 || output > chromo.numOutputs) {
            System.out.print("Error: outputNodes less than or greater than the number of chromosome outputs. Called from getChromosomeOutput.\n");
            System.exit(0);
        }

        return chromo.outputValues[output];
    }

    /**
     * used to access the chromosome node values after executeChromosome
     * has been called
     */
    private static double getChromosomeNodeValue(CGPChromosome chromo, int node) {
        if (node < 0 || node > chromo.numNodes) {
            System.out.print("Error: node less than or greater than the number of nodes  in chromosome. Called from getChromosomeNodeValue.\n");
            System.exit(0);
        }
        return chromo.nodes[node].output;
    }

    /*
        returns whether the specified node is active in the given chromosome
    */
    private static boolean isNodeActive(CGPChromosome chromo, int node) {

        if (node < 0 || node > chromo.numNodes) {
            System.out.print("Error: node less than or greater than the number of nodes  in chromosome. Called from isNodeActive.\n");
            System.exit(0);
        }

        return chromo.nodes[node].active;
    }

    /*
        Mutates the given chromosome using the mutation method described in parameters
    */
    private static void mutateChromosome(CartesianGP params, CGPChromosome chromo) {

        params.mutationType.mutate(params, chromo);

        setChromosomeActiveNodes(chromo);
    }

    /**
     * sets the fitness of the given chromosome
     */
    private static void setChromosomeFitness(CartesianGP params, CGPChromosome chromo, DataSet data) {

        setChromosomeActiveNodes(chromo);

        resetChromosome(chromo);

        chromo.fitness = params.fitnessFunction.calc(params, chromo, data);
    }

    /**
     * reset the outputNodes values of all chromosome nodes to zero
     */
    private static void resetChromosome(CGPChromosome chromo) {
        IntStream.range(0, chromo.numNodes).forEach(i -> chromo.nodes[i].output = 0);
    }

    /*
        copies the contents of one chromosome to another. Provided the number of inputs, nodes, outputs and node arity are the same.
    */
    private static void copyChromosome(CGPChromosome chromoDest, CGPChromosome chromoSrc) {

        int i;

        /* error checking  */
        if (chromoDest.numInputs != chromoSrc.numInputs) {
            System.out.print("Error: cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome inputs do not match.\n");
            System.out.print("Terminating CGP-Library.\n");
            System.exit(0);
        }

        if (chromoDest.numNodes != chromoSrc.numNodes) {
            System.out.print("Error: cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome nodes do not match.\n");
            System.out.print("Terminating CGP-Library.\n");
            System.exit(0);
        }

        if (chromoDest.numOutputs != chromoSrc.numOutputs) {
            System.out.print("Error: cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome outputs do not match.\n");
            System.out.print("Terminating CGP-Library.\n");
            System.exit(0);
        }

        if (chromoDest.arity != chromoSrc.arity) {
            System.out.print("Error: cannot copy a chromosome to a chromosome of different dimensions. The arity of the chromosome nodes do not match.\n");
            System.out.print("Terminating CGP-Library.\n");
            System.exit(0);
        }

        /* copy nodes and which are active */
        for (i = 0; i < chromoSrc.numNodes; i++) {
            copyNode(chromoDest.nodes[i], chromoSrc.nodes[i]);
            chromoDest.activeNodes[i] = chromoSrc.activeNodes[i];
        }

        /* copy fuctionset */
        copyFunctionSet(chromoDest.funcSet, chromoSrc.funcSet);

        /* copy each of the chromosomes outputs */
        for (i = 0; i < chromoSrc.numOutputs; i++) {
            chromoDest.outputNodes[i] = chromoSrc.outputNodes[i];
        }

        /* copy the number of active node */
        chromoDest.numActiveNodes = chromoSrc.numActiveNodes;

        /* copy the fitness */
        chromoDest.fitness = chromoSrc.fitness;

        /* copy generation */
        chromoDest.generation = chromoSrc.generation;
    }

    /**
     * Gets the chromosome node arity
     */
    private static int getChromosomeNodeArity(CGPChromosome chromo, int index) {

        int chromoArity = chromo.arity;
        int maxArity = chromo.funcSet.maxNumInputs[chromo.nodes[index].function];

        if (maxArity == -1) {
            return chromoArity;
        } else if (maxArity < chromoArity) {
            return maxArity;
        } else {
            return chromoArity;
        }
    }

    /*
        Gets the number of active connections in the given chromosome
    */
    static int getNumChromosomeActiveConnections(CGPChromosome chromo) {

        int i;
        int complexity = 0;

        for (i = 0; i < chromo.numActiveNodes; i++) {
            complexity += chromo.nodes[chromo.activeNodes[i]].actArity;
        }

        return complexity;
    }

    /**
     * set the active nodes in the given chromosome
     */
    static void setChromosomeActiveNodes(CGPChromosome chromo) {
        /* error checking */
        if (chromo == null) {
            throw new IllegalArgumentException("Error: chromosome has not been initialised and so the active nodes cannot be set.");
        }

        /* set the number of active nodes to zero */
        chromo.numActiveNodes = 0;

        /* reset the active nodes */
        for (int i = 0; i < chromo.numNodes; i++) {
            chromo.nodes[i].active = false;
        }

        /* start the recursive search for active nodes from the outputNodes nodes for the number of outputNodes nodes */
        for (int i = 0; i < chromo.numOutputs; i++) {

            /* if the outputNodes connects to a chromosome input, skip */
            if (chromo.outputNodes[i] < chromo.numInputs) {
                continue;
            }

            /* begin a recursive search for active nodes */
            recursivelySetActiveNodes(chromo, chromo.outputNodes[i]);
        }

        /* place active nodes in order */
        Arrays.sort(chromo.activeNodes, 0, chromo.numActiveNodes);
    }

    /*
        used by setActiveNodes to recursively search for active nodes
    */
    private static void recursivelySetActiveNodes(CGPChromosome chromo, int nodeIndex) {
        /* if the given node is an input, stop */
        if (nodeIndex < chromo.numInputs) {
            return;
        }

        /* if the given node has already been flagged as active */
        if (chromo.nodes[nodeIndex - chromo.numInputs].active) {
            return;
        }

        /* log the node as active */
        chromo.nodes[nodeIndex - chromo.numInputs].active = true;
        chromo.activeNodes[chromo.numActiveNodes] = nodeIndex - chromo.numInputs;
        chromo.numActiveNodes++;

        /* set the nodes actual arity*/
        chromo.nodes[nodeIndex - chromo.numInputs].actArity = getChromosomeNodeArity(chromo, nodeIndex - chromo.numInputs);

        /* recursively log all the nodes to which the current nodes connect as active */
        for (int i = 0; i < chromo.nodes[nodeIndex - chromo.numInputs].actArity; i++) {
            recursivelySetActiveNodes(chromo, chromo.nodes[nodeIndex - chromo.numInputs].inputs[i]);
        }
    }

    /**
     * Sorts the given array of chromosomes by fitness, lowest to highest
     * uses insertion sort (quickish and stable)
     */
    private static void sortChromosomeArray(CGPChromosome[] chromoArray, int numChromos) {
        CGPChromosome chromoTmp;
        for (int i = 0; i < numChromos; i++) {
            for (int j = i; j < numChromos; j++) {
                if (chromoArray[i].fitness > chromoArray[j].fitness) {
                    chromoTmp = chromoArray[i];
                    chromoArray[i] = chromoArray[j];
                    chromoArray[j] = chromoTmp;
                }
            }
        }
    }

    private static Results initialiseResults(int numRuns) {
        Results rels = new Results();
        rels.bestCGPChromosomes = new CGPChromosome[numRuns];
        rels.numRuns = numRuns;
        return rels;
    }

    /**
     * repetitively applies runCGP to obtain average behaviour
     */
    public static Results repeatCGP(CartesianGP params, DataSet data, int numGens, int numRuns) {
        Results rels;
        int updateFrequency = params.updateFrequency;

        /* set the update frequency so as to to so generational results */
        params.updateFrequency = 0;

        rels = initialiseResults(numRuns);

        System.out.print("Run\tFitness\t\tGenerations\tActive nodes\n");

        /* for each run */
        for (int i = 0; i < numRuns; i++) {
            rels.bestCGPChromosomes[i] = runCGP(params, data, numGens);
            System.out.printf("%d\t%f\t%d\t\t%d\n", i, rels.bestCGPChromosomes[i].fitness, rels.bestCGPChromosomes[i].generation, rels.bestCGPChromosomes[i].numActiveNodes);
        }

        System.out.print("----------------------------------------------------\n");
        System.out.printf("MEAN\t%f\t%f\t%f\n", rels.getAverageFitness(), rels.getAverageGenerations(), rels.getAverageActiveNodes());
        System.out.printf("MEDIAN\t%f\t%f\t%f\n", rels.getMedianFitness(), rels.getMedianGenerations(), rels.getMedianActiveNodes());
        System.out.print("----------------------------------------------------\n\n");

        /* restore the original value for the update frequency */
        params.updateFrequency = updateFrequency;

        return rels;
    }

    public static CGPChromosome runCGP(CartesianGP params, DataSet data, int numGens) {
        int gen;

        /* bestChromosome found using runCGP */
        CGPChromosome bestChromosome;

        /* error checking */
        if (numGens < 0) {
            System.out.printf("Error: %d generations is invalid. The number of generations must be >= 0.\n Terminating CGP-Library.\n", numGens);
            System.exit(0);
        }

        if (data != null && params.numInputs != data.numInputs) {
            System.out.printf("Error: The number of inputs specified in the dataSet (%d) does not match the number of inputs specified in the parameters (%d).\n", data.numInputs, params.numInputs);
            System.out.print("Terminating CGP-Library.\n");
            System.exit(0);
        }

        if (data != null && params.numOutputs != data.numOutputs) {
            System.out.printf("Error: The number of outputs specified in the dataSet (%d) does not match the number of outputs specified in the parameters (%d).\n", data.numOutputs, params.numOutputs);
            System.out.print("Terminating CGP-Library.\n");
            System.exit(0);
        }

        /* initialise parent chromosomes */
        CGPChromosome[] parents = new CGPChromosome[params.mu];

        for (int i = 0; i < params.mu; i++) {
            parents[i] = initialiseChromosome(params);
        }

        /* initialise children chromosomes */
        CGPChromosome[] children = new CGPChromosome[params.lambda];

        for (int i = 0; i < params.lambda; i++) {
            children[i] = initialiseChromosome(params);
        }

        /* initialize best chromosome */
        bestChromosome = initialiseChromosome(params);

        /* determine the size of the Candidate Chromos based on the evolutionary Strategy */
        int numCandidate = 0;
        if (params.evolutionaryStrategy == '+') {
            numCandidate = params.mu + params.lambda;
        } else if (params.evolutionaryStrategy == ',') {
            numCandidate = params.lambda;
        } else {
            System.out.printf("Error: the evolutionary strategy '%c' is not known.\nTerminating CGP-Library.\n", params.evolutionaryStrategy);
            System.exit(0);
        }

        /* initialise the candidateChromos */
        CGPChromosome[] candidates = new CGPChromosome[numCandidate];

        for (int i = 0; i < numCandidate; i++) {
            candidates[i] = initialiseChromosome(params);
        }

        /* set fitness of the parents */
        for (int i = 0; i < params.mu; i++) {
            setChromosomeFitness(params, parents[i], data);
        }

        /* show the user whats going on */
        if (params.updateFrequency != 0) {
            System.out.print("\n-- Starting CGP --\n\n");
            System.out.print("Gen\tfitness\n");
        }

        /* for each generation */
        for (gen = 0; gen < numGens; gen++) {

            /* set fitness of the children of the population */
            for (int i = 0; i < params.lambda; i++) {
                setChromosomeFitness(params, children[i], data);
            }

            /* get best chromosome */
            getBestChromosome(parents, children, params.mu, params.lambda, bestChromosome);

            /* check termination conditions */
            if (bestChromosome.fitness <= params.targetFitness) {
                if (params.updateFrequency != 0) {
                    System.out.printf("%d\t%f - Solution Found\n", gen, bestChromosome.fitness);
                }
                break;
            }

            /* display progress to the user at the update frequency specified */
            if (params.updateFrequency != 0 && (gen % params.updateFrequency == 0 || gen >= numGens - 1)) {
                System.out.printf("%d\t%f\n", gen, bestChromosome.fitness);
            }

            // Set the chromosomes which will be used by the selection scheme
            // dependant upon the evolutionary strategy. i.e. '+' all are used
            // by the selection scheme, ',' only the children are.
            if (params.evolutionaryStrategy == '+') {

			/*
				Note: the children are placed before the parents to
				ensure 'new blood' is always selected over old if the
				fitness are equal.
			*/

                for (int i = 0; i < numCandidate; i++) {
                    if (i < params.lambda) {
                        copyChromosome(candidates[i], children[i]);
                    } else {
                        copyChromosome(candidates[i], parents[i - params.lambda]);
                    }
                }
            } else if (params.evolutionaryStrategy == ',') {
                for (int i = 0; i < numCandidate; i++) {
                    copyChromosome(candidates[i], children[i]);
                }
            }

            /* select the parents from the candidateChromos */
            params.selectionScheme.select(params, parents, candidates, params.mu, numCandidate);

            /* create the children from the parents */
            params.reproductionScheme.reproduce(params, parents, children, params.mu, params.lambda);
        }

        /* deal with formatting for displaying progress */
        if (params.updateFrequency != 0) {
            System.out.print("\n");
        }

        /* copy the best best chromosome */
        bestChromosome.generation = gen;
        /*copyChromosome(chromo, bestChromosome);*/

        return bestChromosome;
    }

    /**
     * returns a pointer to the fittest chromosome in the two arrays of chromosomes
     * <p>
     * loops through parents and then the children in order for the children to always be selected over the parents
     */
    private static void getBestChromosome(CGPChromosome[] parents, CGPChromosome[] children, int numParents, int numChildren, CGPChromosome best) {

        int i;
        CGPChromosome bestChromoSoFar;

        bestChromoSoFar = parents[0];

        for (i = 1; i < numParents; i++) {

            if (parents[i].fitness <= bestChromoSoFar.fitness) {
                bestChromoSoFar = parents[i];
            }
        }

        for (i = 0; i < numChildren; i++) {

            if (children[i].fitness <= bestChromoSoFar.fitness) {
                bestChromoSoFar = children[i];
            }
        }

        copyChromosome(best, bestChromoSoFar);
    }

    /**
     * copies the contents of funcSetSrc to funcSetDest
     */
    private static void copyFunctionSet(FunctionSet funcSetDest, FunctionSet funcSetSrc) {
        funcSetDest.numFunctions = funcSetSrc.numFunctions;

        for (int i = 0; i < funcSetDest.numFunctions; i++) {
            funcSetDest.functionNames[i] = funcSetSrc.functionNames[i];
            funcSetDest.functions[i] = funcSetSrc.functions[i];
            funcSetDest.maxNumInputs[i] = funcSetSrc.maxNumInputs[i];
        }
    }

    /**
     * copy the contents for the src node into dest node.
     */
    private static void copyNode(Node nodeDest, Node nodeSrc) {
        // copy the node's function
        nodeDest.function = nodeSrc.function;

        // copy active flag
        nodeDest.active = nodeSrc.active;

        // copy the node arity
        nodeDest.maxArity = nodeSrc.maxArity;
        nodeDest.actArity = nodeSrc.actArity;

        // copy the nodes inputs and connection weights
        for (int i = 0; i < nodeSrc.maxArity; i++) {
            nodeDest.inputs[i] = nodeSrc.inputs[i];
            nodeDest.weights[i] = nodeSrc.weights[i];
        }
    }

    /**
     * returns a pointer to an initialised node. Initialised means that necessary
     * memory has been allocated and values set.
     */
    private static Node initialiseNode(final int numInputs,
                                       final int numNodes,
                                       final int arity,
                                       final int numFunctions,
                                       final double connectionWeightRange,
                                       final double recurrentConnectionProbability,
                                       final int nodePosition) {

        Node n = new Node();

        /* allocate memory for the node's inputs and connection weights */
        n.inputs = new int[arity];
        n.weights = new double[arity];

        /* set the node's function */
        n.function = getRandomFunction(numFunctions);

        /* set as active by default */
        n.active = true;

        /* set the nodes inputs and connection weights */
        for (int i = 0; i < arity; i++) {
            n.inputs[i] = getRandomNodeInput(numInputs, numNodes, nodePosition, recurrentConnectionProbability);
            n.weights[i] = getRandomConnectionWeight(connectionWeightRange);
        }

        /* set the outputNodes of the node to zero*/
        n.output = 0;

        /* set the arity of the node */
        n.maxArity = arity;

        return n;
    }

    /**
     * returns a random connection weight value
     */
    private static double getRandomConnectionWeight(double weightRange) {
        return (randEngine.uniform() * 2 * weightRange) - weightRange;
    }

    /**
     * returns a random function index
     */
    private static int getRandomFunction(int numFunctions) {

        /* check that funcSet contains functions */
        if (numFunctions < 1) {
            System.out.print("Error: cannot assign the function gene a value as the Fuction Set is empty.\nTerminating CGP-Library.\n");
            System.exit(0);
        }

        return randEngine.nextInt(numFunctions);
    }

    /**
     * returns a random input for the given node
     */
    private static int getRandomNodeInput(int numChromoInputs, int numNodes, int nodePosition, double recurrentConnectionProbability) {
        return randEngine.uniform() < recurrentConnectionProbability ?
                randEngine.nextInt(numNodes - nodePosition) + nodePosition + 1 : /* pick any ahead nodes or the node itself */
                randEngine.nextInt(numChromoInputs + nodePosition);  /* pick any previous node including inputs */
    }

    /**
     * returns a random chromosome outputNodes
     */
    private static int getRandomChromosomeOutput(int numInputs, int numNodes, int shortcutConnections) {
        return shortcutConnections == 1 ?
                randEngine.nextInt(numInputs + numNodes) :
                randEngine.nextInt(numNodes) + numInputs;
    }

    /**
     * Returns the sum of the weighted inputs.
     */
    private static double sumWeightedInputs(int numInputs, double[] inputs, double[] connectionWeights) {
        double weightedSum = 0;

        for (int i = 0; i < numInputs; i++) {
            weightedSum += (inputs[i] * connectionWeights[i]);
        }
        return weightedSum;
    }

    /**
     * Prints the current functions in the function set to
     * the terminal.
     */
    private static void printFunctionSet(CartesianGP params) {
        System.out.print("Function Set:");
        for (int i = 0; i < params.funcSet.numFunctions; i++) {
            System.out.printf(" %s", params.funcSet.functionNames[i]);
        }
        System.out.printf(" (%d)\n", params.funcSet.numFunctions);
    }

    /**
     * prints the given parameters to the terminal
     */
    public static void printParameters(CartesianGP params) {

        if (params == null) {
            System.out.print("Error: cannot print uninitialised parameters.\nTerminating CGP-Library.\n");
            System.exit(0);
        }

        System.out.print("-----------------------------------------------------------\n");
        System.out.print("                       Parameters                          \n");
        System.out.print("-----------------------------------------------------------\n");
        System.out.printf("Evolutionary Strategy:\t\t\t(%d%c%d)-ES\n", params.mu, params.evolutionaryStrategy, params.lambda);
        System.out.printf("Inputs:\t\t\t\t\t%d\n", params.numInputs);
        System.out.printf("nodes:\t\t\t\t\t%d\n", params.numNodes);
        System.out.printf("Outputs:\t\t\t\t%d\n", params.numOutputs);
        System.out.printf("Node Arity:\t\t\t\t%d\n", params.arity);
        System.out.printf("Connection weights range:\t\t+/- %f\n", params.connectionWeightRange);
        System.out.printf("Mutation Type:\t\t\t\t%s\n", params.mutationTypeName);
        System.out.printf("Mutation rate:\t\t\t\t%f\n", params.mutationRate);
        System.out.printf("Recurrent Connection Probability:\t%f\n", params.recurrentConnectionProbability);
        System.out.printf("Shortcut Connections:\t\t\t%d\n", params.shortcutConnections);
        System.out.printf("Fitness Function:\t\t\t%s\n", params.fitnessFunctionName);
        System.out.printf("Target Fitness:\t\t\t\t%f\n", params.targetFitness);
        System.out.printf("Selection scheme:\t\t\t%s\n", params.selectionSchemeName);
        System.out.printf("Reproduction scheme:\t\t\t%s\n", params.reproductionSchemeName);
        System.out.printf("Update frequency:\t\t\t%d\n", params.updateFrequency);
        printFunctionSet(params);
        System.out.print("-----------------------------------------------------------\n\n");
    }

    /**
     * Prints the given chromosome to the screen
     */
    public static void printChromosome(CGPChromosome chromo, boolean weights) {

        int i, j;

        /* error checking */
        if (chromo == null) {
            System.out.print("Error: chromosome has not been initialised and cannot be printed.\n");
            return;
        }

        /* set the active nodes in the given chromosome */
        setChromosomeActiveNodes(chromo);

        /* for all the chromo inputs*/
        for (i = 0; i < chromo.numInputs; i++) {
            System.out.printf("(%d):\tinput\n", i);
        }

        /* for all the hidden nodes */
        for (i = 0; i < chromo.numNodes; i++) {

            /* print the node function */
            System.out.printf("(%d):\t%s\t", chromo.numInputs + i, chromo.funcSet.functionNames[chromo.nodes[i].function]);

            /* for the arity of the node */
            for (j = 0; j < getChromosomeNodeArity(chromo, i); j++) {

                /* print the node input information */
                if (weights) {
                    System.out.printf("%d,%+.1f\t", chromo.nodes[i].inputs[j], chromo.nodes[i].weights[j]);
                } else {
                    System.out.printf("%d ", chromo.nodes[i].inputs[j]);
                }
            }

            /* Highlight active nodes */
            if (chromo.nodes[i].active) {
                System.out.print("*");
            }

            System.out.print("\n");
        }

        /* for all of the outputs */
        System.out.print("outputs: ");
        for (i = 0; i < chromo.numOutputs; i++) {

            /* print the outputNodes node locations */
            System.out.printf("%d ", chromo.outputNodes[i]);
        }

        System.out.print("\n\n");
    }

    public static void removeInactiveNodes(CGPChromosome chromosome) {

        /* set the active nodes */
        setChromosomeActiveNodes(chromosome);

        /* for all nodes */
        for (int i = 0; i < chromosome.numNodes - 1; i++) {

            /* if the node is inactive */
            if (!chromosome.nodes[i].active) {

                /* set the node to be the next node */
                for (int j = i; j < chromosome.numNodes - 1; j++) {
                    copyNode(chromosome.nodes[j], chromosome.nodes[j + 1]);
                }

                /* */
                for (int j = 0; j < chromosome.numNodes; j++) {
                    for (int k = 0; k < chromosome.arity; k++) {

                        if (chromosome.nodes[j].inputs[k] >= i + chromosome.numInputs) {
                            chromosome.nodes[j].inputs[k]--;
                        }
                    }
                }

                /* for the number of chromosome outputs */
                for (int j = 0; j < chromosome.numOutputs; j++) {

                    if (chromosome.outputNodes[j] >= i + chromosome.numInputs) {
                        chromosome.outputNodes[j]--;
                    }
                }

                /* de-increment the number of nodes */
                chromosome.numNodes--;

                /* made the newly assigned node be evaluated */
                i--;
            }
        }

        if (!chromosome.nodes[chromosome.numNodes - 1].active) {
            chromosome.numNodes--;
        }

        /* set the active nodes */
        setChromosomeActiveNodes(chromosome);
    }

    public enum mutationStrategy implements CGPMutationStrategy {
        pointMutation {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                int nodeIndex;
                /* get the number of each type of gene */
                int numFunctionGenes = params.numNodes;
                int numInputGenes = params.numNodes * params.arity;
                int numOutputGenes = params.numOutputs;

                /* set the total number of chromosome genes */
                int numGenes = numFunctionGenes + numInputGenes + numOutputGenes;

                /* calculate the number of genes to mutate */
                int numGenesToMutate = (int) round(numGenes * params.mutationRate);

                /* for the number of genes to mutate */
                for (int i = 0; i < numGenesToMutate; i++) {

                    /* select a random gene */
                    int geneToMutate = randEngine.nextInt(numGenes);

                    /* mutate function gene */
                    if (geneToMutate < numFunctionGenes) {

                        nodeIndex = geneToMutate;

                        chromosome.nodes[nodeIndex].function = getRandomFunction(chromosome.funcSet.numFunctions);
                    }

                    /* mutate node input gene */
                    else if (geneToMutate < numFunctionGenes + numInputGenes) {

                        nodeIndex = (geneToMutate - numFunctionGenes) / chromosome.arity;
                        int nodeInputIndex = (geneToMutate - numFunctionGenes) % chromosome.arity;

                        chromosome.nodes[nodeIndex].inputs[nodeInputIndex] = getRandomNodeInput(chromosome.numInputs, chromosome.numNodes, nodeIndex, params.recurrentConnectionProbability);
                    }

                    /* mutate outputNodes gene */
                    else {
                        nodeIndex = geneToMutate - numFunctionGenes - numInputGenes;
                        chromosome.outputNodes[nodeIndex] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);
                    }
                }
            }
        },
        pointMutationANN {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                int i;
                int numGenes;
                int numFunctionGenes, numInputGenes, numWeightGenes, numOutputGenes;
                int numGenesToMutate;
                int geneToMutate;
                int nodeIndex;
                int nodeInputIndex;

                /* get the number of each type of gene */
                numFunctionGenes = params.numNodes;
                numInputGenes = params.numNodes * params.arity;
                numWeightGenes = params.numNodes * params.arity;
                numOutputGenes = params.numOutputs;

                /* set the total number of chromosome genes */
                numGenes = numFunctionGenes + numInputGenes + numWeightGenes + numOutputGenes;

                /* calculate the number of genes to mutate */
                numGenesToMutate = (int) round(numGenes * params.mutationRate);

                /* for the number of genes to mutate */
                for (i = 0; i < numGenesToMutate; i++) {

                    /* select a random gene */
                    geneToMutate = randEngine.nextInt(numGenes);

                    /* mutate function gene */
                    if (geneToMutate < numFunctionGenes) {

                        nodeIndex = geneToMutate;

                        chromosome.nodes[nodeIndex].function = getRandomFunction(chromosome.funcSet.numFunctions);
                    }

                    /* mutate node input gene */
                    else if (geneToMutate < numFunctionGenes + numInputGenes) {

                        nodeIndex = (geneToMutate - numFunctionGenes) / chromosome.arity;
                        nodeInputIndex = (geneToMutate - numFunctionGenes) % chromosome.arity;

                        chromosome.nodes[nodeIndex].inputs[nodeInputIndex] = getRandomNodeInput(chromosome.numInputs, chromosome.numNodes, nodeIndex, params.recurrentConnectionProbability);
                    }

                    /* mutate connection weight */
                    else if (geneToMutate < numFunctionGenes + numInputGenes + numWeightGenes) {

                        nodeIndex = (geneToMutate - numFunctionGenes - numInputGenes) / chromosome.arity;
                        nodeInputIndex = (geneToMutate - numFunctionGenes - numInputGenes) % chromosome.arity;

                        chromosome.nodes[nodeIndex].weights[nodeInputIndex] = getRandomConnectionWeight(params.connectionWeightRange);
                    }

                    /* mutate outputNodes gene */
                    else {
                        nodeIndex = geneToMutate - numFunctionGenes - numInputGenes - numWeightGenes;
                        chromosome.outputNodes[nodeIndex] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);
                    }
                }
            }
        },
        singleMutation {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                int numFunctionGenes, numInputGenes, numOutputGenes;
                int numGenes;
                int geneToMutate;
                int nodeIndex;
                int nodeInputIndex;

                int mutatedActive = 0;
                int previousGeneValue;
                int newGeneValue;

                /* get the number of each type of gene */
                numFunctionGenes = params.numNodes;
                numInputGenes = params.numNodes * params.arity;
                numOutputGenes = params.numOutputs;

                /* set the total number of chromosome genes */
                numGenes = numFunctionGenes + numInputGenes + numOutputGenes;

                /* while active gene not mutated */
                while (mutatedActive == 0) {

                    /* select a random gene */
                    geneToMutate = randEngine.nextInt(numGenes);

                    /* mutate function gene */
                    if (geneToMutate < numFunctionGenes) {

                        nodeIndex = geneToMutate;

                        previousGeneValue = chromosome.nodes[nodeIndex].function;

                        chromosome.nodes[nodeIndex].function = getRandomFunction(chromosome.funcSet.numFunctions);

                        newGeneValue = chromosome.nodes[nodeIndex].function;

                        if ((previousGeneValue != newGeneValue) && (chromosome.nodes[nodeIndex].active)) {
                            mutatedActive = 1;
                        }

                    }

                    /* mutate node input gene */
                    else if (geneToMutate < numFunctionGenes + numInputGenes) {

                        nodeIndex = (geneToMutate - numFunctionGenes) / chromosome.arity;
                        nodeInputIndex = (geneToMutate - numFunctionGenes) % chromosome.arity;

                        previousGeneValue = chromosome.nodes[nodeIndex].inputs[nodeInputIndex];

                        chromosome.nodes[nodeIndex].inputs[nodeInputIndex] = getRandomNodeInput(chromosome.numInputs, chromosome.numNodes, nodeIndex, params.recurrentConnectionProbability);

                        newGeneValue = chromosome.nodes[nodeIndex].inputs[nodeInputIndex];

                        if ((previousGeneValue != newGeneValue) && (chromosome.nodes[nodeIndex].active)) {
                            mutatedActive = 1;
                        }
                    }

                    /* mutate outputNodes gene */
                    else {
                        nodeIndex = geneToMutate - numFunctionGenes - numInputGenes;

                        previousGeneValue = chromosome.outputNodes[nodeIndex];

                        chromosome.outputNodes[nodeIndex] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);

                        newGeneValue = chromosome.outputNodes[nodeIndex];

                        if (previousGeneValue != newGeneValue) {
                            mutatedActive = 1;
                        }
                    }
                }
            }
        },
        probabilisticMutation {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                int i, j;

                /* for every nodes in the chromosome */
                for (i = 0; i < params.numNodes; i++) {

                    /* mutate the function gene */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.nodes[i].function = getRandomFunction(chromosome.funcSet.numFunctions);
                    }

                    /* for every input to each chromosome */
                    for (j = 0; j < params.arity; j++) {

                        /* mutate the node input */
                        if (randEngine.uniform() <= params.mutationRate) {
                            chromosome.nodes[i].inputs[j] = getRandomNodeInput(chromosome.numInputs, chromosome.numNodes, i, params.recurrentConnectionProbability);
                        }

                        /* mutate the node connection weight */
                        if (randEngine.uniform() <= params.mutationRate) {
                            chromosome.nodes[i].weights[j] = getRandomConnectionWeight(params.connectionWeightRange);
                        }
                    }
                }

                /* for every chromosome outputNodes */
                for (i = 0; i < params.numOutputs; i++) {

                    /* mutate the chromosome outputNodes */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.outputNodes[i] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);
                    }
                }
            }
        },
        probabilisticMutationOnlyActive {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                int i, j;
                int activeNode;

                /* for every active node in the chromosome */
                for (i = 0; i < chromosome.numActiveNodes; i++) {

                    activeNode = chromosome.activeNodes[i];

                    /* mutate the function gene */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.nodes[activeNode].function = getRandomFunction(chromosome.funcSet.numFunctions);
                    }

                    /* for every input to each chromosome */
                    for (j = 0; j < params.arity; j++) {

                        /* mutate the node input */
                        if (randEngine.uniform() <= params.mutationRate) {
                            chromosome.nodes[activeNode].inputs[j] = getRandomNodeInput(chromosome.numInputs, chromosome.numNodes, activeNode, params.recurrentConnectionProbability);
                        }

                        /* mutate the node connection weight */
                        if (randEngine.uniform() <= params.mutationRate) {
                            chromosome.nodes[activeNode].weights[j] = getRandomConnectionWeight(params.connectionWeightRange);
                        }
                    }
                }

                /* for every chromosome outputNodes */
                for (i = 0; i < params.numOutputs; i++) {

                    /* mutate the chromosome outputNodes */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.outputNodes[i] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);
                    }
                }
            }
        }
    }

    /**
     * mutate Random parent reproduction method.
     */
    public enum reproduction implements CGPReproductionStrategy {
        mutateRandomParent {
            @Override
            public void reproduce(CartesianGP params, CGPChromosome[] parents, CGPChromosome[] children, int numParents, int numChildren) {
                int i;

                /* for each child */
                for (i = 0; i < numChildren; i++) {

                    /* set child as clone of random parent */
                    copyChromosome(children[i], parents[randEngine.nextInt(numParents)]);

                    /* mutate newly cloned child */
                    mutateChromosome(params, children[i]);
                }
            }
        }
    }

    public enum selection implements CGPSelectionStrategy {
        selectFittest {
            @Override
            public void select(CartesianGP params, CGPChromosome[] parents, CGPChromosome[] candidateChromos, int numParents, int numCandidateChromos) {
                int i;

                sortChromosomeArray(candidateChromos, numCandidateChromos);

                for (i = 0; i < numParents; i++) {
                    copyChromosome(parents[i], candidateChromos[i]);
                }
            }
        }
    }

    enum operations implements Function {
        _add {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;
                double sum = inputs[0];

                for (i = 1; i < numInputs; i++) {
                    sum += inputs[i];
                }

                return sum;
            }
        },
        _sub {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;
                double sum = inputs[0];

                for (i = 1; i < numInputs; i++) {
                    sum -= inputs[i];
                }

                return sum;
            }
        },
        _mul {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;
                double multiplication = inputs[0];

                for (i = 1; i < numInputs; i++) {
                    multiplication *= inputs[i];
                }

                return multiplication;
            }
        },
        _divide {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;
                double divide = inputs[0];

                for (i = 1; i < numInputs; i++) {
                    divide /= inputs[i];
                }

                return divide;
            }
        },
        _absolute {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return abs(inputs[0]);
            }
        },
        _squareRoot {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return sqrt(inputs[0]);
            }
        },
        _square {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return pow(inputs[0], 2);
            }
        },
        _cube {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return pow(inputs[0], 3);
            }
        },
        _power {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return pow(inputs[0], inputs[1]);
            }
        },
        _exponential {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return exp(inputs[0]);
            }
        },
        _sine {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return sin(inputs[0]);
            }
        },
        _cosine {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return Math.cos(inputs[0]);
            }
        },
        _tangent {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return Math.tan(inputs[0]);
            }
        },
        _One {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return 1;
            }
        },
        _Zero {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return 0;
            }
        },
        _PI {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return Math.PI;
            }
        },
        _randFloat {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                return randEngine.uniform();
            }
        },
        _and {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;

                for (i = 0; i < numInputs; i++) {

                    if (inputs[i] == 0) {
                        return 0;
                    }
                }

                return 1;
            }
        },
        _nand {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;

                for (i = 0; i < numInputs; i++) {

                    if (inputs[i] == 0) {
                        return 1;
                    }
                }

                return 0;
            }
        },
        _or {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;

                for (i = 0; i < numInputs; i++) {

                    if (inputs[i] == 1) {
                        return 1;
                    }
                }

                return 0;
            }
        },
        _nor {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;

                for (i = 0; i < numInputs; i++) {

                    if (inputs[i] == 1) {
                        return 0;
                    }
                }

                return 1;
            }
        },
        _xor {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {

                int i;
                int numOnes = 0;
                int out;

                for (i = 0; i < numInputs; i++) {

                    if (inputs[i] == 1) {
                        numOnes++;
                    }

                    if (numOnes > 1) {
                        break;
                    }
                }

                if (numOnes == 1) {
                    out = 1;
                } else {
                    out = 0;
                }

                return out;
            }
        },
        _xnor {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                int i;
                int numOnes = 0;
                int out;

                for (i = 0; i < numInputs; i++) {

                    if (inputs[i] == 1) {
                        numOnes++;
                    }

                    if (numOnes > 1) {
                        break;
                    }
                }

                if (numOnes == 1) {
                    out = 0;
                } else {
                    out = 1;
                }

                return out;
            }
        },
        _not {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {

                double out;

                if (inputs[0] == 0) {
                    out = 1;
                } else {
                    out = 0;
                }

                return out;
            }
        },
        _wire {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                double out;

                out = inputs[0];

                return out;
            }
        },
        _sigmoid {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                double weightedInputSum;
                double out;

                weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);

                out = 1 / (1 + exp(-weightedInputSum));

                return out;
            }
        },
        _gaussian {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                double weightedInputSum;
                double out;

                int centre = 0;
                int width = 1;

                weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);

                out = exp(-(Math.pow(weightedInputSum - centre, 2)) / (2 * Math.pow(width, 2)));

                return out;
            }
        },
        _step {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
                double weightedInputSum;
                double out;

                weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);

                if (weightedInputSum < 0) {
                    out = 0;
                } else {
                    out = 1;
                }

                return out;
            }
        },
        _softsign {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {

                double weightedInputSum;
                double out;

                weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);

                out = weightedInputSum / (1 + Math.abs(weightedInputSum));

                return out;
            }
        },
        _hyperbolicTangent {
            @Override
            public double calc(int numInputs, double[] inputs, double[] connectionWeights) {

                double weightedInputSum;
                double out;

                weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);

                out = Math.tanh(weightedInputSum);

                return out;
            }
        }
    }

    public enum fitnessCalc implements CGPFitness {
        supervisedLearning {
            @Override
            public double calc(CartesianGP params, CGPChromosome chromosome, DataSet data) {

                int i, j;
                double error = 0;

                /* error checking */
                if (chromosome.getNumInputs() != data.numInputs) {
                    System.out.print("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
                    System.out.print("Terminating CGP-Library.\n");
                    System.exit(0);
                }

                if (chromosome.getNumOutputs() != data.numOutputs) {
                    System.out.print("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
                    System.out.print("Terminating CGP-Library.\n");
                    System.exit(0);
                }

                /* for each sample in data */
                for (i = 0; i < data.numSamples; i++) {

                    /* calculate the chromosome outputs for the set of inputs  */
                    executeChromosome(chromosome, data.getDataSetSampleInputs(i));

                    /* for each chromosome outputNodes */
                    for (j = 0; j < chromosome.getNumOutputs(); j++) {

                        error += Math.abs(getChromosomeOutput(chromosome, j) - data.getDataSetSampleOutput(i, j));
                    }
                }

                return error;
            }
        }
    }
}
