package genetics;

import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

import java.util.*;

public class GeneticAlgorithm {

    protected static final RandEngine randEngine = new SimpleRandEngine();
    protected final ChromosomesComparator comparator;
    private final CrossoverPolicy crossoverPolicy;
    //    private final SelectionPolicy selectionPolicy;
    private final MutationPolicy mutationPolicy;
    private final double uniformRate;
    private final double mutationRate;
    private final int tournamentSize;
    private final int elitism;
    private final List<Interrupt> interrupts = new LinkedList<>();
    protected Population pop;
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
//        this.selectionPolicy = selectionPolicy;
        comparator = new ChromosomesComparator();
        pop.sort(comparator);
    }

    public GeneticAlgorithm(Population pop) {
        this.pop = pop;
        this.crossoverPolicy = null;
        this.mutationPolicy = null;
        this.uniformRate = 0;
        this.mutationRate = 0;
        this.tournamentSize = 0;
        this.elitism = 0;
//        this.selectionPolicy = selectionPolicy;
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
            List<Chromosome> pair = Arrays.asList(c1, c2);
            if (randEngine.uniform() < uniformRate) {
                pair = crossoverPolicy.crossover(pair.get(0), pair.get(1));
            }

            if (randEngine.uniform() < mutationRate) {
                pair = Arrays.asList(
                        mutationPolicy.mutate(pair.get(0)), mutationPolicy.mutate(pair.get(1)));
            }
            pair.forEach(newPopulation::addChromosome);
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
            int rind = (int) (Math.random() * chromosomes.size());
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