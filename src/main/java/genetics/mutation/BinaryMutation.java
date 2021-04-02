package genetics.mutation;

import genetics.chromosome.BinaryChromosome;
import genetics.interfaces.MutationPolicy;
import org.eclipse.collections.impl.factory.Lists;

import java.util.List;
import java.util.Random;

public class BinaryMutation<T extends BinaryChromosome> implements MutationPolicy<T> {
    public T mutate(T original) {
        if (original == null) {
            throw new NullPointerException();
        }

        // make sure does not modify the original chromosome
        List<Integer> newRepr = Lists.mutable.ofAll(original.getRepresentation());

        // randomly select a gene
        int geneIndex = new Random().nextInt(original.length());
        // and change it
        newRepr.set(geneIndex, original.getRepresentation().get(geneIndex) == 0 ? 1 : 0);

        return (T) original.newCopy(newRepr);
    }
}
