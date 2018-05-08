package com.zluo.tgp.gp;

import com.zluo.ga.Chromosome;
import com.zluo.ga.Fitness;
import com.zluo.ga.GeneticAlgorithm;
import com.zluo.ga.Population;
import com.zluo.ga.utils.RandEngine;
import com.zluo.tgp.enums.TGPPopulationReplacementStrategy;
import com.zluo.tgp.solver.TreeGP;
import com.zluo.tgp.tools.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TGA<E extends Chromosome<E>> extends GeneticAlgorithm<E> {

    private TreeGP manager;

    public TGA(Population<E> pop, Fitness<E> fitness) {
        super(pop, fitness);
    }

    public void setManager(TreeGP manager) {
        this.manager = manager;
    }

    @Override
    protected Population<E> evolvePopulation() {
        Population<E> newPop = new Population<>();
        TGPPopulationReplacementStrategy populationReplacement = manager.replacementStrategy;
        if (populationReplacement == TGPPopulationReplacementStrategy.MU_PLUS_LAMBDA) {
            newPop = muPlusLambdaEvolve(manager.populationSize);
        } else if (populationReplacement == TGPPopulationReplacementStrategy.TINY_GP) {
            newPop = tinyGPEvolve(manager.populationSize);
        }
        return newPop;
    }

    private Population<E> muPlusLambdaEvolve(int populationSize) {
        Population<E> newPopulation = new Population<>();

        int elite_count = (int) (manager.elitismRatio * populationSize);

        pop.sort(comparator);
        for (int i = 0; (i < populationSize) && (i < elite_count); i++) {
            newPopulation.addChromosome(pop.getChromosome(i));
        }

        int crossover_count = (int) (manager.crossoverRate * populationSize);

        if (crossover_count % 2 != 0) crossover_count += 1;

        int micro_mutation_count = (int) (manager.microMutationRate * populationSize);
        int macro_mutation_count = (int) (manager.macroMutationRate * populationSize);
        int reproduction_count = populationSize - crossover_count - micro_mutation_count - macro_mutation_count;

        //do crossover
        for (int offspring_index = 0; offspring_index < crossover_count; offspring_index += 2) {

            E child1 = tournamentSelection().makeCopy();
            E child2 = tournamentSelection().makeCopy();

            List<E> genes = child1.crossover(child2, 0);
            for (E e : genes) {
                newPopulation.addChromosome(e);
            }
        }

        // do point mutation
        for (int offspring_index = 0; offspring_index < micro_mutation_count; ++offspring_index) {
            E child = tournamentSelection();
            newPopulation.addChromosome(child.mutate(0));
        }

        // do subtree mutation
        for (int offspring_index = 0; offspring_index < macro_mutation_count; ++offspring_index) {
            E child = tournamentSelection();
            newPopulation.addChromosome(child.mutate(1));
        }

        // do reproduction
        for (int offspring_index = 0; offspring_index < reproduction_count; ++offspring_index) {
            E child = tournamentSelection().makeCopy();
            newPopulation.addChromosome(child);
        }
        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }

    /**
     * Similar to Evolve2, but use offspring solution to replace bad parent,
     * in a way similar to TinyGP as as specified in "A Field Guide to Genetic Programming"
     *
     * @return evolved population
     */
    private Population<E> tinyGPEvolve(int populationSize) {

        double sum_rate = manager.crossoverRate + manager.macroMutationRate + manager.microMutationRate + manager.reproductionRate;
        double crossover_disk = manager.crossoverRate / sum_rate;
        double micro_mutation_disk = (manager.crossoverRate + manager.microMutationRate) / sum_rate;
        double macro_mutation_disk = (manager.crossoverRate + manager.microMutationRate + manager.macroMutationRate) / sum_rate;

        RandEngine randEngine = manager.randEngine;

        Population<E> newPopulation = new Population<>();
        newPopulation.setChromosomes(pop.getChromosomes());

        for (int offspring_index = 0; offspring_index < populationSize; offspring_index += 1) {
            double r = randEngine.uniform();
            List<E> children = new ArrayList<>();

            List<E> bad_parents = new ArrayList<>();

            Pair<Pair<E>> tournament = tournamentSelection(newPopulation.getChromosomes());
            Pair<E> tournament_winners = tournament._1();
            Pair<E> tournament_losers = tournament._2();

            bad_parents.add(tournament_losers._1());
            bad_parents.add(tournament_losers._2());

            if (r <= crossover_disk) {
                E child1 = tournament_winners._1().makeCopy();
                E child2 = tournament_winners._2().makeCopy();

                List<E> list = child1.crossover(child2, 0);

                children.addAll(list);
            } else if (r <= micro_mutation_disk) {
                E child = tournament_winners._1().makeCopy();
                child.mutate(0);
                children.add(child);
            } else if (r <= macro_mutation_disk) {
                E child = tournament_winners._1().makeCopy();
                child.mutate(1);
                children.add(child);
            } else {
                E child = tournament_winners._1().makeCopy();
                children.add(child);
            }

            boolean successfully_replaced = false;
            for (E child : children) {
                for (E bad_parent : bad_parents) {
                    if (comparator.compare(child, bad_parent) < 0) {
                        successfully_replaced = true;
                        newPopulation.set(newPopulation.indexOf(bad_parent), child);
                        break;
                    }
                }
                if (successfully_replaced) {
                    break;
                }
            }
        }
        return newPopulation;
    }

    //Todo update
    private Pair<Pair<E>> tournamentSelection(List<E> population) {

        Collections.shuffle(population);

        E good1, good2;
        E bad1, bad2;
        if (comparator.compare(population.get(0), population.get(1)) < 0) {
            good1 = population.get(0);
            bad1 = population.get(1);
        } else {
            good1 = population.get(1);
            bad1 = population.get(0);
        }

        if (comparator.compare(population.get(2), population.get(3)) > 0) {
            good2 = population.get(2);
            bad2 = population.get(3);
        } else {
            good2 = population.get(3);
            bad2 = population.get(2);
        }

        Pair<E> winners = new Pair<>(good1, good2);
        Pair<E> losers = new Pair<>(bad1, bad2);

        return new Pair<>(winners, losers);
    }
}
