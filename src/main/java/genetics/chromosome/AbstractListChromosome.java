package genetics.chromosome;

import lombok.Getter;
import org.eclipse.collections.impl.factory.Lists;

import java.util.List;

/**
 * Copy paste and modified from apache genetic library
 *
 * @param <E> Data type
 */
@Getter
public abstract class AbstractListChromosome<E> extends Chromosome {
    private final List<E> representation;

    public AbstractListChromosome(final List<E> representation) {
        this(representation, true);
    }

    public AbstractListChromosome(final E[] representation) {
        this(Lists.mutable.of(representation));
    }

    public AbstractListChromosome(final List<E> representation, final boolean copyList) {
        checkValidity(representation);
        this.representation = copyList ? Lists.mutable.ofAll(representation) : representation;
    }

    /**
     * Check if presentation is in valid format
     *
     * @param chromosomeRepresentation chromosome to be checked
     */
    protected abstract void checkValidity(List<E> chromosomeRepresentation);

    /**
     * @return length of the chromosome
     */
    public int length() {
        return getRepresentation().size();
    }

    /**
     * @param representation new representation of the chromosome
     * @return new instance of chromosome
     */
    public abstract AbstractListChromosome<E> newCopy(List<E> representation);
}
