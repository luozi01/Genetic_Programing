package EvoImage;


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
        final int populationSize = population.size();
        Population nextGeneration = new Population(population.getChromosomes());

        for (int i = 0; i < populationSize; i++) {
            nextGeneration.addChromosome(mutate.mutate(population.getChromosome(i)));
        }
        executor.evaluate(nextGeneration);
        updateGlobal(nextGeneration.trim(populationSize));
        return nextGeneration;
    }
}
