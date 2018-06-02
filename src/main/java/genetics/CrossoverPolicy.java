package genetics;

import genetics.utils.Pair;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

/**
 * Copy paste and modified from apache genetic library
 */
public interface CrossoverPolicy {
    Pair<Chromosome> crossover(Chromosome var1, Chromosome var2) throws MathIllegalArgumentException;
}
