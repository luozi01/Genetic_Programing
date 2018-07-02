package genetics.selection;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.SelectionPolicy;
import genetics.utils.RandEngine;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.ArrayList;
import java.util.List;

public class TournamentSelection implements SelectionPolicy {

    @Override
    public Pair<Chromosome, Chromosome> select(final Population population,
                                               final int arity,
                                               final RandEngine randEngine) throws MathIllegalArgumentException {
        Chromosome c1 = tournament(population, arity, randEngine);
        Chromosome c2 = tournament(population, arity, randEngine);
        return c1.fitness < c2.fitness ? Tuples.pair(c1, c2) : Tuples.pair(c2, c1);
    }

    private Chromosome tournament(final Population population,
                                  final int arity,
                                  final RandEngine randEngine) throws MathIllegalArgumentException {
        if (population.size() < arity) {
            throw new MathIllegalArgumentException(LocalizedFormats.TOO_LARGE_TOURNAMENT_ARITY,
                    arity, population.size());
        }

        // create a copy of the chromosome list
        List<Chromosome> copy = new ArrayList<>(population.getChromosomes());
        Chromosome best = null;
        double fitness = Double.MAX_VALUE;
        for (int i = 0; i < arity; i++) {
            // select a random individual and add it to the tournament
            int rind = randEngine.nextInt(copy.size());
            Chromosome chose = copy.get(rind);
            if (chose.fitness < fitness) {
                fitness = chose.fitness;
                best = chose;
            }
            // do not select it again
            copy.remove(rind);
        }
        // the winner takes it all
        return best;
    }
}
