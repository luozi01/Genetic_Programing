package genetics.utils;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class SimpleRandEngine implements RandEngine {

    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public double uniform() {
        return random.nextDouble();
    }

    @Override
    public double normal(double mean, double sd) {
        NormalDistribution normal = new NormalDistribution(mean, sd);
        return normal.sample();
    }

    @Override
    public int nextInt(int upper) {
        return random.nextInt(upper);
    }


    @Override
    public int nextInt(int lower, int upper) {
        return random.nextInt(upper - lower) + lower;
    }

    @Override
    public double nextDouble(double lower, double upper) {
        return lower + (upper - lower) * random.nextDouble();
    }
}
