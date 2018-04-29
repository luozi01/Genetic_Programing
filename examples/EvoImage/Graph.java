package EvoImage;

import com.zluo.ga.Chromosome;
import com.zluo.ga.utils.RandEngine;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;
import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import static EvoImage.EvoSetting.*;

@Setter
@Getter
public class Graph implements Chromosome<Graph> {
    private List<Polygon> polygons = new LinkedList<>();
    private RandEngine randEngine = new Rand();

    @Override
    public List<Graph> crossover(Graph chromosome, double uniformRate) {
        return null;
    }

    @Override
    public void mutate(double mutationRate) {
        switch (EvoSetting.method) {
            case Gaussian:
                break;
            case Soft:
                mutate_soft();
                break;
            case Medium:
                mutate_medium();
                break;
            case Hard:
                break;
        }
    }

    private void mutate_medium() {
        for (Polygon polygon : polygons) {
            if (randEngine.uniform() < .3) {
                double roulette = randEngine.uniform() * 2.0;
                // mutate color
                if (roulette < 1) {
                    if (roulette < 0.25) {
                        polygon.setR(randEngine.uniform());
                    } else if (roulette < 0.5) {
                        polygon.setG(randEngine.uniform());
                    } else if (roulette < 0.75) {
                        polygon.setB(randEngine.uniform());
                    } else if (roulette < 1.0) {
                        polygon.setA(randEngine.uniform());
                    }
                }
                // mutate shape
                else {
                    int CHANGED_POINT_INDEX = randEngine.nextInt(ACTUAL_POINTS - 1);
                    if (roulette < 1.5) {
                        polygon.getX_points()[CHANGED_POINT_INDEX] =
                                randEngine.nextInt(MAX_WIDTH) * 1.0;
                    } else {
                        polygon.getY_points()[CHANGED_POINT_INDEX] =
                                randEngine.nextInt(MAX_HEIGHT) * 1.0;
                    }
                }
            }
        }
    }

    private void mutate_soft() {
        int CHANGED_SHAPE_INDEX = randEngine.nextInt(ACTUAL_SHAPES - 1);

        double roulette = randEngine.uniform() * 2.0;

        double delta = -1 + randEngine.nextInt(3);

        // mutate color
        if (roulette < 1) {
            if (roulette < 0.25) {
                polygons.get(CHANGED_SHAPE_INDEX).setR(
                        CLAMP(polygons.get(CHANGED_SHAPE_INDEX).getR() + delta, 0, 255));
            } else if (roulette < 0.5) {
                polygons.get(CHANGED_SHAPE_INDEX).setG(
                        CLAMP(polygons.get(CHANGED_SHAPE_INDEX).getG() + delta, 0, 255));
            } else if (roulette < 0.75) {
                polygons.get(CHANGED_SHAPE_INDEX).setB(
                        CLAMP(polygons.get(CHANGED_SHAPE_INDEX).getB() + delta, 0, 255));
            } else if (roulette < 1.0) {
                polygons.get(CHANGED_SHAPE_INDEX).setA(
                        CLAMP(polygons.get(CHANGED_SHAPE_INDEX).getA() + 0.1 * delta, 0.0, 1.0));
            }
        }
        // mutate shape
        else {
            int CHANGED_POINT_INDEX = randEngine.nextInt(ACTUAL_POINTS - 1);

            // x-coordinate
            if (roulette < 1.5) {
                polygons.get(CHANGED_SHAPE_INDEX).getX_points()[CHANGED_POINT_INDEX] =
                        CLAMP(polygons.get(CHANGED_SHAPE_INDEX).getX_points()[CHANGED_POINT_INDEX] + delta, 0, MAX_WIDTH);
            }

            // y-coordinate
            else {
                polygons.get(CHANGED_SHAPE_INDEX).getY_points()[CHANGED_POINT_INDEX] =
                        CLAMP(polygons.get(CHANGED_SHAPE_INDEX).getY_points()[CHANGED_POINT_INDEX] + delta, 0, MAX_HEIGHT);
            }
        }
    }


    private double CLAMP(double val, double min, double max) {
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }

    DoubleMatrix toImage() {
        Group root = new Group();
        root.getChildren().clear();

        Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Polygon p : polygons) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.getX_points(), p.getY_points(), p.getN_points());
        }
        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, MAX_WIDTH, MAX_HEIGHT);
        WritableImage image = scene.snapshot(null);
        BufferedImage bi = SwingFXUtils.fromFXImage(image, null);

        double[][] colors = new double[EvoSetting.colors.rows][EvoSetting.colors.columns];
        int count = 0;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                int pixel = bi.getRGB(j, i);
                double[] c = pixelARGB(pixel);
                colors[count++] = c.clone();
            }
        }
        return new DoubleMatrix(colors);
    }

    @Override
    public Graph makeCopy() {
        Graph clone = new Graph();
        for (int i = 0; i < polygons.size(); i++) {
            clone.polygons.add(polygons.get(i).clone());
        }
        return clone;
    }
}
