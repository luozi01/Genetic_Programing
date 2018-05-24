package genetics;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

import java.util.ArrayList;
import java.util.List;

public class BinaryMutation implements MutationPolicy {

    @Override
    public Chromosome mutate(Chromosome chromosome) {
        assert chromosome instanceof BinaryChromosome;

        BinaryChromosome origin = (BinaryChromosome) chromosome;
        List<Integer> newRepr = new ArrayList<>(origin.getRepresentation());

        int geneIndex = (int) (Math.random() * origin.getLength());
        newRepr.set(geneIndex, origin.getRepresentation().get(geneIndex) == 0 ? 1 : 0);

        return origin.newCopy(newRepr);
    }
}
