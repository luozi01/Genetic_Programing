package genetics.driver;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.executor.ExecutionType;
import genetics.executor.Executor;
import genetics.executor.GlobalDistributionExecutor;
import genetics.executor.SequentialExecutor;
import genetics.interfaces.CrossoverPolicy;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initialization;
import genetics.interfaces.MutationPolicy;
import genetics.interfaces.SelectionPolicy;
import genetics.interfaces.TerminationCheck;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GeneticAlgorithm<T extends Chromosome> {
    protected final FitnessCalc<T> fitnessCalc;
    protected final CrossoverPolicy<T> crossoverPolicy;
    private final int populationSize;
    protected final MutationPolicy<T> mutationPolicy;
    protected final SelectionPolicy<T> selectionPolicy;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected Optional<Chromosome> bestChromosome = Optional.empty();
    private final List<TerminationCheck<T>> terminationChecks = Lists.mutable.empty();
    private final Random random = new Random(System.currentTimeMillis());
    @Getter
    @Setter
    protected Population<T> population;
    @Getter
    private int generation;
    private Executor<T> executor;
    private ExecutionType executionType = ExecutionType.SEQUENTIAL;
    private double uniformRate;
    private double mutationRate;
    private int tournamentSize;
    private int elitism;
    private boolean terminate;

    /**
     * Constructor
     *
     * @param initialization  initialization function object
     * @param fitnessCalc     fitness function object
     * @param crossoverPolicy crossover function object
     * @param uniformRate     uniform rate for crossover if needed, [0, 1]
     * @param mutationPolicy  mutation function object
     * @param mutationRate    mutation rate for mutation function, [0, 1]
     * @param selectionPolicy select function
     * @param tournamentSize  tournament size for select function depends on select function
     * @param elitism         number of elites to keep during evolve
     */
    public GeneticAlgorithm(final Initialization<T> initialization,
                            final FitnessCalc<T> fitnessCalc,
                            final CrossoverPolicy<T> crossoverPolicy,
                            final double uniformRate,
                            final MutationPolicy<T> mutationPolicy,
                            final double mutationRate,
                            final SelectionPolicy<T> selectionPolicy,
                            final int tournamentSize,
                            final int elitism) {
        if (uniformRate < 0 || uniformRate > 1) {
            throw new IllegalArgumentException(String.format("Uniform rate should be [0, 1] but %f found", uniformRate));
        }
        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException(String.format("Mutation rate should be [0, 1] but %f found", mutationRate));
        }
        // population
        this.population = new Population<>(initialization);
        this.populationSize = population.size();
        // crossover
        this.crossoverPolicy = crossoverPolicy;
        this.uniformRate = uniformRate;
        // mutation
        this.mutationPolicy = mutationPolicy;
        this.mutationRate = mutationRate;
        // selection
        this.selectionPolicy = selectionPolicy;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        // evaluation
        this.fitnessCalc = fitnessCalc;
    }

    protected GeneticAlgorithm(final Initialization<T> initialization,
                               final CrossoverPolicy<T> crossoverPolicy,
                               final MutationPolicy<T> mutationPolicy,
                               final SelectionPolicy<T> selectionPolicy,
                               final FitnessCalc<T> fitnessCalc) {
        this.population = new Population<>(initialization);
        this.populationSize = this.population.size();
        this.crossoverPolicy = crossoverPolicy;
        this.mutationPolicy = mutationPolicy;
        this.selectionPolicy = selectionPolicy;
        this.fitnessCalc = fitnessCalc;
    }

    /**
     * @param iteration fix iteration evolution
     */
    public void evolve(int iteration) {
        // reset variable
        initExecutor();
        terminate = false;

        // initial evaluation
        bestChromosome = Optional.of(executor.evaluate(population));

        // evolve for given iterations
        for (generation = 0; generation < iteration; generation++) {
            if (terminate) {
                break;
            }
            population = evolvePopulation();
            terminationChecks.forEach(o -> o.update(this));
        }
        executor.shutDown();
    }

    /**
     * evolve until termination conditions fulfill
     */
    public void evolve() {
        // reset variable
        initExecutor();
        terminate = false;
        generation = 0;

        // initial evaluation
        bestChromosome = Optional.of(executor.evaluate(population));
        while (!terminate) {
            population = evolvePopulation();
            generation++;
            terminationChecks.forEach(o -> o.update(this));
        }
        executor.shutDown();
    }

    /**
     * @return next evolved population
     */
    protected Population<T> evolvePopulation() {
        // Keep our best individual, reproduction
        Population<T> nextGeneration = population.nextGeneration(Math.min(populationSize, elitism));
        // generate new generation
        while (nextGeneration.size() < populationSize) {
            // tournament selection
            Pair<T, T> pair = selectionPolicy.select(population, tournamentSize);
            // crossover
            if (random.nextDouble() < uniformRate) {
                pair = crossoverPolicy.crossover(pair.getOne(), pair.getTwo());
            }
            // mutation
            if (random.nextDouble() < mutationRate) {
                pair = Tuples.pair(
                        mutationPolicy.mutate(pair.getOne()),
                        mutationPolicy.mutate(pair.getTwo()));
            }

            // add to new generation
            nextGeneration.addChromosome(pair.getOne());
            if (nextGeneration.size() < populationSize) {
                nextGeneration.addChromosome(pair.getTwo());
            }
        }
        // evaluate new generation
        updateGlobal(executor.evaluate(nextGeneration));

        return nextGeneration;
    }

    //Todo implement island and diffusion model
    private void initExecutor() {
        switch (executionType) {
            case SEQUENTIAL:
                executor = new SequentialExecutor<>(fitnessCalc);
                break;
            case GLOBAL_MODEL:
                executor = new GlobalDistributionExecutor<>(fitnessCalc);
                break;
            case ISLAND_MODEL:
            case DIFFUSION_MODEL:
                break;
        }
    }

    /**
     * @param chromosome global best chromosome
     */
    protected void updateGlobal(Chromosome chromosome) {
        if (!bestChromosome.isPresent() || chromosome.betterThan(bestChromosome.get()))
            bestChromosome = Optional.of(chromosome);
    }

    /**
     * @param listener termination condition check listener
     */
    public void addTerminateListener(TerminationCheck<T> listener) {
        terminationChecks.add(listener);
    }

    /**
     * terminate the program
     */
    public void terminate() {
        terminate = true;
    }

    /**
     * @return global best chromosome
     */
    public Chromosome getBest() {
        return bestChromosome.orElse(null);
    }

    /**
     * helping api to allow program run in parallel
     */
    public void runInGlobal() {
        executionType = ExecutionType.GLOBAL_MODEL;
    }
}