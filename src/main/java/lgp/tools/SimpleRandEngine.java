package lgp.tools;

import ga.utils.RandEngine;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class SimpleRandEngine implements RandEngine {

    private Random random = new Random(System.currentTimeMillis());

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
}