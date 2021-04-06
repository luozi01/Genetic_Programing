package cgp.interfaces;

import cgp.gp.CGPChromosome;
import genetics.interfaces.SelectionPolicy;

import java.util.List;

public interface CGPSelection extends SelectionPolicy<CGPChromosome> {
    void select(List<CGPChromosome> parents, List<CGPChromosome> candidate);
}
