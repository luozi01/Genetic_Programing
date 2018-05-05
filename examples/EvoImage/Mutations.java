package EvoImage;

import com.zluo.ga.utils.RandEngine;

public interface Mutations {
    void apply(Polygon[] polygons, EvoManager manager, RandEngine randEngine);
}
