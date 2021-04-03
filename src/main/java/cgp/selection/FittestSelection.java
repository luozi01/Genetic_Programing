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
        List<CGPChromosome> next_gen = candidate.subList(0, numCandidate);
        next_gen.sort(Comparator.comparingDouble(Chromosome::getFitness));
        for (int i = 0; i < numParents; i++) {
            parents.get(i).copyChromosome(next_gen.get(i));
        }
    }

    /**
     * Sorts the given array of chromosomes by fitness, lowest to highest
     * uses insertion sort (quickest and stable)
     */
    private void sortChromosomeArray(CGPChromosome[] chromoArray, int numChromos) {
        CGPChromosome chromoTmp;
        for (int i = 0; i < numChromos; i++) {
            for (int j = i; j < numChromos; j++) {
                if (chromoArray[i].getFitness() > chromoArray[j].getFitness()) {
                    chromoTmp = chromoArray[i];
                    chromoArray[i] = chromoArray[j];
                    chromoArray[j] = chromoTmp;
                }
            }
        }
    }

    @Override
    public Pair<CGPChromosome, CGPChromosome> select(Population<CGPChromosome> population, int arity) {
        return null;
    }
}
