package EvoImage;

import com.zluo.ga.utils.RandEngine;
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

import static EvoImage.ColorChoice.BLACK;
import static EvoImage.Mutation.MEDIUM;

class EvoManager {

    int MAX_WIDTH;
    int MAX_HEIGHT;
    int MAX_SHAPES = 50;    // max capacity
    int MAX_POINTS = 6;
    int ACTUAL_SHAPES = MAX_SHAPES;
    int ACTUAL_POINTS = MAX_POINTS;

    DoubleMatrix image_colors;
    Mutations method = MEDIUM;
    Colors choice = BLACK;
    RandEngine randEngine = new SimpleRandEngine();

    private List<Double> pixelARGB(int pixel) {
        double red = (pixel >> 16) & 0xff;
        double green = (pixel >> 8) & 0xff;
        double blue = (pixel) & 0xff;
        return Arrays.asList(red / 255.0, green / 255.0, blue / 255.0);
    }

    void readImage(BufferedImage image) {
        MAX_HEIGHT = image.getHeight();
        MAX_WIDTH = image.getWidth();
        List<Double> color = new ArrayList<>(MAX_WIDTH * MAX_HEIGHT * 3);
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                int pixel = image.getRGB(j, i);
                color.addAll(pixelARGB(pixel));
            }
        }
        image_colors = new DoubleMatrix(color);
    }

    DoubleMatrix toImage(Polygon[] polygons) {
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
                colors.addAll(pixelARGB(pixel));
            }
        }
        return new DoubleMatrix(colors);
    }
}