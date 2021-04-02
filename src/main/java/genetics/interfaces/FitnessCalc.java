package genetics.interfaces;

import genetics.chromosome.Chromosome;

public interface FitnessCalc<T extends Chromosome> {
    double calc(T chromosome);
}