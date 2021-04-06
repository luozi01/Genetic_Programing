package cgp.mutation;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.MutationPolicy;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProbabilisticOnlyActiveMutation implements MutationPolicy<CGPChromosome> {
    private final CGPParams params;

    @Override
    public CGPChromosome mutate(CGPChromosome c) {
        CGPChromosome copy = c.copy();
        /* for every active node in the chromosome */
        for (int i = 0; i < copy.getNumActiveNodes(); i++) {

            int activeNode = copy.getActiveNode(i);

            /* mutate the function gene */
            if (params.uniform() <= params.getMutationRate()) {
                copy.getNode(activeNode).setFunction(params.getRandomFunction(copy.getFuncSet().size()));
            }

            /* for every input to each chromosome */
            copy.mutate(params, activeNode);
        }

        /* for every chromosome outputNodes */
        for (int i = 0; i < params.getNumOutputs(); i++) {

            /* mutate the chromosome outputNodes */
            if (params.uniform() <= params.getMutationRate()) {
                copy.setOutputNode(i, params.getRandomChromosomeOutput(copy.getNumInputs(), copy.getNumNodes()));
            }
        }

        return copy;
    }
}
