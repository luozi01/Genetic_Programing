package com.zluo.tgp.program;

import com.zluo.ga.utils.RandEngine;
import com.zluo.tgp.solver.TreeGP;
import com.zluo.tgp.tools.Pair;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TreeNode implements Serializable {
    private static final long serialVersionUID = 350057284330815219L;

    private Operator op;
    private String variable;
    private double value = 0;
    private TreeGP manager;
    private List<TreeNode> children = new ArrayList<>(2);

    TreeNode(Operator op, TreeGP manager) {
        this.op = op;
        this.manager = manager;
        if (op.isNumber()) value = manager.getRandomValue();
        if (op.isVariable()) variable = manager.getRandomVar();
    }

    private int depth(TreeNode node, int depthSoFar) {
        int maxDepth = depthSoFar;
        for (TreeNode child : node.children) {
            int depth = depth(child, depthSoFar + 1);
            maxDepth = Math.max(maxDepth, depth);
        }
        return maxDepth;
    }

    public double eval() {
        return op.eval(this);
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
        if (children.size() <= 1) return null;
        return children.get(1);
    }

    TreeNode getLeft() {
        if (children.isEmpty()) return null;
        return children.get(0);
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

    public Pair<TreeNode> anyNode(RandEngine randEngine) {
        return anyNode(false, randEngine);
    }

    /**
     * Method that returns a randomly selected node from the current tree
     * The tree is first flatten into a list from which a node is randomly selected
     *
     * @param bias       bias or not
     * @param randEngine randomize engine
     * @return new subtree
     */
    public Pair<TreeNode> anyNode(boolean bias, RandEngine randEngine) {
        List<Pair<TreeNode>> nodes = flattenNodes();
        if (bias) {
            // As specified by Koza, 90% select function node, 10% select terminal node
            if (randEngine.uniform() <= 0.1) {
                List<Pair<TreeNode>> terminal_nodes = new ArrayList<>();
                for (Pair<TreeNode> tuple : nodes) {
                    TreeNode node = tuple._1();
                    if (node.isTerminal()) {
                        terminal_nodes.add(tuple);
                    }
                }
                if (terminal_nodes.size() > 0) {
                    return terminal_nodes.get(randEngine.nextInt(terminal_nodes.size()));
                } else {
                    return nodes.get(randEngine.nextInt(nodes.size()));
                }
            } else {
                List<Pair<TreeNode>> function_nodes = new ArrayList<>();
                for (Pair<TreeNode> tuple : nodes) {
                    TreeNode node = tuple._1();
                    if (!node.isTerminal()) {
                        function_nodes.add(tuple);
                    }
                }
                if (function_nodes.size() > 0) {
                    return function_nodes.get(randEngine.nextInt(function_nodes.size()));
                } else {
                    return nodes.get(randEngine.nextInt(nodes.size()));
                }
            }
        } else {
            return nodes.get(randEngine.nextInt(nodes.size()));
        }
    }

    /**
     * Method that flattens the tree and then stores all the nodes of the tree in a list
     *
     * @return The list of nodes in the tree
     */
    private List<Pair<TreeNode>> flattenNodes() {
        List<Pair<TreeNode>> list = new ArrayList<>();
        collectNodes(this, null, list);
        return list;
    }

    private void collectNodes(TreeNode node, TreeNode parent_node, List<Pair<TreeNode>> list) {
        if (node == null) return;
        assert parent_node == null || parent_node.getChildren().contains(node);
        list.add(new Pair<>(node, parent_node));
        for (TreeNode child : node.getChildren()) {
            collectNodes(child, node, list);
        }
    }

    public boolean isTerminal() {
        return argumentCount() == 0;
    }

    public int depth2Node(TreeNode node) {
        return depth2Node(this, node, 0);
    }

    private int depth2Node(TreeNode node, TreeNode target, int depthSoFar) {
        if (node == target) {
            return depthSoFar;
        }

        int maxDepthOfChild = -1;
        for (TreeNode child_node : node.children) {
            int d = depth2Node(child_node, target, depthSoFar + 1);
            if (d > maxDepthOfChild) {
                maxDepthOfChild = d;
            }
        }
        return maxDepthOfChild;
    }
}
