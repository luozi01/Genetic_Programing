package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.solver.CartesianGP;

public interface CGPSelectionStrategy {
    void select(CartesianGP params, CGPChromosome[] parents, CGPChromosome[] candidateChromos, int numParents, int numCandidateChromos);
}
