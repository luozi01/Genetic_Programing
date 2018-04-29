package EvoImage;

import javafx.scene.shape.Polygon;
import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;

class EvoSetting {
    private final static int MAX_SHAPES = 50;    // max capacity
    private final static int MAX_POINTS = 6;

    static int MAX_WIDTH;
    static int MAX_HEIGHT;
    static int populationSize = 2;
    static int ACTUAL_SHAPES = MAX_SHAPES;
    static int ACTUAL_POINTS = MAX_POINTS;
    static Mutation method = Mutation.Medium;

    static DoubleMatrix colors;

    void readImage(BufferedImage image) {
        MAX_HEIGHT = image.getHeight();
        MAX_WIDTH = image.getWidth();
        double[][] array = new double[MAX_WIDTH * MAX_HEIGHT][4];
        int count = 0;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                int pixel = image.getRGB(j, i);
                double[] c = pixelARGB(pixel);
                array[count++] = c.clone();
            }
        }
        colors = new DoubleMatrix(array);
    }

    static double[] pixelARGB(int pixel) {
        double alpha = (pixel >> 24) & 0xff;
        double red = (pixel >> 16) & 0xff;
        double green = (pixel >> 8) & 0xff;
        double blue = (pixel) & 0xff;
        return new double[]{red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0};
    }

    static double[][] toList(Polygon polygon) {
        double[][] list = new double[2][polygon.getPoints().size() / 2];
        for (int i = 0; i < polygon.getPoints().size() / 2; i++) {
            list[0][i] = polygon.getPoints().get(i * 2);
            list[1][i] = polygon.getPoints().get(i * 2 + 1);
        }
        return list;
    }
}