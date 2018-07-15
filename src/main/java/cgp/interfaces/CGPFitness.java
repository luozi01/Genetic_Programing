package cgp.interfaces;

import cgp.gp.CGPChromosome;
import cgp.program.DataSet;
import cgp.solver.CartesianGP;

public interface CGPFitness {
    double calc(CartesianGP params, CGPChromosome chromo, DataSet data);
}
