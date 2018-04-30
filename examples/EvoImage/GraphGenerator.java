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
                Shape shape = new Shape(ACTUAL_POINTS);
                for (int j = 0; j < ACTUAL_POINTS; j++) {
                    shape.add(j, randEngine.nextInt(EvoSetting.MAX_WIDTH) * 1.0,
                            randEngine.nextInt(EvoSetting.MAX_HEIGHT) * 1.0);
                }
                switch (choice) {
                    case BLACK:
                        shape.setColor(0, 0, 0, 0.001);
                        break;
                    case WHITE:
                        shape.setColor(254, 254, 254, 0.001);
                        break;
                    case RANDOM:
                        shape.setColor(randEngine.uniform(), randEngine.uniform(),
                                randEngine.uniform(), randEngine.nextInt(30, 60) / 255.0);
                        break;
                }
                p.getShapes().add(shape);
            }
            paints.add(p);
        }
        return paints;
    }
}
