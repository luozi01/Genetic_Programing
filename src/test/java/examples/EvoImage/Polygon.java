package examples.EvoImage;

import javafx.scene.paint.Color;

public class Polygon implements Cloneable {
    double[] x_points, y_points;
    double r, g, b, a;
    int n_points;

    Polygon(int n_points) {
        this.n_points = n_points;
        x_points = new double[n_points];
        y_points = new double[n_points];
    }

    Polygon(double[] x_points, double[] y_points) {
        this.x_points = x_points;
        this.y_points = y_points;
        assert x_points.length == y_points.length;
        n_points = x_points.length;
    }

    void add(int i, double x, double y) {
        x_points[i] = x;
        y_points[i] = y;
    }

    Color getColor() {
        return new Color(r, g, b, a);
    }

    void setColor(double... colors) {
        r = colors[0];
        g = colors[1];
        b = colors[2];
        a = colors[3];
    }

    @Override
    public Polygon clone() {
        try {
            Polygon clone = (Polygon) super.clone();
            clone.x_points = x_points.clone();
            clone.y_points = y_points.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
