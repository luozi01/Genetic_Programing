package genetics.interfaces;

import genetics.chromosome.Chromosome;

import java.util.List;

public interface Initialization {
    /**
     * Generate chromosome population
     *
     * @return new chromosome population
     */
    List<Chromosome> generate();
}
