package genetics.executor;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.FitnessCalc;

import java.util.Optional;

public class SequentialExecutor<T extends Chromosome> extends Executor<T> {

    private final FitnessCalc<T> fitnessCalc;

    public SequentialExecutor(FitnessCalc<T> fitnessCalc) {
        this.fitnessCalc = fitnessCalc;
    }

    @Override
    public T evaluate(Population<T> population) {
        double bestFitness = Double.MAX_VALUE;
        Optional<T> bestChromosome = Optional.empty();
        for (T chromosome : population) {
            chromosome.setFitness(fitnessCalc.calc(chromosome));
            if (chromosome.getFitness() < bestFitness) {
                bestFitness = chromosome.getFitness();
                bestChromosome = Optional.of(chromosome);
            }
        }
        return bestChromosome.orElse(null);
    }
}
