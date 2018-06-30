package genetics.chromosome;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

/**
 * Copy paste and modified from apache genetic library
 *
 * @param <E> Data type
 */
public abstract class AbstractListChromosome<E> extends Chromosome {
    private final MutableList<E> representation;

    public AbstractListChromosome(final MutableList<E> representation) {
        this(representation, true);
    }

    public AbstractListChromosome(final E[] representation) {
        this(Lists.mutable.of(representation));
    }

    public AbstractListChromosome(final MutableList<E> representation, final boolean copyList) {
        checkValidity(representation);
        if (copyList)
            this.representation = representation.toList();
        else this.representation = representation;
    }

    protected abstract void checkValidity(MutableList<E> chromosomeRepresentation);

    public MutableList<E> getRepresentation() {
        return representation;
    }

    public int getLength() {
        return getRepresentation().size();
    }

    public abstract AbstractListChromosome<E> newCopy(final MutableList<E> chromosomeRepresentation);
}
