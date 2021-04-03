package cgp.reproduction;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import cgp.interfaces.CGPReproduction;

import java.util.List;

public class MutateRandomParentReproduction implements CGPReproduction {
    @Override
    public void reproduce(CGPParams params, List<CGPChromosome> parents, List<CGPChromosome> children, int numParents, int numChildren) {
        /* for each child */
        for (int i = 0; i < numChildren; i++) {

            /* set child as clone of random parent */
            children.get(i).copyChromosome(parents.get(params.nextInt(numParents)));

            /* mutate newly cloned child */
            children.get(i).mutate(params);
        }
    }
}
