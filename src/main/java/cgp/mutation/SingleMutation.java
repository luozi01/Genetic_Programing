package cgp.mutation;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.MutationPolicy;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SingleMutation implements MutationPolicy<CGPChromosome> {
  private final CGPParams params;

  @Override
  public CGPChromosome mutate(CGPChromosome c) {
    CGPChromosome copy = c.copy();
    boolean mutatedActive = false;
    int previousGeneValue, newGeneValue, nodeIndex;

    /* get the number of each type of gene */
    int numFunctionGenes = params.getNumNodes();
    int numInputGenes = params.getNumNodes() * params.getArity();
    int numOutputGenes = params.getNumOutputs();

    /* set the total number of chromosome genes */
    int numGenes = numFunctionGenes + numInputGenes + numOutputGenes;

    /* while active gene not mutated */
    while (!mutatedActive) {
      /* select a random gene */
      int geneToMutate = params.nextInt(numGenes);

      /* mutate function gene */
      if (geneToMutate < numFunctionGenes) {
        nodeIndex = geneToMutate;
        previousGeneValue = copy.getNode(nodeIndex).getFunction();
        copy.getNode(nodeIndex).setFunction(params.getRandomFunction(copy.getFuncSet().size()));
        newGeneValue = copy.getNode(nodeIndex).getFunction();
        if ((previousGeneValue != newGeneValue) && (copy.isNodeActive(nodeIndex))) {
          mutatedActive = true;
        }
      } /* mutate node input gene */ else if (geneToMutate < numFunctionGenes + numInputGenes) {
        nodeIndex = (geneToMutate - numFunctionGenes) / copy.getArity();
        int nodeInputIndex = (geneToMutate - numFunctionGenes) % copy.getArity();
        previousGeneValue = copy.getNode(nodeIndex).getInput(nodeInputIndex);
        copy.getNode(nodeIndex)
            .setInput(
                nodeInputIndex,
                params.getRandomNodeInput(copy.getNumInputs(), copy.getNumNodes(), nodeIndex));
        newGeneValue = copy.getNode(nodeIndex).getInput(nodeInputIndex);
        if ((previousGeneValue != newGeneValue) && (copy.isNodeActive(nodeIndex))) {
          mutatedActive = true;
        }
      } /* mutate outputNodes gene */ else {
        nodeIndex = geneToMutate - numFunctionGenes - numInputGenes;
        previousGeneValue = copy.getOutputNode(nodeIndex);
        copy.setOutputNode(
            nodeIndex, params.getRandomChromosomeOutput(copy.getNumInputs(), copy.getNumNodes()));
        newGeneValue = copy.getOutputNode(nodeIndex);
        if (previousGeneValue != newGeneValue) {
          mutatedActive = true;
        }
      }
    }
    return copy;
  }
}
