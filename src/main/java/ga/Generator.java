package ga;

import java.util.List;

public interface Generator<E extends Chromosome<E>> {
    /**
     * Generate chromosome population
     *
     * @return new chromosome population
     */
    List<E> generate();
}
