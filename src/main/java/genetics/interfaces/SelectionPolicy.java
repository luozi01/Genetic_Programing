package genetics.interfaces;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import org.eclipse.collections.api.tuple.Pair;

public interface SelectionPolicy<T extends Chromosome> {
    Pair<T, T> select(Population<T> population, int arity);
}
