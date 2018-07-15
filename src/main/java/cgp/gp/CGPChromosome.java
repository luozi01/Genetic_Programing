package cgp.gp;

import cgp.program.FunctionSet;
import cgp.program.Node;
import genetics.chromosome.Chromosome;

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
}
