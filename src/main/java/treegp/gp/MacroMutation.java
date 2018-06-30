package treegp.gp;

import genetics.chromosome.Chromosome;
import genetics.interfaces.MutationPolicy;
import genetics.utils.RandEngine;
import treegp.enums.TGPInitializationStrategy;
import treegp.enums.TGPMutationStrategy;
import treegp.program.SyntaxTreeUtils;
import treegp.program.TreeNode;
import treegp.solver.TreeGP;

public class MacroMutation implements MutationPolicy {


    private final TreeGP manager;

    public MacroMutation(TreeGP manager) {
        this.manager = manager;
    }

    /**
     * Method that implements the subtree mutation or "headless chicken" crossover
     * described in Section 2.4 of "A Field Guide to Genetic Programming"
     *
     * @param treeNode node
     */
    private Chromosome apply(TGPChromosome treeNode) {
        int iMaxProgramDepth = manager.getMaxProgramDepth();
        TGPMutationStrategy method = manager.getMutationStrategy();
        RandEngine randEngine = manager.getRandEngine();

        TreeNode node = treeNode.getRoot().anyNode(randEngine).getOne();
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
        return treeNode;
    }

    @Override
    public Chromosome mutate(Chromosome chromosome) {
        if (!(chromosome instanceof TGPChromosome)) {
            throw new IllegalArgumentException("Chromosome should be TGPChromosome");
        }
        return apply(((TGPChromosome) chromosome).makeCopy());
    }
}
