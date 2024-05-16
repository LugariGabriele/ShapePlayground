package game.item;

import java.awt.*;

public class Box {

    double width;
    double height;

    Point UpperLeft;
    Point BottomRight;

    public Box(double width, double height, Point upperLeft, Point bottomRight) {
        this.width = width;
        this.height = height;
        UpperLeft = upperLeft;
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


    public Point getBottomRight() {
        return BottomRight;
    }

    public void setBottomRight(Point bottomRight) {
        BottomRight = bottomRight;
    }

    public double getLowerBound() {
        return Math.min(UpperLeft.x, BottomRight.x);
    }

    public double getUpperBound() {
        return Math.max(UpperLeft.x, BottomRight.x);
    }

    public double getLeftBound() {
        return Math.min(UpperLeft.y, BottomRight.y);
    }

    public double getRightBound() {
        return Math.max(UpperLeft.y, BottomRight.y);
    }

    @Override
    public String toString() {
        return "Box{" +
                "width=" + width +
                ", height=" + height +
                ", UpperLeft=" + UpperLeft +
                ", BottomRight=" + BottomRight +
                '}';
    }
}
