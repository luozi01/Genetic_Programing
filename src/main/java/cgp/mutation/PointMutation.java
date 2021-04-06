package cgp.mutation;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.MutationPolicy;
import lombok.AllArgsConstructor;

import static java.lang.Math.round;

@AllArgsConstructor
public class PointMutation implements MutationPolicy<CGPChromosome> {
    private final CGPParams params;

    @Override
    public CGPChromosome mutate(CGPChromosome c) {
        CGPChromosome copy = c.copy();
        int nodeIndex;
        /* get the number of each type of gene */
        int numFunctionGenes = params.getNumNodes();
        int numInputGenes = params.getNumNodes() * params.getArity();
        int numOutputGenes = params.getNumOutputs();

        /* set the total number of chromosome genes */
        int numGenes = numFunctionGenes + numInputGenes + numOutputGenes;

        /* calculate the number of genes to mutate */
        int numGenesToMutate = (int) round(numGenes * params.getMutationRate());

        /* for the number of genes to mutate */
        for (int i = 0; i < numGenesToMutate; i++) {

            /* select a random gene */
            int geneToMutate = params.nextInt(numGenes);

            /* mutate function gene */
            if (geneToMutate < numFunctionGenes) {
                nodeIndex = geneToMutate;
                copy.getNode(nodeIndex).setFunction(params.getRandomFunction(copy.getFuncSet().size()));
            }

            /* mutate node input gene */
            else if (geneToMutate < numFunctionGenes + numInputGenes) {
                nodeIndex = (geneToMutate - numFunctionGenes) / copy.getArity();
                int nodeInputIndex = (geneToMutate - numFunctionGenes) % copy.getArity();
                copy.getNode(nodeIndex).setInput(nodeInputIndex, params.getRandomNodeInput(copy.getNumInputs(), copy.getNumNodes(), nodeIndex));
            }

            /* mutate outputNodes gene */
            else {
                nodeIndex = geneToMutate - numFunctionGenes - numInputGenes;
                copy.setOutputNode(nodeIndex, params.getRandomChromosomeOutput(copy.getNumInputs(), copy.getNumNodes()));
            }
        }
        return copy;
    }
}
