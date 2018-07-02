package cgp.gp;

import cgp.Solver.CartesianGP;
import cgp.enums.CGPEvolvePolicy;
import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.driver.GeneticAlgorithm;
import genetics.interfaces.FitnessCalc;
import genetics.interfaces.Initialization;
import genetics.interfaces.MutationPolicy;
import genetics.interfaces.SelectionPolicy;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.FastList;

import java.util.Comparator;
import java.util.List;

public class CGA extends GeneticAlgorithm {

    private final CartesianGP manager;
    private final MutationPolicy mutationPolicy;
    private final SelectionPolicy selectionPolicy;

    public CGA(final Initialization initialization,
               final FitnessCalc fitnessCalc,
               final MutationPolicy mutationPolicy,
               final SelectionPolicy selectionPolicy,
               final CartesianGP manager) {
        super(initialization, fitnessCalc);
        this.manager = manager;
        this.mutationPolicy = mutationPolicy;
        this.selectionPolicy = selectionPolicy;
    }

    @Override
    protected Population evolvePopulation() {
        Population newPop = new Population();
        CGPEvolvePolicy populationReplacement = manager.getEvolvePolicy();
        if (populationReplacement == CGPEvolvePolicy.MU_PLUS_LAMBDA) {
            newPop = muPlusLambdaEvolve();
        } else if (populationReplacement == CGPEvolvePolicy.TOURNAMENT) {
            newPop = tournament(manager.getTournamentSize());
        }
        return newPop;
    }

    private Population muPlusLambdaEvolve() {
        Population nextGeneration = new Population();
        List<Chromosome> chromosomes = FastList.newList(population.getChromosomes());
        chromosomes.sort(Comparator.comparingDouble(o -> o.fitness));
        for (int i = 0; i < manager.getMu(); i++) {
            nextGeneration.addChromosome(chromosomes.get(i));
        }
        for (int i = 0; i < manager.getLambda(); i++) {
            chromosomes.sort(Comparator.comparingDouble(o -> o.fitness));
            Chromosome copy = chromosomes.get(0);
            nextGeneration.addChromosome(mutationPolicy.mutate(copy));
        }
        return nextGeneration;
    }

    //Todo fix
    private Population tournament(int tournamentSize) {
        Pair<Chromosome, Chromosome> pair;
        for (int i = 0; i < tournamentSize; i++) {
            pair = selectionPolicy.select(population, manager.getTournamentSize(), manager.getRandEngine());
            population.addChromosome(mutationPolicy.mutate(pair.getOne()));
        }
        return population;
    }
}
