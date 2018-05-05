package EvoImage;

import com.zluo.ga.Chromosome;
import java.util.List;

public class Paintings implements Chromosome<Paintings> {
    EvoManager manager;
    Polygon[] polygons;

    Paintings(EvoManager manager) {
        this.manager = manager;
        polygons = new Polygon[manager.MAX_SHAPES];
    }

    @Override
    public List<Paintings> crossover(Paintings chromosome, double uniformRate) {
        return null;
    }

    @Override
    public void mutate(double mutationRate) {
        manager.method.apply(polygons, manager, manager.randEngine);
    }

    @Override
    public Paintings makeCopy() {
        Paintings clone = new Paintings(manager);
        for (int i = 0; i < manager.MAX_SHAPES; i++) {
            clone.polygons[i] = polygons[i].clone();
        }
        return clone;
    }
}
