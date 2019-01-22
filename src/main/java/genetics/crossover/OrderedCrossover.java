package genetics.crossover;

import genetics.chromosome.AbstractListChromosome;
import genetics.chromosome.Chromosome;
import genetics.interfaces.CrossoverPolicy;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.Collections;

/**
 * A direct transfer and modified from apache math3 library
 */
public class OrderedCrossover<T> implements CrossoverPolicy {

    @Override
    public Pair<Chromosome, Chromosome> crossover(Chromosome var1, Chromosome var2) throws MathIllegalArgumentException {
        if (var1 instanceof AbstractListChromosome<?> && var2 instanceof AbstractListChromosome<?>) {
            return mate((AbstractListChromosome<T>) var1, (AbstractListChromosome<T>) var2);
        } else {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME);
        }
    }

    private Pair<Chromosome, Chromosome> mate(final AbstractListChromosome<T> first,
                                              final AbstractListChromosome<T> second) throws DimensionMismatchException {
        int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        } else {
            final MutableList<T> parent1Rep = first.getRepresentation();
            final MutableList<T> parent2Rep = second.getRepresentation();
            // children
            MutableList<T> child1 = Lists.mutable.withInitialCapacity(length);
            MutableList<T> child2 = Lists.mutable.withInitialCapacity(length);

            MutableSet<T> child1Set = Sets.mutable.withInitialCapacity(length);
            MutableSet<T> child2Set = Sets.mutable.withInitialCapacity(length);
            RandEngine random = new SimpleRandEngine();

            int a = random.nextInt(length);

            int b;
            do {
                b = random.nextInt(length);
            } while (a == b);

            int lb = FastMath.min(a, b);
            int ub = FastMath.max(a, b);

            child1.addAll(parent1Rep.subList(lb, ub + 1));
            child1Set.addAll(child1);
            child2.addAll(parent2Rep.subList(lb, ub + 1));
            child2Set.addAll(child2);

            for (int i = 1; i <= length; ++i) {
                int idx = (ub + i) % length;
                T item1 = parent1Rep.get(idx);
                T item2 = parent2Rep.get(idx);
                if (!child1Set.contains(item2)) {
                    child1.add(item2);
                    child1Set.add(item2);
                }

                if (!child2Set.contains(item1)) {
                    child2.add(item1);
                    child2Set.add(item1);
                }
            }

            Collections.rotate(child1, lb);
            Collections.rotate(child2, lb);
            return Tuples.pair(first.newCopy(child1), second.newCopy(child2));
        }
    }
}
