package genetics.mutation;

import genetics.chromosome.AbstractListChromosome;
import genetics.interfaces.MutationPolicy;
import org.eclipse.collections.impl.factory.Lists;

import java.util.List;
import java.util.Random;

public class SwapMutation<T extends AbstractListChromosome<?>> implements MutationPolicy<T> {

    private final Random random = new Random(System.currentTimeMillis());
    private final double ratio;

    public SwapMutation(final double ratio) {
        if (ratio < 0.0d || ratio > 1.0d)
            throw new IllegalArgumentException(String.format("Ratio should be [0, 1] but found %f", ratio));
        this.ratio = ratio;
    }

    @Override
    public T mutate(T original) {
        if (original == null) {
            throw new NullPointerException();
        }
        final List representation = Lists.mutable.ofAll(original.getRepresentation());
        for (int i = 0; i < representation.size(); i++) {
            if (random.nextDouble() < ratio) {
                int idx;
                do {
                    idx = random.nextInt(representation.size());
                } while (idx == i);

                Object temp = representation.get(i);
                representation.set(i, representation.get(idx));
                representation.set(idx, temp);
            }
        }
        return (T) original.newCopy(representation);
    }
}
