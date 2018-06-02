package cgp.gp;

import cgp.Solver.CartesianGP;
import cgp.enums.CGPEvolvePolicy;
import genetics.*;

public class CGA extends GeneticAlgorithm {

    private final CartesianGP manager;
    private final MutationPolicy mutationPolicy;

    public CGA(Population pop,
               final Fitness fitness,
               final MutationPolicy mutationPolicy,
               final CartesianGP manager) {
        super(pop, fitness);
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
            pop.sort(comparator);
            Chromosome copy = pop.getFirst();
            pop.addChromosome(mutationPolicy.mutate(copy));
        }
        pop.sort(comparator);
        pop.trim(manager.getPopulationSize());
        return pop;
    }

    private Population tournament(int populationSize, int tournamentSize) {
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome chromosome = tournamentSelection(manager.getLambda());
            pop.addChromosome(mutationPolicy.mutate(chromosome));
        }
        pop.sort(comparator);
        pop.trim(populationSize);
        return pop;
    }
}
