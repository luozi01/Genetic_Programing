package genetics.chromosome;

import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

/**
 * Copy paste and modified from apache genetic library
 */
public abstract class BinaryChromosome extends IntegerChromosome {

    /**
     * Constructor.
     *
     * @param representation list of {0,1} values representing the chromosome
     * @throws InvalidRepresentationException iff the <code>representation</code> can not represent a valid chromosome
     */
    public BinaryChromosome(MutableList<Integer> representation) throws InvalidRepresentationException {
        super(representation);
    }

    /**
     * Constructor.
     *
     * @param representation array of {0,1} values representing the chromosome
     * @throws InvalidRepresentationException iff the <code>representation</code> can not represent a valid chromosome
     */
    public BinaryChromosome(Integer[] representation) throws InvalidRepresentationException {
        super(representation);
    }

    /**
     * Returns a representation of a random binary array of length <code>length</code>.
     *
     * @param length length of the array
     * @return a random binary array of length <code>length</code>
     */
    public static MutableList<Integer> randomBinaryRepresentation(int length) {
        // random binary list
        MutableList<Integer> rList = Lists.mutable.withInitialCapacity(length);
        RandEngine randEngine = new SimpleRandEngine();
        for (int j = 0; j < length; j++) {
            rList.add(randEngine.nextInt(2));
        }
        return rList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(MutableList<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
        for (int i : chromosomeRepresentation)
            if (i < 0 || i > 1)
                throw new InvalidRepresentationException(LocalizedFormats.INVALID_BINARY_DIGIT, i);
    }
}
