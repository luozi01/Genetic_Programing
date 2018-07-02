package genetics.common;


import genetics.chromosome.Chromosome;
import genetics.interfaces.Initialization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Population implements Iterable<Chromosome> {

    private final List<Chromosome> chromosomes;

    public Population(Initialization initialization) {
        chromosomes = initialization.generate();
    }

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public Population(List<Chromosome> chromosomes) {
        this.chromosomes = new ArrayList<>(chromosomes);
    }

    public Chromosome getChromosome(int index) {
        return chromosomes.get(index);
    }

    public void setChromosome(int index, Chromosome chromosome) {
        this.chromosomes.set(index, chromosome);
    }

    public int indexOf(Chromosome chromosome) {
        return chromosomes.indexOf(chromosome);
    }

    public void addChromosome(Chromosome chromosome) {
        chromosomes.add(chromosome);
    }

    public void addChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes.addAll(chromosomes);
    }

    public int size() {
        return chromosomes.size();
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    @Override
    public Iterator<Chromosome> iterator() {
        return chromosomes.iterator();
    }
}