package lgp.program;

import lgp.enums.OperatorExecutionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Operator {

    protected boolean conditional = false;
    private int index;
    private String symbol;

    public Operator(String symbol) {
        this.symbol = symbol;
    }

    public abstract Operator makeCopy();

    public Operator copy(Operator rhs) {
        index = rhs.index;
        conditional = rhs.conditional;
        symbol = rhs.symbol;
        return this;
    }

    public abstract OperatorExecutionStatus eval(Register r1, Register r2, Register destination);

    @Override
    public String toString() {
        return symbol;
    }
}
