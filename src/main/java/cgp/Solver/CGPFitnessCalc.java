package cgp.Solver;

import cgp.gp.CGPChromosome;

public interface CGPFitnessCalc {
    double fitness(CGPChromosome chromosome);
}
