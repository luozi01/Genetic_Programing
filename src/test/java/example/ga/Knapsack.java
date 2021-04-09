package example.ga;

import genetics.chromosome.BinaryChromosome;
import genetics.crossover.UniformCrossover;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initializer;
import genetics.mutation.BinaryMutation;
import genetics.selection.TournamentSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/** Created by Mike on 2016/11/3. */
public class Knapsack {

  private static int sum(K individual, int[] set) {
    int total = 0;
    for (int i = 0; i < individual.length(); i++) {
      total += set[i] * individual.getGene(i);
    }
    return total;
  }

  /**
   * find the maximum value that can be put from a knapsack of capacity W
   *
   * @param W capacity
   * @param wt items weight
   * @param val items value
   * @return the maximum value that can be put from a knapsack of capacity W
   */
  private static int knapsack(int W, int[] wt, int[] val) {
    int i, w;
    int[][] K = new int[val.length + 1][W + 1];

    // Build table K[][] from bottom up manner
    for (i = 0; i <= val.length; i++) {
      for (w = 0; w <= W; w++) {
        if (i == 0 || w == 0) K[i][w] = 0;
        else if (wt[i - 1] <= w)
          K[i][w] = Math.max(val[i - 1] + K[i - 1][w - wt[i - 1]], K[i - 1][w]);
        else K[i][w] = K[i - 1][w];
      }
    }

    return K[val.length][W];
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    System.out.println("Initialization");

    int defaultGeneLength = 40;
    int capacity = 10 * defaultGeneLength;
    Random randEngine = new Random();
    int[] itemsValue = new int[defaultGeneLength];
    int[] itemsWeight = new int[defaultGeneLength];
    for (int i = 0; i < defaultGeneLength; i++) {
      itemsValue[i] = randEngine.nextInt(15) + 1;
      itemsWeight[i] = randEngine.nextInt(20) + 1;
    }
    System.out.println("Capacity: " + capacity);
    System.out.println("Values: \t" + Arrays.toString(itemsValue));
    System.out.println("Weights: \t" + Arrays.toString(itemsWeight));
    System.out.println("=======================================\n");

    final int optimal = knapsack(capacity, itemsWeight, itemsValue);

    GeneticAlgorithm<K> instance =
        GeneticAlgorithm.<K>builder()
            .initializer(
                new KInitializer(100, itemsWeight, itemsValue, capacity, defaultGeneLength))
            .fitnessCalc(new KEvaluate())
            .crossoverPolicy(new UniformCrossover<>(.5))
            .uniformRate(.4)
            .mutationPolicy(new BinaryMutation<>())
            .mutationRate(.02)
            .selectionPolicy(new TournamentSelection<>())
            .tournamentSize(3)
            .elitism(1)
            .build();
    instance.addTerminateListener(
        o -> {
          K best = o.getBest();
          int value = sum(best, itemsValue);
          System.out.printf("Generation: %s,\tValue: %d\n", o.getGeneration(), value);

          if (value == optimal) {
            o.terminate();
          }
        });
    instance.evolve(2000);
    K best = instance.getBest();

    System.out.println("Genes: " + best.toString());
    System.out.println("Weights: " + sum(best, itemsWeight));
    System.out.println("Value: " + sum(best, itemsValue));

    System.out.println("Optimal value: " + knapsack(capacity, itemsWeight, itemsValue));
  }

  private static class K extends BinaryChromosome {
    private int[] itemsWeight, itemsValue;
    private int capacity;

    K(List<Integer> representation) {
      super(representation);
    }

    K(int defaultGeneLength, int[] itemsWeight, int[] itemsValue, int capacity) {
      this(BinaryChromosome.randomBinaryRepresentation(defaultGeneLength));
      this.capacity = capacity;
      this.itemsValue = itemsValue;
      this.itemsWeight = itemsWeight;
    }

    @Override
    public K newCopy(List<Integer> representation) {
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

  static class KEvaluate implements FitnessCalc<K> {

    @Override
    public double calc(K chromosome) {
      int totalWeight = 0, totalValue = 0;
      // Loop through our individuals genes and compare them to our candidates
      for (int i = 0; i < chromosome.length(); i++) {
        if (chromosome.getGene(i) == 1) {
          totalWeight += chromosome.itemsWeight[i];
          totalValue += chromosome.itemsValue[i];
        }
      }
      double fitness = Double.MIN_VALUE;
      if (totalWeight <= chromosome.capacity) if (fitness < totalValue) fitness = totalValue;
      return 1 / fitness;
    }
  }

  static class KInitializer implements Initializer<K> {
    private final int populationSize;
    private final int[] itemsWeight;
    private final int[] itemsValue;
    private final int capacity;
    private final int defaultGeneLength;

    KInitializer(
        int populationSize,
        int[] itemsWeight,
        int[] itemsValue,
        int capacity,
        int defaultGeneLength) {
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
    public List<K> generate() {
      List<K> list = new ArrayList<>();
      for (int i = 0; i < populationSize; i++) {
        K k = new K(defaultGeneLength, itemsWeight, itemsValue, capacity);
        list.add(k);
      }
      return list;
    }
  }
}
