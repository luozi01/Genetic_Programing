package cgp.gp;

import cgp.Solver.CartesianGP;
import genetics.Generator;
import org.apache.commons.math3.genetics.Chromosome;

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
