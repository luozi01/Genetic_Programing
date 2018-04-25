package com.zluo.lgp.program.operators;

import com.zluo.lgp.enums.OperatorExecutionStatus;
import com.zluo.lgp.program.Operator;
import com.zluo.lgp.program.Register;

public class POW extends Operator {
    public POW() {
        super("^");
    }

    @Override
    public Operator makeCopy() {
        POW clone = new POW();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        destination.setValue(Math.abs(r2.getValue()) <= 10 ? Math.pow(Math.abs(r1.getValue()), r2.getValue()) : Double.MAX_VALUE);
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
