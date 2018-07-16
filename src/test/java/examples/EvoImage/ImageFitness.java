package examples.EvoImage;

import genetics.chromosome.Chromosome;
import genetics.interfaces.FitnessCalc;
import org.jblas.DoubleMatrix;

public class ImageFitness implements FitnessCalc {
    @Override
    public double calc(Chromosome chromosome) {
        DoubleMatrix color = EvoManager.toImage(((Paintings) chromosome).polygons);
        return color.sub(EvoManager.image_colors).norm1();
    }
}
