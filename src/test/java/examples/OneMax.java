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
import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.eclipse.collections.api.list.MutableList;

import java.util.ArrayList;
import java.util.List;

public class OneMax {

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new OMInitialization(100, 1000),
                new OMEval(),
                new UniformCrossover<OM>(.5), .4,
                new BinaryMutation(), .2,
                new TournamentSelection(),
                3, 5);
        ga.addIterationListener(environment -> {
            Chromosome best = environment.getBest();
            String represent = best.toString();
            System.out.printf("Generation: %s, Gene: %s, Fitness: %s\n",
                    environment.getGeneration(), represent, best.fitness);

            if (!((BinaryChromosome) best).getRepresentation().contains(0)) {
                environment.terminate();
            }
        });
        ga.evolve();
    }

    public static class OM extends BinaryChromosome {

        public OM(MutableList<Integer> representation) throws InvalidRepresentationException {
            super(representation);
        }

        public OM(int length) {
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

    static class OMEval implements FitnessCalc {

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

    static class OMInitialization implements Initialization {

        private int length, populationSize;

        public OMInitialization(int length, int populationSize) {
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
