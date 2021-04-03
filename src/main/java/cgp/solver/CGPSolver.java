package cgp.solver;

import cgp.emum.CGPMutationStrategy;
import cgp.fitness.SupervisedLearning;
import cgp.gp.CGPChromosome;
import cgp.gp.CGPEvolve;
import cgp.gp.CGPParams;
import cgp.initialization.CGPInitializer;
import cgp.interfaces.CGPFitness;
import cgp.interfaces.CGPFunction;
import cgp.interfaces.CGPReproduction;
import cgp.interfaces.CGPSelection;
import cgp.program.DataSet;
import cgp.program.Results;
import cgp.reproduction.MutateRandomParentReproduction;
import cgp.selection.FittestSelection;
import genetics.common.Population;
import lombok.NonNull;

import java.util.Optional;


public class CGPSolver {
    private final CGPParams params;
    private final CGPEvolve model;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Results> results = Optional.empty();

    public CGPSolver(final int numInputs,
                     final int numNodes,
                     final int numOutputs,
                     final int nodeArity) {
        this.params = CGPParams.initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

        this.model = new CGPEvolve(
                new CGPInitializer(),
                null,
                this.params.getMutationPolicy(),
                new FittestSelection(),
                new MutateRandomParentReproduction(),
                new SupervisedLearning(),
                this.params
        );
    }

    public void setData(Optional<DataSet> date) {
        this.params.setData(date);
    }

    /**
     * Sets the connection weight range given in parameters.
     */
    public void setConnectionWeightRange(double weightRange) {
        params.setConnectionWeightRange(weightRange);
    }

    /**
     * Sets the mu value in given parameters to the new given value. If mu value
     * is invalid a warning is displayed and the mu value is left unchanged.
     */
    public void setMu(int mu) {
        if (mu > 0) {
            params.setMu(mu);
        } else {
            System.err.printf("Mu value '%d' is invalid. Mu value must have a value of one or greater. " +
                    "Mu value left unchanged as '%d'.\n", mu, params.getMu());
        }
    }

    /**
     * Sets the lambda value in given parameters to the new given value.
     * If lambda value is invalid a warning is displayed and the lambda value
     * is left unchanged.
     */
    public void setLambda(int lambda) {
        if (lambda > 0) {
            params.setLambda(lambda);
        } else {
            System.err.printf("Lambda value '%d' is invalid. Lambda value must have a value of one or greater. " +
                    "Lambda value left unchanged as '%d'.\n", lambda, params.getLambda());
        }
    }

    /**
     * Sets the evolutionary strategy given in parameters to '+' or ','.
     * If an invalid option is given a warning is displayed and the evolutionary
     * strategy is left unchanged.
     */
    public void setEvolutionaryStrategy(char evolutionaryStrategy) {
        if (evolutionaryStrategy == '+' || evolutionaryStrategy == ',') {
            params.setEvolutionaryStrategy(evolutionaryStrategy);
        } else {
            System.err.printf("\nWarning: the evolutionary strategy '%c' is invalid. " +
                            "The evolutionary strategy must be '+' or ','. " +
                            "The evolutionary strategy has been left unchanged as '%c'.\n",
                    evolutionaryStrategy, params.getEvolutionaryStrategy());
        }
    }

    /**
     * Sets the mutation rate given in parameters. If an invalid mutation
     * rate is given a warning is displayed and the mutation rate is left
     * unchanged.
     */
    public void setMutationRate(double mutationRate) {
        if (mutationRate >= 0 && mutationRate <= 1) {
            params.setMutationRate(mutationRate);
        } else {
            System.err.printf("\nWarning: mutation rate '%f' is invalid. " +
                    "The mutation rate must be in the range [0,1]. " +
                    "The mutation rate has been left unchanged as '%f'.\n", mutationRate, params.getMutationRate());
        }
    }

    /**
     * Sets the recurrent connection probability given in parameters. If an invalid
     * value is given a warning is displayed and the value is left	unchanged.
     */
    public void setRecurrentConnectionProbability(double recurrentConnectionProbability) {
        if (recurrentConnectionProbability >= 0 && recurrentConnectionProbability <= 1) {
            params.setRecurrentConnectionProbability(recurrentConnectionProbability);
        } else {
            System.err.printf("\nWarning: recurrent connection probability '%f' is invalid. " +
                            "The recurrent connection probability must be in the range [0,1]. " +
                            "The recurrent connection probability has been left unchanged as '%f'.\n",
                    recurrentConnectionProbability, params.getRecurrentConnectionProbability());
        }
    }

    /**
     * Sets the whether shortcut connections are used. If an invalid
     * value is given a warning is displayed and the value is left	unchanged.
     */
    public void setShortcutConnections(boolean shortcutConnections) {
        params.setShortcutConnections(shortcutConnections);
    }

    /**
     * Sets the update frequency in generations
     */
    public void setUpdateFrequency(int updateFrequency) {
        if (updateFrequency < 0) {
            System.err.printf("Warning: update frequency of %d is invalid. Update frequency must be >= 0. " +
                    "Update frequency is left unchanged as %d.\n", updateFrequency, params.getUpdateFrequency());
        } else {
            params.setUpdateFrequency(updateFrequency);
        }
    }

    /**
     * clears the given function set of functions
     */
    public void clearFunctionSet() {
        params.getFunctions().clear();
    }

    /**
     * sets the fitness function to the fitnessFuction passed. If the fitnessFunction is NULL
     * then the default supervisedLearning fitness function is used.
     */
    public void setCustomFitnessFunction(@NonNull CGPFitness fitnessFunction) {
        this.model.setFitnessCalc(fitnessFunction);
    }

    /**
     * sets the selection scheme used to select the parents from the candidate chromosomes.
     * If the selectionScheme is NULL then the default selectFittest selection scheme is used.
     */
    public void setCustomSelectionScheme(@NonNull CGPSelection selectionScheme) {
        this.model.setSelectionPolicy(selectionScheme);
    }

    public void setCustomReproductionScheme(@NonNull CGPReproduction reproductionScheme) {
        this.model.setReproduction(reproductionScheme);
    }

    /**
     * Sets the target fitness
     */
    public void setTargetFitness(double targetFitness) {
        params.setTargetFitness(targetFitness);
    }

    /**
     * sets the mutation type in params
     */
    public void setMutationType(CGPMutationStrategy mutationType) {
        params.setMutation(mutationType);
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
        this.params.setData(Optional.of(data));
    }

    public void evolve(int iteration, CGPChromosome... chromosomes) {
        if (chromosomes.length > 0) {
            this.model.setPopulation(new Population<>(chromosomes));
        }
        this.model.evolve(iteration);
    }

    public void repeatEvolve(int numGens, int numRuns, CGPChromosome... chromosomes) {
        results = Optional.of(this.model.repeatEvolve(params, numGens, numRuns, chromosomes));
        results.ifPresent(o -> this.model.updateGlobal(o.getBestChromosome()));
    }

    /**
     * if node needs for continues training, then should not
     * remove inactive nodes
     *
     * @param concise if true, will remove inactive nodes
     * @return the best gene
     */
    public CGPChromosome getBestGene(boolean concise) {
        CGPChromosome best = this.model.getBestChromosome().orElse(null);
        if (concise && best != null) {
            best.removeInactiveNodes();
        }
        return best;
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
        if (params.getFunctions().isEmpty()) {
            System.err.print("Warning: No Functions added to function set.\n");
        }
    }

    public void addSelfDefineFunction(CGPFunction function) {
        params.addCustomNodeFunction(function);
    }

    public void printParams() {
        System.out.println(params);
    }
}
