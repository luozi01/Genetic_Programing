package examples.EvoImage;

import genetics.utils.RandEngine;

public interface Mutations {
    void apply(Polygon[] polygons, EvoManager manager, RandEngine randEngine);
}
