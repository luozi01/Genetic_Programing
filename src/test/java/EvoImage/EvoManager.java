package EvoImage;

import genetics.interfaces.MutationPolicy;
import genetics.utils.RandEngine;
import genetics.utils.SimpleRandEngine;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class EvoManager {
    final static int MAX_SHAPES = 50;    // max capacity
    final static int MAX_POINTS = 6;
    final static int ACTUAL_SHAPES = MAX_SHAPES;
    final static int ACTUAL_POINTS = MAX_POINTS;
    final static MutationPolicy method = Mutation.MEDIUM;
    final static Colors choice = ColorChoice.BLACK;
    final static RandEngine randEngine = new SimpleRandEngine();
    static int MAX_WIDTH;
    static int MAX_HEIGHT;
    static DoubleMatrix image_colors;

    static DoubleMatrix toImage(Polygon[] polygons) {
        Group root = new Group();

        Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Polygon p : polygons) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.x_points, p.y_points, p.n_points);
        }
        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, MAX_WIDTH, MAX_HEIGHT);
        WritableImage image = scene.snapshot(null);
        BufferedImage bi = SwingFXUtils.fromFXImage(image, null);

        List<Double> colors = new ArrayList<>(image_colors.length);
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                int pixel = bi.getRGB(j, i);
                colors.addAll(pixelRGB(pixel));
            }
        }
        return new DoubleMatrix(colors);
    }

    private static List<Double> pixelRGB(int pixel) {
        double red = (pixel >> 16) & 0xff;
        double green = (pixel >> 8) & 0xff;
        double blue = (pixel) & 0xff;
        return Arrays.asList(red / 255.0, green / 255.0, blue / 255.0);
    }

    static void readImage(BufferedImage image) {
        MAX_HEIGHT = image.getHeight();
        MAX_WIDTH = image.getWidth();
        List<Double> color = new ArrayList<>(MAX_WIDTH * MAX_HEIGHT * 3);
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                int pixel = image.getRGB(j, i);
                color.addAll(pixelRGB(pixel));
            }
        }
        image_colors = new DoubleMatrix(color);
    }
}