package cgp.program;

import cgp.gp.CGPChromosome;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Results {
    public int numRuns;
    public CGPChromosome[] bestCGPChromosomes;

    /**
     * returns the average number of chromosome active nodes from repeated
     * run results specified in results.
     */
    public double getAverageActiveNodes() {
        double avgActiveNodes = IntStream.range(0, numRuns).
                mapToDouble(i -> bestCGPChromosomes[i].getNumActiveNodes()).sum();
        return avgActiveNodes / numRuns;
    }

    /**
     * returns the median number of chromosome active nodes from repeated
     * run results specified in results.
     */
    public double getMedianActiveNodes() {
        int[] array = IntStream.range(0, numRuns).
                map(i -> bestCGPChromosomes[i].getNumActiveNodes()).toArray();
        return medianInt(array, numRuns);
    }

    private double medianInt(int[] anArray, int length) {
        int[] copyArray = new int[length];

        /* make a copy of the array */
        System.arraycopy(anArray, 0, copyArray, 0, length);

        /* sort the copy array */
        Arrays.sort(copyArray, 0, length);

        return (double) (length % 2 == 0 ?
                (copyArray[(length / 2)] + copyArray[(length / 2) - 1]) / 2 :  /* if even */
                copyArray[(length - 1) / 2]);  /* if odd */
    }

    private double medianDouble(double[] anArray, int length) {
        double[] copyArray = new double[length];

        /* make a copy of the array */
        System.arraycopy(anArray, 0, copyArray, 0, length);

        /* sort the copy array */
        Arrays.sort(copyArray, 0, length);

        return length % 2 == 0 ?
                (copyArray[(length / 2)] + copyArray[(length / 2) - 1]) / 2 :    /* if even */
                copyArray[(length - 1) / 2]; /* if odd */
    }

    /**
     * returns the median chromosome fitness from repeated
     * run results specified in results.
     */
    public double getMedianFitness() {
        double[] array = IntStream.range(0, numRuns)
                .mapToDouble(i -> bestCGPChromosomes[i].fitness).toArray();
        return medianDouble(array, numRuns);
    }

    /**
     * returns the average number of generations used by each run  specified in results.
     */
    public double getAverageGenerations() {
        double avgGens = IntStream.range(0, numRuns).mapToDouble(i -> bestCGPChromosomes[i].getGeneration()).sum();
        return avgGens / numRuns;
    }

    /**
     * returns the average chromosome fitness from repeated
     * run results specified in results.
     */
    public double getAverageFitness() {
        double avgFit = IntStream.range(0, numRuns).mapToDouble(i -> bestCGPChromosomes[i].fitness).sum();
        return avgFit / numRuns;
    }

    /**
     * returns the median number of generations used by each run  specified in results.
     */
    public double getMedianGenerations() {
        int[] array = IntStream.range(0, numRuns).
                map(i -> bestCGPChromosomes[i].getGeneration()).toArray();
        return medianInt(array, numRuns);
    }
}
