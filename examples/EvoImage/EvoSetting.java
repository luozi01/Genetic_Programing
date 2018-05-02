package EvoImage;

import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static EvoImage.ColorChoice.BLACK;
import static EvoImage.Mutation.MEDIUM;

class EvoSetting {
    static int MAX_SHAPES = 50;    // max capacity
    static int MAX_POINTS = 6;
    static int MAX_WIDTH;
    static int MAX_HEIGHT;
    static int ACTUAL_SHAPES = MAX_SHAPES;
    static int ACTUAL_POINTS = MAX_POINTS;
    static Mutations method = MEDIUM;
    static Colors choice = BLACK;
    static DoubleMatrix image_colors;

    static int rand_int(int val) {
        return (int) (val * Math.random());
    }

    static double CLAMP(double val, double min, double max) {
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }

    static List<Double> pixelARGB(int pixel) {
//        double alpha = (pixel >> 24) & 0xff;
        double red = (pixel >> 16) & 0xff;
        double green = (pixel >> 8) & 0xff;
        double blue = (pixel) & 0xff;
        return new ArrayList<>(Arrays.asList(red / 255.0, green / 255.0, blue / 255.0));
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
}