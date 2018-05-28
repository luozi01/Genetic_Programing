package examples.Symbolic_engine;

import cgp.Solver.CGPSolver;
import cgp.Solver.CartesianGP;
import genetics.utils.Observation;
import org.apache.commons.math3.genetics.Chromosome;

import java.util.List;

class CGPTest {

    private static double fittest = Double.MAX_VALUE;

    public static void main(String[] args) {
        List<Observation> list = Test.function();

        int split_point = (int) (list.size() * .9);
        List<Observation> training = list.subList(0, split_point);
        List<Observation> testing = list.subList(split_point, list.size());
        System.out.printf("Training: %d\t Testing: %d\t\n", training.size(), testing.size());

        CartesianGP env = CartesianGP.defaultConfig(training);
        CGPSolver solver = new CGPSolver(env);
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

            double bestFit = engine1.fitness(bestGene);

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
}
