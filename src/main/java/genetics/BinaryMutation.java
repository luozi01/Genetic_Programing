package genetics;

import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

import java.util.ArrayList;
import java.util.List;

/**
 * Copy paste and modified from apache genetic library
 */
public class BinaryMutation implements MutationPolicy{
    public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof BinaryChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_BINARY_CHROMOSOME);
        }

        BinaryChromosome origChrom = (BinaryChromosome) original;
        List<Integer> newRepr = new ArrayList<>(origChrom.getRepresentation());

        // randomly select a gene
        int geneIndex = new SimpleRandEngine().nextInt(origChrom.getLength());
        // and change it
        newRepr.set(geneIndex, origChrom.getRepresentation().get(geneIndex) == 0 ? 1 : 0);

        return origChrom.newCopy(newRepr);
    }
}
