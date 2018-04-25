package com.zluo.lgp.program.operators;

import com.zluo.lgp.enums.OperatorExecutionStatus;
import com.zluo.lgp.program.Operator;
import com.zluo.lgp.program.Register;

public class COS extends Operator {
    public COS() {
        super("cos");
    }

    @Override
    public Operator makeCopy() {
        COS clone = new COS();
        clone.copy(this);
        return clone;
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        destination.setValue(Math.cos(r1.getValue()));
        return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
    }
}
