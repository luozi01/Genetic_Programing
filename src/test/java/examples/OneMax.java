package examples;

import genetics.chromosome.AbstractListChromosome;
import genetics.chromosome.BinaryChromosome;
import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.crossover.UniformCrossover;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initialization;
import genetics.mutation.BinaryMutation;
import genetics.selection.TournamentSelection;
import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.collections.api.list.MutableList;

import java.util.ArrayList;
import java.util.List;

public class OneMax {

    public static void main(String[] args) {
        DescriptiveStatistics sequential_stats = new DescriptiveStatistics();
        DescriptiveStatistics parallelStats = new DescriptiveStatistics();
        for (int i = 0; i < 100; i++) {
            List<Chromosome> sample = new OMInitialization(1000, 10000).generate();
            System.out.println("Running sequential");
            sequential_stats.addValue(benchmark(new Population(sample), false));
            System.out.println("Running parallel");
            parallelStats.addValue(benchmark(new Population(sample), true));
            System.out.println("End of round: " + (i + 1));
        }
        System.out.println(sequential_stats.toString());
        System.out.println(parallelStats.toString());
    }

    private static long benchmark(Population population, boolean parallel) {
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new OMInitialization(10000, 10000),
                new OMEval(),
                new UniformCrossover<OM>(.5), .4,
                new BinaryMutation(), .2,
                new TournamentSelection(),
                3, 5);
        ga.setPopulation(population);
        ga.addIterationListener(environment -> {
            Chromosome best = environment.getBest();
//            System.out.printf("Generation: %s, Fitness: %s\n",
//                    environment.getGeneration(), best.fitness);

            if (!((BinaryChromosome) best).getRepresentation().contains(0)) {
                environment.terminate();
            }
        });
        if (parallel)
            ga.runInParallel();
        long startTime = System.currentTimeMillis();
        ga.evolve();
        long useTime = System.currentTimeMillis() - startTime;
        System.out.println(useTime);
        return useTime;
    }

    private static class OM extends BinaryChromosome {

        OM(MutableList<Integer> representation) throws InvalidRepresentationException {
            super(representation);
        }

        OM(int length) {
            this(BinaryChromosome.randomBinaryRepresentation(length));
        }

        @Override
        public AbstractListChromosome<Integer> newCopy(MutableList<Integer> representation) {
            return new OM(representation);
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

    private static class OMEval implements FitnessCalc {

        @Override
        public double calc(Chromosome chromosome) {
            int count = 0;
            // Loop through our individuals genes and compare them to our candidates
            for (int i = 0; i < ((OM) chromosome).getLength(); i++) {
                if (((OM) chromosome).getGene(i) == 1) count++;
            }
            return ((OM) chromosome).getLength() / (count * 1.0);
        }
    }

    private static class OMInitialization implements Initialization {

        private final int length;
        private final int populationSize;

        OMInitialization(int length, int populationSize) {
            this.length = length;
            this.populationSize = populationSize;
        }

        @Override
        public List<Chromosome> generate() {
            List<Chromosome> population = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                population.add(new OM(length));
            }
            return population;
        }
    }
}
