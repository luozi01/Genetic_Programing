package genetics.interfaces;

import genetics.chromosome.Chromosome;
import lombok.NonNull;

public interface FitnessCalc<T extends Chromosome> {
    double calc(@NonNull T chromosome);
}