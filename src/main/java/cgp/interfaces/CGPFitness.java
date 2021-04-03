package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.program.DataSet;
import genetics.interfaces.FitnessCalc;

public interface CGPFitness extends FitnessCalc<CGPChromosome> {
    double calc(CGPChromosome chromosome, DataSet data);
}
