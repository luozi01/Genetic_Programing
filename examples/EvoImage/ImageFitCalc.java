package EvoImage;

import com.zluo.ga.Fitness;
import org.jblas.DoubleMatrix;

public class ImageFitCalc implements Fitness<Graph> {
    @Override
    public double calc(Graph chromosome) {
        DoubleMatrix color = chromosome.toImage();
        double error = 0;
        DoubleMatrix diff = color.sub(EvoSetting.colors);
        for (int i = 0; i < diff.length; i++) {
            error += diff.get(i) * diff.get(i);
        }
        return error;
    }
}
