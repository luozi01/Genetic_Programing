package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copy paste and modified from apache genetic library
 * @param <E> Data type
 */
public abstract class AbstractListChromosome<E> extends Chromosome {
    private final List<E> representation;

    public AbstractListChromosome(final List<E> representation) {
        this(representation, true);
    }

    public AbstractListChromosome(final E[] representation) {
        this(Arrays.asList(representation));
    }

    public AbstractListChromosome(final List<E> representation, final boolean copyList) {
        checkValidity(representation);
        this.representation =
                Collections.unmodifiableList(copyList ? new ArrayList<>(representation) : representation);
    }

    protected abstract void checkValidity(List<E> chromosomeRepresentation);

    protected List<E> getRepresentation() {
        return representation;
    }

    public int getLength() {
        return getRepresentation().size();
    }

    public abstract AbstractListChromosome<E> newCopy(final List<E> chromosomeRepresentation);
}
