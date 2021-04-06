package genetics.interfaces;

import genetics.chromosome.Chromosome;

import java.util.List;

public interface Initializer<T extends Chromosome> {
    /**
     * Generate chromosome population
     *
     * @return new chromosome population
     */
    List<T> generate();
}
