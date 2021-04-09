package cgp.gp;

import cgp.interfaces.CGPFunction;
import cgp.program.Node;
import genetics.chromosome.Chromosome;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.collections.api.list.MutableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

@NoArgsConstructor
public class CGPChromosome extends Chromosome {
  @Getter @Setter private int numInputs;

  @Getter @Setter private int numOutputs;

  @Getter @Setter private int numNodes;

  @Getter @Setter private int numActiveNodes;

  @Getter @Setter private int arity;

  @Getter @Setter private int generation;

  @Getter private MutableList<CGPFunction> funcSet;

  private Node[] nodes;
  private int[] outputNodes;
  private int[] activeNodes;
  private double[] outputValues;
  private double[] nodeInputsHold;

  public CGPChromosome(CGPParams params) {
    this.numNodes = params.getNumNodes();
    this.numActiveNodes = params.getNumNodes();
    this.numInputs = params.getNumInputs();
    this.numOutputs = params.getNumOutputs();
    this.arity = params.getArity();

    this.nodes = new Node[this.numNodes];
    this.activeNodes = new int[this.numNodes];

    this.outputNodes = new int[this.numOutputs];
    this.outputValues = new double[this.numOutputs];

    this.nodeInputsHold = new double[this.arity];

    this.funcSet = params.getFunctions().clone();
  }

  /**
   * Initialize CGP chromosome
   *
   * @param params hyper parameters
   * @return CGP chromosome
   */
  public static CGPChromosome initializeChromosome(CGPParams params) {
    if (params.getFunctions().isEmpty()) {
      throw new IllegalArgumentException("Chromosome not initialised due to empty functionSet.");
    }
    CGPChromosome chromosome = new CGPChromosome(params);

    for (int i = 0; i < params.getNumNodes(); i++) {
      chromosome.setNode(i, Node.initializeNode(params, i));
    }
    for (int i = 0; i < params.getNumOutputs(); i++) {
      chromosome.setOutputNode(
          i, params.getRandomChromosomeOutput(params.getNumInputs(), params.getNumNodes()));
    }

    chromosome.setChromosomeActiveNodes();
    return chromosome;
  }

  /**
   * reconstruct chromosome from serialize string representation
   *
   * @param serialize serialize representation of a chromosome
   * @return chromosome
   */
  public static CGPChromosome reconstruct(String serialize) {
    String[] line;

    /* open the chromosome file */
    @Cleanup Scanner scanner = new Scanner(serialize);

    /* get num inputs */
    line = scanner.nextLine().split(",");
    int numInputs = Integer.parseInt(line[1]);

    /* get num nodes */
    line = scanner.nextLine().split(",");
    int numNodes = Integer.parseInt(line[1]);

    /* get num outputs */
    line = scanner.nextLine().split(",");
    int numOutputs = Integer.parseInt(line[1]);

    /* get arity */
    line = scanner.nextLine().split(",");
    int arity = Integer.parseInt(line[1]);

    /* initialise parameters  */
    CGPParams params = CGPParams.initialiseParameters(numInputs, numNodes, numOutputs, arity);

    /* for each function name */
    line = scanner.nextLine().split(",");
    for (int k = 0; k < line.length - 1; k++) {
      params.addPresetFunctionToFunctionSet(line[k + 1]);
    }

    /* initialise a chromosome based on the parameters associated with given chromosome */
    CGPChromosome chromosome = initializeChromosome(params);

    /* set the node parameters */
    for (int i = 0; i < numNodes; i++) {
      /* get the function gene */
      chromosome.nodes[i].setFunction(scanner.nextInt());

      scanner.nextLine();
      for (int j = 0; j < arity; j++) {
        line = scanner.nextLine().split(",");
        chromosome.nodes[i].setInput(j, Integer.parseInt(line[0]));
        chromosome.nodes[i].setWeight(j, Double.parseDouble(line[1]));
      }
    }

    line = scanner.nextLine().split(",");
    /* set the outputs */
    for (int i = 0; i < numOutputs; i++) {
      chromosome.outputNodes[i] = Integer.parseInt(line[i]);
    }
    /* set the active nodes in the copied chromosome */
    chromosome.setChromosomeActiveNodes();
    return chromosome;
  }

  public static CGPChromosome deserialization(String fileName) throws FileNotFoundException {
    File file = new File(fileName);
    StringBuilder serial = new StringBuilder();
    @Cleanup Scanner scanner = new Scanner(file);
    while (scanner.hasNextLine()) {
      serial.append(scanner.nextLine()).append("\n");
    }

    if (serial.length() == 0) {
      throw new IllegalStateException("File is empty, cannot reconstruct chromosome from it");
    }
    return reconstruct(serial.toString());
  }

