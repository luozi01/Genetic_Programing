package EvoImage;

import com.zluo.ga.Generator;

import java.util.Collections;
import java.util.List;

public class GraphGenerator implements Generator<Paintings> {

    private EvoManager manager;

    GraphGenerator(EvoManager manager) {
        this.manager = manager;
    }

    @Override
    public List<Paintings> generate() {
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
