package cgp.interfaces;

import cgp.gp.CGPChromosome;

import java.util.List;

public interface CGPReproduction {
    void reproduce(List<CGPChromosome> parents, List<CGPChromosome> children);
}
