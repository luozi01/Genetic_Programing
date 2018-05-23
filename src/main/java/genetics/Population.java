package genetics;

import java.util.*;

public class Population<E extends Chromosome<E>> implements Iterable<E> {

    private List<E> chromosomes;

    public Population(Generator<E> generator) {
        chromosomes = generator.generate();
    }

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public E getChromosome(int index) {
        return chromosomes.get(index);
    }

    public void addChromosome(E chromosome) {
        chromosomes.add(chromosome);
    }

    public int size() {
        return chromosomes.size();
    }

    public void sort(Comparator<E> comparator) {
        chromosomes.sort(comparator);
    }

    public E getFirst() {
        return chromosomes.get(0);
    }

    public List<E> getChromosomes() {
        return chromosomes;
    }

    public void trim(int length) {
        chromosomes.subList(length, chromosomes.size()).clear();
    }

    @Override
    public Iterator<E> iterator() {
        return chromosomes.iterator();
    }
}