package genetics.executor;

import genetics.chromosome.Chromosome;
import genetics.common.Population;

import java.util.concurrent.ExecutionException;

public abstract class Executor<T extends Chromosome> {
    public abstract T evaluate(Population<T> population) throws InterruptedException, ExecutionException;

    public void shutDown() {
    }
}
