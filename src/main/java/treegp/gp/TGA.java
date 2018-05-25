package treegp.gp;

import genetics.GeneticAlgorithm;
import genetics.Population;
import genetics.utils.RandEngine;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.MutationPolicy;
import treegp.enums.TGPEvolveStrategy;
import treegp.solver.TreeGP;

public class TGA extends GeneticAlgorithm {

    private final CrossoverPolicy crossoverPolicy;
    private final MutationPolicy micro, macro;
    private final TreeGP manager;

    public TGA(Population pop, final CrossoverPolicy crossoverPolicy,
               final MutationPolicy micro, final MutationPolicy macro, final TreeGP manager) {
        super(pop);
        this.crossoverPolicy = crossoverPolicy;
        this.micro = micro;
        this.macro = macro;
        this.manager = manager;
    }

    @Override
    protected Population evolvePopulation() {
        Population newPop = new Population();
        TGPEvolveStrategy populationReplacement = manager.replacementStrategy;
        if (populationReplacement == TGPEvolveStrategy.MU_PLUS_LAMBDA) {
            newPop = muPlusLambdaEvolve(manager.populationSize);
        } else if (populationReplacement == TGPEvolveStrategy.TINY_GP) {
            newPop = tinyGPEvolve(manager.populationSize);
        }
        return newPop;
    }

    private Population muPlusLambdaEvolve(int populationSize) {
        Population newPopulation = new Population();

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

            Chromosome child1 = tournamentSelection(manager.tournamentSize);
            Chromosome child2 = tournamentSelection(manager.tournamentSize);

            ChromosomePair genes = crossoverPolicy.crossover(child1, child2);
            newPopulation.addChromosome(genes.getFirst());
            newPopulation.addChromosome(genes.getSecond());
        }

        // do point mutation
        for (int offspring_index = 0; offspring_index < micro_mutation_count; ++offspring_index) {
            Chromosome child = tournamentSelection(manager.tournamentSize);
            newPopulation.addChromosome(micro.mutate(child));
        }

        // do subtree mutation
        for (int offspring_index = 0; offspring_index < macro_mutation_count; ++offspring_index) {
            Chromosome child = tournamentSelection(manager.tournamentSize);
            newPopulation.addChromosome(macro.mutate(child));
        }

        // do reproduction
        for (int offspring_index = 0; offspring_index < reproduction_count; ++offspring_index) {
            Chromosome child = tournamentSelection(manager.tournamentSize);
            newPopulation.addChromosome(child);
        }
        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }

    private Population tinyGPEvolve(int populationSize) {

        double sum_rate = manager.crossoverRate + manager.macroMutationRate + manager.microMutationRate + manager.reproductionRate;
        double crossover_disk = manager.crossoverRate / sum_rate;
        double micro_mutation_disk = (manager.crossoverRate + manager.microMutationRate) / sum_rate;
        double macro_mutation_disk = (manager.crossoverRate + manager.microMutationRate + manager.macroMutationRate) / sum_rate;

        RandEngine randEngine = manager.randEngine;

        for (int offspring_index = 0; offspring_index < populationSize; offspring_index += 1) {
            double r = randEngine.uniform();

            if (r <= crossover_disk) {
                Chromosome child1 = tournamentSelection(manager.tournamentSize);
                Chromosome child2 = tournamentSelection(manager.tournamentSize);

                ChromosomePair pair = crossoverPolicy.crossover(child1, child2);
                pop.addChromosome(pair.getFirst());
                pop.addChromosome(pair.getSecond());
            } else if (r <= micro_mutation_disk) {
                Chromosome child = tournamentSelection(manager.tournamentSize);
                pop.addChromosome(micro.mutate(child));
            } else if (r <= macro_mutation_disk) {
                Chromosome child = tournamentSelection(manager.tournamentSize);
                pop.addChromosome(macro.mutate(child));
            } else {
                Chromosome child = tournamentSelection(manager.tournamentSize);
                pop.addChromosome(child);
            }
            pop.sort(comparator);
            pop.trim(populationSize);
        }
        return pop;
    }
}
