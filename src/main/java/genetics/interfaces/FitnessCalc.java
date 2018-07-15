package genetics.interfaces;

import genetics.chromosome.Chromosome;

public interface FitnessCalc {
    double calc(Chromosome chromosome);
}