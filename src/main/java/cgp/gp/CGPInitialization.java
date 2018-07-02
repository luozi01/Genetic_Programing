package cgp.gp;

import cgp.Solver.CartesianGP;
import genetics.chromosome.Chromosome;
import genetics.interfaces.Initialization;

import java.util.ArrayList;
import java.util.List;

public class CGPInitialization implements Initialization {

    private final CartesianGP manager;

    public CGPInitialization(CartesianGP manager) {
        this.manager = manager;
    }

    /**
     * Generate chromosome population
     *
     * @return new chromosome population
     */
    @Override
    public List<Chromosome> generate() {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < manager.getPopulationSize(); i++) {
            CGPChromosome gp = new CGPChromosome(manager);
            gp.initialize();
            population.add(gp);
        }
        return population;
    }
}
