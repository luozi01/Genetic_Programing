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
        for (int i = 0; i < EvoManager.MAX_SHAPES; i++) {
            Polygon polygon = new Polygon(EvoManager.MAX_POINTS);
            for (int j = 0; j < EvoManager.MAX_POINTS; j++) {
                polygon.add(j, EvoManager.randEngine.nextInt(EvoManager.MAX_WIDTH),
                        EvoManager.randEngine.nextInt(EvoManager.MAX_HEIGHT));
            }
            EvoManager.choice.apply(polygon);
            p.polygons[i] = polygon;
        }
        return Collections.singletonList(p);
    }
}
