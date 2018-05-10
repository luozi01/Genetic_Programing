package lgp.program.operators;

import lgp.enums.OperatorExecutionStatus;
import lgp.program.Operator;
import lgp.program.Register;

public class DIV extends Operator {
    public DIV() {
        super("/");
    }

    @Override
    public Operator makeCopy() {
        DIV clone = new DIV();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        destination.setValue(r2.getValue() == 0 ? Double.MAX_VALUE : r1.getValue() / r2.getValue());
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
