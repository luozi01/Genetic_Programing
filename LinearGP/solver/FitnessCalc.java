package com.zluo.lgp.solver;

import com.zluo.lgp.gp.LGPChromosome;

public interface FitnessCalc {
    double fitness(LGPChromosome chromosome);
}
