package genetics.common;


import genetics.chromosome.Chromosome;
import genetics.interfaces.Initialization;
import org.eclipse.collections.impl.list.mutable.FastList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Population implements Iterable<Chromosome> {

    private List<Chromosome> chromosomes;

    public Population(Initialization initialization) {
        if (initialization == null)
            throw new IllegalStateException("Initialization method cannot be null");
        chromosomes = initialization.generate();
    }

    public Population() {
        chromosomes = new ArrayList<>();
    }

    public Population(List<Chromosome> chromosomes) {
        this.chromosomes = FastList.newList(chromosomes);
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

    public void trim(int length) {
        chromosomes = chromosomes.stream()
                .sorted(Comparator.comparingDouble(o -> o.fitness))
                .limit(length).collect(Collectors.toList());
    }

    @Override
    public Iterator<Chromosome> iterator() {
        return chromosomes.iterator();
    }
}