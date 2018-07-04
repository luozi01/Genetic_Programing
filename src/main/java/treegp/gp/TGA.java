package treegp.gp;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.CrossoverPolicy;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.MutationPolicy;
import genetics.interfaces.SelectionPolicy;
import genetics.selection.TournamentSelection;
import genetics.utils.RandEngine;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.FastList;
import treegp.enums.TGPEvolveStrategy;
import treegp.solver.TreeGP;

import java.util.List;

public class TGA extends GeneticAlgorithm {

    private final CrossoverPolicy crossoverPolicy;
    private final MutationPolicy micro, macro;
    private final SelectionPolicy selectionPolicy;
    private final TreeGP manager;

    public TGA(final FitnessCalc fitnessCalc,
               final TreeGP manager) {
        super(new TGPInitialization(manager), fitnessCalc);
        this.crossoverPolicy = new Crossover(manager);
        this.micro = new MicroMutation(manager);
        this.macro = new MacroMutation(manager);
        this.selectionPolicy = new TournamentSelection();
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
        int crossover_count = (int) (manager.getCrossoverRate() * populationSize);
        Population copy = new Population(population.getChromosomes());
        if (crossover_count % 2 != 0) crossover_count += 1;

        int micro_mutation_count = (int) (manager.getMicroMutationRate() * populationSize);
        int macro_mutation_count = (int) (manager.getMacroMutationRate() * populationSize);
        int reproduction_count = populationSize - crossover_count - micro_mutation_count - macro_mutation_count;

        Pair<Chromosome, Chromosome> pair;
        //do crossover
        for (int offspring_index = 0; offspring_index < crossover_count; offspring_index += 2) {

            pair = selectionPolicy.select(copy, manager.getTournamentSize(), manager.getRandEngine());
            Pair<Chromosome, Chromosome> genes = crossoverPolicy.crossover(pair.getOne(), pair.getTwo());
            population.addChromosome(genes.getOne());
            population.addChromosome(genes.getTwo());
        }

        // do point mutation
        for (int offspring_index = 0; offspring_index < micro_mutation_count; ++offspring_index) {
            pair = selectionPolicy.select(copy, manager.getTournamentSize(), manager.getRandEngine());
            population.addChromosome(micro.mutate(pair.getOne()));
        }

        // do subtree mutation
        for (int offspring_index = 0; offspring_index < macro_mutation_count; ++offspring_index) {
            pair = selectionPolicy.select(copy, manager.getTournamentSize(), manager.getRandEngine());
            population.addChromosome(macro.mutate(pair.getOne()));
        }

        // do reproduction
        for (int offspring_index = 0; offspring_index < reproduction_count; ++offspring_index) {
            pair = selectionPolicy.select(copy, manager.getTournamentSize(), manager.getRandEngine());
            population.addChromosome(pair.getOne());
        }
        return population;
    }

    private Population tinyGPEvolve(int populationSize) {
        Population nextGeneration = new Population(population.getChromosomes());
        double sum_rate = manager.getCrossoverRate() + manager.getMacroMutationRate()
                + manager.getMicroMutationRate() + manager.getReproductionRate();
        double crossover_disk = manager.getCrossoverRate() / sum_rate;
        double micro_mutation_disk = (manager.getCrossoverRate() + manager.getMicroMutationRate()) / sum_rate;
        double macro_mutation_disk = (manager.getCrossoverRate() + manager.getMicroMutationRate() + manager.getMacroMutationRate()) / sum_rate;

        RandEngine randEngine = manager.getRandEngine();
        List<Chromosome> children, bad_parents;
        for (int offspring_index = 0; offspring_index < populationSize; offspring_index += 1) {
            double r = randEngine.uniform();
            children = FastList.newList();
            bad_parents = FastList.newList();

            Pair<Chromosome, Chromosome> p1 = selectionPolicy.select(nextGeneration, manager.getTournamentSize(), randEngine);
            Pair<Chromosome, Chromosome> p2 = selectionPolicy.select(nextGeneration, manager.getTournamentSize(), randEngine);

            bad_parents.add(p1.getTwo());
            bad_parents.add(p2.getTwo());

            if (r <= crossover_disk) {
                Pair<Chromosome, Chromosome> crossover = crossoverPolicy.crossover(p1.getOne(), p2.getOne());
                children.add(crossover.getOne());
                children.add(crossover.getTwo());
            } else if (r <= micro_mutation_disk) {
                children.add(micro.mutate(p1.getOne()));
            } else if (r <= macro_mutation_disk) {
                children.add(macro.mutate(p2.getOne()));
            } else {
                children.add(((TGPChromosome) p1.getOne()).makeCopy());
            }

            boolean successfully_replaced = false;
            for (Chromosome child : children) {
                if (Double.isNaN(child.fitness))
                    child.fitness = fitnessCalc.calc(child);

                for (Chromosome bad_parent : bad_parents) {
                    if (child.betterThan(bad_parent)) {
                        successfully_replaced = true;
                        nextGeneration.setChromosome(nextGeneration.indexOf(bad_parent), child);
                        break;
                    }
                }
                if (successfully_replaced) break;
            }
        }
        return nextGeneration;
    }
}
