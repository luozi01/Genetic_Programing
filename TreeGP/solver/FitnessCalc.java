package com.zluo.tgp.solver;

import com.zluo.tgp.gp.TGPChromosome;

public interface FitnessCalc {
    double fitness(TGPChromosome chromosome);
}
