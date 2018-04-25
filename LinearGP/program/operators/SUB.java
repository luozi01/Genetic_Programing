package com.zluo.lgp.program.operators;

import com.zluo.lgp.enums.OperatorExecutionStatus;
import com.zluo.lgp.program.Operator;
import com.zluo.lgp.program.Register;

public class SUB extends Operator {
    public SUB() {
        super("-");
    }

    @Override
    public Operator makeCopy() {
        SUB clone = new SUB();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        destination.setValue(r1.getValue() - r2.getValue());
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
