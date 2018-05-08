import com.zluo.ga.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Function_Maximum {
    public static void main(String[] args) {
        Population<Solve> population = new Population<>(new SolveGenerate(100));
        GeneticAlgorithm<Solve> ga = new GeneticAlgorithm<>(population, new FuncFitness());
        ga.evolve(1000);
        String output = ga.getBest().toString();
        System.out.println(output);
        System.out.println(output.substring(0, 32) + " " +
                (int) Long.parseLong(output.substring(0, 32), 2));
        System.out.println(output.substring(32, 64) + " " +
                (int) Long.parseLong(output.substring(32, 64), 2));
        System.out.println(output.substring(64, 96) + " " +
                (int) Long.parseLong(output.substring(64, 96), 2));
    }

    static class Solve implements Chromosome<Solve> {

        private byte[] genes = new byte[96];

        @Override
        public List<Solve> crossover(Solve chromosome, double uniformRate) {
            Solve gene1 = new Solve();
            Solve gene2 = new Solve();
            // Loop through genes
            for (int i = 0; i < genes.length; i++) {
                // Crossover
                if (Math.random() <= uniformRate) {
                    gene1.genes[i] = genes[i];
                    gene2.genes[i] = chromosome.genes[i];
                } else {
                    gene1.genes[i] = chromosome.genes[i];
                    gene1.genes[i] = genes[i];
                }
            }
            return new ArrayList<>(Arrays.asList(gene1, gene2));
        }

        @Override
        public Solve mutate(double mutationRate) {
            Solve clone = makeCopy();
            for (int i = 0; i < clone.genes.length; i++) {
                if (Math.random() <= mutationRate) {
                    clone.genes[i] = (byte) Math.round(Math.random());
                }
            }
            return clone;
        }

        @Override
        public Solve makeCopy() {
            Solve copy = new Solve();
            copy.genes = genes.clone();
            return copy;
        }

        @Override
        public String toString() {
            StringBuilder geneString = new StringBuilder();
            for (byte gene : genes) {
                geneString.append(gene);
            }
            return geneString.toString();
        }
    }

    static class SolveGenerate implements Generator<Solve> {

        private int populationSize;

        SolveGenerate(int populationSize) {
            this.populationSize = populationSize;
        }

        @Override
        public List<Solve> generate() {
            List<Solve> list = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                Solve solve = new Solve();
                for (int j = 0; j < solve.genes.length; j++) {
                    solve.genes[j] = (byte) Math.round(Math.random());
                }
                list.add(solve);
            }
            return list;
        }
    }

    private static class FuncFitness implements Fitness<Solve> {

        /**
         * f(x,y,z) = -x^2 + 1000000x - y^2 - 40000y - z^2
         *
         * @param x x
         * @return f(x, y, z)
         */
        private double function(int x, int y, int z) {
            return -Math.pow(x, 2) + 1000000.0 * x - Math.pow(y, 2) - 40000.0 * y + -Math.pow(z, 2);
        }

        //Todo fix not getting the optimal output
        @Override
        public double calc(Solve chromosome) {
            int length = chromosome.genes.length;
            String gene = chromosome.toString();
            return -function((int) Long.parseLong(gene.substring(0, length / 3), 2),
                    (int) Long.parseLong(gene.substring(length / 3, length * 2 / 3), 2),
                    (int) Long.parseLong(gene.substring(length * 2 / 3, length), 2));
        }
    }
}
