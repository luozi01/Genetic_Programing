package examples.Symbolic_engine;

import genetics.utils.Observation;
import org.apache.commons.math3.genetics.Chromosome;
import treegp.program.Operator;
import treegp.solver.TGPSolver;
import treegp.solver.TreeGP;

import java.util.Arrays;
import java.util.List;

import static treegp.program.Type.*;


class TGPTest {

    public static void main(String[] args) {
        List<Observation> list = Test.function();

        int split_point = (int) (list.size() * .9);
        List<Observation> training = list.subList(0, split_point);
        List<Observation> testing = list.subList(split_point, list.size());
        System.out.printf("Training: %d\t Testing: %d\t\n", training.size(), testing.size());

        List<Operator> func = Arrays.asList(ADD, SUB, MUL, VARIABLE, CONSTANT);
        TreeGP env = new TreeGP(func, Arrays.asList("x", "y", "z"), training);
        TGPSolver solver = new TGPSolver(env);
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
            double predicted = solver.getBestGene().eval();
            double actual = o.getOutput(0);

            System.out.printf("predicted: %f\t actual: %f\t difference: %f\t\n",
                    predicted, actual, Math.abs(predicted - actual));
        }
    }

    private static void addListener(TGPSolver engine) {
        engine.addIterationListener(engine1 -> {

            Chromosome bestGene = engine1.getBestGene();

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
}

