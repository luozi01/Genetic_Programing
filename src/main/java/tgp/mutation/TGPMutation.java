package tgp.mutation;

import genetics.interfaces.MutationPolicy;
import lombok.AllArgsConstructor;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import tgp.enums.TGPInitializationStrategy;
import tgp.gp.TGPChromosome;
import tgp.gp.TGPParams;
import tgp.program.Operator;
import tgp.program.SyntaxTreeUtils;
import tgp.program.TreeNode;

@AllArgsConstructor
public class TGPMutation implements MutationPolicy<TGPChromosome> {
  private final TGPParams manager;

  /**
   * Method that implements the subtree mutation described from Section 2.4 of "A Field Guide to
   * Genetic Programming"
   *
   * @param chromosome node
   */
  @Override
  public TGPChromosome mutate(TGPChromosome chromosome) {
    TGPChromosome treeNode = chromosome.makeCopy();

    TreeNode node = treeNode.getRoot().anyNode().getOne();
    switch (this.manager.getMutationStrategy()) {
      case MUTATION_POINT:
        if (node.isTerminal()) {
          Operator terminal = manager.getRandomTerminal();
          int trials = 0;
          int max_trials = 50;
          while (node.getOp() == terminal) {
            terminal = manager.getRandomTerminal();
            trials++;
            if (trials > max_trials) break;
          }
          if (terminal != null) {
            node.setOp(terminal);
            if (node.isVariable()) node.setVariable(manager.getRandomVar());
            if (node.isNumber()) node.setValue(manager.uniform());
          }
        } else {
          MutableList<Operator> candidates = Lists.mutable.empty();
          for (Operator op : manager.getNonTerminal()) {
            if (op != node.getOp() && op.argumentCount() == node.argumentCount())
              candidates.add(op);
          }
          if (!candidates.isEmpty()) node.setOp(candidates.get(manager.nextInt(candidates.size())));
        }
        break;
      case MUTATION_SUBTREE:
        int node_depth = treeNode.getRoot().depth2Node(node);
        int iMaxProgramDepth = this.manager.getMaxProgramDepth();
        node.getChildren().clear();

        node.setOp(manager.getRandomOperator());

        if (!node.isTerminal()) {
          int max_depth = iMaxProgramDepth - node_depth;
          SyntaxTreeUtils.createWithDepth(
              manager, node, max_depth, TGPInitializationStrategy.INITIALIZATION_METHOD_GROW);
        } else {
          if (node.isVariable()) node.setVariable(manager.getRandomVar());
          if (node.isNumber()) node.setValue(manager.uniform());
        }
        break;
      case MUTATION_SUBTREE_KINNEAR:
        int depth = treeNode.getRoot().length();
        int subtree_depth = treeNode.getRoot().depth2Node(node);
        int current_depth = depth - subtree_depth;
        int max_depth = (int) (depth * 1.15) - current_depth;

        node.getChildren().clear();
        node.setOp(manager.getRandomOperator());

        if (!node.isTerminal()) {
          SyntaxTreeUtils.createWithDepth(
              manager, node, max_depth, TGPInitializationStrategy.INITIALIZATION_METHOD_GROW);
        } else {
          if (node.isVariable()) node.setVariable(manager.getRandomVar());
          if (node.isNumber()) node.setValue(manager.uniform());
        }
        break;
      case MUTATION_HOIST:
        if (node != treeNode.getRoot()) {
          treeNode.setRoot(node);
        }
        break;
      case MUTATION_SHRINK:
        node.getChildren().clear();
        node.setOp(manager.getRandomTerminal());

        if (node.isVariable()) node.setVariable(manager.getRandomVar());
        if (node.isNumber()) node.setValue(manager.uniform());
        break;
      default:
        throw new IllegalArgumentException(
            "Invalid mutation type found: " + this.manager.getMutationStrategy());
    }
    return treeNode;
  }
}
