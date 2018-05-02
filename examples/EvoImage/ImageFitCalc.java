package EvoImage;

import com.zluo.ga.Fitness;
import org.jblas.DoubleMatrix;

public class ImageFitCalc implements Fitness<Paintings> {
    @Override
    public double calc(Paintings chromosome) {
        DoubleMatrix color = chromosome.toImage();
        return color.sub(EvoSetting.image_colors).norm1();
    }
}