  public void removeInactiveNodes() {
    /* set the active nodes */
    setChromosomeActiveNodes();

    /* for all nodes */
    for (int i = 0; i < this.numNodes - 1; i++) {
      /* if the node is inactive */
      if (!this.nodes[i].isActive()) {
        /* set the node to be the next node */
        for (int j = i; j < this.numNodes - 1; j++) {
          this.nodes[j].copyNode(this.nodes[j + 1]);
        }

        /* */
        for (int j = 0; j < this.numNodes; j++) {
          for (int k = 0; k < this.arity; k++) {
            if (this.nodes[j].getInput(k) >= i + this.numInputs) {
              this.nodes[j].setInput(k, this.nodes[j].getInput(k) - 1);
            }
          }
        }

        /* for the number of chromosome outputs */
        for (int j = 0; j < this.numOutputs; j++) {
          if (this.outputNodes[j] >= i + this.numInputs) {
            this.outputNodes[j]--;
          }
        }

        /* de-increment the number of nodes */
        this.numNodes--;

        /* made the newly assigned node be evaluated */
        i--;
      }
    }

    if (!this.nodes[this.numNodes - 1].isActive()) {
      this.numNodes--;
    }

    /* set the active nodes */
    setChromosomeActiveNodes();
  }

  public void updateFitness(double value) {
    this.setChromosomeActiveNodes();
    this.resetChromosome();
    this.setFitness(value);
  }

  /** Update active nodes based on current connection */
  private void setChromosomeActiveNodes() {
    /* set the number of active nodes to zero */
    this.numActiveNodes = 0;

    /* reset the active nodes */
    for (int i = 0; i < this.numNodes; i++) {
      nodes[i].setActive(false);
    }

    /* start the recursive search for active nodes from the outputNodes nodes for the number of outputNodes nodes */
    for (int i = 0; i < this.numOutputs; i++) {
      /* if the outputNodes connects to a chromosome input, skip */
      if (this.outputNodes[i] < this.numInputs) {
        continue;
      }

      /* begin a recursive search for active nodes */
      recursivelySetActiveNodes(this, this.outputNodes[i]);
    }

    /* place active nodes in order */
    Arrays.sort(activeNodes, 0, numActiveNodes);
  }

  /** used by setActiveNodes to recursively search for active nodes */
  private void recursivelySetActiveNodes(CGPChromosome chromosome, int nodeIndex) {
    /* if the given node is an input, stop */
    if (nodeIndex < chromosome.numInputs) {
      return;
    }

    /* if the given node has already been flagged as active */
    if (chromosome.isNodeActive(nodeIndex - chromosome.numInputs)) {
      return;
    }

    /* log the node as active */
    chromosome.nodes[nodeIndex - chromosome.numInputs].setActive(true);
    chromosome.activeNodes[chromosome.numActiveNodes] = nodeIndex - chromosome.numInputs;
    chromosome.numActiveNodes++;

    /* set the nodes actual arity*/
    chromosome.nodes[nodeIndex - chromosome.numInputs].setActArity(
        chromosome.getChromosomeNodeArity(nodeIndex - chromosome.numInputs));

    /* recursively log all the nodes to which the current nodes connect as active */
    for (int i = 0; i < chromosome.nodes[nodeIndex - chromosome.numInputs].getActArity(); i++) {
      recursivelySetActiveNodes(
          chromosome, chromosome.nodes[nodeIndex - chromosome.numInputs].getInput(i));
    }
  }

  public void mutate(CGPParams params) {
    this.copyChromosome(params.getMutationPolicy().mutate(this));
    this.setChromosomeActiveNodes();
  }

  /**
   * Evaluate chromosome
   *
   * @param inputs input data
   */
  public void evaluate(double[] inputs) {
    /* for all of the active nodes */
    for (int i = 0; i < this.numActiveNodes; i++) {
      /* get the index of the current active node */
      int currentActiveNode = this.activeNodes[i];

      /* get the arity of the current node */
      int nodeArity = this.nodes[currentActiveNode].getActArity();

      /* for each of the active nodes inputs */
      for (int j = 0; j < nodeArity; j++) {
        /* gather the nodes input locations */
        int nodeInputLocation = this.nodes[currentActiveNode].getInput(j);

        if (nodeInputLocation < numInputs) {
          this.nodeInputsHold[j] = inputs[nodeInputLocation];
        } else {
          this.nodeInputsHold[j] = this.nodes[nodeInputLocation - numInputs].getOutput();
        }
      }

      /* get the functionality of the active node under evaluation */
      int currentActiveNodeFunction = this.nodes[currentActiveNode].getFunction();

      /* calculate the outputNodes of the active node under evaluation */
      this.nodes[currentActiveNode].setOutput(
          this.funcSet
              .get(currentActiveNodeFunction)
              .calc(nodeArity, this.nodeInputsHold, this.nodes[currentActiveNode].getWeights()));

      /* deal with doubles becoming NAN */
      if (Double.isNaN(this.nodes[currentActiveNode].getOutput())) {
        this.nodes[currentActiveNode].setOutput(0);
      } else if (Double.isInfinite(this.nodes[currentActiveNode].getOutput())) {
        this.nodes[currentActiveNode].setOutput(
            this.nodes[currentActiveNode].getOutput() > 0 ? Double.MAX_VALUE : Double.MIN_VALUE);
      }
    }

    /* Set the chromosome outputs */
    for (int i = 0; i < numOutputs; i++) {
      if (this.outputNodes[i] < numInputs) {
        this.outputValues[i] = inputs[this.outputNodes[i]];
      } else {
        this.outputValues[i] = this.nodes[this.outputNodes[i] - numInputs].getOutput();
      }
    }
  }

