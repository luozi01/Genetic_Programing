package cgp.selection;

import cgp.gp.CGPChromosome;
import cgp.interfaces.CGPSelection;
import genetics.chromosome.Chromosome;
import genetics.common.Population;
import org.eclipse.collections.api.tuple.Pair;

import java.util.Comparator;
import java.util.List;

public class FittestSelection implements CGPSelection {
    /**
     * Select the best k children for next generation
     *
     * @param parents      parent chromosome
     * @param candidate    candidate chromosome
     * @param numParents   parent number
     * @param numCandidate candidate number
     */
    @Override
    public void select(List<CGPChromosome> parents, List<CGPChromosome> candidate, int numParents, int numCandidate) {
        List<CGPChromosome> nextGen = candidate.subList(0, numCandidate);
        nextGen.sort(Comparator.comparingDouble(Chromosome::getFitness));
        for (int i = 0; i < numParents; i++) {
            parents.get(i).copyChromosome(nextGen.get(i));
        }
    }

    /**
     * Not used in CGP
     *
     * @param population population
     * @param arity      number to select
     * @return selected sub population
     */
    @Override
    public Pair<CGPChromosome, CGPChromosome> select(Population<CGPChromosome> population, int arity) {
        return null;
    }
}
