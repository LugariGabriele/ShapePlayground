package game.tool;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.util.Objects;


public class Ball extends Circle {
    double radius;
    Paint color;
    Circle circle;
    Point center;
    Point velocity;
    double mass;

    public Ball(double radius, Paint color, Point center) {
        this.radius = radius;
        this.center = center;
        this.circle = new Circle(center.getX(), center.getY(), radius, color);
        this.velocity = new Point(0, 0);


    }


    /**
     * @param radius
     * @return a warning string if the radius is negative
     */
    private static boolean validate(double radius) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius can't be a negative value");
        } else {
            return true;
        }
    }

    /**
     * @return the circumference of the Ball
     */
    public double getCircumference() {
        return 2 * this.radius * Math.PI;
    }

    /**
     * @return the area of the ball
     */
    public double getArea() {

        return Math.PI * this.radius * this.radius;
    }


    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        circle.setCenterX(center.getX());
        circle.setCenterY(center.getY());
    }

    public Circle getCircle() {
        return circle;
    }

    public Point getVelocity() {
        return velocity;
    }

    public void setVelocity(Point velocity) {
        this.velocity = velocity;
    }

    public double getMass() {
        return mass;
    }

    public void fallAnimation(double finalY) {
        double fallSpeed = 5;
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double elapsedTime = (now - lastUpdate) / 1.5e7;
                double newY = circle.getCenterY() + fallSpeed * elapsedTime;

                if (newY >= finalY - circle.getRadius()) {
                    newY = finalY - circle.getRadius();
                    stop();
                }

                circle.setCenterY(newY);
                lastUpdate = now;
            }
        }.start();
    }

    @Override
    public String toString() {
        return "org.example.Ball{" +
                "radius=" + radius +
                ", color=" + color +
                ", XY=" + center +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ball ball = (Ball) o;
        return Double.compare(radius, ball.radius) == 0 && Double.compare(mass, ball.mass) == 0 && Objects.equals(color, ball.color) && Objects.equals(circle, ball.circle) && Objects.equals(center, ball.center) && Objects.equals(velocity, ball.velocity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, color, circle, center, velocity, mass);
    }
}


