package cgp_examples;

import cgp.gp.CGPChromosome;
import cgp.interfaces.CGPFitness;
import cgp.program.DataSet;
import cgp.solver.CGPSolver;
import cgp.solver.CartesianGP;

import static cgp.gp.CGPCore.executeChromosome;
import static cgp.gp.CGPCore.printChromosome;

public class NeuroEvolution {

    public static void main(String[] args) {
        int numInputs = 1;
        int numNodes = 20;
        int numOutputs = 1;
        int nodeArity = 5;

        int numGens = 25000;
        double targetFitness = 0.5;
        int updateFrequency = 500;

        double weightRange = 5;

        CGPSolver solver = new CGPSolver(numInputs, numNodes, numOutputs, nodeArity);

        solver.setTargetFitness(targetFitness);
        solver.setUpdateFrequency(updateFrequency);
        solver.setConnectionWeightRange(weightRange);
        solver.setCustomFitnessFunction(new sinWave());

        solver.addNodeFunction("tanh,softsign");
        solver.setMutationType("pointANN");
        solver.printParams();

        solver.evolve(numGens);


        CGPChromosome bestGene = solver.getBestGene(false);

        // continues training
        while (bestGene.fitness > targetFitness) {
            solver.evolve(numGens, bestGene);
            bestGene = solver.getBestGene(false);
        }

        printChromosome(bestGene, true);
    }

    static class sinWave implements CGPFitness {
        @Override
        public double calc(CartesianGP params, CGPChromosome chromosome, DataSet data) {
            double i;

            double error = 0;
            double range = 6;
            double stepSize = 0.5;

            double[] inputs = new double[1];

            for (i = 0; i < range; i += stepSize) {

                inputs[0] = i;

                executeChromosome(chromosome, inputs);

                error += Math.abs(chromosome.getChromosomeOutput(0) - Math.sin(i));
            }

            return error;
        }

        @Override
        public String toString() {
            return "sinWave";
        }
    }
}
