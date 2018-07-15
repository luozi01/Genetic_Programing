package genetics.interfaces;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.utils.RandEngine;
import org.eclipse.collections.api.tuple.Pair;

public interface SelectionPolicy {
    Pair<Chromosome, Chromosome> select(Population population, int arity, RandEngine randEngine);
}
