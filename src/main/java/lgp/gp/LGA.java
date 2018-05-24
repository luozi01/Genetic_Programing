package lgp.gp;


import genetics.*;
import genetics.utils.RandEngine;
import lgp.solver.LinearGP;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

import java.util.List;

public class LGA extends GeneticAlgorithm {

    private final CrossoverPolicy crossoverPolicy;
    private final MutationPolicy micro, macro;
    private final LinearGP manager;

    public LGA(Population pop, final CrossoverPolicy crossoverPolicy,
               final MutationPolicy micro, final MutationPolicy macro, final LinearGP manager) {
        super(pop);
        this.crossoverPolicy = crossoverPolicy;
        this.micro = micro;
        this.macro = macro;
        this.manager = manager;
    }

    @Override
    protected Population evolvePopulation() {
        RandEngine randEngine = manager.getRandEngine();
        Population pop = new Population();
        int iPopSize = manager.getPopulationSize();
        int program_count = 0;

        //Todo keep elite

        int computationBudget = iPopSize * 8;
        int counter = 0;
        while (program_count < iPopSize && counter < computationBudget) {

            Chromosome gp1 = tournamentSelection(manager.getTournamentSize());
            Chromosome gp2 = tournamentSelection(manager.getTournamentSize());

            double r = randEngine.uniform();
            if (r < manager.getCrossoverRate()) {
                List<Chromosome> list = crossoverPolicy.crossover(gp1, gp2);
                for (Chromosome e : list) {
                    pop.addChromosome(e);
                }
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                pop.addChromosome(macro.mutate(gp1));
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                pop.addChromosome(macro.mutate(gp2));
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                pop.addChromosome(micro.mutate(gp1));
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                pop.addChromosome(micro.mutate(gp2));
            }
            counter++;
        }

        pop.sort(comparator);
        pop.trim(iPopSize);

        return pop;
    }
}
