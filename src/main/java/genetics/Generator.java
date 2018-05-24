package genetics;

import org.apache.commons.math3.genetics.Chromosome;

import java.util.List;

public interface Generator {
    /**
     * Generate chromosome population
     *
     * @return new chromosome population
     */
    List<Chromosome> generate();
}
