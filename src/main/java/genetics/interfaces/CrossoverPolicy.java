package genetics.interfaces;

import genetics.chromosome.Chromosome;
import org.eclipse.collections.api.tuple.Pair;

/**
 * Copy paste and modified from apache genetic library
 */
public interface CrossoverPolicy<T extends Chromosome> {
    Pair<T, T> crossover(T var1, T var2);
}
