package lgp.program;


import ga.utils.RandEngine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Register {

    private double value;
    private int index;
    private boolean isConstant;

    public Register makeCopy() {
        Register clone = new Register();
        clone.isConstant = isConstant;
        clone.value = value;
        clone.index = index;
        return clone;
    }

    public void mutate(RandEngine randomEngine, double sd) {
        value += randomEngine.normal(0, 1.0) * sd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Register register = (Register) o;

        if (isConstant != register.isConstant)
            return false;
        if (Double.compare(register.value, value) != 0)
            return false;
        return index == register.index;

    }

    @Override
    public String toString() {
        return (isConstant ? "c" : "r") + "[" + index + "]";
    }
}
