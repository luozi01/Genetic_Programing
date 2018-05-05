package Symbolic_engine;

import com.zluo.ga.utils.Observation;
import com.zluo.tgp.gp.TGPChromosome;
import com.zluo.tgp.program.Operator;
import com.zluo.tgp.solver.FitnessCalc;
import com.zluo.tgp.solver.TGPSolver;
import com.zluo.tgp.solver.TreeGP;

import java.util.*;

import static com.zluo.tgp.program.Type.*;

public class TGPTest {

    public static void main(String[] args) {
        List<Observation> list = Test.function();

        int split_point = (int) (list.size() * .9);
        List<Observation> training = list.subList(0, split_point);
        List<Observation> testing = list.subList(split_point, list.size());
        System.out.printf("Training: %d\t Testing: %d\t\n", training.size(), testing.size());

        TabulatedFunctionFitness fitnessFunction = new TabulatedFunctionFitness(training);
        List<Operator> func = new ArrayList<>(
                Arrays.asList(ADD, SUB, MUL, VARIABLE, CONSTANT));
        TreeGP env = new TreeGP(func, list("x", "y", "z"));
        TGPSolver solver = new TGPSolver(env, fitnessFunction);
        addListener(solver);
        Long startTime = System.currentTimeMillis();
        solver.evolve(100000);
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);

        test(solver, testing, false);
    }

    private static void test(TGPSolver solver, List<Observation> observations, boolean silent) {
        for (Observation o : observations) {
            for (int i = 0; i < o.inputCount(); i++) {
                solver.getBestGene().getManager().setVariable(o.getTextInput(i), o.getInput(i));
            }
            double predicted = solver.getBestGene().eval();
            double actual = o.getOutput(0);

            if (!silent) {
                System.out.printf("predicted: %f\t actual: %f\t difference: %f\t\n",
                        predicted, actual, Math.abs(predicted - actual));
            }
        }
    }

    private static void addListener(TGPSolver engine) {
        engine.addIterationListener(engine1 -> {

            TGPChromosome bestGene = engine1.getBestGene();

            double bestFit = engine1.fitness(bestGene);

            // log to console
            System.out.printf("Generation = %s\t fit = %s\t\n", engine1.getIteration(), bestFit);
//            System.out.println(engine1.getGeneration() + " " + bestFit);

            // halt condition
            if (bestFit < 5) {
                engine1.terminate();
                System.out.printf("Function: %s\n", bestGene);
            }
        });
    }

    @SafeVarargs
    private static <T> List<T> list(T... items) {
        List<T> list = new LinkedList<>();
        Collections.addAll(list, items);
        return list;
    }

    private static class TabulatedFunctionFitness implements FitnessCalc {

        private List<Observation> targets = new LinkedList<>();

        TabulatedFunctionFitness(List<Observation> targets) {
            this.targets.addAll(targets);
        }

        @Override
        public double fitness(TGPChromosome expression) {
            double diff = 0;

            for (Observation o : this.targets) {
                for (int i = 0; i < o.inputCount(); i++) {
                    expression.getManager().setVariable(o.getTextInput(i), o.getInput(i));
                }
                double targetValue = o.getOutput(0);
                double calculatedValue = expression.eval();
                diff += Math.pow(targetValue - calculatedValue, 2);
            }
            return diff;
        }
    }
}

