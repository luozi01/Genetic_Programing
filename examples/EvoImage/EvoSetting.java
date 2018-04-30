package EvoImage;

import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class EvoSetting {
    private final static int MAX_SHAPES = 50;    // max capacity
    private final static int MAX_POINTS = 6;

    static int MAX_WIDTH;
    static int MAX_HEIGHT;
    static int populationSize = 1;
    static int ACTUAL_SHAPES = MAX_SHAPES;
    static int ACTUAL_POINTS = MAX_POINTS;
    static Mutation method = Mutation.Medium;
    static ColorChoice choice = ColorChoice.BLACK;

    static DoubleMatrix colors;

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
        colors = new DoubleMatrix(color);
    }

    static List<Double> pixelARGB(int pixel) {
//        double alpha = (pixel >> 24) & 0xff;
        double red = (pixel >> 16) & 0xff;
        double green = (pixel >> 8) & 0xff;
        double blue = (pixel) & 0xff;
        return new ArrayList<>(Arrays.asList(red / 255.0, green / 255.0, blue / 255.0));
    }
}