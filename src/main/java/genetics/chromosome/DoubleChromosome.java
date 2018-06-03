package genetics.chromosome;

import genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import java.util.List;

public abstract class DoubleChromosome extends AbstractListChromosome<Double> {

    public DoubleChromosome(List<Double> representation) throws InvalidRepresentationException {
        super(representation);
    }

    public DoubleChromosome(Double[] representation) throws InvalidRepresentationException {
        super(representation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(List<Double> chromosomeRepresentation) throws InvalidRepresentationException {
    }
}
