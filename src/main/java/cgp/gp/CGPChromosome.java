package cgp.gp;

import cgp.program.FunctionSet;
import cgp.program.Node;
import cgp.solver.CartesianGP;
import genetics.chromosome.Chromosome;

import java.util.Scanner;

import static cgp.gp.CGPCore.*;
import static cgp.solver.CGPSolver.initialiseParameters;

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
    FunctionSet funcSet;
    double[] nodeInputsHold;
    int generation;

    public static CGPChromosome deserialization(String serialize) {
        String[] line;

        /* open the chromosome file */
        Scanner scanner = new Scanner(serialize);

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
            if (!addPresetFuctionToFunctionSet(params, line[k + 1])) {
                System.out.print("Error: cannot load chromosome which contains custom node functions.\n");
                System.out.print("Terminating CGP-Library.\n");
                System.exit(0);
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
        scanner.close();
        /* set the active nodes in the copied chromosome */
        setChromosomeActiveNodes(chromosome);

        return chromosome;
    }

    @Override
    public String serialization() {
        StringBuilder sb = new StringBuilder();

        /* save meta information */
        sb.append(String.format("numInputs,%d\n", numInputs));
        sb.append(String.format("numNodes,%d\n", numNodes));
        sb.append(String.format("numOutputs,%d\n", numOutputs));
        sb.append(String.format("arity,%d\n", arity));

        sb.append("FunctionSet");

        for (int i = 0; i < funcSet.numFunctions; i++) {
            sb.append(String.format(",%s", funcSet.functionNames[i]));
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
}
