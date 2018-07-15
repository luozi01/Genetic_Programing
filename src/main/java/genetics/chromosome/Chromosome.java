package genetics.chromosome;

import com.google.gson.Gson;

public abstract class Chromosome {
    public double fitness = Double.NaN;

    public boolean betterThan(Chromosome another) {
        if (another == null)
            throw new IllegalStateException("compare chromosome cannot be null");
        return Double.compare(fitness, another.fitness) < 0;
    }

    public String serialization() {
        return new Gson().toJson(this);
    }
}
