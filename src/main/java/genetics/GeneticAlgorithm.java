package genetics;

import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.MutationPolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GeneticAlgorithm {

    protected final ChromosomesComparator comparator;
    private final RandEngine randEngine = new SimpleRandEngine();
    private final List<Interrupt> interrupts = new LinkedList<>();
    protected Population pop;
    private CrossoverPolicy crossoverPolicy;
    private MutationPolicy mutationPolicy;
    private double uniformRate;
    private double mutationRate;
    private int tournamentSize;
    private int elitism;
    private boolean terminate;
    private int generation;

    public GeneticAlgorithm(Population pop,
                            final CrossoverPolicy crossoverPolicy,
                            final double uniformRate,
                            final MutationPolicy mutationPolicy,
                            final double mutationRate,
                            final int tournamentSize,
                            final int elitism) {
        this.pop = pop;
        this.crossoverPolicy = crossoverPolicy;
        this.uniformRate = uniformRate;
        this.mutationPolicy = mutationPolicy;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
        this.elitism = elitism;
        comparator = new ChromosomesComparator();
        pop.sort(comparator);
    }

    protected GeneticAlgorithm(Population pop) {
        this.pop = pop;
        comparator = new ChromosomesComparator();
        pop.sort(comparator);
    }

    public void evolve(int iteration) {
        terminate = false;
        for (int i = 0; i < iteration; i++) {
            if (terminate) {
                break;
            }
            pop = evolvePopulation();
            generation = i;
            for (Interrupt l : interrupts) {
                l.update(this);
            }
        }
    }

    public void evolve() {
        terminate = false;
        generation = 0;
        while (!terminate) {
            pop = evolvePopulation();
            generation++;
            for (Interrupt l : interrupts) {
                l.update(this);
            }
        }
    }

    protected Population evolvePopulation() {
        int populationSize = pop.size();
        Population newPopulation = new Population();

        // Keep our best individual, reproduction
        pop.sort(comparator);
        for (int i = 0; (i < populationSize) && (i < elitism); i++) {
            newPopulation.addChromosome(pop.getChromosome(i));
        }

        for (int i = elitism; i < pop.size(); i++) {
            Chromosome c1 = tournamentSelection(tournamentSize);
            Chromosome c2 = tournamentSelection(tournamentSize);
            ChromosomePair pair = new ChromosomePair(c1, c2);
            if (randEngine.uniform() < uniformRate) {
                pair = crossoverPolicy.crossover(pair.getFirst(), pair.getSecond());
            }

            if (randEngine.uniform() < mutationRate) {
                pair = new ChromosomePair(
                        mutationPolicy.mutate(pair.getFirst()),
                        mutationPolicy.mutate(pair.getSecond()));
            }
            newPopulation.addChromosome(pair.getFirst());
            newPopulation.addChromosome(pair.getSecond());
        }
        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }

    public void addIterationListener(Interrupt listener) {
        interrupts.add(listener);
    }

    public void terminate() {
        terminate = true;
    }

    public int getGeneration() {
        return generation;
    }

    public Chromosome getBest() {
        return pop.getFirst();
    }

    /**
     * select chromosome from population
     *
     * @return best chromosome in the selecting group
     */
    protected Chromosome tournamentSelection(int tournamentSize) {
        assert tournamentSize < pop.size();
        List<Chromosome> selection = new ArrayList<>();
        List<Chromosome> chromosomes = new ArrayList<>(pop.getChromosomes());
        for (int i = 0; i < tournamentSize; i++) {
            int rind = randEngine.nextInt(chromosomes.size());
            selection.add(chromosomes.get(rind));
            chromosomes.remove(rind);
        }
        selection.sort(comparator);
        return selection.get(0);
    }

    protected class ChromosomesComparator implements Comparator<Chromosome> {
        @Override
        public int compare(Chromosome e1, Chromosome e2) {
            return e1.compareTo(e2);
        }
    }
}