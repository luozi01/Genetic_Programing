package cgp.Solver;

import cgp.gp.CGPChromosome;

public interface FitnessCalc {
    double fitness(CGPChromosome chromosome);
}
