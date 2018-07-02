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
import org.eclipse.collections.api.list.MutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mike on 2016/11/3.
 */
public class Knapsack {

    private static int sum(K individual, int[] set) {
        int total = 0;
        for (int i = 0; i < individual.getLength(); i++) {
            if (individual.getGene(i) == 1) total += set[i];
        }
        return total;
    }

    private static int max(int i, int j) {
        return (i > j) ? i : j;
    }

//    private static int unboundedKnapsack(int W, int[] val, int[] wt) {
//        // dp[i] is going to store maximum value
//        // with knapsack capacity i.
//        int dp[] = new int[W + 1];
//
//        // Fill dp[] using above recursive formula
//        for (int i = 0; i <= W; i++) {
//            for (int j = 0; j < val.length; j++) {
//                if (wt[j] <= i) {
//                    dp[i] = max(dp[i], dp[i - wt[j]] + val[j]);
//                }
//            }
//        }
//        return dp[W];
//    }

    /**
     * find the maximum value that can be put in a knapsack of capacity W
     *
     * @param W   capacity
     * @param wt  items weight
     * @param val items value
     * @return the maximum value that can be put in a knapsack of capacity W
     */
    private static int knapsack(int W, int wt[], int val[]) {
        int i, w;
        int K[][] = new int[val.length + 1][W + 1];

        // Build table K[][] in bottom up manner
        for (i = 0; i <= val.length; i++) {
            for (w = 0; w <= W; w++) {
                if (i == 0 || w == 0)
                    K[i][w] = 0;
                else if (wt[i - 1] <= w)
                    K[i][w] = max(val[i - 1] + K[i - 1][w - wt[i - 1]], K[i - 1][w]);
                else
                    K[i][w] = K[i - 1][w];
            }
        }

        return K[val.length][W];
    }

    public static void main(String[] args) {
        System.out.println("Initialization");
        int[] itemsWeight = new int[]{6, 6, 5, 1, 4, 12, 15, 15};
        int[] itemsValue = new int[]{4, 9, 13, 15, 18, 7, 15, 3};
        int defaultGeneLength = itemsValue.length;
        int capacity = 10 * defaultGeneLength;
//        int defaultGeneLength = 40;
//        RandEngine randEngine = new SimpleRandEngine();
//        int[] itemsValue = new int[defaultGeneLength];
//        int[] itemsWeight = new int[defaultGeneLength];
//        for (int i = 0; i < defaultGeneLength; i++) {
//            itemsValue[i] = randEngine.nextInt(15) + 1;
//            itemsWeight[i] = randEngine.nextInt(20) + 1;
//        }
        System.out.println("Capacity: " + capacity);
        System.out.println("Values: \t" + Arrays.toString(itemsValue));
        System.out.println("Weights: \t" + Arrays.toString(itemsWeight));
        System.out.println("=======================================\n");


        GeneticAlgorithm ga = new GeneticAlgorithm(
                new KInitialization(100, itemsWeight, itemsValue, capacity, defaultGeneLength),
                new KEvaluate(),
                new UniformCrossover<K>(.5), .4,
                new BinaryMutation(), .02,
                new TournamentSelection(), 3, 1);
        ga.evolve(1000);
        Chromosome k = ga.getBest();
        System.out.println("Genes: " + k.toString());
        System.out.println("Weights: " + sum((K) k, itemsWeight));
        System.out.println("Value: " + sum((K) k, itemsValue));

        System.out.println("Optimal value: " + knapsack(capacity, itemsWeight, itemsValue));

    }

    private static class K extends BinaryChromosome {

        private int[] itemsWeight, itemsValue;
        private int capacity;

        K(MutableList<Integer> representation) {
            super(representation);
        }

        K(int defaultGeneLength, int[] itemsWeight, int[] itemsValue, int capacity) {
            this(BinaryChromosome.randomBinaryRepresentation(defaultGeneLength));
            this.capacity = capacity;
            this.itemsValue = itemsValue;
            this.itemsWeight = itemsWeight;
        }

        @Override
        public AbstractListChromosome<Integer> newCopy(MutableList<Integer> representation) {
            K k = new K(representation);
            k.itemsWeight = itemsWeight;
            k.itemsValue = itemsValue;
            k.capacity = capacity;
            return k;
        }

        int getGene(int index) {
            return getRepresentation().get(index);
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

    static class KEvaluate implements FitnessCalc {

        @Override
        public double calc(Chromosome chromosome) {
            int totalWeight = 0, totalValue = 0;
            // Loop through our individuals genes and compare them to our candidates
            for (int i = 0; i < ((K) chromosome).getLength(); i++) {
                if (((K) chromosome).getGene(i) == 1) {
                    totalWeight += ((K) chromosome).itemsWeight[i];
                    totalValue += ((K) chromosome).itemsValue[i];
                }
            }
            double fitness = Double.MIN_VALUE;
            if (totalWeight <= ((K) chromosome).capacity)
                if (fitness < totalValue) fitness = totalValue;
            return 1 / fitness;
        }
    }

    static class KInitialization implements Initialization {

        private final int populationSize;
        private final int[] itemsWeight;
        private final int[] itemsValue;
        private final int capacity;
        private final int defaultGeneLength;

        KInitialization(int populationSize, int[] itemsWeight, int[] itemsValue, int capacity, int defaultGeneLength) {
            this.populationSize = populationSize;
            this.itemsWeight = itemsWeight;
            this.itemsValue = itemsValue;
            this.capacity = capacity;
            this.defaultGeneLength = defaultGeneLength;
        }

        /**
         * Generate chromosome population
         *
         * @return new chromosome population
         */
        @Override
        public List<Chromosome> generate() {
            List<Chromosome> list = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                K k = new K(defaultGeneLength, itemsWeight, itemsValue, capacity);
                list.add(k);
            }
            return list;
        }
    }
}
