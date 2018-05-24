package genetics;

import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import java.util.ArrayList;
import java.util.List;

public abstract class BinaryChromosome extends AbstractListChromosome<Integer> {

    public BinaryChromosome(List<Integer> representation) throws InvalidRepresentationException {
        super(representation);
    }

    public BinaryChromosome(Integer[] representation) throws InvalidRepresentationException {
        super(representation);
    }

    public static List<Integer> randomBinaryRepresentation(int length) {
        // random binary list
        List<Integer> rList = new ArrayList<>(length);
        for (int j = 0; j < length; j++) {
            rList.add(GeneticAlgorithm.randEngine.nextInt(2));
        }
        return rList;
    }

    @Override
    protected void checkValidity(List<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
        for (int i : chromosomeRepresentation) {
            if (i < 0 || i > 1) {
                throw new InvalidRepresentationException(LocalizedFormats.INVALID_BINARY_DIGIT, i);
            }
        }
    }

    @Override
    protected boolean isSame(Chromosome another) {
        // type check
        if (!(another instanceof BinaryChromosome)) {
            return false;
        }
        BinaryChromosome anotherBc = (BinaryChromosome) another;
        // size check
        if (getLength() != anotherBc.getLength()) {
            return false;
        }

        for (int i = 0; i < getRepresentation().size(); i++) {
            if (!(getRepresentation().get(i).equals(anotherBc.getRepresentation().get(i)))) {
                return false;
            }
        }
        // all is ok
        return true;
    }
}
