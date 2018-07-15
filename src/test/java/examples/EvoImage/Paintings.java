package examples.EvoImage;

import genetics.chromosome.Chromosome;

class Paintings extends Chromosome {

    final EvoManager manager;
    final Polygon[] polygons;

    Paintings(EvoManager manager) {
        polygons = new Polygon[manager.MAX_SHAPES];
        this.manager = manager;
    }

    Paintings makeCopy() {
        Paintings clone = new Paintings(manager);
        for (int i = 0; i < manager.MAX_SHAPES; i++) {
            clone.polygons[i] = polygons[i].clone();
        }
        return clone;
    }
}
