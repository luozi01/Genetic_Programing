package tgp.gp;

import genetics.chromosome.Chromosome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tgp.program.TreeNode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TGPChromosome extends Chromosome {
  private TreeNode root;

  public TGPChromosome makeCopy() {
    return new TGPChromosome(root.copy());
  }

  @Override
  public String toString() {
    return root.toString();
  }

  public double eval(double[] inputs) {
    return root.eval(inputs);
  }
}
