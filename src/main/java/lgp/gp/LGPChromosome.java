package lgp.gp;

import genetics.chromosome.Chromosome;
import genetics.utils.Observation;
import lgp.enums.OperatorExecutionStatus;
import lgp.program.Instruction;
import lgp.program.Operator;
import lgp.program.Register;
import lgp.solver.LinearGP;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class LGPChromosome extends Chromosome {

    private transient static int index = 0;
    private List<Register> registerSet = new ArrayList<>(); //variable
    private List<Register> constantSet = new ArrayList<>(); //numbers
    private List<Operator> operatorSet = new ArrayList<>(); //operators
    private List<Instruction> instructions = new ArrayList<>(); //instructions
    private Map<String, Double> variables = new HashMap<>();
    private LinearGP manager;

    LGPChromosome(LinearGP manager) {
        this.manager = manager;
    }

    void addConstant(List<Double> constant) {
        for (double num : constant) {
            Register register = new Register();
            register.setIndex(constantSet.size());
            register.setConstant(true);
            register.setValue(num);
            constantSet.add(register);
        }
    }

    void addOperators(List<Operator> operators) {
        for (Operator op : operators) {
            op.setIndex(operatorSet.size());
            operatorSet.add(op);
        }
    }

    void addRegister(int registerCount) {
        for (int i = 0; i < registerCount; i++) {
            Register register = new Register();
            register.setIndex(registerSet.size());
            register.setConstant(false);
            registerSet.add(register);
        }
    }

    public Operator getRandomOperator() {
        return roundRobinFunctionSelection();
    }

    private Operator roundRobinFunctionSelection() {
        if (index >= operatorSet.size()) {
            index = 0;
            Collections.shuffle(operatorSet);
        }
        return operatorSet.get(index++);
    }

    public Register getRandomConstant() {
        return constantSet.get(manager.getRandEngine().nextInt(constantSet.size()));
    }

    public Register getRandomRegister() {
        return registerSet.get(manager.getRandEngine().nextInt(registerSet.size()));
    }

    /**
     * Structural or data flow introns denote single noneffective instructions that
     * emerge from a linear program from manipulating noneffective registers.
     *
     * @param stop_point stop point for checking structural introns
     * @param manager    data manager
     */
    Set<Integer> markStructuralIntrons(int stop_point, LinearGP manager) {
         /*
        Source: Brameier, M 2004  On Linear Genetic Programming (thesis)

        Algorithm 3.1 (detection of structural introns)
        1. Let set R_eff always contain all registers that are effective at the current program
           position. R_eff := { r | r is numOutputs register }.
           Start at the last program instruction and move backwards.
        2. Mark the next preceding operation from program with:
            destination register r_dest element-of R_eff.
           If such an instruction is not found then go to 5.
        3. If the operation directly follows a branch or a sequence of branches then mark these
           instructions too. Otherwise remove r_dest from R_eff .
        4. Insert each source (operand) register r_op of newly marked instructions from R_eff
           if not already contained. Go to 2.
        5. Stop. All unmarked instructions are introns.
        */

        Set<Integer> effective_register = new HashSet<>();

        int instruction_count = length();
        for (int i = instruction_count - 1; i > stop_point; i--) {
            instructions.get(i).setStructuralIntron(true);
        }

        effective_register.clear();
        int register_count = manager.getRegisterCount();
        for (int i = 0; i < register_count; ++i) {
            effective_register.add(i);
        }

        Instruction current_instruction = null;
        Instruction prev_instruction;
        for (int i = instruction_count - 1; i > stop_point; i--) {
            prev_instruction = current_instruction;
            current_instruction = instructions.get(i);

            //if current instruction is an if statement and it has contents to operate,
            // then it's content is not an intron, it is not an intron
            if (current_instruction.getOperator().isConditional() && prev_instruction != null) {
                if (!prev_instruction.isStructuralIntron()) {
                    current_instruction.setStructuralIntron(false);
                }
            } else {
                if (effective_register.contains(current_instruction.getTargetOperand().getIndex())) {
                    current_instruction.setStructuralIntron(false);
                    effective_register.remove(current_instruction.getTargetOperand().getIndex());

                    if (!current_instruction.getR1().isConstant())
                        effective_register.add(current_instruction.getR1().getIndex());
                    if (!current_instruction.getR2().isConstant())
                        effective_register.add(current_instruction.getR2().getIndex());
                }
            }
        }
        return effective_register;
    }

    private void execute(Observation observation) {
        int inputRegisterCount = registerSet.size();
        for (int i = 0; i < inputRegisterCount; ++i) {
            registerSet.get(i).setValue(observation.getInput(i));
        }

        OperatorExecutionStatus command = OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
        Instruction current_effective_instruction = null;
        Instruction prev_effective_instruction;
        for (Instruction instruction : instructions) {
            if (instruction.isStructuralIntron()) {
                continue;
            }
            prev_effective_instruction = current_effective_instruction;
            current_effective_instruction = instruction;
            if (command == OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION) {
                command = current_effective_instruction.execute();
            } else if (prev_effective_instruction.getOperator().isConditional()) {
                command = OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
            }
        }

        int outputRegisterCount = Math.min(registerSet.size(), observation.outputCount());
        for (int i = 0; i < outputRegisterCount; ++i) {
            observation.setPredictedOutput(i, registerSet.get(i).getValue());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("constants:");
        sb.append("\n").append(constantSet);
        sb.append("\nregisters:");
        sb.append("\n").append(registerSet);
        sb.append("\noperators:");
        sb.append("\n").append(operatorSet);
        for (int i = 0; i < instructions.size(); ++i) {
            sb.append("\ninstruction[").append(i).append("]: ").append(instructions.get(i)).append("\n");
        }
        return sb.toString();
    }

    public int length() {
        return instructions.size();
    }

    public void eval(Observation observation) {
        markStructuralIntrons(0, manager);
        execute(observation);
    }

    private void copy(LGPChromosome that, boolean effectiveOnly) {
        for (int i = 0; i < that.registerSet.size(); i++) {
            registerSet.add(that.registerSet.get(i).makeCopy());
        }
        for (int i = 0; i < that.constantSet.size(); i++) {
            constantSet.add(that.constantSet.get(i).makeCopy());
        }
        for (int i = 0; i < that.operatorSet.size(); i++) {
            operatorSet.add(that.operatorSet.get(i).makeCopy());
        }

        instructions.clear();
        for (int i = 0; i < that.instructions.size(); ++i) {
            if (effectiveOnly && !that.instructions.get(i).isStructuralIntron()) {
                continue;
            }
            instructions.add(that.instructions.get(i).makeCopy(registerSet, constantSet, operatorSet));
        }
    }

    public LGPChromosome makeCopy() {
        LGPChromosome clone = new LGPChromosome(manager);
        clone.copy(this, false);
        return clone;
    }
}
