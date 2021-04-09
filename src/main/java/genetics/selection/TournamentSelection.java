package genetics.selection;

import genetics.chromosome.Chromosome;
import genetics.common.Population;
import genetics.interfaces.SelectionPolicy;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.Comparator;
import java.util.Random;

public class TournamentSelection<T extends Chromosome> implements SelectionPolicy<T> {
  private final Random random = new Random(System.currentTimeMillis());

  /**
   * Random select two groups of children based on arity and return the best from each group
   *
   * @param population population
   * @param arity complete group size
   * @return best children from each tournament group
   */
  @Override
  public Pair<T, T> select(final Population<T> population, final int arity) {
    if (population.size() < arity) {
      throw new IllegalArgumentException(
          String.format(
              "Population size (%d) should be greater than arity (%d).", population.size(), arity));
    }
    // create a copy of population
    final MutableList<T> chromosomes = Lists.mutable.ofAll(population.getChromosomes());
    T c1 = tournament(chromosomes, arity);
    // exclude c1 from the population
    T c2 = tournament(chromosomes, arity);
    return c1.betterThan(c2) ? Tuples.pair(c1, c2) : Tuples.pair(c2, c1);
  }

  /**
   * Deterministic tournament selection, random select size of arity sub population without
   * replacement from the population and return the child with best fitness.
   *
   * @param population population
   * @param arity arity
   * @return child with best fitness
   */
  private T tournament(final MutableList<T> population, final int arity) {
    return population
        .shuffleThis(random)
        .take(arity)
        .min(Comparator.comparingDouble(Chromosome::getFitness));
  }
}
