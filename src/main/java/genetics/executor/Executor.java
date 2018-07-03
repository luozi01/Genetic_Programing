package genetics.executor;

import genetics.chromosome.Chromosome;
import genetics.common.Population;

public abstract class Executor {
    public abstract Chromosome evaluate(Population population);
}
