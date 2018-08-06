package cgp.gp;

import cgp.interfaces.CGPFitness;
import cgp.interfaces.CGPMutationStrategy;
import cgp.interfaces.CGPReproductionStrategy;
import cgp.interfaces.CGPSelectionStrategy;
import cgp.program.DataSet;
import cgp.program.Node;
import cgp.program.Results;
import cgp.solver.CartesianGP;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Arrays;

import static java.lang.Math.round;

public class CGPCore {
    private final static RandEngine randEngine = new SimpleRandEngine();

    /**
     * Returns a pointer to an initialised chromosome with values obeying the given parameters.
     */
    static CGPChromosome initialiseChromosome(CartesianGP params) {
        CGPChromosome chromosome = new CGPChromosome();
        if (params.functions.isEmpty()) {
            throw new IllegalArgumentException("Chromosome not initialised due to empty functionSet.");
        }
        chromosome.nodes = new Node[params.numNodes];
        chromosome.outputNodes = new int[params.numOutputs];
        chromosome.activeNodes = new int[params.numNodes];
        chromosome.outputValues = new double[params.numOutputs];
        for (int i = 0; i < params.numNodes; i++) {
            chromosome.nodes[i] = initialiseNode(
                    params.numInputs,
                    params.numNodes,
                    params.arity,
                    params.functions.size(),
                    params.connectionWeightRange,
                    params.recurrentConnectionProbability, i);
        }
        for (int i = 0; i < params.numOutputs; i++) {
            chromosome.outputNodes[i] = getRandomChromosomeOutput(params.numInputs, params.numNodes, params.shortcutConnections);
        }
        chromosome.numInputs = params.numInputs;
        chromosome.numNodes = params.numNodes;
        chromosome.numOutputs = params.numOutputs;
        chromosome.arity = params.arity;
        chromosome.numActiveNodes = params.numNodes;
        chromosome.funcSet = params.functions.clone();
        setChromosomeActiveNodes(chromosome);
        chromosome.nodeInputsHold = new double[params.arity];
        return chromosome;
    }

    /**
     * Executes the given chromosome
     */
    public static void executeChromosome(CGPChromosome chromosome, double[] inputs) {
        int numInputs = chromosome.numInputs;
        int numActiveNodes = chromosome.numActiveNodes;
        int numOutputs = chromosome.numOutputs;

        /* for all of the active nodes */
        for (int i = 0; i < numActiveNodes; i++) {

            /* get the index of the current active node */
            int currentActiveNode = chromosome.activeNodes[i];

            /* get the arity of the current node */
            int nodeArity = chromosome.nodes[currentActiveNode].actArity;

            /* for each of the active nodes inputs */
            for (int j = 0; j < nodeArity; j++) {

                /* gather the nodes input locations */
                int nodeInputLocation = chromosome.nodes[currentActiveNode].inputs[j];

                if (nodeInputLocation < numInputs) {
                    chromosome.nodeInputsHold[j] = inputs[nodeInputLocation];
                } else {
                    chromosome.nodeInputsHold[j] = chromosome.nodes[nodeInputLocation - numInputs].output;
                }
            }

            /* get the functionality of the active node under evaluation */
            int currentActiveNodeFunction = chromosome.nodes[currentActiveNode].function;

            /* calculate the outputNodes of the active node under evaluation */
            chromosome.nodes[currentActiveNode].output =
                    chromosome.funcSet.get(currentActiveNodeFunction)
                            .calc(nodeArity, chromosome.nodeInputsHold, chromosome.nodes[currentActiveNode].weights);

            /* deal with doubles becoming NAN */
            if (Double.isNaN(chromosome.nodes[currentActiveNode].output)) {
                chromosome.nodes[currentActiveNode].output = 0;
            } else if (Double.isInfinite(chromosome.nodes[currentActiveNode].output)) {
                chromosome.nodes[currentActiveNode].output = chromosome.nodes[currentActiveNode].output > 0 ?
                        Double.MAX_VALUE : Double.MIN_VALUE;
            }
        }

        /* Set the chromosome outputs */
        for (int i = 0; i < numOutputs; i++) {
            if (chromosome.outputNodes[i] < numInputs) chromosome.outputValues[i] = inputs[chromosome.outputNodes[i]];
            else chromosome.outputValues[i] = chromosome.nodes[chromosome.outputNodes[i] - numInputs].output;
        }
    }

