package com.zluo.tgp.program;

import com.zluo.ga.utils.RandEngine;
import com.zluo.tgp.enums.TGPInitializationStrategy;
import com.zluo.tgp.solver.TreeGP;

import java.util.List;

public class SyntaxTreeUtils {

    /**
     * Method that creates a subtree of maximum depth
     *
     * @param context        related data
     * @param treeNode       v
     * @param allowableDepth The maximum depth
     * @param method         The method used to build the subtree
     * @param randEngine     randomized engine
     */
    public static void createWithDepth(TreeGP context, TreeNode treeNode,
                                       int allowableDepth, TGPInitializationStrategy method, RandEngine randEngine) {

        int child_count = treeNode.argumentCount();

        for (int i = 0; i != child_count; ++i) {
            Operator op = randomOperator(context, allowableDepth, method, randEngine);
            TreeNode child = new TreeNode(op, context);
            treeNode.addChildren(child);

            if (!child.isTerminal()) {
                createWithDepth(context, child, allowableDepth - 1, method, randEngine);
            }
        }
    }

    /**
     * Method that creates a GP tree with a maximum tree depth
     *
     * @param context        related data
     * @param allowableDepth maximum depth of the tree
     * @param manager        GP related data
     * @param method         The method used to build the subtree
     * @return new subtree
     */
    public static TreeNode createWithDepth(TreeGP context, int allowableDepth, TreeGP manager, TGPInitializationStrategy method) {
        List<Operator> operatorSet = context.getNonTerminal();
        RandEngine randEngine = manager.randEngine;

        TreeNode root;
        // Population Initialization method following the "RandomBranch" method described in
        // "Kumar Chellapilla. Evolving computer programs without subtree crossover.
        // IEEE Transactions on Evolutionary Computation, 1(3):209–216, September 1997."
        if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_RANDOM_BRANCH) {
            int s = allowableDepth; //tree size
            Operator non_terminal = context.anyOperatorWithArityLessThan(s, randEngine);
            if (non_terminal == null) {
                root = new TreeNode(context.getRandomTerminal(), context);
            } else {
                root = new TreeNode(non_terminal, context);
                int b_n = non_terminal.argumentCount();
                s = (int) Math.floor((double) s / b_n);
                randomBranch(context, root, s, randEngine);
            }
        }
        // Population Initialization method following the "PTC1" method described in "Sean Luke.
        // Two fast tree-creation algorithms for genetic programming.
        // IEEE Transactions in Evolutionary Computation, 4(3), 2000b."
        else if (method == TGPInitializationStrategy.INITIALIZATION_METHOD_PTC1) {
            // TODO: Change this one later back to use tag
            int expectedTreeSize = 20; //Convert.ToInt32(tag);

            int b_n_sum = 0;
            for (Operator anOperatorSet : operatorSet) {
                b_n_sum += anOperatorSet.argumentCount();
            }
            double p = (1 - 1.0 / expectedTreeSize) / ((double) b_n_sum / operatorSet.size());

            Operator op;
            if (randEngine.uniform() <= p) {
                op = context.getRandomNonTerminal();
            } else {
                op = context.getRandomTerminal();
            }

            root = new TreeNode(op, context);
            PTC1(context, root, p, allowableDepth - 1, randEngine);
        } else { // handle full and grow method
            root = new TreeNode(randomOperator(context, allowableDepth, method, randEngine), context);
            createWithDepth(context, root, allowableDepth - 1, method, randEngine);
        }
        return root;
    }

    /**
     * Population Initialization Method described in "Kumar Chellapilla.
     * Evolving computer programs without subtree crossover.
     * IEEE Transactions on Evolutionary Computation, 1(3):209–216, September 1997."
     *
     * @param context    related data
     * @param treeNode   parent_node
     * @param s
     * @param randEngine randomize engine
     */
    private static void randomBranch(TreeGP context, TreeNode treeNode, int s, RandEngine randEngine) {
        int child_count = treeNode.argumentCount();

        for (int i = 0; i != child_count; i++) {
            Operator non_terminal = context.anyOperatorWithArityLessThan(s, randEngine);
            if (non_terminal == null) {
                TreeNode child = new TreeNode(context.getRandomTerminal(), context);
                treeNode.getChildren().add(child);
            } else {
                TreeNode child = new TreeNode(non_terminal, context);
                treeNode.getChildren().add(child);
                int b_n = non_terminal.argumentCount();
                int s_pi = (int) Math.floor((double) s / b_n);
                randomBranch(context, child, s_pi, randEngine);
            }
        }
    }

    /**
     * Population Initialization method following the "PTC1" method described in
     * "Sean Luke.Two fast tree-creation algorithms for genetic programming.
     * IEEE Transactions in Evolutionary Computation, 4(3), 2000b."
     *
     * @param context        related data
     * @param parent_node    The node for which the child nodes are generated in this method
     * @param p              expected probability
     * @param allowableDepth The maximum tree depth
     * @param randEngine     randomize engine
     */
    private static void PTC1(TreeGP context, TreeNode parent_node, double p, int allowableDepth, RandEngine randEngine) {
        int child_count = parent_node.argumentCount();

        for (int i = 0; i != child_count; i++) {
            Operator data;
            if (allowableDepth == 0) {
                data = context.getRandomTerminal();
            } else if (randEngine.uniform() <= p) {
                data = context.getRandomNonTerminal();
            } else {
                data = context.getRandomTerminal();
            }

            TreeNode child = new TreeNode(data, context);
            parent_node.getChildren().add(child);

            if (data.argumentCount() != 0) {
                PTC1(context, child, p, allowableDepth - 1, randEngine);
            }
        }
    }

    private static Operator randomOperator(TreeGP context, int allowableDepth,
                                           TGPInitializationStrategy method, RandEngine randEngine) {
        //Todo fix, right now does not have function without arguments such as rand()
        int terminal_count = context.getTerminal().size();
        int function_count = context.getNonTerminal().size();

        double terminal_prob = (double) terminal_count / (terminal_count + function_count);
        if (allowableDepth <= 0 || (method == TGPInitializationStrategy.INITIALIZATION_METHOD_GROW
                && randEngine.uniform() <= terminal_prob)) {
            return context.getRandomTerminal();
        } else {
            return context.getRandomNonTerminal();
        }
    }
}
