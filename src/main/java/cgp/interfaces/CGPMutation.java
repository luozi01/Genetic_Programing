package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.MutationPolicy;

public interface CGPMutation extends MutationPolicy<CGPChromosome> {
    CGPChromosome mutate(CGPParams params, CGPChromosome chromosome);
}
