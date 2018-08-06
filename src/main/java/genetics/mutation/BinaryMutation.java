package genetics.mutation;

import genetics.chromosome.BinaryChromosome;
import genetics.chromosome.Chromosome;
import genetics.interfaces.MutationPolicy;
import genetics.utils.SimpleRandEngine;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.eclipse.collections.api.list.MutableList;

/**
 * Copy paste and modified from apache genetic library
 */
public class BinaryMutation implements MutationPolicy {
    public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof BinaryChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_BINARY_CHROMOSOME);
        }

        BinaryChromosome origChrom = (BinaryChromosome) original;
        MutableList<Integer> newRepr = origChrom.getRepresentation();

        // randomly select a gene
        int geneIndex = new SimpleRandEngine().nextInt(origChrom.getLength());
        // and change it
        newRepr.set(geneIndex, origChrom.getRepresentation().get(geneIndex) == 0 ? 1 : 0);

        return origChrom.newCopy(newRepr);
    }
}
