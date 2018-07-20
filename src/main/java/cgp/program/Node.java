package cgp.program;

public final class Node {
    public int function;
    public int[] inputs;
    public double[] weights;
    public boolean active;
    public double output;
    public int actArity;
    public int maxArity;

    /**
     * copy the contents for the src node into dest node.
     */
    public void copyNode(Node nodeSrc) {
        // copy the node's function
        this.function = nodeSrc.function;

        // copy active flag
        this.active = nodeSrc.active;

        // copy the node arity
        this.maxArity = nodeSrc.maxArity;
        this.actArity = nodeSrc.actArity;

        // copy the nodes inputs and connection weights
        for (int i = 0; i < nodeSrc.maxArity; i++) {
            this.inputs[i] = nodeSrc.inputs[i];
            this.weights[i] = nodeSrc.weights[i];
        }
    }
}
