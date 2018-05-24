package lgp.solver;


import genetics.utils.Observation;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import lgp.enums.LGPCrossover;
import lgp.enums.LGPInitialization;
import lgp.enums.LGPSelection;
import lgp.program.Operator;
import lgp.program.operators.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class LinearGP {
    private RandEngine randEngine = new SimpleRandEngine();

    private int populationSize = 500;

    // number of registers of a linear program
    private int registerCount;
    private List<Double> constantSet = new ArrayList<>();
    private List<Operator> operatorSet = new ArrayList<>();

    // SEC: parameters for population initialization
    // BEGIN
    private LGPInitialization initialization = LGPInitialization.VARIABLE_LENGTH;
    private int popInitConstantProgramLength = 10;
    private int popInitMaxProgramLength = 15;
    private int popInitMinProgramLength = 5;
    // END

    // SEC: parameters for crossover
    // BEGIN
    private double crossoverRate = 0.1;
    private LGPCrossover crossoverStrategy = LGPCrossover.LINEAR;
    private int maxProgramLength = 100;
    private int minProgramLength = 20;
    private int maxSegmentLength = 10;
    private int maxDistanceOfCrossoverPoints = 10;
    private int maxDifferenceOfSegmentLength = 5;
    private double insertionProbability = 0.5;
    // END

    // SEC: parameters for micro-mutation
    // BEGIN
    private double microMutationRate = 0.75;
    private double microMutateConstantStandardDeviation = 1;
    private double microMutateOperatorRate = 0.5;
    private double microMutateRegisterRate = 0.5;
    private double microMutateConstantRate = 0.5;
    // END

    // SEC: parameters for macro-mutation
    // BEGIN
    private double macroMutationRate = 0.25;
    private boolean effectiveMutation = false;
    private double macroMutateInsertionRate = 0.5;
    private double macroMutateDeletionRate = 0.5;
    private int macroMutateMaxProgramLength = 100;
    private int macroMutateMinProgramLength = 20;
    // END

    // SEC: parameters for replacement
    // BEGIN
    private LGPSelection replacementStrategy = LGPSelection.TOURNAMENT;
    private double replacementProbability = 1.0;
    private int tournamentSize = 3;
    // END

    private List<Observation> targets = new LinkedList<>();

    public static LinearGP defaultConfig() {
        LinearGP lgp = new LinearGP();
        lgp.getOperatorSet().addAll(Arrays.asList(new ADD(), new SUB(), new DIV(), new MUL(), new POW(), new IFGREATERTHAN()));
        lgp.addConstants(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0);
        return lgp;
    }

    public void addObservations(List<Observation> targets) {
        this.targets.addAll(targets);
    }

    private void addConstants(double... constants) {
        for (double constant : constants) {
            constantSet.add(constant);
        }
    }

}
