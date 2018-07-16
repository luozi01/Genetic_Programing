package lgp.program.operators;

import lgp.enums.OperatorExecutionStatus;
import lgp.program.Operator;
import lgp.program.Register;

public class IFGREATERTHAN extends Operator {

    public IFGREATERTHAN() {
        super("If>");
        setConditional(true);
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
