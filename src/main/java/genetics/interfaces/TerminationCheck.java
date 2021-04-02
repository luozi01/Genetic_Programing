package genetics.interfaces;

import genetics.chromosome.Chromosome;
import genetics.driver.GeneticAlgorithm;

public interface TerminationCheck<T extends Chromosome> {
    void update(GeneticAlgorithm<T> environment);
}