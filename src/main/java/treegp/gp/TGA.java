package treegp.gp;

import genetics.*;
import genetics.utils.Pair;
import genetics.utils.RandEngine;
import treegp.enums.TGPEvolveStrategy;
import treegp.solver.TreeGP;

public class TGA extends GeneticAlgorithm {

    private final CrossoverPolicy crossoverPolicy;
    private final MutationPolicy micro, macro;
    private final TreeGP manager;

    public TGA(Population pop,
               final FitnessCalc fitnessCalc,
               final CrossoverPolicy crossoverPolicy,
               final MutationPolicy micro,
               final MutationPolicy macro,
               final TreeGP manager) {
        super(pop, fitnessCalc);
        this.crossoverPolicy = crossoverPolicy;
        this.micro = micro;
        this.macro = macro;
        this.manager = manager;
    }

    @Override
    protected Population evolvePopulation() {
        Population newPop = new Population();
        TGPEvolveStrategy populationReplacement = manager.getReplacementStrategy();
        if (populationReplacement == TGPEvolveStrategy.MU_PLUS_LAMBDA) {
            newPop = muPlusLambdaEvolve(manager.getPopulationSize());
        } else if (populationReplacement == TGPEvolveStrategy.TINY_GP) {
            newPop = tinyGPEvolve(manager.getPopulationSize());
        }
        return newPop;
    }

    private Population muPlusLambdaEvolve(int populationSize) {
        Population newPopulation = new Population();

        int elite_count = (int) (manager.getElitismRatio() * populationSize);

        population.sort(comparator);
        for (int i = 0; (i < populationSize) && (i < elite_count); i++) {
            newPopulation.addChromosome(population.getChromosome(i));
        }

        int crossover_count = (int) (manager.getCrossoverRate() * populationSize);

        if (crossover_count % 2 != 0) crossover_count += 1;

        int micro_mutation_count = (int) (manager.getMicroMutationRate() * populationSize);
        int macro_mutation_count = (int) (manager.getMacroMutationRate() * populationSize);
        int reproduction_count = populationSize - crossover_count - micro_mutation_count - macro_mutation_count;

        //do crossover
        for (int offspring_index = 0; offspring_index < crossover_count; offspring_index += 2) {

            Chromosome child1 = tournamentSelection(manager.getTournamentSize());
            Chromosome child2 = tournamentSelection(manager.getTournamentSize());

            Pair<Chromosome> genes = crossoverPolicy.crossover(child1, child2);
            newPopulation.addChromosome(genes.getFirst());
            newPopulation.addChromosome(genes.getSecond());
        }

        // do point mutation
        for (int offspring_index = 0; offspring_index < micro_mutation_count; ++offspring_index) {
            Chromosome child = tournamentSelection(manager.getTournamentSize());
            newPopulation.addChromosome(micro.mutate(child));
        }

        // do subtree mutation
        for (int offspring_index = 0; offspring_index < macro_mutation_count; ++offspring_index) {
            Chromosome child = tournamentSelection(manager.getTournamentSize());
            newPopulation.addChromosome(macro.mutate(child));
        }

        // do reproduction
        for (int offspring_index = 0; offspring_index < reproduction_count; ++offspring_index) {
            Chromosome child = tournamentSelection(manager.getTournamentSize());
            newPopulation.addChromosome(child);
        }
        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }

    private Population tinyGPEvolve(int populationSize) {

        double sum_rate = manager.getCrossoverRate() + manager.getMacroMutationRate()
                + manager.getMicroMutationRate() + manager.getReproductionRate();
        double crossover_disk = manager.getCrossoverRate() / sum_rate;
        double micro_mutation_disk = (manager.getCrossoverRate() + manager.getMicroMutationRate()) / sum_rate;
        double macro_mutation_disk = (manager.getCrossoverRate() + manager.getMicroMutationRate() + manager.getMacroMutationRate()) / sum_rate;

        RandEngine randEngine = manager.getRandEngine();

        for (int offspring_index = 0; offspring_index < populationSize; offspring_index += 1) {
            double r = randEngine.uniform();

            if (r <= crossover_disk) {
                Chromosome child1 = tournamentSelection(manager.getTournamentSize());
                Chromosome child2 = tournamentSelection(manager.getTournamentSize());

                Pair<Chromosome> pair = crossoverPolicy.crossover(child1, child2);
                population.addChromosome(pair.getFirst());
                population.addChromosome(pair.getSecond());
            } else if (r <= micro_mutation_disk) {
                Chromosome child = tournamentSelection(manager.getTournamentSize());
                population.addChromosome(micro.mutate(child));
            } else if (r <= macro_mutation_disk) {
                Chromosome child = tournamentSelection(manager.getTournamentSize());
                population.addChromosome(macro.mutate(child));
            } else {
                Chromosome child = tournamentSelection(manager.getTournamentSize());
                population.addChromosome(child);
            }
            population.sort(comparator);
            population.trim(populationSize);
        }
        return population;
    }
}
