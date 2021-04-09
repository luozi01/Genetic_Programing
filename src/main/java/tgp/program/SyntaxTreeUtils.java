package tgp.program;

import tgp.enums.TGPInitializationStrategy;
import tgp.gp.TGPParams;

import java.util.List;

public class SyntaxTreeUtils {

  /**
   * Method that creates a subtree of maximum depth
   *
   * @param context related data
   * @param treeNode v
   * @param allowableDepth The maximum depth
   * @param method The method used to build the subtree
   */
  public static void createWithDepth(
      TGPParams context, TreeNode treeNode, int allowableDepth, TGPInitializationStrategy method) {
    int childCount = treeNode.argumentCount();

    for (int i = 0; i != childCount; ++i) {
      Operator op = randomOperator(context, allowableDepth, method);
      TreeNode child = new TreeNode(op, context);
      treeNode.addChildren(child);

      if (!child.isTerminal()) {
        createWithDepth(context, child, allowableDepth - 1, method);
      }
    }
  }

  /**
   * Method that creates a GP tree with a maximum tree depth
   *
   * @param context related data
   * @param allowableDepth maximum depth of the tree
   * @param method The method used to build the subtree
   * @return new subtree
   */
  public static TreeNode createWithDepth(
      TGPParams context, int allowableDepth, TGPInitializationStrategy method) {
    List<Operator> operatorSet = context.getNonTerminal();

    TreeNode root;
    // Population Initialization method following the "RandomBranch" method described from
    // "Kumar Chellapilla. Evolving computer programs without subtree crossover.
    // IEEE Transactions on Evolutionary Computation, 1(3):209–216, September 1997."
    if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_RANDOM_BRANCH) {
      int s = allowableDepth; // tree size
      Operator nonTerminal = context.anyOperatorWithArityLessThan(s);
      if (nonTerminal == null) {
        root = new TreeNode(context.getRandomTerminal(), context);
      } else {
        root = new TreeNode(nonTerminal, context);
        int b_n = nonTerminal.argumentCount();
        s = (int) Math.floor((double) s / b_n);
        randomBranch(context, root, s);
      }
    }
    // Population Initialization method following the "PTC1" method described from "Sean Luke.
    // Two fast tree-creation algorithms for genetic programming.
    // IEEE Transactions from Evolutionary Computation, 4(3), 2000b."
    else if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_PTC1) {
      int expectedTreeSize = 20;

      int b_n_sum = 0;
      for (Operator anOperatorSet : operatorSet) {
        b_n_sum += anOperatorSet.argumentCount();
      }
      double p = (1 - 1.0 / expectedTreeSize) / ((double) b_n_sum / operatorSet.size());

      Operator op;
      if (context.uniform() <= p) {
        op = context.getRandomNonTerminal();
      } else {
        op = context.getRandomTerminal();
      }

      root = new TreeNode(op, context);
      PTC1(context, root, p, allowableDepth - 1);
    } else { // handle full and grow method
      root = new TreeNode(randomOperator(context, allowableDepth, method), context);
      createWithDepth(context, root, allowableDepth - 1, method);
    }
    return root;
  }

  /**
   * Population Initialization Method described from "Kumar Chellapilla. Evolving computer programs
   * without subtree crossover. IEEE Transactions on Evolutionary Computation, 1(3):209–216,
   * September 1997."
   *
   * @param context related data
   * @param treeNode parent_node
   * @param s max operator arity
   */
  private static void randomBranch(TGPParams context, TreeNode treeNode, int s) {
    int childCount = treeNode.argumentCount();

    for (int i = 0; i != childCount; i++) {
      Operator nonTerminal = context.anyOperatorWithArityLessThan(s);
      if (nonTerminal == null) {
        TreeNode child = new TreeNode(context.getRandomTerminal(), context);
        treeNode.getChildren().add(child);
      } else {
        TreeNode child = new TreeNode(nonTerminal, context);
        treeNode.getChildren().add(child);
        int b_n = nonTerminal.argumentCount();
        int s_pi = (int) Math.floor((double) s / b_n);
        randomBranch(context, child, s_pi);
      }
    }
  }

  /**
   * Population Initialization method following the "PTC1" method described from "Sean Luke.Two fast
   * tree-creation algorithms for genetic programming. IEEE Transactions from Evolutionary
   * Computation, 4(3), 2000b."
   *
   * @param context related data
   * @param parentNode The node for which the child numNodes are generated from this method
   * @param p expected probability
   * @param allowableDepth The maximum tree depth
   */
  private static void PTC1(TGPParams context, TreeNode parentNode, double p, int allowableDepth) {
    int childCount = parentNode.argumentCount();

    for (int i = 0; i != childCount; i++) {
      Operator data;
      if (allowableDepth == 0) {
        data = context.getRandomTerminal();
      } else if (context.uniform() <= p) {
        data = context.getRandomNonTerminal();
      } else {
        data = context.getRandomTerminal();
      }

      TreeNode child = new TreeNode(data, context);
      parentNode.getChildren().add(child);

      if (data.argumentCount() != 0) {
        PTC1(context, child, p, allowableDepth - 1);
      }
    }
  }

  private static Operator randomOperator(
      TGPParams context, int allowableDepth, TGPInitializationStrategy method) {
    int terminalCount = context.getTerminal().size();
    int functionCount = context.getNonTerminal().size();

    double terminalProb = (double) terminalCount / (terminalCount + functionCount);
    if (allowableDepth <= 0
        || (method == TGPInitializationStrategy.INITIALIZATION_METHOD_GROW
            && context.uniform() <= terminalProb)) {
      return context.getRandomTerminal();
    } else {
      return context.getRandomNonTerminal();
    }
  }
}