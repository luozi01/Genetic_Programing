package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.solver.CartesianGP;

public interface CGPReproductionStrategy {
    void reproduce(CartesianGP params, CGPChromosome[] parents, CGPChromosome[] children, int numParents, int numChildren);
}