    /**
     * Mutates the given chromosome using the mutation method described in parameters
     */
    private static void mutateChromosome(CartesianGP params, CGPChromosome chromosome) {
        params.mutationType.mutate(params, chromosome);
        setChromosomeActiveNodes(chromosome);
    }

    /**
     * sets the fitness of the given chromosome
     */
    private static void setChromosomeFitness(CartesianGP params, CGPChromosome chromo, DataSet data) {
        setChromosomeActiveNodes(chromo);
        chromo.resetChromosome();
        chromo.fitness = params.fitnessFunction.calc(params, chromo, data);
    }

    /**
     * set the active nodes in the given chromosome
     */
    static void setChromosomeActiveNodes(CGPChromosome chromo) {
        /* error checking */
        if (chromo == null) {
            throw new NullPointerException("Chromosome has not been initialised and so the active nodes cannot be set.");
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

    /**
     * used by setActiveNodes to recursively search for active nodes
     */
    private static void recursivelySetActiveNodes(CGPChromosome chromosome, int nodeIndex) {
        /* if the given node is an input, stop */
        if (nodeIndex < chromosome.numInputs) {
            return;
        }

        /* if the given node has already been flagged as active */
        if (chromosome.nodes[nodeIndex - chromosome.numInputs].active) {
            return;
        }

        /* log the node as active */
        chromosome.nodes[nodeIndex - chromosome.numInputs].active = true;
        chromosome.activeNodes[chromosome.numActiveNodes] = nodeIndex - chromosome.numInputs;
        chromosome.numActiveNodes++;

        /* set the nodes actual arity*/
        chromosome.nodes[nodeIndex - chromosome.numInputs].actArity =
                chromosome.getChromosomeNodeArity(nodeIndex - chromosome.numInputs);

        /* recursively log all the nodes to which the current nodes connect as active */
        for (int i = 0; i < chromosome.nodes[nodeIndex - chromosome.numInputs].actArity; i++) {
            recursivelySetActiveNodes(chromosome, chromosome.nodes[nodeIndex - chromosome.numInputs].inputs[i]);
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

    /**
     * repetitively applies runCGP to obtain average behaviour
     */
    public static Results repeatCGP(CartesianGP params,
                                    DataSet data,
                                    int numGens,
                                    int numRuns,
                                    CGPChromosome... chromosomes) {
        int updateFrequency = params.updateFrequency;

        /* set the update frequency so as to to so generational results */
        params.updateFrequency = 0;

        Results results = new Results();

        System.out.print("Run\tFitness\t\tGenerations\tActive nodes\n");

        /* for each run */
        for (int i = 0; i < numRuns; i++) {
            results.add(runCGP(params, data, numGens, chromosomes));
            System.out.printf("%d\t%f\t%d\t\t%d\n", i,
                    results.bestCGPChromosomes.get(i).fitness,
                    results.bestCGPChromosomes.get(i).generation,
                    results.bestCGPChromosomes.get(i).numActiveNodes);
        }

        System.out.print("----------------------------------------------------\n");
        System.out.printf("MEAN\t%f\t%f\t%f\n", results.getAverageFitness(), results.getAverageGenerations(), results.getAverageActiveNodes());
        System.out.printf("MEDIAN\t%f\t%f\t%f\n", results.getMedianFitness(), results.getMedianGenerations(), results.getMedianActiveNodes());
        System.out.print("----------------------------------------------------\n\n");

        /* restore the original value for the update frequency */
        params.updateFrequency = updateFrequency;

        return results;
    }

    public static CGPChromosome runCGP(CartesianGP params, DataSet data, int numGens, CGPChromosome... chromosomes) {
        int gen;

        /* error checking */
        if (numGens < 0) {
            throw new IllegalArgumentException(String.format("%d generations is invalid. The number of generations must be >= 0.", numGens));
        }

        if (data != null && params.numInputs != data.numInputs) {
            throw new IllegalArgumentException(String.format("The number of inputs specified in the dataSet (%d) does not match the number of inputs specified in the parameters (%d).", data.numInputs, params.numInputs));
        }

        if (data != null && params.numOutputs != data.numOutputs) {
            throw new IllegalArgumentException(String.format("The number of outputs specified in the dataSet (%d) does not match the number of outputs specified in the parameters (%d).", data.numOutputs, params.numOutputs));
        }

        /* initialise parent chromosomes */
        CGPChromosome[] parents = new CGPChromosome[params.mu];

        /* initialise children chromosomes */
        CGPChromosome[] children = new CGPChromosome[params.lambda];

        initializePopulation(params, parents, children, params.mu, params.lambda, chromosomes);

        /* initialize best chromosome */
        CGPChromosome bestChromosome = initialiseChromosome(params);

        /* determine the size of the candidate chromosome based on the evolutionary Strategy */
        int numCandidate;
        if (params.evolutionaryStrategy == '+') {
            numCandidate = params.mu + params.lambda;
        } else if (params.evolutionaryStrategy == ',') {
            numCandidate = params.lambda;
        } else {
            throw new IllegalArgumentException(String.format("The evolutionary strategy '%c' is not known. ", params.evolutionaryStrategy));
        }

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

            getBestChromosome(parents, children, params.mu, params.lambda, bestChromosome);

            // check termination conditions
            if (bestChromosome.fitness <= params.targetFitness) {
                if (params.updateFrequency != 0) {
                    System.out.printf("%d\t%f - Solution Found\n", gen, bestChromosome.fitness);
                }
                break;
            }

            // display progress to the user at the update frequency specified
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
                        candidates[i].copyChromosome(children[i]);
                    } else {
                        candidates[i].copyChromosome(parents[i - params.lambda]);
                    }
                }
            } else if (params.evolutionaryStrategy == ',') {
                for (int i = 0; i < numCandidate; i++) {
                    candidates[i].copyChromosome(children[i]);
                }
            }

            /* select the parents from the candidateChromos */
            params.selectionScheme.select(params, parents, candidates, params.mu, numCandidate);

            /* create the children from the parents */
            params.reproductionScheme.reproduce(params, parents, children, params.mu, params.lambda);
        }

        /* deal with formatting for displaying progress */
        if (params.updateFrequency != 0) {
            System.out.println();
        }

        /* copy the best best chromosome */
        bestChromosome.generation = gen;
        /*copyChromosome(chromo, bestChromosome);*/

        return bestChromosome;
    }

