package example.cgp;

import cgp.emum.CGPMutationStrategy;
import cgp.gp.CGPChromosome;
import cgp.solver.CGPSolver;
import genetics.interfaces.FitnessCalc;

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
        solver.setMutationType(CGPMutationStrategy.POINT_ANN);
        solver.printParams();

        solver.evolve(numGens);

        CGPChromosome bestGene = solver.getBestGene(false);

        // save to file
        String serialize = bestGene.serialization();
        //        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        //                new FileOutputStream("serialize.txt"), StandardCharsets.UTF_8))) {
        //            writer.write(serialize);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

        // reconstruct
        //        bestGene = CGPChromosome.deserialization("serialize.txt");
        bestGene = CGPChromosome.reconstruct(serialize);
        System.out.println(bestGene);

        // continues training
        while (bestGene.getFitness() > targetFitness || bestGene.notEvaluated()) {
            solver.evolve(numGens, bestGene);
            bestGene = solver.getBestGene(false);
        }

        System.out.println(bestGene.toString(true));
    }

    static class sinWave implements FitnessCalc<CGPChromosome> {
        @Override
        public double calc(CGPChromosome chromosome) {
            double error = 0;
            double range = 6;
            double stepSize = 0.5;

            double[] inputs = new double[1];

            for (double i = 0; i < range; i += stepSize) {

                inputs[0] = i;

                chromosome.evaluate(inputs);

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
