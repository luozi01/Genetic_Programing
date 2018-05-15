package examples.EvoImage;

import genetics.Chromosome;

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
    public Paintings mutate(double mutationRate) {
        Paintings clone = makeCopy();
        manager.method.apply(clone.polygons, manager, manager.randEngine);
        return clone;
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
