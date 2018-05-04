package EvoImage;

import com.zluo.ga.Generator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static EvoImage.EvoSetting.*;

public class GraphGenerator implements Generator<Paintings> {
    @Override
    public List<Paintings> generate() {
        Paintings p = new Paintings();
        for (int i = 0; i < MAX_SHAPES; i++) {
            Polygon polygon = new Polygon(MAX_POINTS);
            for (int j = 0; j < MAX_POINTS; j++) {
                polygon.add(j, randEngine.nextInt(MAX_WIDTH), randEngine.nextInt(MAX_HEIGHT));
            }
            choice.apply(polygon);
            p.getPolygons()[i] = polygon;
        }
        return new LinkedList<Paintings>(Collections.singletonList(p));
    }
}
