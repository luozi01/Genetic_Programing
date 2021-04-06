package genetics.interfaces;

import genetics.chromosome.Chromosome;
import lombok.NonNull;

/**
 * Copy paste and modified from apache genetic library
 */
public interface MutationPolicy<T extends Chromosome> {
    T mutate(@NonNull T var1);
}

