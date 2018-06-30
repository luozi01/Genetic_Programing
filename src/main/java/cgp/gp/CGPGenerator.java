package cgp.gp;

import cgp.Solver.CartesianGP;
import genetics.chromosome.Chromosome;
import genetics.common.Generator;

import java.util.ArrayList;
import java.util.List;

public class CGPGenerator implements Generator {

    private final CartesianGP manager;

    public CGPGenerator(CartesianGP manager) {
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
