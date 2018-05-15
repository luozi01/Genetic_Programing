package treegp.gp;

import genetics.Chromosome;
import genetics.Fitness;
import genetics.GeneticAlgorithm;
import genetics.Population;
import genetics.utils.RandEngine;
import treegp.enums.TGPPopulationReplacementStrategy;
import treegp.solver.TreeGP;

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

            E child1 = tournamentSelection();
            E child2 = tournamentSelection();

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
            E child = tournamentSelection();
            newPopulation.addChromosome(child);
        }
        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }

    private Population<E> tinyGPEvolve(int populationSize) {

        double sum_rate = manager.crossoverRate + manager.macroMutationRate + manager.microMutationRate + manager.reproductionRate;
        double crossover_disk = manager.crossoverRate / sum_rate;
        double micro_mutation_disk = (manager.crossoverRate + manager.microMutationRate) / sum_rate;
        double macro_mutation_disk = (manager.crossoverRate + manager.microMutationRate + manager.macroMutationRate) / sum_rate;

        RandEngine randEngine = manager.randEngine;

        for (int offspring_index = 0; offspring_index < populationSize; offspring_index += 1) {
            double r = randEngine.uniform();

            if (r <= crossover_disk) {
                E child1 = tournamentSelection();
                E child2 = tournamentSelection();

                List<E> list = child1.crossover(child2, 0);

                for (E e : list) {
                    pop.addChromosome(e);
                }
            } else if (r <= micro_mutation_disk) {
                E child = tournamentSelection();
                pop.addChromosome(child.mutate(0));
            } else if (r <= macro_mutation_disk) {
                E child = tournamentSelection();
                pop.addChromosome(child.mutate(1));
            } else {
                E child = tournamentSelection();
                pop.addChromosome(child);
            }
            pop.sort(comparator);
            pop.trim(populationSize);
        }
        return pop;
    }
}
