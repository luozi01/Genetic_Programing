package com.zluo.ga;

public interface Fitness<E extends Chromosome<E>> {
    double calc(E chromosome);
}
