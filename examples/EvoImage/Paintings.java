package EvoImage;

import com.zluo.ga.Chromosome;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;
import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static EvoImage.EvoSetting.*;

@Setter
@Getter
public class Paintings implements Chromosome<Paintings> {
    private Polygon[] polygons = new Polygon[MAX_SHAPES];
    private double fitness = 0;

    @Override
    public List<Paintings> crossover(Paintings chromosome, double uniformRate) {
        return null;
    }

    @Override
    public void mutate(double mutationRate) {
        method.apply(polygons, randEngine);
    }

    DoubleMatrix toImage() {
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

        List<Double> colors = new ArrayList<>(EvoSetting.image_colors.length);
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                int pixel = bi.getRGB(j, i);
                colors.addAll(pixelARGB(pixel));
            }
        }
        return new DoubleMatrix(colors);
    }

    @Override
    public Paintings makeCopy() {
        Paintings clone = new Paintings();
        for (int i = 0; i < MAX_SHAPES; i++) {
            clone.polygons[i] = polygons[i].clone();
        }
        return clone;
    }
}
