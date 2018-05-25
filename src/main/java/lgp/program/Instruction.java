package lgp.program;

import genetics.utils.RandEngine;
import lgp.enums.OperatorExecutionStatus;
import lgp.gp.LGPChromosome;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Instruction {
    private Operator operator;
    private Register r1;
    private Register r2;
    private Register targetOperand;
    private boolean structuralIntron = false;

    public void mutateConstant(RandEngine randEngine, double sd) {
        if (r1.isConstant()) {
            r1.mutate(randEngine, sd);
        } else {
            r2.mutate(randEngine, sd);
        }
    }

    public void mutateRegister(LGPChromosome gp, RandEngine randEngine) {
        mutateRegister(gp, randEngine, 0.5);
    }

    public void mutateRegister(LGPChromosome gp, RandEngine randEngine, double p_const) {
        if (randEngine.uniform() < 0.5) {
            Register other = gp.getRandomRegister();
            while (other.equals(targetOperand)) {
                other = gp.getRandomRegister();
            }
            targetOperand = other;
        } else {
            Register arg1, arg2;
            arg2 = randEngine.uniform() < 0.5 ? r2 : r1;

            if (arg2.isConstant()) {
                arg1 = gp.getRandomRegister();
            } else {
                arg1 = randEngine.uniform() < p_const ? gp.getRandomConstant() : gp.getRandomRegister();
            }
            r1 = arg1;
            r2 = arg2;
        }
    }

    public void initialize(LGPChromosome gp, RandEngine randEngine) {
        operator = gp.getRandomOperator();

        double p_const = 0.5;
        double r = randEngine.uniform();
        r1 = r < p_const ? gp.getRandomConstant() : gp.getRandomRegister();

        if (r1.isConstant()) {
            r2 = gp.getRandomRegister();
        } else {
            r = randEngine.uniform();
            r2 = r < p_const ? gp.getRandomConstant() : gp.getRandomRegister();
        }
        targetOperand = gp.getRandomRegister();
    }

    public void resign(LGPChromosome gp) {
        operator = gp.getOperatorSet().get(operator.getIndex());
        r1 = r1.isConstant() ? gp.getConstantSet().get(r1.getIndex()) :
                gp.getRegisterSet().get(r1.getIndex());

        r2 = r2.isConstant() ? gp.getConstantSet().get(r2.getIndex()) :
                gp.getRegisterSet().get(r2.getIndex());
    }

    public OperatorExecutionStatus execute() {
        return operator.eval(r1, r2, targetOperand);
    }

    public Instruction makeCopy(List<Register> registerSet, List<Register> constantSet, List<Operator> operatorSet) {
        Instruction clone = new Instruction();
        clone.r1 = r1.isConstant() ? constantSet.get(r1.getIndex()) : registerSet.get(r1.getIndex());
        clone.r2 = r2.isConstant() ? constantSet.get(r2.getIndex()) : registerSet.get(r2.getIndex());

        clone.operator = operatorSet.get(operator.getIndex());
        clone.targetOperand = registerSet.get(targetOperand.getIndex());
        return clone;
    }

    public Instruction makeCopy() {
        Instruction clone = new Instruction();
        clone.structuralIntron = structuralIntron;
        clone.operator = operator;
        clone.r1 = r1;
        clone.r2 = r2;
        clone.targetOperand = targetOperand;
        return clone;
    }

    @Override
    public String toString() {
        return operator.toString()
                .concat("\t").concat(r1.toString())
                .concat("\t").concat(r2.toString())
                .concat("\t").concat(targetOperand.toString())
                .concat(structuralIntron ? "(intron)" : "");
    }
}