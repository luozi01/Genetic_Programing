package examples.EvoImage;

import genetics.Chromosome;
import genetics.FitnessCalc;
import org.jblas.DoubleMatrix;

public class ImageFitness implements FitnessCalc {
    @Override
    public double calc(Chromosome chromosome) {
        DoubleMatrix color = ((Paintings) chromosome).manager.toImage(((Paintings) chromosome).polygons);
        return color.sub(((Paintings) chromosome).manager.image_colors).norm1();
    }
}
