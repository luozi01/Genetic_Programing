package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;

import java.util.List;

public interface CGPReproduction {
    void reproduce(CGPParams params, List<CGPChromosome> parents, List<CGPChromosome> children, int numParents, int numChildren);
}
