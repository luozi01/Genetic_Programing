package lgp.solver;


import lgp.gp.LGPChromosome;

public interface FitnessCalc {
    double fitness(LGPChromosome chromosome);
}
