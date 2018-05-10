package lgp.program.operators;

import lgp.enums.OperatorExecutionStatus;
import lgp.program.Operator;
import lgp.program.Register;

public class ADD extends Operator {

    public ADD() {
        super("+");
    }

    @Override
    public Operator makeCopy() {
        ADD clone = new ADD();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        destination.setValue(r1.getValue() + r2.getValue());
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
