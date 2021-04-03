package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.gp.CGPParams;
import cgp.program.DataSet;
import genetics.interfaces.FitnessCalc;

public interface CGPFitness extends FitnessCalc<CGPChromosome> {
    double calc(CGPParams params, CGPChromosome chromosome, DataSet data);
}
