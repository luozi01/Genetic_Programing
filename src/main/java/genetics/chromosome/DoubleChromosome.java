package genetics.chromosome;

import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.eclipse.collections.api.list.MutableList;

public abstract class DoubleChromosome extends AbstractListChromosome<Double> {

    public DoubleChromosome(MutableList<Double> representation) throws InvalidRepresentationException {
        super(representation);
    }

    public DoubleChromosome(Double[] representation) throws InvalidRepresentationException {
        super(representation);
    }
}
