package com.zluo.tgp.gp;

import com.zluo.ga.utils.RandEngine;
import com.zluo.tgp.enums.TGPCrossoverStrategy;
import com.zluo.tgp.program.TreeNode;
import com.zluo.tgp.solver.TreeGP;
import com.zluo.tgp.tools.Pair;

import java.util.ArrayList;
import java.util.List;

class Crossover {

    /**
     * Method that implements the subtree crossover described in Section 2.4 of "A Field Guide to Genetic Programming"
     *
     * @param chromosome1 One tree to be crossover with
     * @param chromosome2 Another tree to be crossover with
     * @param manager     GP data
     */
    static void apply(TGPChromosome chromosome1, TGPChromosome chromosome2, TreeGP manager) {
        int iMaxDepthForCrossover = manager.maxDepthForCrossover;
        TGPCrossoverStrategy method = manager.crossoverStrategy;

        RandEngine randEngine = manager.randEngine;
        boolean bias = (method == TGPCrossoverStrategy.CROSSOVER_SUBTREE_BIAS);

        int iMaxDepth1 = chromosome1.getRoot().depth();
        int iMaxDepth2 = chromosome2.getRoot().depth();

        Pair<TreeNode> pCutPoint1, pCutPoint2;

        boolean is_crossover_performed = false;
        // Suppose that at the beginning both the current tree and the other tree do not violate max depth constraint
        // then try to see whether a crossover can be performed in such a way that after the crossover,
        // both GP still have depth <= max depth
        if (iMaxDepth1 <= iMaxDepthForCrossover && iMaxDepth2 <= iMaxDepthForCrossover) {
            int max_trials = 50;
            int trials = 0;
            do {
                pCutPoint1 = chromosome1.getRoot().anyNode(bias, randEngine);
                pCutPoint2 = chromosome2.getRoot().anyNode(bias, randEngine);


                if (pCutPoint1 != null && pCutPoint2 != null) {
                    Pair<Pair<TreeNode>> result = swap(pCutPoint1, pCutPoint2);

                    iMaxDepth1 = chromosome1.getRoot().depth();
                    iMaxDepth2 = chromosome2.getRoot().depth();

                    //crossover is successful
                    if (iMaxDepth1 <= iMaxDepthForCrossover && iMaxDepth2 <= iMaxDepthForCrossover) {
                        is_crossover_performed = true;
                        break;
                    } else {
                        Pair<TreeNode> newCutPoint1 = result._1();
                        Pair<TreeNode> newCutPoint2 = result._2();

                        // swap back so as to restore to the original GP trees
                        // if the crossover is not valid due to max depth violation
                        swap(newCutPoint1, newCutPoint2);
                    }
                }
                trials++;
            } while (trials < max_trials);
        }

        // force at least one crossover even if the maximum depth is violated above
        // so that this operator won't end up like a reproduction operator
        if (!is_crossover_performed) {
            pCutPoint1 = chromosome1.getRoot().anyNode(bias, randEngine);
            pCutPoint2 = chromosome2.getRoot().anyNode(bias, randEngine);

            if (pCutPoint1 != null && pCutPoint2 != null) {
                swap(pCutPoint1, pCutPoint2);
            }
        }
    }

    private static Pair<Pair<TreeNode>> swap(Pair<TreeNode> cutPoint1, Pair<TreeNode> cutPoint2) {
        TreeNode parent1 = cutPoint1._2();
        TreeNode parent2 = cutPoint2._2();

        TreeNode point1 = cutPoint1._1();
        TreeNode point2 = cutPoint2._1();

        //if either of the point is a root
        if (parent1 == null || parent2 == null) {
            TreeNode content1 = point1.copy();

            point1.setOp(point2.getOp());
            point1.setValue(point2.getValue());
            point1.setVariable(point2.getVariable());

            point2.setOp(content1.getOp());
            point2.setValue(content1.getValue());
            point2.setVariable(content1.getVariable());

            List<TreeNode> children1 = new ArrayList<>(point1.getChildren());
            List<TreeNode> children2 = new ArrayList<>(point2.getChildren());
            point1.getChildren().clear();
            point2.getChildren().clear();
            for (TreeNode aChildren1 : children1) {
                point2.getChildren().add(aChildren1.copy());
            }
            for (TreeNode aChildren2 : children2) {
                point1.getChildren().add(aChildren2.copy());
            }
            return new Pair<>(cutPoint1, cutPoint2);
        } else {
            int child_index1 = parent1.getChildren().indexOf(point1);
            int child_index2 = parent2.getChildren().indexOf(point2);

            TreeNode newChild1 = point2.copy();
            TreeNode newChild2 = point1.copy();

            parent1.getChildren().set(child_index1, newChild1);
            parent2.getChildren().set(child_index2, newChild2);

            return new Pair<>(new Pair<>(newChild1, parent1), new Pair<>(newChild2, parent2));
        }
    }
}
