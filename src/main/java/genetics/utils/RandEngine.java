package genetics.utils;

public interface RandEngine {
    double uniform();

    double normal(double mean, double sd);

    int nextInt(int upper);

    int nextInt(int lower, int upper);

    double nextDouble(double lower, double upper);
}
