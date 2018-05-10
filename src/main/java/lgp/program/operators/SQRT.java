package lgp.program.operators;

import lgp.enums.OperatorExecutionStatus;
import lgp.program.Operator;
import lgp.program.Register;

public class SQRT extends Operator {
    public SQRT() {
        super("√");
    }

    @Override
    public Operator makeCopy() {
        SQRT clone = new SQRT();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        destination.setValue(Math.sqrt(Math.abs(r1.getValue())));
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
