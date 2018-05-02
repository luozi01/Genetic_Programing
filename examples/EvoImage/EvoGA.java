package EvoImage;

import com.zluo.ga.Chromosome;
import com.zluo.ga.Fitness;
import com.zluo.ga.GeneticAlgorithm;
import com.zluo.ga.Population;

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
        }

        for (int i = 0; i < populationSize; i++) {
            E e = pop.getChromosome(i).makeCopy();
            e.mutate(0);
            newPopulation.addChromosome(e);
        }
        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }
}
