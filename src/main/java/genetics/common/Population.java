package genetics.common;

import genetics.chromosome.Chromosome;
import genetics.interfaces.Initializer;
import lombok.NonNull;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Population<T extends Chromosome> implements Iterable<T> {
  private MutableList<T> chromosomes;

  /** @param initializer initialize function */
  public Population(@NonNull Initializer<T> initializer) {
    this.chromosomes = Lists.mutable.empty();
    List<T> generation = initializer.generate();
    if (generation != null) {
      this.chromosomes.addAll(generation);
    }
  }

  /** empty population instance */
  public Population() {
    this.chromosomes = Lists.mutable.empty();
  }

  /** @param chromosomes collection of chromosomes */
  public Population(Collection<T> chromosomes) {
    this.chromosomes = Lists.mutable.ofAll(chromosomes);
  }

  public Population(T[] chromosomes) {
    this.chromosomes = Lists.mutable.ofAll(Arrays.asList(chromosomes));
  }

  /**
   * @param index position to get
   * @return index in the population
   */
  public T getChromosome(int index) {
    return this.chromosomes.get(index);
  }

  /**
   * @param index position to set
   * @param chromosome target to set
   */
  public void setChromosome(int index, T chromosome) {
    this.chromosomes.set(index, chromosome);
  }

  /**
   * @param chromosome target
   * @return -1 if not in the population, index otherwise
   */
  public int indexOf(T chromosome) {
    return this.chromosomes.indexOf(chromosome);
  }

  /** @param chromosome target to add */
  public void addChromosome(T chromosome) {
    this.chromosomes.add(chromosome);
  }

  /** @param chromosomes collection of chromosomes to add */
  public void addChromosomes(List<T> chromosomes) {
    this.chromosomes.addAll(chromosomes);
  }

  /** @return size of the population */
  public int size() {
    return this.chromosomes.size();
  }

  /** @return population */
  public List<T> getChromosomes() {
    return this.chromosomes;
  }

  /**
   * Generate the next generation
   *
   * @param elitism the number of chromosomes will inherit by next generation
   * @return new population
   */
  public Population<T> nextGeneration(int elitism) {
    return new Population<>(
        this.chromosomes.sortThisByDouble(Chromosome::getFitness).take(elitism));
  }

  /**
   * Sort and trim the population to given length and return the best child
   *
   * @param length size of the population to be kept
   * @return the best child
   */
  public T trim(int length) {
    this.chromosomes = this.chromosomes.sortThisByDouble(Chromosome::getFitness).take(length);
    return chromosomes.get(0);
  }

  /** @return iterator object */
  @NonNull
  @Override
  public Iterator<T> iterator() {
    return chromosomes.iterator();
  }
}
