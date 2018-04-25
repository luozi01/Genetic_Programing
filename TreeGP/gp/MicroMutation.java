package com.zluo.tgp.gp;

import com.zluo.ga.utils.RandEngine;
import com.zluo.tgp.program.Operator;
import com.zluo.tgp.program.TreeNode;
import com.zluo.tgp.solver.TreeGP;

import java.util.ArrayList;
import java.util.List;

class MicroMutation {
    /**
     * Method that implements the "Point Mutation" described in
     * Section 2.4 of "A Field Guide to Genetic Programming"
     * In Section 5.2.2 of "A Field Guide to Genetic Programming",
     * this is also described as node replacement mutation
     *
     * @param treeNode tree node
     * @param manager  gp related data
     */
    static void apply(TGPChromosome treeNode, TreeGP manager) {
        RandEngine randEngine = manager.randEngine;
        TreeNode node = treeNode.getRoot().anyNode(randEngine)._1();

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
                if (node.isNumber()) node.setValue(manager.getRandomValue());
            }
        } else {
            List<Operator> candidates = new ArrayList<>();
            for (Operator op : manager.getNonTerminal()) {
                if (op != node.getOp() && op.argumentCount() == node.argumentCount()) candidates.add(op);
            }
            if (!candidates.isEmpty()) node.setOp(candidates.get(randEngine.nextInt(candidates.size())));
        }
    }
}
