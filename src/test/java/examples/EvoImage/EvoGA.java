package examples.EvoImage;


import genetics.GeneticAlgorithm;
import genetics.Population;
import org.apache.commons.math3.genetics.MutationPolicy;

public class EvoGA extends GeneticAlgorithm {
    private final MutationPolicy mutate;

    EvoGA(Population pop, final MutationPolicy mutate) {
        super(pop);
        this.mutate = mutate;
    }

    @Override
    protected Population evolvePopulation() {
        int populationSize = pop.size();
        Population newPopulation = new Population();

        for (int i = 0; i < populationSize; i++) {
            newPopulation.addChromosome(pop.getChromosome(i));
            newPopulation.addChromosome(mutate.mutate(pop.getChromosome(i)));
        }

        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }
}
