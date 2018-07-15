package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.solver.CartesianGP;

public interface CGPMutationStrategy {
    void mutate(CartesianGP params, CGPChromosome chromo);
}
