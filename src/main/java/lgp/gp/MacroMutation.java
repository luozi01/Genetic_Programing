package lgp.gp;

import genetics.utils.RandEngine;
import lgp.program.Instruction;
import lgp.solver.LinearGP;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

import java.util.List;
import java.util.Set;

public class MacroMutation implements MutationPolicy {

    private final LinearGP manager;

    public MacroMutation(LinearGP manager) {
        this.manager = manager;
    }

    @Override
    public Chromosome mutate(Chromosome chromosome) {
        if (!(chromosome instanceof LGPChromosome)) {
            throw new IllegalArgumentException("Chromosome should be LGPChromosome");
        }
        return apply(((LGPChromosome) chromosome).makeCopy(), manager.getRandEngine());
    }

    // This is derived from Algorithm 6.1 (Section 6.2.1) of Linear Genetic Programming
    // Macro instruction mutations either insert or delete a single instruction.
    // In doing so, they change absolute program length with minimum step size on the
    // level of full instructions, the macro level. On the functional level , a single
    // node is inserted in or deleted from the program graph, together with all
    // its connecting edges.
    // Exchanging an instruction or change the position of an existing instruction is not
    // regarded as macro mutation. Both of these variants are on average more
    // destructive, i.e. they imply a larger variation step size, since they include a deletion
    // and an insertion at the same time. A further, but important argument against
    // substitution of single instructions is that these do not vary program length. If
    // single instruction would only be exchanged there would be no code growth.
    private Chromosome apply(LGPChromosome chromosome, RandEngine randEngine) {
        double r = randEngine.uniform();
        final List<Instruction> instructions = chromosome.getInstructions();
        if (chromosome.length() < manager.getMacroMutateMaxProgramLength() &&
                (r < manager.getMacroMutateInsertionRate() ||
                        chromosome.length() == manager.getMacroMutateMinProgramLength())) {
            Instruction inserted_instruction = new Instruction();
            inserted_instruction.initialize(chromosome, randEngine);
            int loc = randEngine.nextInt(chromosome.getInstructions().size());

            if (loc == chromosome.length() - 1) { // if random pos at last, add last directly
                instructions.add(inserted_instruction);
            } else { // otherwise, add to the random position
                instructions.add(loc, inserted_instruction);
            }

            // mutate the target register of the instruction locate at loc to an effective register
            if (manager.isEffectiveMutation()) {
                while (loc < instructions.size() && instructions.get(loc).getOperator().isConditional()) {
                    loc++;
                }
                if (loc < instructions.size()) {
                    Set<Integer> effective_registers = chromosome.markStructuralIntrons(loc, manager);

                    if (effective_registers.size() > 0) {
                        int iRegisterIndex = -1;
                        for (Integer register : effective_registers) {
                            if (iRegisterIndex == -1) {
                                iRegisterIndex = register;
                            } else if (randEngine.uniform() < 0.5) {
                                iRegisterIndex = register;
                            }
                        }
                        instructions.get(loc).setTargetOperand(chromosome.getRegisterSet().get(iRegisterIndex));
                    }
                }
            }
        } else if (chromosome.length() > manager.getMacroMutateMinProgramLength()
                && ((r > manager.getMacroMutateInsertionRate())
                || chromosome.length() == manager.getMacroMutateMaxProgramLength())) {
            int loc = randEngine.nextInt(instructions.size());
            if (manager.isEffectiveMutation()) {
                for (int i = 0; i < 10; i++) {
                    loc = randEngine.nextInt(instructions.size());
                    if (!instructions.get(loc).isStructuralIntron()) {
                        break;
                    }
                }
            }
            instructions.remove(loc);
        }
        return chromosome;
    }
}
