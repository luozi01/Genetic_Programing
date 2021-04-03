package cgp.mutation;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import cgp.interfaces.CGPMutation;

public class ProbabilisticMutation implements CGPMutation {
    @Override
    public CGPChromosome mutate(CGPParams params, CGPChromosome c) {
        CGPChromosome copy = c.copy();
        /* for every nodes in the chromosome */
        for (int i = 0; i < params.getNumNodes(); i++) {

            /* mutate the function gene */
            if (params.uniform() <= params.getMutationRate()) {
                copy.getNode(i).function = params.getRandomFunction(copy.getFuncSet().size());
            }

            /* for every input to each chromosome */
            copy.mutate(params, i);
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

    @Override
    public CGPChromosome mutate(CGPChromosome c) {
        return null;
    }
}
