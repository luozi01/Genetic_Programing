package EvoImage;

import com.zluo.ga.Generator;
import com.zluo.ga.utils.RandEngine;

import java.util.ArrayList;
import java.util.List;

import static EvoImage.EvoSetting.*;

public class GraphGenerator implements Generator<Graph> {

    private RandEngine randEngine = new Rand();

    @Override
    public List<Graph> generate() {
        List<Graph> paints = new ArrayList<>();
        for (int index = 0; index < populationSize; index++) {
            Graph p = new Graph();
            for (int i = 0; i < ACTUAL_SHAPES; i++) {
                Polygon polygon = new Polygon(ACTUAL_POINTS);
                for (int j = 0; j < ACTUAL_POINTS; j++) {
                    polygon.add(j, randEngine.nextInt(EvoSetting.MAX_WIDTH) * 1.0,
                            randEngine.nextInt(EvoSetting.MAX_HEIGHT) * 1.0);
                }
                polygon.setColor(randEngine.uniform(), randEngine.uniform(),
                        randEngine.uniform(), randEngine.nextInt(30, 60) / 255.0);
                p.getPolygons().add(polygon);
            }
            paints.add(p);
        }
        return paints;
    }
}
