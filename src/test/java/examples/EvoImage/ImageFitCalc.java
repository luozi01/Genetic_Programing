package examples.EvoImage;

import ga.Fitness;
import org.jblas.DoubleMatrix;

public class ImageFitCalc implements Fitness<Paintings> {
    @Override
    public double calc(Paintings chromosome) {
        DoubleMatrix color = chromosome.manager.toImage(chromosome.polygons);
        return color.sub(chromosome.manager.image_colors).norm1();
    }
}