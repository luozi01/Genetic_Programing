package examples.Symbolic_engine;

import genetics.utils.Observation;
import org.apache.commons.math3.analysis.TrivariateFunction;

import java.util.ArrayList;
import java.util.List;

class Test {
    //x^2+3x+2y+z+5
    static List<Observation> function() {
        List<Observation> result = new ArrayList<>();
        TrivariateFunction func = (x, y, z) -> (x * x + 3 * x + 2 * y + z + 5);

        double lower_bound = -50;
        double upper_bound = 50;
        int period = 16;

        double interval = (upper_bound - lower_bound) / period;

        for (int i = 0; i < period; i++) {
            double x = lower_bound + interval * i;
            for (int j = 0; j < period; j++) {
                double y = lower_bound + interval * j;
                for (int k = 0; k < period; k++) {
                    double z = lower_bound + interval * k;
                    Observation observation = new BasicObservation(3, 1);

                    observation.setInput(0, x);
                    observation.setInput(0, "x");
                    observation.setInput(1, y);
                    observation.setInput(1, "y");
                    observation.setInput(2, z);
                    observation.setInput(2, "z");
                    observation.setOutput(0, func.value(x, y, z));
                    result.add(observation);
                }
            }
        }
        return result;
    }

}
