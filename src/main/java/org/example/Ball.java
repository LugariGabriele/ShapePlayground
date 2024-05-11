package org.example;

import java.awt.*;


public class Ball {
    double radius;
    Color color;

    Point center;



    public Ball(double radius, Color color, Point XY) {
        this.radius = radius;
        this.color = color;
        this.center = XY;
    }


    /**
     *
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
     *
     * @return the circumference of the Ball
     */
    public double getCircumference(){
        return 2*this.radius*Math.PI;
    }

    /**
     *
     * @return the area of the org.example.Ball
     */
    public double getArea() {

        return Math.PI * this.radius * this.radius;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public Point getCenter() {
        return center;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    @Override
    public String toString() {
        return "org.example.Ball{" +
                "radius=" + radius +
                ", color=" + color +
                ", XY=" + center +
                '}';
    }
}