  public void mutate(CGPParams params, int i) {
    for (int j = 0; j < params.getArity(); j++) {
      /* mutate the node input */
      if (params.uniform() <= params.getMutationRate()) {
        this.nodes[i].setInput(j, params.getRandomNodeInput(this.numInputs, this.numNodes, i));
      }

      /* mutate the node connection weight */
      if (params.uniform() <= params.getMutationRate()) {
        this.nodes[i].setWeight(j, params.getRandomConnectionWeight());
      }
    }
  }

  public String serialization() {
    StringBuilder sb = new StringBuilder();

    /* save meta information */
    sb.append(String.format("numInputs,%d\n", numInputs));
    sb.append(String.format("numNodes,%d\n", numNodes));
    sb.append(String.format("numOutputs,%d\n", numOutputs));
    sb.append(String.format("arity,%d\n", arity));

    sb.append("FunctionSet");
    for (int i = 0; i < funcSet.size(); i++) {
      sb.append(String.format(",%s", funcSet.get(i)));
    }
    sb.append("\n");

    /* save the chromosome structure */
    for (int i = 0; i < numNodes; i++) {
      sb.append(String.format("%d\n", nodes[i].getFunction()));
      for (int j = 0; j < arity; j++) {
        sb.append(String.format("%d,%f\n", nodes[i].getInput(j), nodes[i].getWeight(j)));
      }
    }

    for (int i = 0; i < numOutputs; i++) {
      sb.append(String.format("%d,", outputNodes[i]));
    }
    return sb.toString();
  }

  /**
   * Clone method
   *
   * @return clone instance
   */
  public CGPChromosome copy() {
    CGPChromosome copy = new CGPChromosome();

    copy.numInputs = this.numInputs;
    copy.numOutputs = this.numOutputs;
    copy.numNodes = this.numNodes;
    copy.numActiveNodes = this.numActiveNodes;
    copy.arity = this.arity;
    copy.generation = this.generation;

    copy.nodes = new Node[this.numNodes];
    for (int i = 0; i < this.nodes.length; i++) {
      copy.nodes[i] = this.nodes[i].copy();
    }
    copy.activeNodes = this.activeNodes.clone();

    copy.outputNodes = this.outputNodes.clone();
    copy.outputValues = this.outputValues.clone();

    copy.nodeInputsHold = this.nodeInputsHold.clone();
    copy.funcSet = this.funcSet.clone();

    return copy;
  }

  public void copyChromosome(CGPChromosome src) {
    /* error checking  */
    if (this.numInputs != src.numInputs) {
      throw new IllegalArgumentException(
          "Cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome inputs do not match.");
    }

    if (this.numNodes != src.numNodes) {
      throw new IllegalArgumentException(
          "Cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome nodes do not match.");
    }

    if (this.numOutputs != src.numOutputs) {
      throw new IllegalArgumentException(
          "Cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome outputs do not match.");
    }

    if (this.arity != src.arity) {
      throw new IllegalArgumentException(
          "Cannot copy a chromosome to a chromosome of different dimensions. The arity of the chromosome nodes do not match.");
    }

    /* copy nodes and which are active */
    for (int i = 0; i < src.numNodes; i++) {
      this.nodes[i].copyNode(src.nodes[i]);
    }

    System.arraycopy(src.activeNodes, 0, this.activeNodes, 0, src.numNodes);

    /* copy functionSet */
    this.funcSet = src.funcSet.clone();

    /* copy each of the chromosomes outputs */
    System.arraycopy(src.outputNodes, 0, this.outputNodes, 0, src.numOutputs);

    /* copy the number of active node */
    this.numActiveNodes = src.numActiveNodes;

    /* copy the fitness */
    this.setFitness(src.getFitness());

    /* copy generation */
    this.generation = src.generation;
  }

