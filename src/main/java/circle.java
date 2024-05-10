import org.dyn4j.exception.ValueOutOfRangeException;
import java.awt.geom.Dimension2D;
import java.awt.*;

public class circle {
    double radius;
    Color color;

    public circle(double radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    /**
     *
     * @param radius
     * @return a warning string if the radius is negative
     */
    private static boolean validate(double radius) {
        if (radius <= 0.0) {
            throw new ValueOutOfRangeException("radius", radius, "greater than", 0.0);
        } else {
            return true;
        }
    }

    /**
     *
     * @return the circonference of the circle
     */
    public double getCirconference(){
        return 2*this.radius*Math.PI;
    }

    /**
     *
     * @return the area of the circle
     */
    public double getArea() {

        return Math.PI * this.radius * this.radius;
    }
}


