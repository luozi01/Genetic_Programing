package lgp.solver;

import lgp.gp.LGPChromosome;

public interface LGPFitnessCalc {
    double fitness(LGPChromosome chromosome);
}
