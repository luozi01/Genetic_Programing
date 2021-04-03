package cgp.initialization;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.common.Population;
import lombok.Data;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

@Data
public class CGPInitializer implements cgp.interfaces.CGPInitialization {
    @Override
    public List<CGPChromosome> generate(CGPParams params, int numParents, int numChildren, Population<CGPChromosome> population) {
        List<CGPChromosome> results = Lists.mutable.empty();
        if (population != null && population.size() > 0) {
            results.addAll(population.getChromosomes());
        }
        final int size = numParents + numChildren - results.size();
        for (int i = 0; i < size; i++) {
            results.add(CGPChromosome.initializeChromosome(params));
        }
        return results;
    }

    @Override
    public List<CGPChromosome> generate(CGPParams params, int size) {
        MutableList<CGPChromosome> population = Lists.mutable.empty();

        for (int i = 0; i < size; i++) {
            population.add(CGPChromosome.initializeChromosome(params));
        }

        return population;
    }

    @Override
    public List<CGPChromosome> generate() {
        return null;
    }
}
