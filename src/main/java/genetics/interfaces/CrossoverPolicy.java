package genetics.interfaces;

import genetics.chromosome.Chromosome;
import lombok.NonNull;
import org.eclipse.collections.api.tuple.Pair;

/**
 * Copy paste and modified from apache genetic library
 */
public interface CrossoverPolicy<T extends Chromosome> {
    Pair<T, T> crossover(@NonNull T var1, @NonNull T var2);
}
