package examples;

import genetics.chromosome.AbstractListChromosome;
import genetics.chromosome.BinaryChromosome;
import genetics.chromosome.Chromosome;
import genetics.crossover.UniformCrossover;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initialization;
import genetics.mutation.BinaryMutation;
import genetics.selection.TournamentSelection;
import org.apache.commons.math3.analysis.TrivariateFunction;
import org.eclipse.collections.api.list.MutableList;

import java.util.ArrayList;
import java.util.List;

public class Function_Maximum {

    private static final TrivariateFunction function = (x, y, z) -> (-x * x + 1000000.0 * x - y * y - 40000.0 * y - z * z);

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new SolveGenerate(100),
                new Solve_Evaluate(),
                new UniformCrossover<>(.5), .4,
                new BinaryMutation(), .02,
                new TournamentSelection(), 3, 1);
        ga.evolve(1000);
        String output = ga.getBest().toString();
        System.out.println(new Solve_Evaluate().calc(ga.getBest()));
        System.out.println(output.substring(0, 32) + " " +
                (int) Long.parseLong(output.substring(0, 32), 2));
        System.out.println(output.substring(32, 64) + " " +
                (int) Long.parseLong(output.substring(32, 64), 2));
        System.out.println(output.substring(64, 96) + " " +
                (int) Long.parseLong(output.substring(64, 96), 2));
    }

    private static class Solve extends BinaryChromosome {

        Solve(MutableList<Integer> representation) {
            super(representation);
        }

        Solve() {
            super(BinaryChromosome.randomBinaryRepresentation(96));
        }

        @Override
        public AbstractListChromosome<Integer> newCopy(MutableList<Integer> representation) {
            return new Solve(representation);
        }

        @Override
        public String toString() {
            StringBuilder geneString = new StringBuilder();
            for (Integer gene : getRepresentation()) {
                geneString.append(gene);
            }
            return geneString.toString();
        }
    }

    private static class Solve_Evaluate implements FitnessCalc {

        private double function(int x, int y, int z) {
            return function.value(x, y, z);
        }

        @Override
        public double calc(Chromosome chromosome) {
            int length = ((Solve) chromosome).getLength();
            String gene = chromosome.toString();
            return 1 / function((int) Long.parseLong(gene.substring(0, length / 3), 2),
                    (int) Long.parseLong(gene.substring(length / 3, length * 2 / 3), 2),
                    (int) Long.parseLong(gene.substring(length * 2 / 3, length), 2));
        }
    }

    private static class SolveGenerate implements Initialization {

        private final int populationSize;

        SolveGenerate(int populationSize) {
            this.populationSize = populationSize;
        }

        @Override
        public List<Chromosome> generate() {
            List<Chromosome> list = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                Solve solve = new Solve();
                list.add(solve);
            }
            return list;
        }
    }
}
