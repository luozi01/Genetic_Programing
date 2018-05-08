package com.zluo.lgp.gp;

import com.zluo.ga.Chromosome;
import com.zluo.ga.Fitness;
import com.zluo.ga.GeneticAlgorithm;
import com.zluo.ga.Population;
import com.zluo.ga.utils.RandEngine;
import com.zluo.lgp.solver.LinearGP;

public class LGA<E extends Chromosome<E>> extends GeneticAlgorithm<E> {

    private LinearGP manager = new LinearGP();

    public LGA(Population<E> pop, Fitness<E> fitness) {
        super(pop, fitness);
    }

    public void setManager(LinearGP manager) {
        this.manager = manager;
    }

    @Override
    protected Population<E> evolvePopulation() {
        RandEngine randEngine = manager.getRandEngine();
        Population<E> pop = new Population<>();
        int iPopSize = manager.getPopulationSize();
        int program_count = 0;

        int computationBudget = iPopSize * 8;
        int counter = 0;
        while (program_count < iPopSize && counter < computationBudget) {

            E gp1 = tournamentSelection().makeCopy();
            E gp2 = tournamentSelection().makeCopy();

            double r = randEngine.uniform();
            if (r < manager.getCrossoverRate()) {
                for (E e : gp1.crossover(gp2, 0)) {
                    pop.addChromosome(e.makeCopy());
                }
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                pop.addChromosome(gp1.mutate(1));
            }

            r = randEngine.uniform();
            if (r < manager.getMacroMutationRate()) {
                pop.addChromosome(gp2.mutate(1));
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                pop.addChromosome(gp1.mutate(0));
            }

            r = randEngine.uniform();
            if (r < manager.getMicroMutationRate()) {
                pop.addChromosome(gp2.mutate(0));
            }
            counter++;
        }

        pop.sort(comparator);
        pop.trim(iPopSize);

        return pop;
    }
}
