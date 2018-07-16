package examples.cgp_test;

import cgp.gp.CGPChromosome;
import cgp.solver.CGPSolver;

import static cgp.gp.CGPCore.printChromosome;

public class recurrentCGP {

    public static void main(String[] args) {
        int numInputs = 1;
        int numNodes = 15;
        int numOutputs = 1;
        int nodeArity = 2;

        int numGens = 100000;
        int updateFrequency = 500;

        double recurrentConnectionProbability = 0.10;

        CGPSolver solver = new CGPSolver(numInputs, numNodes, numOutputs, nodeArity);
        solver.addNodeFunction("add,sub,mul,div");
        solver.setUpdateFrequency(updateFrequency);
        solver.setRecurrentConnectionProbability(recurrentConnectionProbability);
        solver.printParams();

        solver.initialiseDataSetFromFile("fibonacci.data");

        solver.evolve(numGens);

        CGPChromosome gene = solver.getBestGene(true);

        printChromosome(gene, false);

        String serialization = gene.serialization();

        System.out.println(serialization);
    }
}

