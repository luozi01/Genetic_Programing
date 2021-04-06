package cgp.solver;

import cgp.emum.CGPMutationStrategy;
import cgp.fitness.SupervisedLearning;
import cgp.gp.CGPChromosome;
import cgp.gp.CGPEvolve;
import cgp.gp.CGPParams;
import cgp.initialization.CGPInitializer;
import cgp.interfaces.CGPFunction;
import cgp.interfaces.CGPReproduction;
import cgp.interfaces.CGPSelection;
import cgp.program.DataSet;
import cgp.program.Results;
import cgp.reproduction.MutateRandomParentReproduction;
import cgp.selection.FittestSelection;
import genetics.interfaces.FitnessCalc;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log4j2
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
                new CGPInitializer(this.params),
                new FittestSelection(),
                new MutateRandomParentReproduction(this.params),
                new SupervisedLearning(this.params),
                this.params
        );
    }

    public void setData(@NonNull DataSet data) {
        if (params.getNumInputs() != data.getNumInputs()) {
            throw new IllegalArgumentException(String.format("The number of inputs specified in the dataSet (%d) does not match the number of inputs specified in the parameters (%d).", data.getNumInputs(), params.getNumInputs()));
        }

        if (params.getNumOutputs() != data.getNumOutputs()) {
            throw new IllegalArgumentException(String.format("The number of outputs specified in the dataSet (%d) does not match the number of outputs specified in the parameters (%d).", data.getNumOutputs(), params.getNumOutputs()));
        }
        this.params.setData(Optional.of(data));
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
        if (mu <= 0) {
            throw new IllegalArgumentException(String.format("Mu value '%d' is invalid. Mu value must have a value of one or greater. " +
                    "Mu value left unchanged as '%d'.\n", mu, params.getMu()));
        }
        params.setMu(mu);
    }

    /**
     * Sets the lambda value in given parameters to the new given value.
     * If lambda value is invalid a warning is displayed and the lambda value
     * is left unchanged.
     */
    public void setLambda(int lambda) {
        if (lambda <= 0) {
            throw new IllegalArgumentException(String.format("Lambda value '%d' is invalid. Lambda value must have a value of one or greater. " +
                    "Lambda value left unchanged as '%d'.\n", lambda, params.getLambda()));
        }
        params.setLambda(lambda);
    }

    /**
     * Sets the evolutionary strategy given in parameters to '+' or ','.
     * If an invalid option is given a warning is displayed and the evolutionary
     * strategy is left unchanged.
     */
    public void setEvolutionaryStrategy(char evolutionaryStrategy) { // Todo strategy to enum
        if (evolutionaryStrategy != '+' && evolutionaryStrategy != ',') {
            throw new IllegalArgumentException(String.format("\nWarning: the evolutionary strategy '%c' is invalid. " +
                            "The evolutionary strategy must be '+' or ','. " +
                            "The evolutionary strategy has been left unchanged as '%c'.\n",
                    evolutionaryStrategy, params.getEvolutionaryStrategy()));
        }
        params.setEvolutionaryStrategy(evolutionaryStrategy);
    }

    /**
     * Sets the mutation rate given in parameters. If an invalid mutation
     * rate is given a warning is displayed and the mutation rate is left
     * unchanged.
     */
    public void setMutationRate(double mutationRate) {
        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException(String.format("\nWarning: mutation rate '%f' is invalid. " +
                    "The mutation rate must be in the range [0,1]. " +
                    "The mutation rate has been left unchanged as '%f'.\n", mutationRate, params.getMutationRate()));
        }
        params.setMutationRate(mutationRate);
    }

    /**
     * Sets the recurrent connection probability given in parameters. If an invalid
     * value is given a warning is displayed and the value is left	unchanged.
     */
    public void setRecurrentConnectionProbability(double recurrentConnectionProbability) {
        if (recurrentConnectionProbability < 0 || recurrentConnectionProbability > 1) {
            throw new IllegalArgumentException(String.format("\nWarning: recurrent connection probability '%f' is invalid. " +
                            "The recurrent connection probability must be in the range [0,1]. " +
                            "The recurrent connection probability has been left unchanged as '%f'.\n",
                    recurrentConnectionProbability, params.getRecurrentConnectionProbability()));
        }
        params.setRecurrentConnectionProbability(recurrentConnectionProbability);
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
            throw new IllegalArgumentException(String.format("Warning: update frequency of %d is invalid. Update frequency must be >= 0. " +
                    "Update frequency is left unchanged as %d.\n", updateFrequency, params.getUpdateFrequency()));
        }
        params.setUpdateFrequency(updateFrequency);
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
    public void setCustomFitnessFunction(@NonNull FitnessCalc<CGPChromosome> fitnessFunction) {
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

    public void initialiseDataSet(@NonNull double[][] input, @NonNull double[][] output, int numSamples, int numInput, int numOutput) {
        DataSet data = new DataSet(numSamples, numInput, numOutput);

        for (int i = 0; i < input.length; i++) {
            data.setInputData(i, input[i]);
            data.setOutputData(i, output[i]);
        }
        this.params.setData(Optional.of(data));
    }

    public void evolve(int iteration, CGPChromosome... chromosomes) throws ExecutionException, InterruptedException {
        if (chromosomes.length > 0) {
            this.model.injectPrevPopulation(chromosomes);
        }
        this.model.evolve(iteration);
    }

    public void repeatEvolve(int numGens, int numRuns, CGPChromosome... chromosomes) throws ExecutionException, InterruptedException {
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
        return results.map(o -> o.getBestCGPChromosomes().get(index)).orElse(null);
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
            log.error("Warning: No Functions added to function set.");
        }
        this.model.initialize();
    }

    public void addSelfDefineFunction(CGPFunction function) {
        this.params.addCustomNodeFunction(function);
        this.model.initialize();
    }

    public void printParams() {
        log.info('\n' + params.toString());
    }
}
