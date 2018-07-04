package genetics.driver;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.executor.ExecutionType;
import genetics.executor.Executor;
import genetics.executor.SequentialExecutor;
import genetics.executor.global.GlobalDistributionExecutor;
import genetics.interfaces.*;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GeneticAlgorithm {

    protected final FitnessCalc fitnessCalc;
    private final RandEngine randEngine = new SimpleRandEngine();
    private final List<TerminationCheck> terminationChecks = new LinkedList<>();
    private final int populationSize;
    @Getter
    @Setter
    protected Population population;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Chromosome> bestChromosome = Optional.empty();
    private CrossoverPolicy crossoverPolicy;
    private MutationPolicy mutationPolicy;
    private SelectionPolicy selectionPolicy;
    private double uniformRate;
    private double mutationRate;
    private int tournamentSize;
    private int elitism;
    private boolean terminate;
    @Getter
    private int generation;
    private boolean alwaysEval = false;
    private ExecutionType executionType = ExecutionType.SEQUENTIAL;
    private Executor executor;

    public GeneticAlgorithm(final Initialization initialization,
                            final FitnessCalc fitnessCalc,
                            final CrossoverPolicy crossoverPolicy,
                            final double uniformRate,
                            final MutationPolicy mutationPolicy,
                            final double mutationRate,
                            final SelectionPolicy selectionPolicy,
                            final int tournamentSize,
                            final int elitism) throws OutOfRangeException {
        if (uniformRate < 0 || uniformRate > 1) {
            throw new OutOfRangeException(LocalizedFormats.CROSSOVER_RATE, uniformRate, 0, 1);
        }
        if (mutationRate < 0 || mutationRate > 1) {
            throw new OutOfRangeException(LocalizedFormats.MUTATION_RATE, mutationRate, 0, 1);
        }
        this.population = new Population(initialization);
        this.fitnessCalc = fitnessCalc;
        this.crossoverPolicy = crossoverPolicy;
        this.uniformRate = uniformRate;
        this.mutationPolicy = mutationPolicy;
        this.mutationRate = mutationRate;
        this.selectionPolicy = selectionPolicy;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        this.populationSize = population.size();
    }

    protected GeneticAlgorithm(final Initialization initialization,
                               final FitnessCalc fitnessCalc) {
        this.population = new Population(initialization);
        this.fitnessCalc = fitnessCalc;
        this.populationSize = population.size();
    }

    public void evolve(int iteration) {
        initExecutor();
        terminate = false;
        bestChromosome = Optional.of(executor.evaluate(population));
        for (int i = 0; i < iteration; i++) {
            if (terminate) {
                break;
            }
            population = evolvePopulation();
            bestChromosome = Optional.of(executor.evaluate(population));
            population.trim(populationSize);
            generation = i;
            for (TerminationCheck l : terminationChecks) {
                l.update(this);
            }
        }
        if (executor instanceof GlobalDistributionExecutor)
            ((GlobalDistributionExecutor) executor).killAll();
    }

    public void evolve() {
        initExecutor();
        terminate = false;
        generation = 0;
        bestChromosome = Optional.of(executor.evaluate(population));
        while (!terminate) {
            population = evolvePopulation();
            bestChromosome = Optional.of(executor.evaluate(population));
            population.trim(populationSize);
            generation++;
            for (TerminationCheck l : terminationChecks) {
                l.update(this);
            }
        }
        if (executor instanceof GlobalDistributionExecutor)
            ((GlobalDistributionExecutor) executor).killAll();
    }

    protected Population evolvePopulation() {
        Population nextGeneration = new Population();

        // Keep our best individual, reproduction
        for (int i = 0; (i < populationSize) && (i < elitism); i++) {
            nextGeneration.addChromosome(population.getChromosome(i));
        }
        Pair<Chromosome, Chromosome> pair;
        while (nextGeneration.size() < populationSize) {
            pair = selectionPolicy.select(population, tournamentSize, randEngine);
            if (randEngine.uniform() < uniformRate) {
                pair = crossoverPolicy.crossover(pair.getOne(), pair.getTwo());
            }

            if (randEngine.uniform() < mutationRate) {
                pair = Tuples.pair(
                        mutationPolicy.mutate(pair.getOne()),
                        mutationPolicy.mutate(pair.getTwo()));
            }

            nextGeneration.addChromosome(pair.getOne());
            if (nextGeneration.size() < populationSize) {
                nextGeneration.addChromosome(pair.getTwo());
            }
        }
        return nextGeneration;
    }

    //Todo implement island and diffusion model
    private void initExecutor() {
        switch (executionType) {
            case SEQUENTIAL:
                executor = new SequentialExecutor(fitnessCalc);
                break;
            case GLOBAL_MODEL:
                final int MAX_THREAD = Runtime.getRuntime().availableProcessors();
                BlockingQueue<Optional<Chromosome>> tasks = new LinkedBlockingQueue<>();
                BlockingQueue<Optional<Chromosome>> results = new LinkedBlockingQueue<>();
                executor = new GlobalDistributionExecutor(MAX_THREAD, alwaysEval, tasks, results, fitnessCalc);
                break;
            case ISLAND_MODEL:
                break;
            case DIFFUSION_MODEL:
                break;
        }
    }

    public void addIterationListener(TerminationCheck listener) {
        terminationChecks.add(listener);
    }

    public void terminate() {
        terminate = true;
    }

    public Chromosome getBest() {
        return bestChromosome.orElse(null);
    }

    public void runInGlobal() {
        executionType = ExecutionType.GLOBAL_MODEL;
    }
}