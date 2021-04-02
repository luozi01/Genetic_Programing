package genetics.crossover;

import genetics.chromosome.AbstractListChromosome;
import genetics.interfaces.CrossoverPolicy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.List;
import java.util.Random;

public class UniformCrossover<T extends AbstractListChromosome<?>> implements CrossoverPolicy<T> {

    /**
     * The mixing ratio.
     */
    private final double ratio;

    /**
     * @param ratio Crossover ratio
     */
    public UniformCrossover(final double ratio) {
        if (ratio < 0.0d || ratio > 1.0d)
            throw new IllegalArgumentException(String.format("Ratio should be [0, 1] but found %f", ratio));
        this.ratio = ratio;
    }

    /**
     * @param first  first parent
     * @param second second parent
     * @return two children crossover from two parents
     */
    public Pair<T, T> crossover(final T first, final T second) {
        if (first == null || second == null) {
            throw new NullPointerException(); // Todo
        }
        return mate(first, second);
    }

    /**
     * @param first  first parent
     * @param second second parent
     * @return two children crossover from two parents
     */
    // Todo handle wildcard
    private Pair<T, T> mate(final T first,
                            final T second) {
        if (first.length() != second.length()) {
            throw new IllegalArgumentException(String.format("Length for both chromosome should be the same, but chromosome1 has %d, chromosome2 has %d", first.length(), second.length()));
        }

        final int length = first.length();
        // array representations of the parents
        final List parent1Rep = first.getRepresentation();
        final List parent2Rep = second.getRepresentation();
        // and of the children
        final MutableList child1Rep = Lists.mutable.ofInitialCapacity(length);
        final MutableList child2Rep = Lists.mutable.ofInitialCapacity(length);

        final Random random = new Random();

        for (int index = 0; index < length; index++) {
            if (random.nextDouble() < ratio) {
                child1Rep.add(parent2Rep.get(index));
                child2Rep.add(parent1Rep.get(index));
            } else {
                child1Rep.add(parent1Rep.get(index));
                child2Rep.add(parent2Rep.get(index));
            }
        }

        return Tuples.pair((T) first.newCopy(child1Rep), (T) second.newCopy(child2Rep));
    }
}
