package cgp.reproduction;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import cgp.interfaces.CGPReproduction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MutateRandomParentReproduction implements CGPReproduction {
    private final CGPParams params;

    @Override
    public void reproduce(List<CGPChromosome> parents, List<CGPChromosome> children) {
        /* for each child */
        for (CGPChromosome child : children) {

            /* set child as clone of random parent */
            child.copyChromosome(parents.get(params.nextInt(parents.size())));

            /* mutate newly cloned child */
            child.mutate(params);
        }
    }
}
