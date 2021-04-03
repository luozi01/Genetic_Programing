package genetics.chromosome;

import lombok.Data;

@Data
public abstract class Chromosome {
    private double fitness = Double.NaN;

    /**
     * @param another chromosome compares to
     * @return if current chromosome has smaller fitness than the compared one
     */
    public boolean betterThan(Chromosome another) {
        if (another == null) {
            throw new IllegalStateException("compare chromosome cannot be null");
        }
        return Double.compare(fitness, another.fitness) < 0;
    }

    /**
     * @return if chromosome is evaluated
     */
    public boolean notEvaluated() {
        return Double.isNaN(fitness);
    }
}