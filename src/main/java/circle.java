import org.dyn4j.exception.ValueOutOfRangeException;

public class circle {
    double radius;

    public circle(double radius) {
        this.radius = radius;
    }
    private static boolean validate(double radius) {
        if (radius <= 0.0) {
            throw new ValueOutOfRangeException("radius", radius, "greater than", 0.0);
        } else {
            return true;
        }
    }
    public double circonference(){
        return 2*this.radius*Math.PI;
    }
    public double getArea() {

        return Math.PI * this.radius * this.radius;
    }
}

