package cgp.solver;

import cgp.interfaces.CGPFitness;
import cgp.interfaces.CGPMutationStrategy;
import cgp.interfaces.CGPReproductionStrategy;
import cgp.interfaces.CGPSelectionStrategy;
import cgp.program.FunctionSet;

public class CartesianGP {
    public int mu;
    public int lambda;
    public char evolutionaryStrategy;
    public double mutationRate;
    public double recurrentConnectionProbability;
    public double connectionWeightRange;
    public int numInputs;
    public int numNodes;
    public int numOutputs;
    public int arity;
    public FunctionSet funcSet;
    public double targetFitness;
    public int updateFrequency;
    public int shortcutConnections;
    public CGPMutationStrategy mutationType;
    public String mutationTypeName;
    public CGPFitness fitnessFunction;
    public String fitnessFunctionName;
    public CGPSelectionStrategy selectionScheme;
    public String selectionSchemeName;
    public CGPReproductionStrategy reproductionScheme;
    public String reproductionSchemeName;
}
