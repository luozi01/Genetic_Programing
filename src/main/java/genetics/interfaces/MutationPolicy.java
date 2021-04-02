package genetics.interfaces;

import genetics.chromosome.Chromosome;

/**
 * Copy paste and modified from apache genetic library
 */
public interface MutationPolicy<T extends Chromosome> {
    T mutate(T var1);
}

