package EvoImage;

import com.zluo.ga.Population;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static EvoImage.EvoSetting.toList;

public class EvoImage extends Application {


    private Graph run() {
        Population<Graph> pop = new Population<>(new GraphGenerator());
        EvoGA<Graph> ga = new EvoGA<>(pop, new ImageFitCalc());
        ga.addIterationListener(environment -> {
            Graph bestGene = environment.getBest();
            double bestFit = new ImageFitCalc().calc(bestGene);

            // log to console
            System.out.printf("Generation = %s \t fit = %s \n", environment.getIteration(), bestFit);

            // halt condition
            if (bestFit < 5) {
                environment.terminate();
            }
        });
        System.out.println("start");
        ga.evolve();
        return ga.getBest();
    }

    @Override
    public void start(Stage stage) {

        Graph best = run();

        //Creating a Group object
        Group root = new Group();
        root.getChildren().clear();

        Canvas canvas = new Canvas(EvoSetting.MAX_WIDTH, EvoSetting.MAX_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < best.getPolygons().size(); i++) {
            Polygon p = new Polygon();
            p.getPoints().addAll(best.getPolygons().get(i).getPoints());
            gc.setFill(best.getPolygons().get(i).getColor());
            double[][] co = toList(p);
            gc.fillPolygon(co[0], co[1], co[0].length);
        }

        root.getChildren().add(canvas);
        //Creating a scene object
        Scene scene = new Scene(root, EvoSetting.MAX_WIDTH, EvoSetting.MAX_HEIGHT);
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
            e.printStackTrace();
        }
        long start_time = System.currentTimeMillis();
        launch(args);
        System.out.println((System.currentTimeMillis() - start_time) / 60000.0);
    }
}
