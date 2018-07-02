package examples.EvoImage;


import genetics.common.Population;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initialization;
import genetics.interfaces.MutationPolicy;

public class EvoGA extends GeneticAlgorithm {
    private final MutationPolicy mutate;

    EvoGA(final Initialization initialization,
          final FitnessCalc fitnessCalc,
          final MutationPolicy mutate) {
        super(initialization, fitnessCalc);
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
        return newPopulation;
    }
}
