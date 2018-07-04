package examples.Symbolic_engine;

import genetics.utils.Observation;
import treegp.gp.TGPChromosome;
import treegp.program.Operator;
import treegp.solver.TGPFitnessCalc;
import treegp.solver.TGPSolver;
import treegp.solver.TreeGP;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static treegp.program.Type.*;


class TGPTest {

    public static void main(String[] args) {
        List<Observation> list = Test.function();
//        Collections.shuffle(list);
        int split_point = (int) (list.size() * .9);
        List<Observation> training = list.subList(0, split_point);
        List<Observation> testing = list.subList(split_point, list.size());
        System.out.printf("Training: %d\t Testing: %d\t\n", training.size(), testing.size());

        TabulatedFunctionFitness fitnessFunction = new TabulatedFunctionFitness(training);

        List<Operator> func = Arrays.asList(ADD, SUB, MUL, VARIABLE, CONSTANT);
        TreeGP env = new TreeGP(func, Arrays.asList("x", "y", "z"));
        TGPSolver solver = new TGPSolver(env, fitnessFunction);
        solver.runGlobal();
        addListener(solver);
        Long startTime = System.currentTimeMillis();
        solver.evolve();
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);

        test(solver, testing);
    }

    private static void test(TGPSolver solver, List<Observation> observations) {
        for (Observation o : observations) {
            for (int i = 0; i < o.inputCount(); i++) {
                solver.getBestGene().getManager().setVariable(o.getTextInput(i), o.getInput(i));
            }
            double predicted = solver.getBestGene().eval(o);
            double actual = o.getOutput(0);

            System.out.printf("predicted: %f \tactual: %f \tdifference: %f\n",
                    predicted, actual, Math.abs(predicted - actual));
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
                System.out.println(bestGene.serialization());
            }
        });
    }

    private static class TabulatedFunctionFitness implements TGPFitnessCalc {

        private final List<Observation> targets = new LinkedList<>();

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
                double calculatedValue = expression.eval(o);
                diff += Math.pow(targetValue - calculatedValue, 2);
            }
            return diff;
        }
    }
}