  /**
   * @param index
   * @return
   */
  public Node getNode(int index) {
    if (index < 0 || index >= this.nodes.length) {
      throw new IndexOutOfBoundsException(
          String.format("Nodes have size %s but get index %s", this.nodes.length, index));
    }
    return this.nodes[index];
  }

  public void setNode(int index, Node node) {
    if (index < 0 || index >= this.nodes.length) {
      throw new IndexOutOfBoundsException(
          String.format("Node has length %s but get index %s", this.nodes.length, index));
    }
    this.nodes[index] = node;
  }

  public int getOutputNode(int index) {
    if (index < 0 || index >= this.outputNodes.length) {
      throw new IndexOutOfBoundsException(
          String.format(
              "Output Nodes have size %s but get index %s", this.outputNodes.length, index));
    }
    return this.outputNodes[index];
  }

  /**
   * @param index
   * @param outputNodeIndex
   */
  public void setOutputNode(int index, int outputNodeIndex) {
    this.outputNodes[index] = outputNodeIndex;
  }

  public int getActiveNode(int index) {
    if (index < 0 || index >= this.activeNodes.length) {
      throw new IndexOutOfBoundsException(
          String.format(
              "Active Nodes have size %s but get index %s", this.activeNodes.length, index));
    }
    return this.activeNodes[index];
  }

  /** reset the outputNodes values of all chromosome nodes to zero */
  void resetChromosome() {
    IntStream.range(0, this.numNodes).forEach(i -> this.nodes[i].setOutput(0));
  }

  /** used to access the chromosome outputs after executeChromosome has been called */
  public double getChromosomeOutput(int index) {
    if (index < 0 || index > this.numOutputs) {
      throw new IndexOutOfBoundsException(
          "Error: outputNodes less than or greater than the number of chromosome outputs. Called from getChromosomeOutput.");
    }

    return this.outputValues[index];
  }

  /** used to access the chromosome node values after executeChromosome has been called */
  public double getChromosomeNodeValue(int index) {
    if (index < 0 || index > this.numNodes) {
      throw new IndexOutOfBoundsException(
          "Error: node less than or greater than the number of nodes  in chromosome. Called from getChromosomeNodeValue.");
    }
    return this.nodes[index].getOutput();
  }

  /** returns whether the specified node is active in the given chromosome */
  public boolean isNodeActive(int index) {
    if (index < 0 || index > this.numNodes) {
      throw new IllegalArgumentException(
          "Error: node less than or greater than the number of nodes  in chromosome. Called from isNodeActive.");
    }

    return this.nodes[index].isActive();
  }

  /** Gets the chromosome node arity */
  int getChromosomeNodeArity(int index) {
    int chromoArity = this.arity;
    int maxArity = this.funcSet.get(this.nodes[index].getFunction()).arity();

    return maxArity == -1 ? chromoArity : Math.min(maxArity, chromoArity);
  }

  /** Gets the number of active connections in the given chromosome */
  public int getNumChromosomeActiveConnections() {
    return IntStream.range(0, this.numActiveNodes)
        .map(i -> this.nodes[this.activeNodes[i]].getActArity())
        .sum();
  }

  public String toString(boolean weights) {
    StringBuilder sb = new StringBuilder();
    /* set the active nodes in the given chromosome */
    setChromosomeActiveNodes();

    /* for all the chromosome inputs*/
    for (int i = 0; i < this.numInputs; i++) {
      sb.append(String.format("(%d):\tinput\n", i));
    }

    /* for all the hidden nodes */
    for (int i = 0; i < this.numNodes; i++) {
      /* print the node function */
      sb.append(
          String.format(
              "(%d):\t%s\t", this.numInputs + i, this.funcSet.get(this.nodes[i].getFunction())));
      /* for the arity of the node */
      for (int j = 0; j < this.getChromosomeNodeArity(i); j++) {
        /* print the node input information */
        if (weights) {
          sb.append(
              String.format("%d,%+.1f\t", this.nodes[i].getInput(j), this.nodes[i].getWeight(j)));
        } else {
          sb.append(String.format("%d ", this.nodes[i].getInput(j)));
        }
      }
      /* Highlight active nodes */
      if (this.nodes[i].isActive()) {
        sb.append("*");
      }
      sb.append("\n");
    }
    /* for all of the outputs */
    sb.append("outputs: ");
    for (int i = 0; i < this.numOutputs; i++) {
      /* print the outputNodes node locations */
      sb.append(String.format("%d ", this.outputNodes[i]));
    }
    return sb.append("\n\n").toString();
  }
}
