package examples.EvoImage;


import genetics.utils.RandEngine;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

import java.util.List;

public enum Mutation implements MutationPolicy {

    GAUSSIAN {
        @Override
        public Chromosome mutate(Chromosome chromosome) {
            return null;
        }
    },
    //    SOFT {
//        @Override
//        public Chromosome mutate(Chromosome chromosome) {
//            return null;
//        }
//
//        public void apply(Polygon[] polygons, EvoManager manager, RandEngine randEngine) {
//            int CHANGED_SHAPE_INDEX = randEngine.nextInt(manager.ACTUAL_SHAPES - 1);
//
//            double roulette = randEngine.uniform() * 2.0;
//
//            double delta = -1 + randEngine.nextInt(3);
//
//            // mutate color
//            if (roulette < 1) {
//                if (roulette < 0.25) {
//                    polygons[CHANGED_SHAPE_INDEX].r =
//                            CLAMP(polygons[CHANGED_SHAPE_INDEX].r + 0.1 * delta, 0, 1);
//                } else if (roulette < 0.5) {
//                    polygons[CHANGED_SHAPE_INDEX].g =
//                            CLAMP(polygons[CHANGED_SHAPE_INDEX].g + 0.1 * delta, 0, 1);
//                } else if (roulette < 0.75) {
//                    polygons[CHANGED_SHAPE_INDEX].b =
//                            CLAMP(polygons[CHANGED_SHAPE_INDEX].b + 0.1 * delta, 0, 1);
//                } else if (roulette < 1.0) {
//                    polygons[CHANGED_SHAPE_INDEX].a =
//                            CLAMP(polygons[CHANGED_SHAPE_INDEX].a + 0.1 * delta, 0.0, 1.0);
//                }
//            }
//            // mutate shape
//            else {
//                int CHANGED_POINT_INDEX = randEngine.nextInt(manager.ACTUAL_POINTS - 1);
//
//                // x-coordinate
//                if (roulette < 1.5) {
//                    polygons[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] =
//                            CLAMP(polygons[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] + delta, 0, manager.MAX_WIDTH);
//                }
//
//                // y-coordinate
//                else {
//                    polygons[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] =
//                            CLAMP(polygons[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] + delta, 0, manager.MAX_HEIGHT);
//                }
//            }
//        }
//
//        private double CLAMP(double val, double min, double max) {
//            if (val < min) return min;
//            if (val > max) return max;
//            return val;
//        }
//    },
    MEDIUM {
        @Override
        public Chromosome mutate(Chromosome chromosome) {
            assert chromosome instanceof Paintings;
            return apply(chromosome);
        }

        private Chromosome apply(Chromosome chromosome) {
            Paintings paintings = (Paintings) chromosome;
            EvoManager manager = paintings.manager;
            RandEngine randEngine = manager.randEngine;
            List<Polygon> dna_out = paintings.getPolygons();

            int CHANGED_SHAPE_INDEX = randEngine.nextInt(manager.ACTUAL_SHAPES - 1);
            double roulette = randEngine.uniform() * 2.0;

            // mutate color
            if (roulette < 1) {
                if (roulette < 0.25) {
                    dna_out.get(CHANGED_SHAPE_INDEX).r = randEngine.uniform();
                } else if (roulette < 0.5) {
                    dna_out.get(CHANGED_SHAPE_INDEX).g = randEngine.uniform();
                } else if (roulette < 0.75) {
                    dna_out.get(CHANGED_SHAPE_INDEX).b = randEngine.uniform();
                } else if (roulette < 1.0) {
                    dna_out.get(CHANGED_SHAPE_INDEX).a = randEngine.uniform();
                }
            }
            // mutate shape
            else {
                int CHANGED_POINT_INDEX = randEngine.nextInt(manager.ACTUAL_POINTS - 1);
                if (roulette < 1.5) {
                    dna_out.get(CHANGED_SHAPE_INDEX).x_points[CHANGED_POINT_INDEX] = randEngine.nextInt(manager.MAX_WIDTH);
                } else {
                    dna_out.get(CHANGED_SHAPE_INDEX).y_points[CHANGED_POINT_INDEX] = randEngine.nextInt(manager.MAX_HEIGHT);
                }
            }
            return paintings.newCopy(dna_out);
        }
    },;
//    HARD {
//        @Override
//        public void apply(Polygon[] polygons, EvoManager manager, RandEngine randEngine) {
//            int CHANGED_SHAPE_INDEX = randEngine.nextInt(manager.ACTUAL_SHAPES - 1);
//
//            polygons[CHANGED_SHAPE_INDEX].r = randEngine.uniform();
//            polygons[CHANGED_SHAPE_INDEX].g = randEngine.uniform();
//            polygons[CHANGED_SHAPE_INDEX].b = randEngine.uniform();
//            polygons[CHANGED_SHAPE_INDEX].a = randEngine.uniform();
//            int CHANGED_POINT_INDEX = randEngine.nextInt(manager.ACTUAL_POINTS - 1);
//
//            polygons[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] = randEngine.nextInt(manager.MAX_WIDTH);
//            polygons[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] = randEngine.nextInt(manager.MAX_HEIGHT);
//        }
//    }
}
