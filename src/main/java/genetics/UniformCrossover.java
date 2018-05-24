package genetics;

import genetics.utils.RandEngine;
import org.apache.commons.math3.genetics.Chromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniformCrossover<E> implements CrossoverPolicy {

    private final double ratio;

    public UniformCrossover(final double ratio) {
        if (ratio < 0.0d || ratio > 1.0d) {
            throw new IllegalArgumentException("ratio should between 0 and 1");
        }
        this.ratio = ratio;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Chromosome> crossover(Chromosome c1, Chromosome c2) {
        if (!(c1 instanceof AbstractListChromosome<?> && c2 instanceof AbstractListChromosome<?>)) {
            throw new IllegalArgumentException("Both chromosome should be ListChromosome");
        }
        return mate((AbstractListChromosome<E>) c1, (AbstractListChromosome<E>) c2);
    }

    private List<Chromosome> mate(final AbstractListChromosome<E> first, final AbstractListChromosome<E> second) {
        final int length = first.getLength();
        if (length != second.getLength()) {
            throw new IllegalArgumentException("Length should be equal");
        }

        // array representations of the parents
        final List<E> parent1Rep = first.getRepresentation();
        final List<E> parent2Rep = second.getRepresentation();
        // and of the children
        final List<E> child1Rep = new ArrayList<>(length);
        final List<E> child2Rep = new ArrayList<>(length);

        final RandEngine random = GeneticAlgorithm.randEngine;

        for (int index = 0; index < length; index++) {

            if (random.uniform() < ratio) {
                // swap the bits -> take other parent
                child1Rep.add(parent2Rep.get(index));
                child2Rep.add(parent1Rep.get(index));
            } else {
                child1Rep.add(parent1Rep.get(index));
                child2Rep.add(parent2Rep.get(index));
            }
        }

        return Arrays.asList(first.newCopy(child1Rep),
                second.newCopy(child2Rep));
    }
}
