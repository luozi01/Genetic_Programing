package lgp.program.operators;

import lgp.enums.OperatorExecutionStatus;
import lgp.program.Operator;
import lgp.program.Register;

public class LOG extends Operator {
    public LOG() {
        super("log");
    }

    @Override
    public Operator makeCopy() {
        LOG clone = new LOG();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        double arg = r1.getValue();
        destination.setValue(arg == 0 ? Double.MAX_VALUE : Math.log(Math.abs(arg)));
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
