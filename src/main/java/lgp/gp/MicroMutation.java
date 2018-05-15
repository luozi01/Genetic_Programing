package lgp.gp;

import genetics.utils.RandEngine;
import lgp.program.Instruction;
import lgp.solver.LinearGP;

import java.util.List;

class MicroMutation {

    /// <summary>
    /// the micro-mutation is derived from Linear Genetic Programming 2004 chapter 6 section 6.2.2
    /// three type selection probability are first determined and roulette wheel is used to decide which
    /// mutation type is to be performed
    /// 1. if micro-mutate-operator type is selected, then randomly pick an instruction and
    /// randomly select an instruction and mutate its operator to some other operator from the operator set
    /// 2. if micro-mutate-register type is selected, then randomly pick an instruction and
    /// randomly select one of the two operands, then
    /// 2.1 with a constant selection probability p_{const}, a randomly selected constant register is assigned to the selected operand
    /// 2.2 with probability 1-p_{const}, a randomly selected variable register is assigned to the selected operand
    /// p_{const} is the proportion of instruction that holds a constant value.
    /// 3. if micro-mutate-constant type is selected, then randomly pick an effective instruction with a constant as one
    /// of its register value, mutate the constant to c+$N(0, \omega_{\mu}$
    /// </summary>
    static void apply(LGPChromosome chromosome) {
        double mutateOperatorRate = chromosome.getManager().getMicroMutateOperatorRate();
        double mutateRegisterRate = chromosome.getManager().getMicroMutateRegisterRate();
        double mutateConstantRate = chromosome.getManager().getMicroMutateConstantRate();

        double sum = mutateConstantRate + mutateOperatorRate + mutateRegisterRate;

        mutateRegisterRate /= sum;
        mutateOperatorRate /= sum;

        double operator_sector = mutateOperatorRate;
        double register_sector = operator_sector + mutateRegisterRate;

        double r = chromosome.getManager().getRandEngine().uniform();
        if (r < operator_sector) {
            mutateInstructionOperator(chromosome, chromosome.getManager().getRandEngine());
        } else if (r < register_sector) {
            mutateInstructionRegister(chromosome, chromosome.getManager().getRandEngine());
        } else {
            mutateInstructionConstant(chromosome, chromosome.getManager());
        }
    }

    /**
     * randomly select an (effective) instruction with a constant c
     * change constant c through a standard deviation from the current value
     * c:=c + normal(mean:=0, standard_deviation)
     *
     * @param chromosome chromosome
     * @param manager    manager
     */
    private static void mutateInstructionConstant(LGPChromosome chromosome, LinearGP manager) {
        Instruction selected = null;
        for (Instruction instruction : chromosome.getInstructions()) {
            if (!instruction.isStructuralIntron() &&
                    (instruction.getR1().isConstant() || instruction.getR2().isConstant()) &&
                    (selected == null || manager.getRandEngine().uniform() < 0.5)) {
                selected = instruction;
            }
        }
        if (selected != null) {
            selected.mutateConstant(manager.getRandEngine(), manager.getMicroMutateConstantStandardDeviation());
        }
    }

    private static void mutateInstructionRegister(LGPChromosome chromosome, RandEngine randEngine) {
        final List<Instruction> instructions = chromosome.getInstructions();
        final int instructionCount = instructions.size();
        Instruction selected_instruction = instructions.get(randEngine.nextInt(instructionCount));
        double p_const = 0;
        for (Instruction instruction : instructions) {
            if (instruction.getR1().isConstant() || instruction.getR2().isConstant()) {
                p_const++;
            }
        }
        p_const /= instructionCount;
        selected_instruction.mutateRegister(chromosome, randEngine, p_const);
    }

    private static void mutateInstructionOperator(LGPChromosome chromosome, RandEngine randEngine) {
        final List<Instruction> instructions = chromosome.getInstructions();
        final int instructionCount = instructions.size();
        Instruction instruction = instructions.get(randEngine.nextInt(instructionCount));
        instruction.setOperator(chromosome.getRandomOperator());
    }
}
