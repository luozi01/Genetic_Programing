package ga;

import java.util.List;

public interface Generator<E extends Chromosome<E>> {
    /**
     * Generate a chromosome
     *
     * @return new chromosome
     */
    List<E> generate();
}
