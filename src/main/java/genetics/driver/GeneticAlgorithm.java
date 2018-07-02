package genetics.driver;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.*;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneticAlgorithm {

    protected final FitnessCalc fitnessCalc;
    private final RandEngine randEngine = new SimpleRandEngine();
    private final List<TerminationCheck> terminationChecks = new LinkedList<>();
    private final int populationSize;
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
    private int generation;
    private boolean isParallel = false;
    private boolean alwaysEval = false;

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
        terminate = false;
        calcFitness(population);
        for (int i = 0; i < iteration; i++) {
            if (terminate) {
                break;
            }
            population = evolvePopulation();
            calcFitness(population);
            generation = i;
            for (TerminationCheck l : terminationChecks) {
                l.update(this);
            }
        }
    }

    public void evolve() {
        terminate = false;
        generation = 0;
        calcFitness(population);
        while (!terminate) {
            population = evolvePopulation();
            calcFitness(population);
            generation++;
            for (TerminationCheck l : terminationChecks) {
                l.update(this);
            }
        }
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

    private void calcFitness(Population population) {
        if (isParallel)
            master_slave_evaluation(population);
        else
            sequential_evaluation(population);
    }

    private void master_slave_evaluation(Population population) {
        final int numberOfNodes = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfNodes);
        List<Chromosome> chromosomes = Collections.synchronizedList(population.getChromosomes());
        for (Chromosome chromosome : chromosomes) {
            executor.execute(new ParallelFitnessCalc(chromosome));
        }
        executor.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executor.isTerminated()) ;
        chromosomes.stream()
                .min(Comparator.comparingDouble(o -> o.fitness))
                .ifPresent(o -> bestChromosome = Optional.of(o));
    }

    private void sequential_evaluation(Population population) {
        double bestFitness = Double.MAX_VALUE;
        for (Chromosome chromosome : population) {
            if (alwaysEval || Double.isNaN(chromosome.fitness)) chromosome.fitness = fitnessCalc.calc(chromosome);
            if (chromosome.fitness < bestFitness) {
                bestFitness = chromosome.fitness;
                bestChromosome = Optional.of(chromosome);
            }
        }
    }

    public void addIterationListener(TerminationCheck listener) {
        terminationChecks.add(listener);
    }

    public void terminate() {
        terminate = true;
    }

    public int getGeneration() {
        return generation;
    }

    public Population getPopulation() {
        return population;
    }

    public Chromosome getBest() {
        return bestChromosome.orElse(null);
    }

    public void runInParallel() {
        isParallel = true;
    }

    public void evalAll() {
        alwaysEval = true;
    }

    private class ParallelFitnessCalc implements Runnable {

        private Chromosome chromosome;

        ParallelFitnessCalc(Chromosome chromosome) {
            this.chromosome = chromosome;
        }

        @Override
        public void run() {
            if (alwaysEval || Double.isNaN(chromosome.fitness)) chromosome.fitness = fitnessCalc.calc(chromosome);
        }
    }
}