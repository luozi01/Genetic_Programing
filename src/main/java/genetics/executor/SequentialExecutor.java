package genetics.executor;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.FitnessCalc;

import java.util.Optional;

public class SequentialExecutor extends Executor {

    private final FitnessCalc fitnessCalc;

    public SequentialExecutor(FitnessCalc fitnessCalc) {
        this.fitnessCalc = fitnessCalc;
    }

    @Override
    public Chromosome evaluate(Population population) {
        double bestFitness = Double.MAX_VALUE;
        Optional<Chromosome> bestChromosome = Optional.empty();
        for (Chromosome chromosome : population) {
            chromosome.fitness = fitnessCalc.calc(chromosome);
            if (chromosome.fitness < bestFitness) {
                bestFitness = chromosome.fitness;
                bestChromosome = Optional.of(chromosome);
            }
        }
        return bestChromosome.orElse(null);
    }
}
