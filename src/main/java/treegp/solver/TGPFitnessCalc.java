package treegp.solver;

import treegp.gp.TGPChromosome;

public interface TGPFitnessCalc {
    double fitness(TGPChromosome chromosome);
}
