package game.framework;

import javafx.geometry.Point2D;
import org.dyn4j.geometry.Vector2;

public class Camera {
    public double scale;
    public double offsetX;
    public double offsetY;

    public Camera() {
    }

    public final Vector2 toWorldCoordinates(double width, double height, Point2D p) {
        if (p != null) {
            Vector2 v = new Vector2();
            v.x = (p.getX() - width * 0.5 - this.offsetX) / this.scale;
            v.y = -(p.getY() - height * 0.5 + this.offsetY) / this.scale;
            return v;
        } else {
            return null;
        }
    }
}
