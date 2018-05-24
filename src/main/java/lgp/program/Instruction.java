package lgp.program;

import genetics.utils.RandEngine;
import lgp.enums.OperatorExecutionStatus;
import lgp.gp.LGPChromosome;
import lgp.solver.LinearGP;
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

    public void mutateRegister(LGPChromosome Context, RandEngine randEngine) {
        mutateRegister(Context, randEngine, 0.5);
    }

    public void mutateRegister(LGPChromosome context, RandEngine randEngine, double p_const) {
        if (randEngine.uniform() < 0.5) {
            Register other = context.getRandomRegister();
            while (other.equals(targetOperand)) {
                other = context.getRandomRegister();
            }
            targetOperand = other;
        } else {
            Register arg1, arg2;
            arg2 = randEngine.uniform() < 0.5 ? r2 : r1;

            if (arg2.isConstant()) {
                arg1 = context.getRandomRegister();
            } else {
                arg1 = randEngine.uniform() < p_const ? context.getRandomConstant() : context.getRandomRegister();
            }
            r1 = arg1;
            r2 = arg2;
        }
    }

    public void initialize(LGPChromosome context, RandEngine randEngine) {
        operator = context.getRandomOperator();

        double p_const = 0.5;
        double r = randEngine.uniform();
        r1 = r < p_const ? context.getRandomConstant() : context.getRandomRegister();

        if (r1.isConstant()) {
            r2 = context.getRandomRegister();
        } else {
            r = randEngine.uniform();
            if (r < p_const) {
                r2 = context.getRandomConstant();
            } else {
                r2 = context.getRandomRegister();
            }
        }
        targetOperand = context.getRandomRegister();
    }

    public void resign(LGPChromosome context) {
        operator = context.getOperatorSet().get(operator.getIndex());
        if (r1.isConstant()) {
            r1 = context.getConstantSet().get(r1.getIndex());
        } else {
            r1 = context.getRegisterSet().get(r1.getIndex());
        }

        if (r2.isConstant()) {
            r2 = context.getConstantSet().get(r2.getIndex());
        } else {
            r2 = context.getRegisterSet().get(r2.getIndex());
        }
    }

    public Instruction makeCopy(List<Register> registerSet, List<Register> constantSet, List<Operator> operatorSet) {
        Instruction clone = new Instruction();
        if (r1.isConstant()) {
            clone.r1 = constantSet.get(r1.getIndex());
        } else {
            clone.r1 = registerSet.get(r1.getIndex());
        }

        if (r2.isConstant()) {
            clone.r2 = constantSet.get(r2.getIndex());
        } else {
            clone.r2 = registerSet.get(r2.getIndex());
        }

        clone.operator = operatorSet.get(operator.getIndex());
        clone.targetOperand = registerSet.get(targetOperand.getIndex());

        return clone;
    }

    public OperatorExecutionStatus execute() {
        return operator.eval(r1, r2, targetOperand);
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