package tgp.program;

public enum Type implements Operator {
  CONSTANT {

    @Override
    public int argumentCount() {
      return 0;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return true;
    }

    @Override
    public String toString(TreeNode treeNode) {
      double retVal = treeNode.getValue();
      return retVal < 0 ? String.format("(%s)", retVal) : "" + retVal;
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      return treeNode.getValue();
    }
  },
  VARIABLE {

    @Override
    public int argumentCount() {
      return 0;
    }

    @Override
    public boolean isVariable() {
      return true;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("v[%d]", treeNode.getVariable());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      return inputs[treeNode.getVariable()];
    }
  },
  ADD {

    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(%s + %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double left = treeNode.getLeft().eval(inputs);
      double right = treeNode.getRight().eval(inputs);
      return left + right;
    }
  },
  SUB {

    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(%s - %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double left = treeNode.getLeft().eval(inputs);
      double right = treeNode.getRight().eval(inputs);
      return left - right;
    }
  },
  MUL {

    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(%s * %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double left = treeNode.getLeft().eval(inputs);
      double right = treeNode.getRight().eval(inputs);
      return left * right;
    }
  },
  DIV {

    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    public String toString(TreeNode treeNode) {
      return String.format("(%s / %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double left = treeNode.getLeft().eval(inputs);
      double right = treeNode.getRight().eval(inputs);
      return right == 0 ? Double.MAX_VALUE : left / right;
    }
  },
  POW {

    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(%s ^ %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double left = treeNode.getLeft().eval(inputs);
      double right = treeNode.getRight().eval(inputs);
      return Math.pow(left, right);
    }
  },
  SQRT {

    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    public String toString(TreeNode treeNode) {
      return String.format("sqrt(%s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double arg = treeNode.getLeft().eval(inputs);
      return arg < 0 ? Double.MAX_VALUE : Math.sqrt(arg);
    }
  },
  LN {
    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("ln(%s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double arg = treeNode.getLeft().eval(inputs);
      return arg < 0 ? Double.MAX_VALUE : Math.log(arg + TOLERANCE);
    }
  },
  SIN {

    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("sin(%s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double arg = treeNode.getLeft().eval(inputs);
      return Math.sin(arg);
    }
  },
  COS {

    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    public String toString(TreeNode treeNode) {
      return String.format("cos(%s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double arg = treeNode.getLeft().eval(inputs);
      return Math.cos(arg);
    }
  },

  TAN {

    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("tan(%s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double arg = treeNode.getLeft().eval(inputs);
      return Math.tan(arg);
    }
  },

  AND {
    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(%s AND %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      return (isTrue(treeNode.getLeft().eval(inputs)) && isTrue(treeNode.getRight().eval(inputs)))
          ? 1
          : 0;
    }
  },

  EXP {

    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("exp(%s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      return Math.exp(treeNode.getLeft().eval(inputs));
    }
  },

  MOD {

    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return "(" + treeNode.getLeft() + " % " + treeNode.getRight() + ")";
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      double left = treeNode.getLeft().eval(inputs);
      double right = treeNode.getRight().eval(inputs);
      return right == 0 ? Double.MAX_VALUE : left % right;
    }
  },

  NOT {
    @Override
    public int argumentCount() {
      return 1;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(NOT %s)", treeNode.getLeft());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      return isTrue(treeNode.getLeft().eval(inputs)) ? 1 : 0;
    }
  },

  OR {
    @Override
    public int argumentCount() {
      return 2;
    }

    @Override
    public boolean isVariable() {
      return false;
    }

    @Override
    public boolean isNumber() {
      return false;
    }

    @Override
    public String toString(TreeNode treeNode) {
      return String.format("(%s OR %s)", treeNode.getLeft(), treeNode.getRight());
    }

    @Override
    public double eval(TreeNode treeNode, double[] inputs) {
      return (isTrue(treeNode.getLeft().eval(inputs)) || isTrue(treeNode.getRight().eval(inputs)))
          ? 1
          : 0;
    }
  };

  private static final double TOLERANCE = 1e-5;

  private static boolean isTrue(double x) {
    return !(x > -TOLERANCE) || !(x < TOLERANCE);
  }
}
