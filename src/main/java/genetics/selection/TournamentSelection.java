package genetics.selection;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.SelectionPolicy;
import genetics.utils.RandEngine;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.Comparator;

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
        MutableList<Chromosome> copy = FastList.newList(population.getChromosomes());
        MutableList<Chromosome> selection = Lists.mutable.empty();
        for (int i = 0; i < arity; i++) {
            int rind = randEngine.nextInt(copy.size());
            selection.add(copy.get(rind));
            copy.remove(rind);
        }
        return selection.min(Comparator.comparingDouble(o -> o.fitness));
    }
}
