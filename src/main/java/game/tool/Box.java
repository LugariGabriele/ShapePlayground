package game.tool;

import java.awt.*;
import javafx.scene.shape.Rectangle;
public class Box extends Rectangle {

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




    public double getArea(){
        return height*width;
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
