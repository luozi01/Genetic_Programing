package example.cgp;

import cgp.gp.CGPChromosome;
import cgp.solver.CGPSolver;

import static example.cgp.AverageBehaviour.initialiseDataSetFromFile;

class recurrentCGP {

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

        solver.setData(initialiseDataSetFromFile("problems/cgp/fibonacci.data"));

        solver.evolve(numGens);

        CGPChromosome gene = solver.getBestGene(true);

        System.out.println(gene.toString(false));

        String serialization = gene.serialization();

        System.out.println(serialization);
    }
}

