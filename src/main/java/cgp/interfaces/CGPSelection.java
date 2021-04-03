package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.SelectionPolicy;

import java.util.List;

public interface CGPSelection extends SelectionPolicy<CGPChromosome> {
    void select(CGPParams params, List<CGPChromosome> parents, List<CGPChromosome> candidate, int numParents, int numCandidate);
}
