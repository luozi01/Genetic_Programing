package genetics.chromosome;

public abstract class Chromosome {
    public double fitness = Double.NaN;

    public boolean betterThan(Chromosome another) {
        if (another == null)
            throw new IllegalStateException("compare chromosome cannot be null");
        return Double.compare(fitness, another.fitness) < 0;
    }
}
