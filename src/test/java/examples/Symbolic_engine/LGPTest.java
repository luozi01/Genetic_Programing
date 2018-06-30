package examples.Symbolic_engine;

import genetics.utils.Observation;
import lgp.gp.LGPChromosome;
import lgp.solver.LGPFitnessCalc;
import lgp.solver.LGPSolver;
import lgp.solver.LinearGP;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class LGPTest {

    public static void main(String[] args) {
        List<Observation> list = Test.function();
        Collections.shuffle(list);
        int split_point = (int) (list.size() * .9);
        List<Observation> training = list.subList(0, split_point);
        List<Observation> testing = list.subList(split_point, list.size());
        System.out.printf("Training: %d\t Testing: %d\t\n", training.size(), testing.size());

        FunctionFitness fitnessFunction = new FunctionFitness(training);

        LinearGP gp = LinearGP.defaultConfig(training, 3);
        LGPSolver solver = new LGPSolver(gp, fitnessFunction);
        System.out.println("Start");
        addListener(solver);
        Long startTime = System.currentTimeMillis();
        solver.evolve();
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);

        test(solver, testing);
    }

    private static void test(LGPSolver solver, List<Observation> observations) {
        for (Observation o : observations) {
            solver.getBestGene().eval(o);
            double predicted = o.getPredictedOutput(0);
            double actual = o.getOutput(0);

            System.out.printf("predicted: %f\t actual: %f\t difference: %f\t\n",
                    predicted, actual, Math.abs(predicted - actual));
        }
    }

    private static void addListener(LGPSolver engine) {
        engine.addIterationListener(engine1 -> {

            LGPChromosome bestGene = engine1.getBestGene();

            double bestFit = engine1.fitness(bestGene);

            // log to console
            System.out.printf("Generation = %s \t fit = %s \n", engine1.getIteration(), bestFit);

            // halt condition
            if (bestFit < 5) {
                engine1.terminate();
                System.out.println(bestGene);
            }
        });
    }

    private static class FunctionFitness implements LGPFitnessCalc {

        private final List<Observation> targets = new LinkedList<>();

        FunctionFitness(List<Observation> targets) {
            this.targets.addAll(targets);
        }

        @Override
        public double fitness(LGPChromosome chromosome) {
            double diff = 0;

            for (Observation o : this.targets) {
                chromosome.eval(o);
                diff += Math.pow(o.getOutput(0) - o.getPredictedOutput(0), 2);
            }
            return diff;
        }
    }
}
