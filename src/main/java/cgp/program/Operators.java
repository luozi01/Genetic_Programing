package cgp.program;

import cgp.interfaces.Function;

import java.util.stream.IntStream;

import static java.lang.Math.*;

public enum Operators implements Function {
    _add {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int i;
            double sum = inputs[0];

            for (i = 1; i < numInputs; i++) {
                sum += inputs[i];
            }

            return sum;
        }

        @Override
        public String getName() {
            return "add";
        }
    },
    _sub {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int i;
            double sum = inputs[0];

            for (i = 1; i < numInputs; i++) {
                sum -= inputs[i];
            }

            return sum;
        }

        @Override
        public String getName() {
            return "sub";
        }
    },
    _mul {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int i;
            double multiplication = inputs[0];

            for (i = 1; i < numInputs; i++) {
                multiplication *= inputs[i];
            }

            return multiplication;
        }

        @Override
        public String getName() {
            return "mul";
        }
    },
    _divide {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int i;
            double divide = inputs[0];

            for (i = 1; i < numInputs; i++) {
                divide /= inputs[i];
            }

            return divide;
        }

        @Override
        public String getName() {
            return "div";
        }
    },
    _absolute {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return abs(inputs[0]);
        }

        @Override
        public String getName() {
            return "abs";
        }
    },
    _squareRoot {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return sqrt(inputs[0]);
        }

        @Override
        public String getName() {
            return "sqrt";
        }
    },
    _square {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return pow(inputs[0], 2);
        }

        @Override
        public String getName() {
            return "sq";
        }
    },
    _cube {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return pow(inputs[0], 3);
        }

        @Override
        public String getName() {
            return "cube";
        }
    },
    _power {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return pow(inputs[0], inputs[1]);
        }

        @Override
        public String getName() {
            return "pow";
        }
    },
    _exponential {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return exp(inputs[0]);
        }

        @Override
        public String getName() {
            return "exp";
        }
    },
    _sine {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return sin(inputs[0]);
        }

        @Override
        public String getName() {
            return "sin";
        }
    },
    _cosine {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return cos(inputs[0]);
        }

        @Override
        public String getName() {
            return "cos";
        }
    },
    _tangent {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return tan(inputs[0]);
        }

        @Override
        public String getName() {
            return "tan";
        }
    },
    _One {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return 1;
        }

        @Override
        public String getName() {
            return "1";
        }
    },
    _Zero {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return 0;
        }

        @Override
        public String getName() {
            return "0";
        }
    },
    _PI {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return PI;
        }

        @Override
        public String getName() {
            return "pi";
        }
    },
    _randFloat {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return random();
        }

        @Override
        public String getName() {
            return "rand";
        }
    },
    _and {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            for (int i = 0; i < numInputs; i++) {
                if (inputs[i] == 0) {
                    return 0;
                }
            }
            return 1;
        }

        @Override
        public String getName() {
            return "and";
        }
    },
    _nand {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            for (int i = 0; i < numInputs; i++) {
                if (inputs[i] == 0) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public String getName() {
            return "nand";
        }
    },
    _or {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int i;

            for (i = 0; i < numInputs; i++) {

                if (inputs[i] == 1) {
                    return 1;
                }
            }

            return 0;
        }

        @Override
        public String getName() {
            return "or";
        }
    },
    _nor {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            for (int i = 0; i < numInputs; i++) {
                if (inputs[i] == 1) {
                    return 0;
                }
            }
            return 1;
        }

        @Override
        public String getName() {
            return "nor";
        }
    },
    _xor {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int numOnes = 0;
            for (int i = 0; i < numInputs; i++) {
                if (inputs[i] == 1) {
                    numOnes++;
                }
                if (numOnes > 1) {
                    break;
                }
            }
            return numOnes == 1 ? 1 : 0;
        }

        @Override
        public String getName() {
            return "xor";
        }
    },
    _xnor {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int numOnes = 0;
            for (int i = 0; i < numInputs; i++) {
                if (inputs[i] == 1) {
                    numOnes++;
                }
                if (numOnes > 1) {
                    break;
                }
            }
            return numOnes == 1 ? 0 : 1;
        }

        @Override
        public String getName() {
            return "xnor";
        }
    },
    _not {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return (double) (inputs[0] == 0 ? 1 : 0);
        }

        @Override
        public String getName() {
            return "not";
        }
    },
    _wire {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return inputs[0];
        }

        @Override
        public String getName() {
            return "wire";
        }
    },
    _sigmoid {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return 1 / (1 + exp(-weightedInputSum));
        }

        @Override
        public String getName() {
            return "sig";
        }
    },
    _gaussian {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            int centre = 0;
            int width = 1;
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return exp(-(pow(weightedInputSum - centre, 2)) / (2 * pow(width, 2)));
        }

        @Override
        public String getName() {
            return "gauss";
        }
    },
    _step {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return (double) (weightedInputSum < 0 ? 0 : 1);
        }

        @Override
        public String getName() {
            return "step";
        }
    },
    _softsign {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return weightedInputSum / (1 + abs(weightedInputSum));
        }

        @Override
        public String getName() {
            return "soft";
        }
    },
    _hyperbolicTangent {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return tanh(weightedInputSum);
        }

        @Override
        public String getName() {
            return "tanh";
        }
    };

    /**
     * Returns the sum of the weighted inputs.
     */
    private static double sumWeightedInputs(int numInputs, double[] inputs, double[] connectionWeights) {
        return IntStream.range(0, numInputs).mapToDouble(i -> (inputs[i] * connectionWeights[i])).sum();
    }
}
