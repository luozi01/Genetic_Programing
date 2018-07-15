package genetics.interfaces;

import genetics.chromosome.Chromosome;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.eclipse.collections.api.tuple.Pair;

/**
 * Copy paste and modified from apache genetic library
 */
public interface CrossoverPolicy {
    Pair<Chromosome, Chromosome> crossover(Chromosome var1, Chromosome var2) throws MathIllegalArgumentException;
}
