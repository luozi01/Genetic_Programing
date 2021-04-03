package cgp.selection;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import cgp.interfaces.CGPSelection;
import genetics.chromosome.Chromosome;
import genetics.common.Population;
import org.eclipse.collections.api.tuple.Pair;

import java.util.Comparator;
import java.util.List;

public class FittestSelection implements CGPSelection {
    @Override
    public void select(CGPParams params, List<CGPChromosome> parents, List<CGPChromosome> candidate, int numParents, int numCandidate) {
        List<CGPChromosome> nextGen = candidate.subList(0, numCandidate);
        nextGen.sort(Comparator.comparingDouble(Chromosome::getFitness));
        for (int i = 0; i < numParents; i++) {
            parents.get(i).copyChromosome(nextGen.get(i));
        }
    }

    @Override
    public Pair<CGPChromosome, CGPChromosome> select(Population<CGPChromosome> population, int arity) {
        return null;
    }
}
