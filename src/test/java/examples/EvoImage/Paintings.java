package examples.EvoImage;

import genetics.AbstractListChromosome;
import org.jblas.DoubleMatrix;

import java.util.List;

public class Paintings extends AbstractListChromosome<Polygon> {

    EvoManager manager;

    Paintings(List<Polygon> polygons, EvoManager manager) {
        super(polygons);
        this.manager = manager;
    }

    public List<Polygon> getPolygons() {
        return getRepresentation();
    }

    @Override
    protected void checkValidity(List<Polygon> representation) {
    }

    @Override
    public AbstractListChromosome<Polygon> newCopy(List<Polygon> list) {
        return new Paintings(list, manager);
    }

    @Override
    public double fitness() {
        DoubleMatrix color = manager.toImage(getRepresentation());
        return color.sub(manager.image_colors).norm1();
    }
}
