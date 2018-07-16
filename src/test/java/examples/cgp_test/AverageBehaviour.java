package examples.cgp_test;

import cgp.solver.CGPSolver;

import static cgp.gp.CGPCore.printChromosome;

class AverageBehaviour {

    public static void main(String[] args) {
        int numInputs = 1;
        int numNodes = 15;
        int numOutputs = 1;
        int nodeArity = 2;

        int numGens = 10000;
        int numRuns = 10;

        double targetFitness = 0.1;
        int updateFrequency = 500;

        double averageFitness;

        CGPSolver solver = new CGPSolver(numInputs, numNodes, numOutputs, nodeArity);

        solver.addNodeFunction("add,sub,mul,div,sin");

        solver.setTargetFitness(targetFitness);

        solver.setUpdateFrequency(updateFrequency);

        solver.initialiseDataSetFromFile("symbolic.data");

        solver.repeatEvolve(numGens, numRuns);

        averageFitness = solver.getResults().getAverageFitness();

        System.out.printf("The average chromosome fitness is: %f\n", averageFitness);

        System.out.println("The best chromosome found on run 4:");

        printChromosome(solver.getChromosome(4), false);
    }
}
