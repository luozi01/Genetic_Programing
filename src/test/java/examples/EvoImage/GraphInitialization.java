package examples.EvoImage;

import genetics.chromosome.Chromosome;
import genetics.interfaces.Initialization;

import java.util.Collections;
import java.util.List;

public class GraphInitialization implements Initialization {

    private final EvoManager manager;

    GraphInitialization(EvoManager manager) {
        this.manager = manager;
    }

    @Override
    public List<Chromosome> generate() {
        Paintings p = new Paintings(manager);
        for (int i = 0; i < manager.MAX_SHAPES; i++) {
            Polygon polygon = new Polygon(manager.MAX_POINTS);
            for (int j = 0; j < manager.MAX_POINTS; j++) {
                polygon.add(j, manager.randEngine.nextInt(manager.MAX_WIDTH), manager.randEngine.nextInt(manager.MAX_HEIGHT));
            }
            manager.choice.apply(polygon);
            p.polygons[i] = polygon;
        }
        return Collections.singletonList(p);
    }
}
