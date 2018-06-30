package genetics;

import genetics.utils.Pair;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy paste and modified from apache genetic library
 *
 * @param <T> Chromosome type
 */
public class UniformCrossover<T> implements CrossoverPolicy {

    /**
     * The mixing ratio.
     */
    private final double ratio;

    /**
     * Creates a new {@link UniformCrossover} policy using the given mixing ratio.
     *
     * @param ratio the mixing ratio
     * @throws OutOfRangeException if the mixing ratio is outside the [0, 1] range
     */
    public UniformCrossover(final double ratio) throws OutOfRangeException {
        if (ratio < 0.0d || ratio > 1.0d) {
            throw new OutOfRangeException(LocalizedFormats.CROSSOVER_RATE, ratio, 0.0d, 1.0d);
        }
        this.ratio = ratio;
    }

    /**
     * Returns the mixing ratio used by this {@link CrossoverPolicy}.
     *
     * @return the mixing ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException iff one of the chromosomes is
     *                                      not an instance of {@link AbstractListChromosome}
     * @throws DimensionMismatchException   if the length of the two chromosomes is different
     */
    @SuppressWarnings("unchecked")
    public Pair<Chromosome> crossover(final Chromosome first, final Chromosome second)
            throws DimensionMismatchException, MathIllegalArgumentException {

        if (!(first instanceof AbstractListChromosome<?> && second instanceof AbstractListChromosome<?>)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME);
        }
        return mate((AbstractListChromosome<T>) first, (AbstractListChromosome<T>) second);
    }

    /**
     * Helper for {@link #crossover(Chromosome, Chromosome)}. Performs the actual crossover.
     *
     * @param first  the first chromosome
     * @param second the second chromosome
     * @return the pair of new chromosomes that resulted from the crossover
     * @throws DimensionMismatchException if the length of the two chromosomes is different
     */
    private Pair<Chromosome> mate(final AbstractListChromosome<T> first,
                                  final AbstractListChromosome<T> second) throws DimensionMismatchException {
        final int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        }

        // array representations of the parents
        final List<T> parent1Rep = first.getRepresentation();
        final List<T> parent2Rep = second.getRepresentation();
        // and of the children
        final List<T> child1Rep = new ArrayList<>(length);
        final List<T> child2Rep = new ArrayList<>(length);

        final RandEngine random = new SimpleRandEngine();

        for (int index = 0; index < length; index++) {

            if (random.uniform() < ratio) {
                // swap the bits -> take other parent
                child1Rep.add(parent2Rep.get(index));
                child2Rep.add(parent1Rep.get(index));
            } else {
                child1Rep.add(parent1Rep.get(index));
                child2Rep.add(parent2Rep.get(index));
            }
        }

        return new Pair<>(first.newCopy(child1Rep),
                second.newCopy(child2Rep));
    }
}
