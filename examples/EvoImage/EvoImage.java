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

    private Graph run() {
        Population<Graph> pop = new Population<>(new GraphGenerator());
        EvoGA<Graph> ga = new EvoGA<>(pop, new ImageFitCalc());
        ga.addIterationListener(environment -> {
            Graph bestGene = environment.getBest();
            double bestFit = new ImageFitCalc().calc(bestGene);
            double norm = 100 * (1 - bestFit / (MAX_WIDTH * MAX_HEIGHT * 3.0));
            // log to console
            if (environment.getIteration() % 100 == 0)
                System.out.printf("Generation = %s \t fit = %s \t norm = %s\n",
                        environment.getIteration(), bestFit, norm);

            // halt condition
            if (norm > 90) {
                environment.terminate();
            }
        });
        System.out.println("start");
        ga.evolve(10000);
        return ga.getBest();
    }

    @Override
    public void start(Stage stage) {
        Graph best = run();

        for (int i = 6000; i < 6090; i += 4) {
            System.out.printf("Red: %f, Green: %f, Blue: %f, Alpha: %f\n", best.toImage().get(i),
                    best.toImage().get(i + 1), best.toImage().get(i + 2), best.toImage().get(i + 3));
        }
        //Creating a Group object
        Group root = new Group();

        Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (Shape p : best.getShapes()) {
            gc.setFill(p.getColor());
            gc.fillPolygon(p.getX_points(), p.getY_points(), p.getN_points());
        }

        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, MAX_WIDTH, MAX_HEIGHT);
        //Adding scene to the stage
        stage.setScene(scene);
        //Displaying the contents of the stage
        stage.show();
    }

    public static void main(String[] args) {
        BufferedImage img;
        EvoSetting evoSetting = new EvoSetting();
        try {
            img = ImageIO.read(new File("ml.bmp"));
            evoSetting.readImage(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        for (int i = 6000; i < 6090; i += 4) {
            System.out.printf("Red: %f, Green: %f, Blue: %f, Alpha: %f\n", EvoSetting.colors.get(i),
                    EvoSetting.colors.get(i + 1), EvoSetting.colors.get(i + 2), EvoSetting.colors.get(i + 3));
        }
        long start_time = System.currentTimeMillis();
        launch(args);
        System.out.println((System.currentTimeMillis() - start_time) / 60000.0);
    }
}
