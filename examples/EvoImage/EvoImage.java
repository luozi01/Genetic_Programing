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

import static EvoImage.EvoSetting.MAX_HEIGHT;
import static EvoImage.EvoSetting.MAX_WIDTH;

public class EvoImage extends Application {

    private static double best_fit = Double.MAX_VALUE;
    private static int effect_generation = 0;

    public static void main(String[] args) {
        BufferedImage img;
        EvoSetting evoSetting = new EvoSetting();
        try {
            img = ImageIO.read(new File("ml.bmp"));
            evoSetting.readImage(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        long start_time = System.currentTimeMillis();
        launch(args);
        System.out.println((System.currentTimeMillis() - start_time) / 60000.0);
    }

    private Paintings run() {
        Population<Paintings> pop = new Population<>(new GraphGenerator());
        EvoGA<Paintings> ga = new EvoGA<>(pop, new ImageFitCalc());
        ga.addIterationListener(environment -> {
            Paintings bestGene = environment.getBest();
            double bestFit = new ImageFitCalc().calc(bestGene);
            double norm = 100 * (1 - bestFit / (MAX_WIDTH * MAX_HEIGHT * 3.0));
            // log to console
            if (bestFit < best_fit) {
                best_fit = bestFit;
                System.out.printf("Generation = %s \t fit = %s \t norm = %s\n",
                        effect_generation++, best_fit, norm);
            }

            // halt condition
            if (norm > 93) {
                environment.terminate();
            }
        });
        System.out.println("start");
        ga.evolve();
        return ga.getBest();
    }

    @Override
    public void start(Stage stage) {
        Paintings best = run();
        //Creating a Group object
        Group root = new Group();

        Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Polygon p : best.getPolygons()) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.x_points, p.y_points, p.n_points);
        }

        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, MAX_WIDTH, MAX_HEIGHT);
        //Adding scene to the stage
        stage.setScene(scene);
        //Displaying the contents of the stage
        stage.show();
    }
}
