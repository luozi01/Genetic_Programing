package cgp.mutation;

import static java.lang.Math.round;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import genetics.interfaces.MutationPolicy;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PointANNMutation implements MutationPolicy<CGPChromosome> {
  private final CGPParams params;

  /**
   * @param c chromosome
   * @return mutated chromosome
   */
  @Override
  public CGPChromosome mutate(CGPChromosome c) {
    CGPChromosome copy = c.copy();

    int nodeIndex;
    int nodeInputIndex;

    /* get the number of each type of gene */
    int numFunctionGenes = params.getNumNodes();
    int numInputGenes = params.getNumNodes() * params.getArity();
    int numWeightGenes = params.getNumNodes() * params.getArity();
    int numOutputGenes = params.getNumOutputs();

    /* set the total number of chromosome genes */
    int numGenes = numFunctionGenes + numInputGenes + numWeightGenes + numOutputGenes;

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
      } /* mutate node input gene */ else if (geneToMutate < numFunctionGenes + numInputGenes) {
        nodeIndex = (geneToMutate - numFunctionGenes) / copy.getArity();
        nodeInputIndex = (geneToMutate - numFunctionGenes) % copy.getArity();

        copy.getNode(nodeIndex)
            .setInput(
                nodeInputIndex,
                params.getRandomNodeInput(copy.getNumInputs(), copy.getNumNodes(), nodeIndex));
      } /* mutate connection weight */ else if (geneToMutate
          < numFunctionGenes + numInputGenes + numWeightGenes) {
        nodeIndex = (geneToMutate - numFunctionGenes - numInputGenes) / copy.getArity();
        nodeInputIndex = (geneToMutate - numFunctionGenes - numInputGenes) % copy.getArity();

        copy.getNode(nodeIndex).setWeight(nodeInputIndex, params.getRandomConnectionWeight());
      } /* mutate outputNodes gene */ else {
        nodeIndex = geneToMutate - numFunctionGenes - numInputGenes - numWeightGenes;
        copy.setOutputNode(
            nodeIndex, params.getRandomChromosomeOutput(copy.getNumInputs(), copy.getNumNodes()));
      }
    }
    return copy;
  }
}
