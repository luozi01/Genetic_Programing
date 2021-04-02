package cgp.gp;

import cgp.interfaces.CGPFunction;
import cgp.program.Node;
import cgp.solver.CartesianGP;
import genetics.chromosome.Chromosome;
import lombok.Cleanup;
import lombok.Getter;
import org.eclipse.collections.api.list.MutableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.IntStream;

import static cgp.gp.CGPCore.initialiseChromosome;
import static cgp.gp.CGPCore.setChromosomeActiveNodes;
import static cgp.solver.CGPSolver.initialiseParameters;

@Getter
public class CGPChromosome extends Chromosome {
    int numInputs;
    int numOutputs;
    int numNodes;
    int numActiveNodes;
    int arity;
    Node[] nodes;
    int[] outputNodes;
    int[] activeNodes;
    double[] outputValues;
    MutableList<CGPFunction> funcSet;
    double[] nodeInputsHold;
    int generation;

    public static CGPChromosome deserialization(String fileName) {
        File file = new File(fileName);
        StringBuilder serial = new StringBuilder();
        try {
            @Cleanup Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine())
                serial.append(scanner.nextLine()).append("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (serial.length() == 0)
            throw new IllegalStateException("File is empty, cannot reconstruct chromosome from it");
        else
            return reconstruct(serial.toString());
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
        CartesianGP params = initialiseParameters(numInputs, numNodes, numOutputs, arity);

        /* for each function name */
        line = scanner.nextLine().split(",");
        for (int k = 0; k < line.length - 1; k++) {
            if (!params.addPresetFunctionToFunctionSet(line[k + 1])) {
                throw new IllegalArgumentException("Error: cannot load chromosome which contains custom node functions.");
            }
        }

        /* initialise a chromosome based on the parameters associated with given chromosome */
        CGPChromosome chromosome = initialiseChromosome(params);

        /* set the node parameters */
        for (int i = 0; i < numNodes; i++) {
            /* get the function gene */
            chromosome.nodes[i].function = scanner.nextInt();

            scanner.nextLine();
            for (int j = 0; j < arity; j++) {
                line = scanner.nextLine().split(",");
                chromosome.nodes[i].inputs[j] = Integer.parseInt(line[0]);
                chromosome.nodes[i].weights[j] = Double.parseDouble(line[1]);
            }
        }

        line = scanner.nextLine().split(",");
        /* set the outputs */
        for (int i = 0; i < numOutputs; i++) {
            chromosome.outputNodes[i] = Integer.parseInt(line[i]);
        }
        /* set the active nodes in the copied chromosome */
        setChromosomeActiveNodes(chromosome);
        return chromosome;
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
            sb.append(String.format("%d\n", nodes[i].function));
            for (int j = 0; j < arity; j++) {
                sb.append(String.format("%d,%f\n", nodes[i].inputs[j], nodes[i].weights[j]));
            }
        }

        for (int i = 0; i < numOutputs; i++) {
            sb.append(String.format("%d,", outputNodes[i]));
        }
        return sb.toString();
    }

    void copyChromosome(CGPChromosome src) {
        /* error checking  */
        if (this.numInputs != src.numInputs) {
            throw new IllegalArgumentException("Cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome inputs do not match.");
        }

        if (this.numNodes != src.numNodes) {
            throw new IllegalArgumentException("Cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome nodes do not match.");
        }

        if (this.numOutputs != src.numOutputs) {
            throw new IllegalArgumentException("Cannot copy a chromosome to a chromosome of different dimensions. The number of chromosome outputs do not match.");
        }

        if (this.arity != src.arity) {
            throw new IllegalArgumentException("Cannot copy a chromosome to a chromosome of different dimensions. The arity of the chromosome nodes do not match.");
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
     * reset the outputNodes values of all chromosome nodes to zero
     */
    void resetChromosome() {
        IntStream.range(0, this.numNodes).forEach(i -> this.nodes[i].output = 0);
    }

    /**
     * used to access the chromosome outputs after executeChromosome
     * has been called
     */
    public double getChromosomeOutput(int output) {

        if (output < 0 || output > this.numOutputs) {
            System.out.print("Error: outputNodes less than or greater than the number of chromosome outputs. Called from getChromosomeOutput.\n");
            System.exit(0);
        }

        return this.outputValues[output];
    }

    /**
     * used to access the chromosome node values after executeChromosome
     * has been called
     */
    public double getChromosomeNodeValue(int node) {
        if (node < 0 || node > this.numNodes) {
            System.out.print("Error: node less than or greater than the number of nodes  in chromosome. Called from getChromosomeNodeValue.\n");
            System.exit(0);
        }
        return this.nodes[node].output;
    }

    /**
     * returns whether the specified node is active in the given chromosome
     */
    public boolean isNodeActive(int node) {

        if (node < 0 || node > this.numNodes) {
            System.out.print("Error: node less than or greater than the number of nodes  in chromosome. Called from isNodeActive.\n");
            System.exit(0);
        }

        return this.nodes[node].active;
    }

    /**
     * Gets the chromosome node arity
     */
    int getChromosomeNodeArity(int index) {
        int chromoArity = this.arity;
        int maxArity = this.funcSet.get(this.nodes[index].function).arity();

        if (maxArity == -1) {
            return chromoArity;
        } else if (maxArity < chromoArity) {
            return maxArity;
        } else {
            return chromoArity;
        }
    }

    /**
     * Gets the number of active connections in the given chromosome
     */
    public int getNumChromosomeActiveConnections() {
        return IntStream.range(0, this.numActiveNodes).map(i -> this.nodes[this.activeNodes[i]].actArity).sum();
    }

    public String toString(boolean weights) {
        StringBuilder sb = new StringBuilder();
        /* set the active nodes in the given chromosome */
        setChromosomeActiveNodes(this);

        /* for all the chromosome inputs*/
        for (int i = 0; i < this.numInputs; i++) {
            sb.append(String.format("(%d):\tinput\n", i));
        }

        /* for all the hidden nodes */
        for (int i = 0; i < this.numNodes; i++) {
            /* print the node function */
            sb.append(String.format("(%d):\t%s\t", this.numInputs + i, this.funcSet.get(this.nodes[i].function)));
            /* for the arity of the node */
            for (int j = 0; j < this.getChromosomeNodeArity(i); j++) {
                /* print the node input information */
                if (weights) {
                    sb.append(String.format("%d,%+.1f\t", this.nodes[i].inputs[j], this.nodes[i].weights[j]));
                } else {
                    sb.append(String.format("%d ", this.nodes[i].inputs[j]));
                }
            }
            /* Highlight active nodes */
            if (this.nodes[i].active) {
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
