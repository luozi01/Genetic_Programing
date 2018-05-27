package cgp.program;

public enum SymbolicRegressionFunctions implements Function {
    ABS {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.abs((Double) args[0]);
        }
    },
    SQRT {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.sqrt(Math.abs((Double) args[0]));
        }
    },
    RECI {
        private final static double DIVISION_LIMIT = 0.0001;

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            Double in0 = ((Double) args[0]);
            return in0 < DIVISION_LIMIT ? in0 : (1 / in0);
        }
    },
    SIN {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.sin((Double) args[0]);
        }
    },
    COS {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.cos((Double) args[0]);
        }
    },
    TAN {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.tan((Double) args[0]);
        }
    },
    EXP {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.exp((Double) args[0]);
        }
    },
    SINH {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.sinh((Double) args[0]);
        }
    },
    COSH {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.cosh((Double) args[0]);
        }
    },
    TANH {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            return Math.tanh((Double) args[0]);
        }
    },
    LN {
        private final static double DIVISION_LIMIT = 0.0001;

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            Double in0 = ((Double) args[0]);
            return in0 < DIVISION_LIMIT ? in0 : Math.log(Math.abs(in0));
        }
    },
    LOG {
        private final static double DIVISION_LIMIT = 0.0001;

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double eval(Object... args) {
            Double in0 = ((Double) args[0]);
            return in0 < DIVISION_LIMIT ? in0 : Math.log10(Math.abs(in0));
        }
    },
    POW {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double eval(Object... args) {
            Double in0 = ((Double) args[0]);
            Double in1 = ((Double) args[1]);
            return Math.pow(Math.abs(in0), in1);
        }
    },
    ADD {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double eval(Object... args) {
            return (Double) args[0] + (Double) args[1];
        }
    },
    SUB {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double eval(Object... args) {
            return (Double) args[0] - (Double) args[1];
        }
    },
    MUL {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double eval(Object... args) {
            return (Double) args[0] * (Double) args[1];
        }
    },
    DIV {
        private final static double DIVISION_LIMIT = 0.0001;

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double eval(Object... args) {
            Double in0 = ((Double) args[0]);
            Double in1 = ((Double) args[1]);

            return in1 < DIVISION_LIMIT ? in0 : (in0 / in1);
        }
    }
}
