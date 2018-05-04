package EvoImage;

import com.zluo.ga.utils.RandEngine;

import static EvoImage.EvoSetting.*;

public enum Mutation implements Mutations {
    GAUSSIAN {
        @Override
        public void apply(Polygon[] polygons, RandEngine randEngine) {

        }
    },
    SOFT {
        @Override
        public void apply(Polygon[] polygons, RandEngine randEngine) {
            int CHANGED_SHAPE_INDEX = randEngine.nextInt(ACTUAL_SHAPES - 1);

            double roulette = randEngine.uniform() * 2.0;

            double delta = -1 + randEngine.nextInt(3);

            // mutate color
            if (roulette < 1) {
                if (roulette < 0.25) {
                    polygons[CHANGED_SHAPE_INDEX].r =
                            CLAMP(polygons[CHANGED_SHAPE_INDEX].r + delta, 0, 255);
                } else if (roulette < 0.5) {
                    polygons[CHANGED_SHAPE_INDEX].g =
                            CLAMP(polygons[CHANGED_SHAPE_INDEX].g + delta, 0, 255);
                } else if (roulette < 0.75) {
                    polygons[CHANGED_SHAPE_INDEX].b =
                            CLAMP(polygons[CHANGED_SHAPE_INDEX].b + delta, 0, 255);
                } else if (roulette < 1.0) {
                    polygons[CHANGED_SHAPE_INDEX].a =
                            CLAMP(polygons[CHANGED_SHAPE_INDEX].a + 0.1 * delta, 0.0, 1.0);
                }
            }
            // mutate shape
            else {
                int CHANGED_POINT_INDEX = randEngine.nextInt(ACTUAL_POINTS - 1);

                // x-coordinate
                if (roulette < 1.5) {
                    polygons[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] =
                            CLAMP(polygons[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] + delta, 0, MAX_WIDTH);
                }

                // y-coordinate
                else {
                    polygons[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] =
                            CLAMP(polygons[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] + delta, 0, MAX_HEIGHT);
                }
            }
        }
    },
    MEDIUM {
        @Override
        public void apply(Polygon[] dna_out, RandEngine randEngine) {
            int CHANGED_SHAPE_INDEX = randEngine.nextInt(ACTUAL_SHAPES - 1);
            double roulette = randEngine.uniform() * 2.0;

            // mutate color
            if (roulette < 1) {
                if (roulette < 0.25) {
                    dna_out[CHANGED_SHAPE_INDEX].r = randEngine.uniform();
                } else if (roulette < 0.5) {
                    dna_out[CHANGED_SHAPE_INDEX].g = randEngine.uniform();
                } else if (roulette < 0.75) {
                    dna_out[CHANGED_SHAPE_INDEX].b = randEngine.uniform();
                } else if (roulette < 1.0) {
                    dna_out[CHANGED_SHAPE_INDEX].a = randEngine.uniform();
                }
            }
            // mutate shape
            else {
                int CHANGED_POINT_INDEX = randEngine.nextInt(ACTUAL_POINTS - 1);
                if (roulette < 1.5) {
                    dna_out[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] = randEngine.nextInt(200);
                } else {
                    dna_out[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] = randEngine.nextInt(2);
                }
            }
        }
    },
    HARD {
        @Override
        public void apply(Polygon[] polygons, RandEngine randEngine) {
            int CHANGED_SHAPE_INDEX = randEngine.nextInt(ACTUAL_SHAPES - 1);

            polygons[CHANGED_SHAPE_INDEX].r = randEngine.uniform();
            polygons[CHANGED_SHAPE_INDEX].g = randEngine.uniform();
            polygons[CHANGED_SHAPE_INDEX].b = randEngine.uniform();
            polygons[CHANGED_SHAPE_INDEX].a = randEngine.uniform();
            int CHANGED_POINT_INDEX = randEngine.nextInt(ACTUAL_POINTS - 1);

            polygons[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] = randEngine.nextInt(MAX_WIDTH);
            polygons[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] = randEngine.nextInt(MAX_HEIGHT);
        }
    }
}
