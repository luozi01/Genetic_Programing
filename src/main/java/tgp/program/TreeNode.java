package tgp.program;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import tgp.gp.TGPParams;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class TreeNode implements Serializable {
  private static final long serialVersionUID = 350057284330815219L;
  private final TGPParams manager;

  private Operator op;
  private int variable;
  private double value = 0;
  private MutableList<TreeNode> children = Lists.mutable.withInitialCapacity(2);

  TreeNode(Operator op, TGPParams manager) {
    this.op = op;
    this.manager = manager;
    if (op.isNumber()) {
      value = manager.uniform();
    }
    if (op.isVariable()) {
      variable = manager.getRandomVar();
    }
  }

  private int depth(TreeNode node, int depthSoFar) {
    int maxDepth = depthSoFar;
    for (TreeNode child : node.children) {
      int depth = depth(child, depthSoFar + 1);
      maxDepth = Math.max(maxDepth, depth);
    }
    return maxDepth;
  }

  public double eval(double[] inputs) {
    return op.eval(this, inputs);
  }

  void addChildren(TreeNode treeNode) {
    children.add(treeNode);
  }

  public boolean isNumber() {
    return op.isNumber();
  }

  public boolean isVariable() {
    return op.isVariable();
  }

  public boolean isTerminal() {
    return argumentCount() == 0;
  }

  public int argumentCount() {
    return op.argumentCount();
  }

  public int length() {
    int lengthSoFar = 1;
    for (TreeNode node : children) {
      lengthSoFar += node.length();
    }
    return lengthSoFar;
  }

  public int depth() {
    return depth(this, 0);
  }

  TreeNode getRight() {
    return children.size() <= 1 ? null : children.get(1);
  }

  TreeNode getLeft() {
    return children.isEmpty() ? null : children.get(0);
  }

  @Override
  public String toString() {
    return op.toString(this);
  }

  public TreeNode copy() {
    TreeNode clone = new TreeNode(op, manager);
    for (TreeNode c : children) {
      clone.children.add(c.copy());
    }
    clone.value = value;
    clone.variable = variable;
    return clone;
  }

  public Pair<TreeNode, TreeNode> anyNode() {
    return anyNode(false);
  }

  /**
   * Method that returns a randomly selected node from the current tree The tree is first flatten
   * into a list from which a node is randomly selected
   *
   * @param bias bias or not
   * @return new subtree
   */
  public Pair<TreeNode, TreeNode> anyNode(boolean bias) {
    MutableList<Pair<TreeNode, TreeNode>> nodes = flattenNodes();
    if (bias) {
      // As specified by Koza, 90% select function node, 10% select terminal node
      if (manager.uniform() <= 0.1) {
        MutableList<Pair<TreeNode, TreeNode>> terminalNodes = Lists.mutable.empty();
        for (Pair<TreeNode, TreeNode> tuple : nodes) {
          TreeNode node = tuple.getOne();
          if (node.isTerminal()) {
            terminalNodes.add(tuple);
          }
        }
        if (terminalNodes.size() > 0) {
          return terminalNodes.get(manager.nextInt(terminalNodes.size()));
        } else {
          return nodes.get(manager.nextInt(nodes.size()));
        }
      } else {
        MutableList<Pair<TreeNode, TreeNode>> functionNodes = Lists.mutable.empty();
        for (Pair<TreeNode, TreeNode> tuple : nodes) {
          TreeNode node = tuple.getOne();
          if (!node.isTerminal()) {
            functionNodes.add(tuple);
          }
        }
        if (functionNodes.size() > 0) {
          return functionNodes.get(manager.nextInt(functionNodes.size()));
        } else {
          return nodes.get(manager.nextInt(nodes.size()));
        }
      }
    } else {
      return nodes.get(manager.nextInt(nodes.size()));
    }
  }

  /**
   * Method that flattens the tree and then stores all the numNodes of the tree from a list
   *
   * @return The list of numNodes from the tree
   */
  private MutableList<Pair<TreeNode, TreeNode>> flattenNodes() {
    MutableList<Pair<TreeNode, TreeNode>> list = Lists.mutable.empty();
    collectNodes(this, null, list);
    return list;
  }

  private void collectNodes(
      TreeNode node, TreeNode parentNode, List<Pair<TreeNode, TreeNode>> list) {
    if (node == null) {
      return;
    }
    list.add(Tuples.pair(node, parentNode));
    for (TreeNode child : node.getChildren()) {
      collectNodes(child, node, list);
    }
  }

  public int depth2Node(@NonNull TreeNode node) {
    return depth2Node(this, node, 0);
  }

  private int depth2Node(@NonNull TreeNode node, @NonNull TreeNode target, int depthSoFar) {
    if (node.equals(target)) {
      return depthSoFar;
    }

    int maxDepthOfChild = -1;
    for (TreeNode childNode : node.children) {
      int d = depth2Node(childNode, target, depthSoFar + 1);
      if (d > maxDepthOfChild) {
        maxDepthOfChild = d;
      }
    }
    return maxDepthOfChild;
  }
}
