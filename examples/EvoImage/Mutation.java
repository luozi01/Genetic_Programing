package EvoImage;

import static EvoImage.EvoSetting.*;

public enum Mutation implements Mutations {
    GAUSSIAN {
        @Override
        public void apply(Shape[] shapes) {

        }
    },
    SOFT {
        @Override
        public void apply(Shape[] shapes) {
            int CHANGED_SHAPE_INDEX = rand_int(ACTUAL_SHAPES - 1);

            double roulette = Math.random() * 2.0;

            double delta = -1 + rand_int(3);

            // mutate color
            if (roulette < 1) {
                if (roulette < 0.25) {
                    shapes[CHANGED_SHAPE_INDEX].r =
                            CLAMP(shapes[CHANGED_SHAPE_INDEX].r + delta, 0, 255);
                } else if (roulette < 0.5) {
                    shapes[CHANGED_SHAPE_INDEX].g =
                            CLAMP(shapes[CHANGED_SHAPE_INDEX].g + delta, 0, 255);
                } else if (roulette < 0.75) {
                    shapes[CHANGED_SHAPE_INDEX].b =
                            CLAMP(shapes[CHANGED_SHAPE_INDEX].b + delta, 0, 255);
                } else if (roulette < 1.0) {
                    shapes[CHANGED_SHAPE_INDEX].a =
                            CLAMP(shapes[CHANGED_SHAPE_INDEX].a + 0.1 * delta, 0.0, 1.0);
                }
            }
            // mutate shape
            else {
                int CHANGED_POINT_INDEX = rand_int(ACTUAL_POINTS - 1);

                // x-coordinate
                if (roulette < 1.5) {
                    shapes[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] =
                            CLAMP(shapes[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] + delta, 0, MAX_WIDTH);
                }

                // y-coordinate
                else {
                    shapes[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] =
                            CLAMP(shapes[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] + delta, 0, MAX_HEIGHT);
                }
            }
        }
    },
    MEDIUM {
        @Override
        public void apply(Shape[] dna_out) {
            int CHANGED_SHAPE_INDEX = rand_int(ACTUAL_SHAPES - 1);

            double roulette = Math.random() * 2.0;

            // mutate color
            if (roulette < 1) {
                if (roulette < 0.25) {
                    dna_out[CHANGED_SHAPE_INDEX].r = Math.random();
                } else if (roulette < 0.5) {
                    dna_out[CHANGED_SHAPE_INDEX].g = Math.random();
                } else if (roulette < 0.75) {
                    dna_out[CHANGED_SHAPE_INDEX].b = Math.random();
                } else if (roulette < 1.0) {
                    dna_out[CHANGED_SHAPE_INDEX].a = Math.random();
                }
            }

            // mutate shape
            else {
                int CHANGED_POINT_INDEX = rand_int(ACTUAL_POINTS - 1);
                if (roulette < 1.5) {
                    dna_out[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] = rand_int(MAX_WIDTH);
                } else {
                    dna_out[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] = rand_int(MAX_HEIGHT);
                }
            }
        }
    },
    HARD {
        @Override
        public void apply(Shape[] shapes) {
            int CHANGED_SHAPE_INDEX = rand_int(ACTUAL_SHAPES - 1);

            shapes[CHANGED_SHAPE_INDEX].r = Math.random();
            shapes[CHANGED_SHAPE_INDEX].g = Math.random();
            shapes[CHANGED_SHAPE_INDEX].b = Math.random();
            shapes[CHANGED_SHAPE_INDEX].a = Math.random();
            int CHANGED_POINT_INDEX = rand_int(ACTUAL_POINTS - 1);

            shapes[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] = rand_int(MAX_WIDTH) * 1.0;
            shapes[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] = rand_int(MAX_HEIGHT) * 1.0;
        }
    }
}
