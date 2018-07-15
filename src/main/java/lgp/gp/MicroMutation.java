package lgp.gp;

import genetics.chromosome.Chromosome;
import genetics.interfaces.MutationPolicy;
import genetics.utils.RandEngine;
import lgp.program.Instruction;
import lgp.solver.LinearGP;

import java.util.List;

public class MicroMutation implements MutationPolicy {

    private final LinearGP manager;

    public MicroMutation(LinearGP manager) {
        this.manager = manager;
    }


    @Override
    public Chromosome mutate(Chromosome chromosome) {
        if (!(chromosome instanceof LGPChromosome)) {
            throw new IllegalArgumentException("Chromosome should be LGPChromosome");
        }
        return apply(((LGPChromosome) chromosome).makeCopy());
    }

    private Chromosome apply(LGPChromosome chromosome) {
        double mutateOperatorRate = chromosome.getManager().getMicroMutateOperatorRate();
        double mutateRegisterRate = chromosome.getManager().getMicroMutateRegisterRate();
        double mutateConstantRate = chromosome.getManager().getMicroMutateConstantRate();

        double sum = mutateConstantRate + mutateOperatorRate + mutateRegisterRate;

        mutateRegisterRate /= sum;
        mutateOperatorRate /= sum;

        double operator_sector = mutateOperatorRate;
        double register_sector = operator_sector + mutateRegisterRate;

        double r = manager.getRandEngine().uniform();
        Chromosome clone;
        if (r < operator_sector) {
            clone = mutateInstructionOperator(chromosome, manager.getRandEngine());
        } else if (r < register_sector) {
            clone = mutateInstructionRegister(chromosome, manager.getRandEngine());
        } else {
            clone = mutateInstructionConstant(chromosome);
        }
        return clone;
    }

    /**
     * randomly select an (effective) instruction with a constant c
     * change constant c through a standard deviation from the current value
     * c:=c + normal(mean:=0, standard_deviation)
     *
     * @param chromosome chromosome
     */
    private Chromosome mutateInstructionConstant(LGPChromosome chromosome) {
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
        return chromosome;
    }

    /**
     * if micro-mutate-register type is selected, then randomly pick an instruction and
     * randomly select one of the two operands, then
     * 1. with a constant selection probability p_{const}, a randomly selected
     * constant register is assigned to the selected operand
     * 2. with probability 1-p_{const}, a randomly selected variable register is assigned to the selected operand
     * p_{const} is the proportion of instruction that holds a constant value.
     *
     * @param chromosome chromosome
     * @param randEngine random engine
     */
    private Chromosome mutateInstructionRegister(LGPChromosome chromosome, RandEngine randEngine) {
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
        return chromosome;
    }

    /**
     * if micro-mutate-operator type is selected, then randomly pick an instruction and
     * randomly select an instruction and mutate its operator to some other operator from the operator set
     *
     * @param chromosome chromosome
     * @param randEngine random engine
     */
    private Chromosome mutateInstructionOperator(LGPChromosome chromosome, RandEngine randEngine) {
        final List<Instruction> instructions = chromosome.getInstructions();
        final int instructionCount = instructions.size();
        Instruction instruction = instructions.get(randEngine.nextInt(instructionCount));
        instruction.setOperator(chromosome.getRandomOperator());
        return chromosome;
    }
}
