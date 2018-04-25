package com.zluo.lgp.program.operators;

import com.zluo.lgp.enums.OperatorExecutionStatus;
import com.zluo.lgp.program.Operator;
import com.zluo.lgp.program.Register;

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
