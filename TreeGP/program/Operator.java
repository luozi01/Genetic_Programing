package com.zluo.tgp.program;

public interface Operator {

    int argumentCount();

    boolean isVariable();

    boolean isNumber();

    String toString(TreeNode treeNode);

    double eval(TreeNode treeNode);
}
