package com.zluo.ga;

public interface Interrupt<E extends Chromosome<E>> {
    void update(GeneticAlgorithm<E> environment);
}