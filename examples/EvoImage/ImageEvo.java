package EvoImage;

import com.zluo.ga.utils.RandEngine;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.jblas.DoubleMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageEvo extends Application {
    private static int IWIDTH = 0;
    private static int IHEIGHT = 0;

    private int COUNTER_TOTAL = 0;
    private int COUNTER_BENEFIT = 0;
    private int MAX_SHAPES = 100;    // max capacity
    private int MAX_POINTS = 6;
    private int ACTUAL_SHAPES = MAX_SHAPES; // current size
    private int ACTUAL_POINTS = MAX_POINTS;
    private Polygon[] DNA_BEST = new Polygon[MAX_SHAPES];
    private Polygon[] DNA_TEST = new Polygon[MAX_SHAPES];
    private int CHANGED_SHAPE_INDEX = 0;
    private double FITNESS_MAX = Double.MAX_VALUE;
    private double FITNESS_TEST = FITNESS_MAX;
    private double FITNESS_BEST = FITNESS_MAX;
    private double FITNESS_BEST_NORMALIZED = 0; // pixel match: 0% worst - 100% best
    private RandEngine randEngine = new SimpleRandEngine();
    private static DoubleMatrix image_colors;

    public static void main(String[] args) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File("ml.bmp"));
            IHEIGHT = img.getHeight();
            IWIDTH = img.getWidth();
            readImage(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        long start_time = System.currentTimeMillis();
        launch(args);
        System.out.println((System.currentTimeMillis() - start_time) / 60000.0);
    }

    private void mutate_medium(Polygon[] dna_out) {
        CHANGED_SHAPE_INDEX = randEngine.nextInt(ACTUAL_SHAPES - 1);
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
                dna_out[CHANGED_SHAPE_INDEX].x_points[CHANGED_POINT_INDEX] = randEngine.nextInt(IWIDTH);
            } else {
                dna_out[CHANGED_SHAPE_INDEX].y_points[CHANGED_POINT_INDEX] = randEngine.nextInt(IHEIGHT);
            }
        }
    }

    private double compute_fitness() {
        DoubleMatrix color = toImage(DNA_TEST);
        return color.sub(image_colors).norm1();
    }

    private void pass_gene_mutation(Polygon[] dna_from, Polygon[] dna_to, int gene_index) {
        dna_to[gene_index].r = dna_from[gene_index].r;
        dna_to[gene_index].g = dna_from[gene_index].g;
        dna_to[gene_index].b = dna_from[gene_index].b;
        dna_to[gene_index].a = dna_from[gene_index].a;

        for (int i = 0; i < MAX_POINTS; i++) {
            dna_to[gene_index].x_points[i] = dna_from[gene_index].x_points[i];
            dna_to[gene_index].y_points[i] = dna_from[gene_index].y_points[i];
        }
    }

    private void copyDNA(Polygon[] dna_from, Polygon[] dna_to) {
        for (int i = 0; i < MAX_SHAPES; i++)
            pass_gene_mutation(dna_from, dna_to, i);
    }

    private void evolve() {
        mutate_medium(DNA_TEST);

        FITNESS_TEST = compute_fitness();
        if (FITNESS_TEST < FITNESS_BEST) {
            pass_gene_mutation(DNA_TEST, DNA_BEST, CHANGED_SHAPE_INDEX);

            FITNESS_BEST = FITNESS_TEST;
            FITNESS_BEST_NORMALIZED = 100 * (1 - FITNESS_BEST / (IWIDTH * IHEIGHT * 3.0));

            COUNTER_BENEFIT++;
            System.out.printf("Generation = %s \t fit = %s \t norm = %s\n",
                    COUNTER_BENEFIT, FITNESS_BEST, FITNESS_BEST_NORMALIZED);
        } else {
            pass_gene_mutation(DNA_BEST, DNA_TEST, CHANGED_SHAPE_INDEX);
        }
        COUNTER_TOTAL++;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void init_dna(Polygon[] dna) {
        for (int i = 0; i < MAX_SHAPES; i++) {
            Polygon polygon = new Polygon(MAX_POINTS);
            for (int j = 0; j < MAX_POINTS; j++) {
                polygon.add(j, randEngine.nextInt(IWIDTH), randEngine.nextInt(IHEIGHT));
            }
            polygon.setColor(0, 0, 0, 0.001);
            dna[i] = polygon;
        }
    }

    int clamp(int val, int minval, int maxval) {
        if (val < minval) return minval;
        if (val > maxval) return maxval;
        return val;
    }

    private static List<Double> pixelARGB(int pixel) {
        double red = (pixel >> 16) & 0xff;
        double green = (pixel >> 8) & 0xff;
        double blue = (pixel) & 0xff;
        return Arrays.asList(red / 255.0, green / 255.0, blue / 255.0);
    }

    private static void readImage(BufferedImage image) {
        IHEIGHT = image.getHeight();
        IWIDTH = image.getWidth();
        List<Double> color = new ArrayList<>(IWIDTH * IHEIGHT * 3);
        for (int i = 0; i < IHEIGHT; i++) {
            for (int j = 0; j < IWIDTH; j++) {
                int pixel = image.getRGB(j, i);
                color.addAll(pixelARGB(pixel));
            }
        }
        image_colors = new DoubleMatrix(color);
    }

    private DoubleMatrix toImage(Polygon[] polygons) {
        Group root = new Group();

        Canvas canvas = new Canvas(IWIDTH, IHEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Polygon p : polygons) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.x_points, p.y_points, p.n_points);
        }
        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, IWIDTH, IHEIGHT);
        WritableImage image = scene.snapshot(null);
        BufferedImage bi = SwingFXUtils.fromFXImage(image, null);

        List<Double> colors = new ArrayList<>(image_colors.length);
        for (int i = 0; i < IHEIGHT; i++) {
            for (int j = 0; j < IWIDTH; j++) {
                int pixel = bi.getRGB(j, i);
                colors.addAll(pixelARGB(pixel));
            }
        }
        return new DoubleMatrix(colors);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) {
        //Creating a Group object
        Group root = new Group();

        init_dna(DNA_TEST);
        init_dna(DNA_BEST);
        copyDNA(DNA_BEST, DNA_TEST);
        while (FITNESS_BEST_NORMALIZED < 93) {
            evolve();
        }

        Canvas canvas = new Canvas(IWIDTH, IHEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Polygon p : DNA_BEST) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.x_points, p.y_points, p.n_points);
        }

        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, IWIDTH, IHEIGHT);
        //Adding scene to the stage
        primaryStage.setScene(scene);
        //Displaying the contents of the stage
        primaryStage.show();
    }
}
