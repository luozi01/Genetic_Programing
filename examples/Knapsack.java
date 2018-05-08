import com.zluo.ga.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Mike on 2016/11/3.
 */
public class Knapsack {

    private static int sum(K individual, int[] set) {
        int total = 0;
        for (int i = 0; i < individual.genes.length; i++) {
            if (individual.genes[i] == 1) total += set[i];
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
        GeneticAlgorithm<K> ga = new GeneticAlgorithm<>(
                new Population<>(new KGenerator(100)), new KFitness());
        ga.evolve(1000);
        K k = ga.getBest();
        System.out.println("Fittest: " + new KFitness().calc(k));
        System.out.println("Genes: " + k.toString());
        System.out.println("Weights: " + sum(k, K.getItemsWeight()));
        System.out.println("Value: " + sum(k, K.getItemsValue()));

        System.out.println("Optimal value: " + knapsack(K.getCapacity(), K.getItemsWeight(), K.getItemsValue()));
    }

    static class K implements Chromosome<K> {

        private static final int capacity = 10 * 40;
        private static int defaultGeneLength;
        private static int[] itemsWeight, itemsValue;

        static {
            System.out.println("Initialization");
//        itemsWeight = new int[]{6, 6, 5, 1, 4, 12, 15, 15};
//        itemsValue = new int[]{4, 9, 13, 15, 18, 7, 15, 3};
//        defaultGeneLength = itemsValue.length;

            defaultGeneLength = 40;
            Random random = new Random();
            itemsValue = new int[defaultGeneLength];
            itemsWeight = new int[defaultGeneLength];
            for (int i = 0; i < defaultGeneLength; i++) {
                itemsValue[i] = random.nextInt(15) + 1;
                itemsWeight[i] = random.nextInt(20) + 1;
            }
            System.out.println("Capacity: " + capacity);
            System.out.println("Values: \t" + Arrays.toString(itemsValue));
            System.out.println("Weights: \t" + Arrays.toString(itemsWeight));
            System.out.println("=======================================\n");
        }

        private byte[] genes = new byte[defaultGeneLength];

        static int[] getItemsWeight() {
            return itemsWeight;
        }

        static int[] getItemsValue() {
            return itemsValue;
        }

        static int getCapacity() {
            return capacity;
        }

        @Override
        public List<K> crossover(K chromosome, double uniformRate) {
            K gene1 = new K();
            K gene2 = new K();
            // Loop through genes
            for (int i = 0; i < genes.length; i++) {
                // Crossover
                if (Math.random() <= uniformRate) {
                    gene1.genes[i] = genes[i];
                    gene2.genes[i] = chromosome.genes[i];
                } else {
                    gene1.genes[i] = chromosome.genes[i];
                    gene2.genes[i] = genes[i];
                }
            }
            return new ArrayList<>(Arrays.asList(gene1, gene2));
        }

        @Override
        public K mutate(double mutationRate) {
            K clone = makeCopy();
            for (int i = 0; i < clone.genes.length; i++) {
                if (Math.random() <= mutationRate) {
                    clone.genes[i] = (byte) Math.round(Math.random());
                }
            }
            return clone;
        }

        @Override
        public K makeCopy() {
            K copy = new K();
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

    static class KGenerator implements Generator<K> {

        private int populationSize;

        KGenerator(int populationSize) {
            this.populationSize = populationSize;
        }

        @Override
        public List<K> generate() {
            List<K> list = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                K k = new K();
                for (int j = 0; j < k.genes.length; j++)
                    k.genes[j] = (byte) Math.round(Math.random());
                list.add(k);
            }
            return list;
        }
    }

    private static class KFitness implements Fitness<K> {
        @Override
        public double calc(K chromosome) {
            int totalWeight = 0, totalValue = 0;
            // Loop through our individuals genes and compare them to our candidates
            for (int i = 0; i < chromosome.genes.length; i++) {
                if (chromosome.genes[i] == 1) {
                    totalWeight += K.itemsWeight[i];
                    totalValue += K.itemsValue[i];
                }
            }
            double fitness = Double.MIN_VALUE;
            if (totalWeight <= K.capacity)
                if (fitness < totalValue) fitness = totalValue;
            return -fitness;
        }
    }
}
