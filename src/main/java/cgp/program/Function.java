package cgp.program;

public interface Function {
    int arity();

    double eval(Object... args);
}
