package org.example;

import java.awt.*;

public class Box {

    double width;
    double height;

    Point UpperLeft;
    Point BottomLeft;
    Point UpperRight;
    Point BottomRight;

    public Box(double width, double height, Point upperLeft, Point bottomLeft, Point upperRight, Point bottomRight) {
        this.width = width;
        this.height = height;
        UpperLeft = upperLeft;
        BottomLeft = bottomLeft;
        UpperRight = upperRight;
        BottomRight = bottomRight;
    }

    /**
     *
     * @param width
     * @param height
     * @return an error string if the value of the parameter is <0
     */
    private static final boolean validate(double width, double height) {
        if (width <= 0.0) {
            throw new IllegalArgumentException("width can't be a negative value");
        } else if (height <= 0.0) {
            throw new IllegalArgumentException("height can't be a negative value");
        } else {
            return true;
        }
    }


    public double getArea(){
        return height*width;
    }



    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Point getUpperLeft() {
        return UpperLeft;
    }

    public void setUpperLeft(Point upperLeft) {
        UpperLeft = upperLeft;
    }

    public Point getBottomLeft() {
        return BottomLeft;
    }

    public void setBottomLeft(Point bottomLeft) {
        BottomLeft = bottomLeft;
    }

    public Point getUpperRight() {
        return UpperRight;
    }

    public void setUpperRight(Point upperRight) {
        UpperRight = upperRight;
    }

    public Point getBottomRight() {
        return BottomRight;
    }

    public void setBottomRight(Point bottomRight) {
        BottomRight = bottomRight;
    }
}
