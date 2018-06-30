package examples.Symbolic_engine;

import cgp.Solver.CGPFitnessCalc;
import cgp.Solver.CGPSolver;
import cgp.Solver.CartesianGP;
import cgp.gp.CGPChromosome;
import genetics.chromosome.Chromosome;
import genetics.utils.Observation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class CGPTest {

    private static double fittest = Double.MAX_VALUE;

    public static void main(String[] args) {
        List<Observation> list = Test.function();
        Collections.shuffle(list);
        int split_point = (int) (list.size() * .9);
        List<Observation> training = list.subList(0, split_point);
        List<Observation> testing = list.subList(split_point, list.size());
        System.out.printf("Training: %d\t Testing: %d\t\n", training.size(), testing.size());

        TabulatedFunctionFitness functionFitness = new TabulatedFunctionFitness(training);

        CartesianGP env = CartesianGP.defaultConfig(3, 1);
        CGPSolver solver = new CGPSolver(env, functionFitness);
        addListener(solver);
        Long startTime = System.currentTimeMillis();
        solver.evolve();
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);

        test(solver, testing);
    }

    private static void test(CGPSolver solver, List<Observation> observations) {
        for (Observation o : observations) {
            solver.getBestGene().eval(o);
            double predicted = o.getPredictedOutput(0);
            double actual = o.getOutput(0);

            System.out.printf("predicted: %f\t actual: %f\t difference: %f\t\n",
                    predicted, actual, Math.abs(predicted - actual));
        }
    }

    private static void addListener(CGPSolver engine) {
        engine.addIterationListener(engine1 -> {

            Chromosome bestGene = engine1.getBestGene();

            double bestFit = engine1.fitness((CGPChromosome) bestGene);

            if (bestFit < fittest) {
                fittest = bestFit;
                System.out.printf("Generation = %s\t fit = %s\t\n", engine1.getIteration(), bestFit);
            }
            // halt condition
            if (bestFit < 5) {
                engine1.terminate();
                System.out.printf("Function: %s\n", bestGene);
            }
        });
    }

    private static class TabulatedFunctionFitness implements CGPFitnessCalc {

        private final List<Observation> targets = new LinkedList<>();

        TabulatedFunctionFitness(List<Observation> targets) {
            this.targets.addAll(targets);
        }

        @Override
        public double fitness(CGPChromosome expression) {
            double diff = 0;
            for (Observation o : targets) {
                for (int i = 0; i < o.outputCount(); i++) {
                    expression.eval(o);
                    diff += Math.pow(o.getOutput(i) - o.getPredictedOutput(i), 2);
                }
            }
            return diff;
        }
    }
}
