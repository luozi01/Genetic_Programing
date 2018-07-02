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
        calcFitness(isParallel);
    }

    protected GeneticAlgorithm(final Initialization initialization,
                               final FitnessCalc fitnessCalc) {
        this.population = new Population(initialization);
        this.fitnessCalc = fitnessCalc;
        this.populationSize = population.size();
        calcFitness(isParallel);
    }

    public void evolve(int iteration) {
        terminate = false;
        for (int i = 0; i < iteration; i++) {
            if (terminate) {
                break;
            }
            population = evolvePopulation();
            calcFitness(isParallel);
            generation = i;
            for (TerminationCheck l : terminationChecks) {
                l.update(this);
            }
        }
    }

    public void evolve() {
        terminate = false;
        generation = 0;
        while (!terminate) {
            System.out.println(population.size());
            population = evolvePopulation();
            System.out.println(population.size());
            calcFitness(isParallel);
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


    private void calcFitness(boolean isParallel) {
        if (isParallel)
            master_slave_evaluation(population);
        else
            sequential_evaluation(population);
    }


    private void master_slave_evaluation(Population population) {
        final int numberOfNodes = Runtime.getRuntime().availableProcessors();
        final int split_length = populationSize / numberOfNodes;
        List<List<Chromosome>> split_population = chopped(population.getChromosomes(), split_length);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfNodes);
        ParallelFitnessCalc[] workers = new ParallelFitnessCalc[split_population.size()];
        int i = 0;
        for (List<Chromosome> pop : split_population) {
            workers[i] = new ParallelFitnessCalc(pop);
            executor.submit(workers[i++]);
        }
        executor.shutdown();
        //noinspection StatementWithEmptyBody
        while (!executor.isTerminated()) ;
        Arrays.stream(workers).
                max(Comparator.comparingDouble(o -> o.best.fitness)).
                ifPresent(parallelFitnessCalc -> bestChromosome = Optional.of(parallelFitnessCalc.best));
        System.out.println("finish");
    }

    private void sequential_evaluation(Population population) {
        double bestFitness = Double.MAX_VALUE;
        for (Chromosome chromosome : population) {
            if (Double.isNaN(chromosome.fitness))
                chromosome.fitness = fitnessCalc.calc(chromosome);
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

    private <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(list.subList(i, Math.min(N, i + L)));
        }
        return parts;
    }

    protected class ParallelFitnessCalc implements Runnable {

        private List<Chromosome> subPopulation;
        private Chromosome best;

        ParallelFitnessCalc(List<Chromosome> subPopulation) {
            this.subPopulation = subPopulation;
        }

        @Override
        public void run() {
            double bestFitness = Double.MAX_VALUE;
            for (Chromosome chromosome : subPopulation) {
                if (Double.isNaN(chromosome.fitness))
                    chromosome.fitness = fitnessCalc.calc(chromosome);
                if (chromosome.fitness < bestFitness) {
                    bestFitness = chromosome.fitness;
                    best = chromosome;
                }
            }
        }
    }
}