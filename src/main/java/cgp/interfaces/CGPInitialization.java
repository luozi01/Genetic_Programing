package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.common.Population;
import genetics.interfaces.Initialization;

import java.util.List;

public interface CGPInitialization extends Initialization<CGPChromosome> {
    List<CGPChromosome> generate(CGPParams params, int numParents, int numChildren, Population<CGPChromosome> population);

    List<CGPChromosome> generate(CGPParams params, int size);
}
