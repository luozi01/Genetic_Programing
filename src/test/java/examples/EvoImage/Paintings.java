package examples.EvoImage;

import org.apache.commons.math3.genetics.Chromosome;
import org.jblas.DoubleMatrix;

public class Paintings extends Chromosome {

    final EvoManager manager;
    final Polygon[] polygons;

    Paintings(EvoManager manager) {
        polygons = new Polygon[manager.MAX_SHAPES];
        this.manager = manager;
    }

    @Override
    public double fitness() {
        DoubleMatrix color = manager.toImage(polygons);
        return color.sub(manager.image_colors).norm1();
    }

    public Paintings makeCopy() {
        Paintings clone = new Paintings(manager);
        for (int i = 0; i < manager.MAX_SHAPES; i++) {
            clone.polygons[i] = polygons[i].clone();
        }
        return clone;
    }
}
