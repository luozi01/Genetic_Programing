package genetics.interfaces;

import genetics.chromosome.Chromosome;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

/**
 * Copy paste and modified from apache genetic library
 */
public interface MutationPolicy {
    Chromosome mutate(Chromosome var1) throws MathIllegalArgumentException;
}

