package cgp.gp;

import cgp.Solver.CartesianGP;
import cgp.enums.CGPEvolvePolicy;
import genetics.*;

public class CGA extends GeneticAlgorithm {

    private final CartesianGP manager;
    private final MutationPolicy mutationPolicy;

    public CGA(Population pop,
               final FitnessCalc fitnessCalc,
               final MutationPolicy mutationPolicy,
               final CartesianGP manager) {
        super(pop, fitnessCalc);
        this.manager = manager;
        this.mutationPolicy = mutationPolicy;
    }

    @Override
    protected Population evolvePopulation() {
        Population newPop = new Population();
        CGPEvolvePolicy populationReplacement = manager.getEvolvePolicy();
        if (populationReplacement == CGPEvolvePolicy.MU_PLUS_LAMBDA) {
            newPop = muPlusLambdaEvolve();
        } else if (populationReplacement == CGPEvolvePolicy.TOURNAMENT) {
            newPop = tournament(manager.getPopulationSize(), manager.getTournamentSize());
        }
        return newPop;
    }

    private Population muPlusLambdaEvolve() {
        for (int i = 0; i < manager.getLambda(); i++) {
            population.sort(comparator);
            Chromosome copy = population.getBest();
            population.addChromosome(mutationPolicy.mutate(copy));
        }
        population.sort(comparator);
        population.trim(manager.getPopulationSize());
        return population;
    }

    private Population tournament(int populationSize, int tournamentSize) {
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome chromosome = tournamentSelection(manager.getLambda());
            population.addChromosome(mutationPolicy.mutate(chromosome));
        }
        population.sort(comparator);
        population.trim(populationSize);
        return population;
    }
}
