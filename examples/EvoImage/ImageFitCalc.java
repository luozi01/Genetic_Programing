package EvoImage;

import com.zluo.ga.Fitness;
import org.jblas.DoubleMatrix;

public class ImageFitCalc implements Fitness<Graph> {
    @Override
    public double calc(Graph chromosome) {
        DoubleMatrix color = chromosome.toImage();
        return color.sub(EvoSetting.colors).norm1();
    }
}