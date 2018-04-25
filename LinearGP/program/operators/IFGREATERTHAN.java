package com.zluo.lgp.program.operators;

import com.zluo.lgp.enums.OperatorExecutionStatus;
import com.zluo.lgp.program.Operator;
import com.zluo.lgp.program.Register;

public class IFGREATERTHAN extends Operator {

    public IFGREATERTHAN() {
        super("If>");
        conditional = true;
    }


    @Override
    public Operator makeCopy() {
        return new IFGREATERTHAN().copy(this);
    }

    @Override
    public OperatorExecutionStatus eval(Register r1, Register r2, Register destination) {
        if (r1.getValue() > r2.getValue()) {
            return OperatorExecutionStatus.LGP_EXECUTE_NEXT_INSTRUCTION;
        } else {
            return OperatorExecutionStatus.LGP_SKIP_NEXT_INSTRUCTION;
        }
    }
}
