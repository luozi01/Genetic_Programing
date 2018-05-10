package lgp.program.operators;

import lgp.enums.OperatorExecutionStatus;
import lgp.program.Operator;
import lgp.program.Register;

public class EXP extends Operator {
    public EXP() {
        super("exp");
    }

    @Override
    public Operator makeCopy() {
        EXP clone = new EXP();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        double arg = r1.getValue();
        destination.setValue(Math.abs(arg) <= 32 ? Math.exp(arg) : Double.MAX_VALUE);
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
