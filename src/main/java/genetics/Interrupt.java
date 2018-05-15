package genetics;

public interface Interrupt<E extends Chromosome<E>> {
    void update(GeneticAlgorithm<E> environment);
}