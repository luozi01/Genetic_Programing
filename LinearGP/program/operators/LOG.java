package com.zluo.lgp.program.operators;

import com.zluo.lgp.enums.OperatorExecutionStatus;
import com.zluo.lgp.program.Operator;
import com.zluo.lgp.program.Register;

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
