package genetics.common;

import genetics.chromosome.Chromosome;

import java.util.List;

public interface Generator {
    /**
     * Generate chromosome population
     *
     * @return new chromosome population
     */
    List<Chromosome> generate();
}
