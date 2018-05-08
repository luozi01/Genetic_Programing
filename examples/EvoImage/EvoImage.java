package EvoImage;

import com.zluo.ga.Population;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EvoImage extends Application {
    private static final double FITNESS_MAX = Double.MAX_VALUE;
    private double FITNESS_TEST = FITNESS_MAX;
    private double FITNESS_BEST = FITNESS_MAX;
    private double FITNESS_BEST_NORMALIZED = 0; // pixel match: 0% worst - 100% best
    private int COUNTER_BENEFIT = 0;
    private EvoManager manager = new EvoManager();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        BufferedImage img;
        try {
            img = ImageIO.read(new File("index.jpeg"));
            manager.readImage(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private Paintings run() {
        Population<Paintings> pop = new Population<>(new GraphGenerator(manager));
        EvoGA<Paintings> ga = new EvoGA<>(pop, new ImageFitCalc());
        ga.addIterationListener(environment -> {
            Paintings bestGene = environment.getBest();
            FITNESS_TEST = new ImageFitCalc().calc(bestGene);
            // log to console
            if (FITNESS_TEST < FITNESS_BEST) {
                FITNESS_BEST = FITNESS_TEST;
                FITNESS_BEST_NORMALIZED = 100 * (1 - FITNESS_BEST / (manager.MAX_WIDTH * manager.MAX_HEIGHT * 3.0));

                System.out.printf("Generation = %s \t fit = %s \t norm = %s\n",
                        COUNTER_BENEFIT++, FITNESS_BEST, FITNESS_BEST_NORMALIZED);
            }

            // halt condition
            if (FITNESS_BEST_NORMALIZED > 93) {
                environment.terminate();
            }
        });
        long start_time = System.currentTimeMillis();
        ga.evolve();
        System.out.println((System.currentTimeMillis() - start_time) / 60000.0);
        return ga.getBest();
    }

    @Override
    public void start(Stage stage) {
        Paintings best = run();
        //Creating a Group object
        Group root = new Group();

        Canvas canvas = new Canvas(manager.MAX_WIDTH, manager.MAX_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Polygon p : best.polygons) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.x_points, p.y_points, p.n_points);
        }

        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, manager.MAX_WIDTH, manager.MAX_HEIGHT);
        //Adding scene to the stage
        stage.setScene(scene);
        //Displaying the contents of the stage
        stage.show();
    }
}
