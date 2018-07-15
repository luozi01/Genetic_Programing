package genetics.chromosome;

import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.eclipse.collections.api.list.MutableList;

public abstract class IntegerChromosome extends AbstractListChromosome<Integer> {
    /**
     * Constructor.
     *
     * @param representation list of {0,1} values representing the chromosome
     * @throws InvalidRepresentationException iff the <code>representation</code> can not represent a valid chromosome
     */
    public IntegerChromosome(MutableList<Integer> representation) throws InvalidRepresentationException {
        super(representation);
    }

    /**
     * Constructor.
     *
     * @param representation array of {0,1} values representing the chromosome
     * @throws InvalidRepresentationException iff the <code>representation</code> can not represent a valid chromosome
     */
    public IntegerChromosome(Integer[] representation) throws InvalidRepresentationException {
        super(representation);
    }
}
