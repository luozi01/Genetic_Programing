package lgp.gp;


import genetics.driver.GeneticAlgorithm;
import genetics.chromosome.Chromosome;
import genetics.common.FitnessCalc;
import genetics.common.Population;
import genetics.interfaces.CrossoverPolicy;
import genetics.interfaces.MutationPolicy;
import genetics.utils.RandEngine;
import lgp.solver.LinearGP;
import org.eclipse.collections.api.tuple.Pair;

public class LGA extends GeneticAlgorithm {

    private final CrossoverPolicy crossoverPolicy;
    private final MutationPolicy micro, macro;
    private final LinearGP manager;

    public LGA(Population pop,
               final FitnessCalc fitnessCalc,
               final CrossoverPolicy crossoverPolicy,
               final MutationPolicy micro,
               final MutationPolicy macro,
               final LinearGP manager) {
        super(pop, fitnessCalc);
        this.crossoverPolicy = crossoverPolicy;
        this.micro = micro;
        this.macro = macro;
        this.manager = manager;
    }

    @Override
    protected Population evolvePopulation() {
        RandEngine randEngine = manager.getRandEngine();
        int iPopSize = manager.getPopulationSize();
        int program_count = 0;

        int computationBudget = iPopSize * 8;
        int counter = 0;
        while (program_count < iPopSize && counter < computationBudget) {
            Chromosome gp1 = tournamentSelection(manager.getTournamentSize());
            Chromosome gp2 = tournamentSelection(manager.getTournamentSize());

            double r = randEngine.uniform();
            if (r < manager.getCrossoverRate()) {
                Pair<Chromosome, Chromosome> pair = crossoverPolicy.crossover(gp1, gp2);
                population.addChromosome(pair.getOne());
                population.addChromosome(pair.getTwo());
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                population.addChromosome(macro.mutate(gp1));
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                population.addChromosome(macro.mutate(gp2));
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                population.addChromosome(micro.mutate(gp1));
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                population.addChromosome(micro.mutate(gp2));
            }
            counter++;
        }
        population.sort(comparator);
        population.trim(iPopSize);
        return population;
    }
}
