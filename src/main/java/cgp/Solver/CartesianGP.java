package cgp.Solver;

import cgp.enums.CGPEvolvePolicy;
import cgp.enums.CGPMutationPolicy;
import cgp.program.Function;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cgp.program.SymbolicRegressionFunctions.*;

@Getter
@Setter
public class CartesianGP {

    private CGPEvolvePolicy evolvePolicy = CGPEvolvePolicy.MU_PLUS_LAMBDA;
    private CGPMutationPolicy mutationPolicy = CGPMutationPolicy.PROBABILISTIC;

    private int rows = 40;
    private int cols = 3;
    private int nodes = rows * cols;
    private int input, output;
    private int populationSize = 5;
    private int levelsBack = 2;
    private int arity;

    private double mutationProbability = .3;
    private double percentMutationRate = .3;
    private int mu = 1;
    private int lambda = populationSize - mu;
    private int tournamentSize = 1;

    private RandEngine randEngine = new SimpleRandEngine();

    private List<Function> functions = new ArrayList<>();

    public static CartesianGP defaultConfig(int input, int output) {
        CartesianGP gp = new CartesianGP();
        gp.functions.addAll(Arrays.asList(ADD, SUB, DIV, MUL, POW, SQRT, SIN, COS, EXP));
        gp.arity = gp.getMaxArity();
        gp.input = input;
        gp.output = output;
        return gp;
    }

    private int getMaxArity() {
        int arity = 0;
        for (Function function : functions) {
            if (function.arity() > arity)
                arity = function.arity();
        }
        return arity;
    }

    public int getRandomInt(int limit) {
        return randEngine.nextInt(limit);
    }

    public double getRandomDouble() {
        return randEngine.uniform();
    }

    public int getRandomFunction() {
        return randEngine.nextInt(functions.size());
    }
}


