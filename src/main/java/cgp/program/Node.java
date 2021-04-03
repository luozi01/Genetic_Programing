package cgp.program;

import cgp.gp.CGPParams;
import lombok.Getter;
import lombok.Setter;

public final class Node {
    @Getter
    @Setter
    private int function;
    @Getter
    @Setter
    private boolean active;
    @Getter
    @Setter
    private double output;
    @Getter
    @Setter
    private int actArity;
    @Getter
    @Setter
    private int maxArity;
    private int[] inputs;
    private double[] weights;

    public static Node initializeNode(CGPParams params, int index) {
        Node node = new Node();
        /* allocate memory for the node's inputs and connection weights */
        node.inputs = new int[params.getArity()];
        node.weights = new double[params.getArity()];

        /* set the node's function */
        node.function = params.getRandomFunction(params.getFunctions().size());

        /* set as active by default */
        node.active = true;

        /* set the nodes inputs and connection weights */
        for (int i = 0; i < params.getArity(); i++) {
            node.inputs[i] = params.getRandomNodeInput(params.getNumInputs(), params.getNumNodes(), index);
            node.weights[i] = params.getRandomConnectionWeight();
        }

        /* set the outputNodes of the node to zero*/
        node.output = 0;

        /* set the arity of the node */
        node.maxArity = params.getArity();

        return node;
    }

    /**
     * Get input value
     *
     * @param index input index
     * @return input value
     */
    public int getInput(int index) {
        if (index < 0 || index >= this.inputs.length) {
            throw new IndexOutOfBoundsException(String.format("Expect index range within [0, %s)", this.inputs.length));
        }
        return this.inputs[index];
    }

    public void setInput(int index, int inputNodeIndex) {
        this.inputs[index] = inputNodeIndex;
    }

    /**
     * Get weight value
     *
     * @param index connection index
     * @return weight value
     */
    public double getWeight(int index) {
        if (index < 0 || index >= this.weights.length) {
            throw new IndexOutOfBoundsException(String.format("Expect index range within [0, %s)", this.weights.length));
        }
        return this.weights[index];
    }

    public double[] getWeights() {
        return this.weights.clone();
    }

    /**
     * Set connection weight
     */
    public void setWeight(int index, double weight) {
        this.weights[index] = weight;
    }

    /**
     * Clone method
     *
     * @return clone instance
     */
    public Node copy() {
        Node copy = new Node();
        // copy the node's function
        copy.function = this.function;

        // copy active flag
        copy.active = this.active;

        // copy the node arity
        copy.maxArity = this.maxArity;
        copy.actArity = this.actArity;

        // copy the nodes inputs and connection weights
        copy.inputs = this.inputs.clone();
        copy.weights = this.weights.clone();
        return copy;
    }

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
