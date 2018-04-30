package EvoImage;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Shape implements Cloneable {
    private double[] x_points, y_points;
    private int n_points;
    private double r, g, b, a;

    Shape(int n_points) {
        this.n_points = n_points;
        x_points = new double[n_points];
        y_points = new double[n_points];
    }

    Shape(double[] x_points, double[] y_points) {
        this.x_points = x_points;
        this.y_points = y_points;
        n_points = x_points.length;
    }

    List<Double> getPoints() {
        List<Double> points = new ArrayList<>();
        for (int i = 0; i < n_points; i++) {
            points.add(x_points[i]);
            points.add(y_points[i]);
        }
        return points;
    }

    void add(int i, double x, double y) {
        x_points[i] = x;
        y_points[i] = y;
    }

    void setColor(double... colors) {
        r = colors[0];
        g = colors[1];
        b = colors[2];
        a = colors[3];
    }

    Color getColor() {
        return new Color(r, g, b, a);
    }

    @Override
    public Shape clone() {
        Shape clone = null;
        try {
            clone = (Shape) super.clone();
            clone.x_points = x_points.clone();
            clone.y_points = y_points.clone();
            clone.n_points = n_points;
            clone.r = r;
            clone.g = g;
            clone.g = b;
            clone.a = a;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
