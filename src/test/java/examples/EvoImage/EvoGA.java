package examples.EvoImage;


import genetics.common.FitnessCalc;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.MutationPolicy;
import genetics.common.Population;

public class EvoGA extends GeneticAlgorithm {
    private final MutationPolicy mutate;

    EvoGA(Population pop, final FitnessCalc fitnessCalc, final MutationPolicy mutate) {
        super(pop, fitnessCalc);
        this.mutate = mutate;
    }

    @Override
    protected Population evolvePopulation() {
        int populationSize = population.size();
        Population newPopulation = new Population();

        for (int i = 0; i < populationSize; i++) {
            newPopulation.addChromosome(population.getChromosome(i));
            newPopulation.addChromosome(mutate.mutate(population.getChromosome(i)));
        }

        newPopulation.sort(comparator);
        newPopulation.trim(populationSize);
        return newPopulation;
    }
}
