package com.zluo.tgp.gp;

import com.zluo.ga.utils.RandEngine;
import com.zluo.tgp.enums.TGPInitializationStrategy;
import com.zluo.tgp.enums.TGPMutationStrategy;
import com.zluo.tgp.program.SyntaxTreeUtils;
import com.zluo.tgp.program.TreeNode;
import com.zluo.tgp.solver.TreeGP;

class MacroMutation {

    /**
     * Method that implements the subtree mutation or "headless chicken" crossover
     * described in Section 2.4 of "A Field Guide to Genetic Programming"
     *
     * @param treeNode node
     * @param manager  gp data
     */
    static void apply(TGPChromosome treeNode, TreeGP manager) {
        int iMaxProgramDepth = manager.maxProgramDepth;
        TGPMutationStrategy method = manager.mutationStrategy;
        RandEngine randEngine = manager.randEngine;

        TreeNode node = treeNode.getRoot().anyNode(randEngine)._1();
        if (method == TGPMutationStrategy.MUTATION_SUBTREE || method == TGPMutationStrategy.MUTATION_SUBTREE_KINNEAR) {
            int depth = treeNode.getRoot().length();

            if (method == TGPMutationStrategy.MUTATION_SUBTREE) {
                int node_depth = treeNode.getRoot().depth2Node(node);
                node.getChildren().clear();

                node.setOp(manager.getRandomOperator(randEngine));

                if (!node.isTerminal()) {
                    int max_depth = iMaxProgramDepth - node_depth;
                    SyntaxTreeUtils.createWithDepth(manager, node, max_depth,
                            TGPInitializationStrategy.INITIALIZATION_METHOD_GROW, randEngine);
                } else {
                    if (node.isVariable()) node.setVariable(manager.getRandomVar());
                    if (node.isNumber()) node.setValue(manager.getRandomValue());
                }
            } else {
                int subtree_depth = treeNode.getRoot().depth2Node(node);
                int current_depth = depth - subtree_depth;
                int max_depth = (int) (depth * 1.15) - current_depth;

                node.getChildren().clear();
                node.setOp(manager.getRandomOperator(randEngine));

                if (!node.isTerminal()) {
                    SyntaxTreeUtils.createWithDepth(manager, node, max_depth,
                            TGPInitializationStrategy.INITIALIZATION_METHOD_GROW, randEngine);
                } else {
                    if (node.isVariable()) node.setVariable(manager.getRandomVar());
                    if (node.isNumber()) node.setValue(manager.getRandomValue());
                }
            }
        } else if (method == TGPMutationStrategy.MUTATION_HOIST) {
            if (node != treeNode.getRoot()) {
                treeNode.setRoot(node);
            }
        } else if (method == TGPMutationStrategy.MUTATION_SHRINK) {
            node.getChildren().clear();
            node.setOp(manager.getRandomTerminal());

            if (node.isVariable()) node.setVariable(manager.getRandomVar());
            if (node.isNumber()) node.setValue(manager.getRandomValue());
        }
    }
}
