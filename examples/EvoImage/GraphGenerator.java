package EvoImage;

import com.zluo.ga.Generator;

import java.util.*;

import static EvoImage.EvoSetting.*;

public class GraphGenerator implements Generator<Paintings> {
    @Override
    public List<Paintings> generate() {
        Paintings p = new Paintings();
        for (int i = 0; i < MAX_SHAPES; i++) {
            Shape shape = new Shape(MAX_POINTS);
            for (int j = 0; j < MAX_POINTS; j++) {
                shape.add(j, rand_int(MAX_WIDTH), rand_int(MAX_HEIGHT));
            }
            choice.apply(shape);
            p.getShapes()[i] = shape;
        }
        return new LinkedList<Paintings>(Collections.singletonList(p));
    }
}
