package examples.EvoImage;


import ga.Chromosome;
import ga.Fitness;
import ga.GeneticAlgorithm;
import ga.Population;

public class EvoGA<E extends Chromosome<E>> extends GeneticAlgorithm<E> {
    EvoGA(Population<E> pop, Fitness<E> fitness) {
        super(pop, fitness);
    }

    @Override
    protected Population<E> evolvePopulation() {
        int populationSize = pop.size();
        Population<E> newPopulation = new Population<>();

        for (int i = 0; i < populationSize; i++) {
            newPopulation.addChromosome(pop.getChromosome(i));
            newPopulation.addChromosome(pop.getChromosome(i).mutate(0));
        }

        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }
}