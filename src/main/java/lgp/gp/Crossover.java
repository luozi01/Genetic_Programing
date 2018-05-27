package lgp.gp;

import genetics.utils.RandEngine;
import lgp.enums.LGPCrossover;
import lgp.program.Instruction;
import lgp.solver.LinearGP;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Crossover implements CrossoverPolicy {

    private final LinearGP manager;

    public Crossover(LinearGP manager) {
        this.manager = manager;
    }

    @Override
    public ChromosomePair crossover(Chromosome c1, Chromosome c2) {
        if (!(c1 instanceof LGPChromosome && c2 instanceof LGPChromosome)) {
            throw new IllegalArgumentException("Both chromosome should be LGPChromosome");
        }
        return apply(((LGPChromosome) c1).makeCopy(), ((LGPChromosome) c2).makeCopy());
    }

    private ChromosomePair apply(LGPChromosome gp1, LGPChromosome gp2) {
        LGPCrossover crossoverType = manager.getCrossoverStrategy();
        ChromosomePair chromosome;
        switch (crossoverType) {
            case LINEAR:
                chromosome = linearCrossover(gp1, gp2);
                break;
            case ONE_POINT:
                chromosome = onePointCrossover(gp1, gp2);
                break;
            case ONE_SEGMENT:
                chromosome = oneSegmentCrossover(gp1, gp2);
                break;
            default:
                throw new NotImplementedException();
        }
        return chromosome;
    }

    /* Xianshun says: (From Section 5.7.3 of Linear Genetic Programming
       Crossover requires, by definition, that information is exchanged between individual programs.
       However, an exchange always includes two operations on an individual, the deletion and
       the insertion of a subprogram. The imperative program representation allows instructions to be
       deleted without replacement since instruction operands, e.g. register pointers, are always defined.
       Instructions may also be inserted at any position without a preceding deletion, at least if the maximum
       program length is not exceeded.

       If we want linear crossover to be less disruptive it may be a good idea to execute only one operation per
       individual. this consideration motivates a one-segment or one-way recombination of linear genetic
       programs as described by Algorithm 5.3.

       Standard linear crossover may also be referred to as two-segment recompilations, in these terms.
    */
    private ChromosomePair oneSegmentCrossover(LGPChromosome gp1, LGPChromosome gp2) {
        RandEngine randEngine = manager.getRandEngine();
        double prob_r = randEngine.uniform();
        if (gp1.length() < manager.getMaxProgramLength() &&
                prob_r <= manager.getInsertionProbability() || gp1.length() == manager.getMinProgramLength()) {

            int i = randEngine.nextInt(gp1.length());
            int s = randEngine.nextInt(1, Math.min(gp2.length(), manager.getMaxSegmentLength()));

            //Todo different from pseudo code
            if (gp1.length() + s > manager.getMaxProgramLength()) {
                s = manager.getMaxProgramLength() - gp1.length() + 1;
            }
            if (s == gp2.length()) s = gp2.length() - 1;

            int i2 = randEngine.nextInt(gp2.length() - s);

            List<Instruction> instructions = new ArrayList<>();
            for (int j = i2; j != (i2 + s); j++) {
                Instruction clone = gp2.getInstructions().get(j).makeCopy();
                clone.resign(gp1);
                instructions.add(clone);
            }
            gp1.getInstructions().addAll(i, instructions);
        }

        if (gp1.length() > manager.getMinProgramLength() &&
                prob_r > manager.getInsertionProbability() || gp1.length() == manager.getMaxProgramLength()) {

            int s = randEngine.nextInt(1, Math.min(gp2.length(), manager.getMaxSegmentLength()));

            if (gp1.length() < s || gp1.length() - s < manager.getMinProgramLength()) {
                s = gp1.length() - manager.getMinProgramLength();
            }

            int i1 = randEngine.nextInt(gp1.length() - s);

            for (int j = s - 1; j >= i1; j--) {
                gp1.getInstructions().remove(j);
            }
        }
        return new ChromosomePair(gp1, gp2);
    }

    /*
    This operator is derived from Algorithm 5.2 in Section 5.7.2 of Linear Genetic Programming
    */
    private ChromosomePair onePointCrossover(LGPChromosome gp1, LGPChromosome gp2) {
        RandEngine randEngine = manager.getRandEngine();

        // length(gp1) <= length(gp2)
        if (gp1.length() > gp2.length()) {
            LGPChromosome temp = gp1;
            gp1 = gp2;
            gp2 = temp;
        }

        int max_distance_of_crossover_points;
        if (gp1.length() - 1 < manager.getMaxDistanceOfCrossoverPoints())
            max_distance_of_crossover_points = manager.getMaxDistanceOfCrossoverPoints();
        else max_distance_of_crossover_points = gp1.length() - 1;

        // Randomly select an instruction position i_k (crossover point) in program gp_k
        int i1 = randEngine.nextInt(gp1.length());
        int i2 = randEngine.nextInt(gp2.length());
        int crossover_point_distance = Math.abs(i1 - i2);

        // 1. assure abs(i1-i2) <= max_distance_of_crossover_points
        // 2. assure l(s1) <= l(s2)
        boolean not_feasible = true;
        int count = 0;

        while (not_feasible && count < 10) {
            // ensure that the maximum distance between two crossover points is not exceeded
            if (crossover_point_distance > max_distance_of_crossover_points) {
                i1 = randEngine.nextInt(gp1.length());
                i2 = randEngine.nextInt(gp2.length());
                crossover_point_distance = (i1 > i2) ? (i1 - i2) : (i2 - i1);
            } else {
                int ls1 = gp1.length() - i1;
                int ls2 = gp2.length() - i2;
                // assure than l(s1) <= l(s2)
                if (ls1 > ls2) {
                    not_feasible = true;
                    i1 = randEngine.nextInt(gp1.length());
                    i2 = randEngine.nextInt(gp2.length());
                    crossover_point_distance = (i1 > i2) ? (i1 - i2) : (i2 - i1);
                } else {
                    // assure the length of the program after crossover do not exceed the maximum program length
                    // or below minimum program length
                    if ((gp2.length() - (ls2 - ls1)) < manager.getMinProgramLength() ||
                            (gp1.length() + (ls2 - ls1)) > manager.getMaxProgramLength()) {
                        // when the length constraint is not satisfied,
                        // make the segments to be exchanged the same length
                        if (gp1.length() >= gp2.length()) {
                            i1 = i2;
                        } else {
                            i2 = i1;
                        }
                        crossover_point_distance = 0;
                        not_feasible = true;
                    } else {
                        not_feasible = false;
                    }
                }
            }
            count++;
        }

        List<Instruction> instructions1 = new ArrayList<>(gp1.getInstructions());
        List<Instruction> instructions2 = new ArrayList<>(gp2.getInstructions());

        List<Instruction> instructions1_1 = new ArrayList<>();
        List<Instruction> instructions1_2 = new ArrayList<>();

        List<Instruction> instructions2_1 = new ArrayList<>();
        List<Instruction> instructions2_2 = new ArrayList<>();

        for (int i = 0; i < i1; ++i) {
            instructions1_1.add(instructions1.get(i));
        }
        for (int i = i1; i < instructions1.size(); ++i) {
            instructions1_2.add(instructions1.get(i));
        }

        for (int i = 0; i < i2; ++i) {
            instructions2_1.add(instructions2.get(i));
        }
        for (int i = i2; i < instructions2.size(); ++i) {
            instructions2_2.add(instructions2.get(i));
        }

        instructions1.clear();
        instructions2.clear();

        for (int i = 0; i < i1; ++i) {
            instructions1.add(instructions1_1.get(i));
        }
        for (Instruction i : instructions2_2) {
            i.resign(gp1);
            instructions1.add(i);
        }

        for (int i = 0; i < i2; ++i) {
            instructions2.add(instructions2_1.get(i));
        }
        for (Instruction i : instructions1_2) {
            i.resign(gp2);
            instructions2.add(i);
        }
        return new ChromosomePair(gp1, gp2);
    }

    //Todo need to optimize
    // this is derived from Algorithm 5.1 of Section 5.7.1 of Linear Genetic Programming
    // this linear crossover can also be considered as two-point crossover
    private ChromosomePair linearCrossover(LGPChromosome gp1, LGPChromosome gp2) {
        RandEngine randEngine = manager.getRandEngine();

        // length(gp1) <= length(gp2)
        if (gp1.length() > gp2.length()) {
            LGPChromosome temp = gp1;
            gp1 = gp2;
            gp2 = temp;
        }

        // select i1 from gp1 and i2 from gp2 such that abs(i1-i2) <= max_crossover_point_distance
        // max_crossover_point_distance=min{length(gp1) - 1, m_max_distance_of_crossover_points}
        int i1 = randEngine.nextInt(gp1.length());
        int i2 = randEngine.nextInt(gp2.length());
        int cross_point_distance = (i1 > i2) ? (i1 - i2) : (i2 - i1);

        int distance_max;
        if (gp1.length() - 1 > manager.getMaxDistanceOfCrossoverPoints())
            distance_max = manager.getMaxDistanceOfCrossoverPoints();
        else distance_max = gp1.length() - 1;

        while (cross_point_distance > distance_max) {
            i1 = randEngine.nextInt(gp1.length());
            i2 = randEngine.nextInt(gp2.length());
            cross_point_distance = (i1 > i2) ? (i1 - i2) : (i2 - i1);
        }

        //Select an instruction segment sk starting at position ik with length 1 ≤ l(s_k) ≤ min(l(gp_k) − i_k, sl_max).
        int s1;
        if ((gp1.length() - i1) > manager.getMaxSegmentLength())
            s1 = manager.getMaxSegmentLength();
        else s1 = gp1.length() - i1;

        int s2;
        if ((gp2.length() - i2) > manager.getMaxSegmentLength())
            s2 = manager.getMaxSegmentLength();
        else s2 = gp2.length() - i2;

        // select s1 from gp1 (start at i1) and s2 from gp2 (start at i2)
        // such that length(s1) <= length(s2)
        // and abs(length(s1) - length(s2)) <= m_max_difference_of_segment_length)
        int s1_length = 1 + randEngine.nextInt(s1);
        int s2_length = 1 + randEngine.nextInt(s2);
        int segment_length_difference = (s1_length > s2_length) ? (s1_length - s2_length) : (s2_length - s1_length);
        while (s1_length > s2_length || segment_length_difference > manager.getMaxDifferenceOfSegmentLength()) {
            s1_length = 1 + randEngine.nextInt(s1);
            s2_length = 1 + randEngine.nextInt(s2);
            segment_length_difference = (s1_length > s2_length) ? (s1_length - s2_length) : (s2_length - s1_length);
        }

        if (gp2.length() - (s2_length - s1_length) < manager.getMinProgramLength() ||
                ((gp1.length() + (s2_length - s1_length)) > manager.getMaxProgramLength())) {
            if (randEngine.uniform() < 0.5) {
                s2_length = s1_length;
            } else {
                s1_length = s2_length;
            }
            if (i1 + s1_length > gp1.length()) s1_length = gp1.length() - i1;
            if (i2 + s2_length > gp2.length()) s2_length = gp2.length() - i2;
        }


        List<Instruction> instructions1 = gp1.getInstructions();
        List<Instruction> instructions2 = gp2.getInstructions();

        List<Instruction> instructions1_1 = new ArrayList<>();
        List<Instruction> instructions1_2 = new ArrayList<>();
        List<Instruction> instructions1_3 = new ArrayList<>();

        List<Instruction> instructions2_1 = new ArrayList<>();
        List<Instruction> instructions2_2 = new ArrayList<>();
        List<Instruction> instructions2_3 = new ArrayList<>();

        for (int i = 0; i < i1; ++i) {
            instructions1_1.add(instructions1.get(i));
        }
        for (int i = i1; i < i1 + s1_length; ++i) {
            instructions1_2.add(instructions1.get(i));
        }
        for (int i = i1 + s1_length; i < instructions1.size(); ++i) {
            instructions1_3.add(instructions1.get(i));
        }

        for (int i = 0; i < i2; ++i) {
            instructions2_1.add(instructions2.get(i));
        }
        for (int i = i2; i < i2 + s2_length; ++i) {
            instructions2_2.add(instructions2.get(i));
        }
        for (int i = i2 + s2_length; i < instructions2.size(); ++i) {
            instructions2_3.add(instructions2.get(i));
        }

        instructions1.clear();
        instructions2.clear();

        for (int i = 0; i < i1; ++i) {
            instructions1.add(instructions1_1.get(i));
        }
        for (int i = 0; i < s2_length; ++i) {
            Instruction clone = instructions2_2.get(i);
            clone.resign(gp1);
            instructions1.add(clone);
        }
        instructions1.addAll(instructions1_3);

        for (int i = 0; i < i2; ++i) {
            instructions2.add(instructions2_1.get(i));
        }
        for (int i = 0; i < s1_length; ++i) {
            Instruction clone = instructions1_2.get(i);
            clone.resign(gp2);
            instructions2.add(clone);
        }
        instructions2.addAll(instructions2_3);

        return new ChromosomePair(gp1, gp2);
    }
}
