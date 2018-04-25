package com.zluo.ga;

import java.util.List;

public interface Chromosome<E extends Chromosome<E>> {
    /**
     * Crossover function for genetic algorithm
     *
     * @param chromosome  chromosome to crossover with
     * @param uniformRate uniformRate
     * @return new chromosome after crossover
     */
    List<E> crossover(E chromosome, double uniformRate);

    /**
     * Mutate given chromosome with given mutationRate
     *
     * @param mutationRate mutationRate
     */
    void mutate(double mutationRate);

    E makeCopy();
}
