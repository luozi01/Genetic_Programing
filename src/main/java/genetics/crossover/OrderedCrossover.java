package genetics.crossover;

import genetics.chromosome.AbstractListChromosome;
import genetics.interfaces.CrossoverPolicy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderedCrossover<T extends AbstractListChromosome<?>> implements CrossoverPolicy<T> {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * Performs order crossover on two parents
     *
     * @param first  first parent
     * @param second second parent
     * @return two new offspring
     */
    @Override
    public Pair<T, T> crossover(T first, T second) {
        if (first.length() != second.length()) {
            throw new IllegalArgumentException(String.format("Length for both chromosome should be the same, but chromosome1 has %d, chromosome2 has %d", first.length(), second.length()));
        }

        int length = first.length();
        // parent presentations
        final List parent1Rep = first.getRepresentation();
        final List parent2Rep = second.getRepresentation();
        // children
        MutableList child1 = Lists.mutable.withInitialCapacity(length);
        MutableList child2 = Lists.mutable.withInitialCapacity(length);

        int a = RANDOM.nextInt(length);
        int b;
        do {
            b = RANDOM.nextInt(length);
        } while (a == b);

        final int start = Math.min(a, b);
        final int end = Math.max(a, b);

        child1.addAll(parent1Rep.subList(start, end));
        child2.addAll(parent2Rep.subList(start, end));

        int index;
        Object c1, c2;
        for (int i = 1; i <= length; ++i) {
            // get the index of the current city
            index = (end + i) % length;

            c1 = parent1Rep.get(index);
            c2 = parent2Rep.get(index);
            if (!child1.contains(c2)) {
                child1.add(c2);
            }

            if (!child2.contains(c1)) {
                child2.add(c1);
            }
        }
        Collections.rotate(child1, start);
        Collections.rotate(child2, start);
        return Tuples.pair((T) first.newCopy(child1), (T) second.newCopy(child2));
    }
}
