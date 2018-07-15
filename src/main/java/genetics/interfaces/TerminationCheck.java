package genetics.interfaces;

import genetics.driver.GeneticAlgorithm;

public interface TerminationCheck {
    void update(GeneticAlgorithm environment);
}