    private static void initializePopulation(CartesianGP params,
                                             CGPChromosome[] parents,
                                             CGPChromosome[] children,
                                             int numParents,
                                             int numChildren,
                                             CGPChromosome... chromosomes) {
        MutableList<CGPChromosome> population = Lists.mutable.of(chromosomes);

        final int populationSize = population.size();
        for (int i = 0; i < numParents + numChildren - populationSize; i++) {
            population.add(initialiseChromosome(params));
        }

        for (int i = 0; i < numParents; i++) {
            parents[i] = population.get(i);
        }

        for (int i = 0; i < numChildren; i++) {
            children[i] = population.get(i + numParents);
        }
    }

    /**
     * returns a pointer to the fittest chromosome in the two arrays of chromosomes
     * <p>
     * loops through parents and then the children in order for the children to always be selected over the parents
     */
    private static void getBestChromosome(CGPChromosome[] parents,
                                          CGPChromosome[] children,
                                          int numParents,
                                          int numChildren,
                                          CGPChromosome best) {
        CGPChromosome bestChromoSoFar;
        bestChromoSoFar = parents[0];
        for (int i = 1; i < numParents; i++) {
            if (parents[i].fitness <= bestChromoSoFar.fitness) {
                bestChromoSoFar = parents[i];
            }
        }

        for (int i = 0; i < numChildren; i++) {
            if (children[i].fitness <= bestChromoSoFar.fitness) {
                bestChromoSoFar = children[i];
            }
        }

        best.copyChromosome(bestChromoSoFar);
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
            throw new IllegalArgumentException("Cannot assign the function gene a value as the Function Set is empty.");
        }
        return randEngine.nextInt(numFunctions);
    }

    /**
     * returns a random input for the given node
     */
    private static int getRandomNodeInput(int numInputs,
                                          int numNodes,
                                          int nodePosition,
                                          double recurrentConnectionProbability) {
        return randEngine.uniform() < recurrentConnectionProbability ?
                randEngine.nextInt(numNodes - nodePosition) + nodePosition + 1 : /* pick any ahead nodes or the node itself */
                randEngine.nextInt(numInputs + nodePosition);  /* pick any previous node including inputs */
    }

    /**
     * returns a random chromosome outputNodes
     */
    private static int getRandomChromosomeOutput(int numInputs, int numNodes, boolean shortcutConnections) {
        return shortcutConnections ?
                randEngine.nextInt(numInputs + numNodes) :
                randEngine.nextInt(numNodes) + numInputs;
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
                    chromosome.nodes[j].copyNode(chromosome.nodes[j + 1]);
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

    public enum MutationStrategy implements CGPMutationStrategy {
        point {
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
                        chromosome.nodes[nodeIndex].function = getRandomFunction(chromosome.funcSet.size());
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
        pointANN {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                int nodeIndex;
                int nodeInputIndex;

                /* get the number of each type of gene */
                int numFunctionGenes = params.numNodes;
                int numInputGenes = params.numNodes * params.arity;
                int numWeightGenes = params.numNodes * params.arity;
                int numOutputGenes = params.numOutputs;

                /* set the total number of chromosome genes */
                int numGenes = numFunctionGenes + numInputGenes + numWeightGenes + numOutputGenes;

                /* calculate the number of genes to mutate */
                int numGenesToMutate = (int) round(numGenes * params.mutationRate);

                /* for the number of genes to mutate */
                for (int i = 0; i < numGenesToMutate; i++) {

                    /* select a random gene */
                    int geneToMutate = randEngine.nextInt(numGenes);

                    /* mutate function gene */
                    if (geneToMutate < numFunctionGenes) {

                        nodeIndex = geneToMutate;

                        chromosome.nodes[nodeIndex].function = getRandomFunction(chromosome.funcSet.size());
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
        single {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                boolean mutatedActive = false;
                int previousGeneValue, newGeneValue, nodeIndex;

                /* get the number of each type of gene */
                int numFunctionGenes = params.numNodes;
                int numInputGenes = params.numNodes * params.arity;
                int numOutputGenes = params.numOutputs;

                /* set the total number of chromosome genes */
                int numGenes = numFunctionGenes + numInputGenes + numOutputGenes;

                /* while active gene not mutated */
                while (!mutatedActive) {

                    /* select a random gene */
                    int geneToMutate = randEngine.nextInt(numGenes);

                    /* mutate function gene */
                    if (geneToMutate < numFunctionGenes) {
                        nodeIndex = geneToMutate;
                        previousGeneValue = chromosome.nodes[nodeIndex].function;
                        chromosome.nodes[nodeIndex].function = getRandomFunction(chromosome.funcSet.size());
                        newGeneValue = chromosome.nodes[nodeIndex].function;
                        if ((previousGeneValue != newGeneValue) && (chromosome.nodes[nodeIndex].active)) {
                            mutatedActive = true;
                        }
                    }
                    /* mutate node input gene */
                    else if (geneToMutate < numFunctionGenes + numInputGenes) {
                        nodeIndex = (geneToMutate - numFunctionGenes) / chromosome.arity;
                        int nodeInputIndex = (geneToMutate - numFunctionGenes) % chromosome.arity;
                        previousGeneValue = chromosome.nodes[nodeIndex].inputs[nodeInputIndex];
                        chromosome.nodes[nodeIndex].inputs[nodeInputIndex] = getRandomNodeInput(chromosome.numInputs, chromosome.numNodes, nodeIndex, params.recurrentConnectionProbability);
                        newGeneValue = chromosome.nodes[nodeIndex].inputs[nodeInputIndex];
                        if ((previousGeneValue != newGeneValue) && (chromosome.nodes[nodeIndex].active)) {
                            mutatedActive = true;
                        }
                    }
                    /* mutate outputNodes gene */
                    else {
                        nodeIndex = geneToMutate - numFunctionGenes - numInputGenes;
                        previousGeneValue = chromosome.outputNodes[nodeIndex];
                        chromosome.outputNodes[nodeIndex] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);
                        newGeneValue = chromosome.outputNodes[nodeIndex];
                        if (previousGeneValue != newGeneValue) {
                            mutatedActive = true;
                        }
                    }
                }
            }
        },
        probabilistic {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                /* for every nodes in the chromosome */
                for (int i = 0; i < params.numNodes; i++) {

                    /* mutate the function gene */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.nodes[i].function = getRandomFunction(chromosome.funcSet.size());
                    }

                    /* for every input to each chromosome */
                    for (int j = 0; j < params.arity; j++) {

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
                for (int i = 0; i < params.numOutputs; i++) {

                    /* mutate the chromosome outputNodes */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.outputNodes[i] = getRandomChromosomeOutput(chromosome.numInputs, chromosome.numNodes, params.shortcutConnections);
                    }
                }
            }
        },
        probabilisticOnlyActive {
            @Override
            public void mutate(CartesianGP params, CGPChromosome chromosome) {
                /* for every active node in the chromosome */
                for (int i = 0; i < chromosome.numActiveNodes; i++) {

                    int activeNode = chromosome.activeNodes[i];

                    /* mutate the function gene */
                    if (randEngine.uniform() <= params.mutationRate) {
                        chromosome.nodes[activeNode].function = getRandomFunction(chromosome.funcSet.size());
                    }

                    /* for every input to each chromosome */
                    for (int j = 0; j < params.arity; j++) {

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
                for (int i = 0; i < params.numOutputs; i++) {

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
                /* for each child */
                for (int i = 0; i < numChildren; i++) {

                    /* set child as clone of random parent */
                    children[i].copyChromosome(parents[randEngine.nextInt(numParents)]);

                    /* mutate newly cloned child */
                    mutateChromosome(params, children[i]);
                }
            }
        }
    }

    public enum selection implements CGPSelectionStrategy {
        selectFittest {
            @Override
            public void select(CartesianGP params, CGPChromosome[] parents, CGPChromosome[] candidate, int numParents, int numCandidate) {
                sortChromosomeArray(candidate, numCandidate);
                for (int i = 0; i < numParents; i++) {
                    parents[i].copyChromosome(candidate[i]);
                }
            }
        }
    }

    public enum fitnessCalc implements CGPFitness {
        supervisedLearning {
            @Override
            public double calc(CartesianGP params, CGPChromosome chromosome, DataSet data) {
                double error = 0;
                if (chromosome.getNumInputs() != data.numInputs) {
                    throw new IllegalArgumentException("The number of chromosome inputs must match the number of inputs specified in the dataSet.");
                }
                if (chromosome.getNumOutputs() != data.numOutputs) {
                    throw new IllegalArgumentException("The number of chromosome outputs must match the number of outputs specified in the dataSet.");
                }
                for (int i = 0; i < data.numSamples; i++) {
                    executeChromosome(chromosome, data.getDataSetSampleInputs(i));
                    for (int j = 0; j < chromosome.getNumOutputs(); j++) {
                        error += Math.abs(chromosome.getChromosomeOutput(j) - data.getDataSetSampleOutput(i, j));
                    }
                }
                return error;
            }
        }
    }
}
