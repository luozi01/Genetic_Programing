package cgp.program;

import cgp.interfaces.CGPFunction;

import java.util.stream.IntStream;

import static java.lang.Math.*;

public enum Operators implements CGPFunction {
    add {
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
    },
    sub {
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
    },
    mul {
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
    },
    div {
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
    },
    abs {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return abs(inputs[0]);
        }
    },
    sqrt {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return sqrt(inputs[0]);
        }
    },
    sq {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return pow(inputs[0], 2);
        }
    },
    cube {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return pow(inputs[0], 3);
        }
    },
    pow {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return pow(inputs[0], inputs[1]);
        }
    },
    exp {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return exp(inputs[0]);
        }
    },
    sin {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return sin(inputs[0]);
        }
    },
    cos {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return cos(inputs[0]);
        }
    },
    tan {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return tan(inputs[0]);
        }
    },
    one {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return 1;
        }
    },
    zero {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return 0;
        }
    },
    pi {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return PI;
        }
    },
    rand {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return random();
        }
    },
    and {
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
    },
    nand {
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
    },
    or {
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
    },
    nor {
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
    },
    xor {
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
    },
    xnor {
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
    },
    not {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return (double) (inputs[0] == 0 ? 1 : 0);
        }
    },
    wire {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            return inputs[0];
        }
    },
    sig {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return 1 / (1 + exp(-weightedInputSum));
        }
    },
    gauss {
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
    },
    step {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return (double) (weightedInputSum < 0 ? 0 : 1);
        }
    },
    soft {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return weightedInputSum / (1 + abs(weightedInputSum));
        }
    },
    tanh {
        @Override
        public int arity() {
            return -1;
        }

        @Override
        public double calc(int numInputs, double[] inputs, double[] connectionWeights) {
            double weightedInputSum = sumWeightedInputs(numInputs, inputs, connectionWeights);
            return tanh(weightedInputSum);
        }
    };

    /**
     * Returns the sum of the weighted inputs.
     */
    private static double sumWeightedInputs(int numInputs, double[] inputs, double[] connectionWeights) {
        return IntStream.range(0, numInputs).mapToDouble(i -> (inputs[i] * connectionWeights[i])).sum();
    }
}
