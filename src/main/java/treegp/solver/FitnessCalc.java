package treegp.solver;

import treegp.gp.TGPChromosome;

public interface FitnessCalc {
    double fitness(TGPChromosome chromosome);
}
