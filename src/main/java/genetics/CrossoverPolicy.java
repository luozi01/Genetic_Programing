package genetics;

import org.apache.commons.math3.genetics.Chromosome;

import java.util.List;

public interface CrossoverPolicy {
    List<Chromosome> crossover(Chromosome c1, Chromosome c2);
}
