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
        Collections.shuffle(chromosomes);
        chromosomes.sort(comparator);
    }

    E getLast() {
        return chromosomes.get(chromosomes.size() - 1);
    }

    E getFirst() {
        return chromosomes.get(0);
    }

    public void set(int index, E e) {
        chromosomes.set(index, e);
    }

    public int indexOf(E e) {
        return chromosomes.indexOf(e);
    }

    public List<E> getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(List<E> chromosomes) {
        this.chromosomes = new ArrayList<>(chromosomes);
    }

    public void trim(int length) {
        chromosomes = chromosomes.subList(0, length);
    }

    @Override
    public Iterator<E> iterator() {
        return chromosomes.iterator();
    }
}