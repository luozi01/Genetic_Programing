package genetics.executor;

import genetics.chromosome.Chromosome;
import genetics.common.Population;

public abstract class Executor<T extends Chromosome> {
    public abstract T evaluate(Population<T> population);

    public void shutDown() {
    }
}
