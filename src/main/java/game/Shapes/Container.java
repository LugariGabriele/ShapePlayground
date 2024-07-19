package game.Shapes;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

public class Container {
    private final Group containerGroup;
    private final Rectangle graphicLeftWall;
    private final Rectangle graphicRightWall;
    private final Rectangle graphicBottomWall;
    private final Body leftBody;
    private final Body rightBody;
    private final Body bottomBody;
    private RotateTransition rotateTransition;
    private double currentRotation = 0;


    /**
     * a geometric figure made up of 3 rectangles
     */
    public Container(double x, double y, double width, double height, double wallThickness) {
        // Create graphic bodies
        graphicLeftWall = new Rectangle(x, y, wallThickness, height);
        graphicRightWall = new Rectangle(x + width - wallThickness, y, wallThickness, height);
        graphicBottomWall = new Rectangle(x, y + height - wallThickness, width, wallThickness);
        graphicBottomWall.setStroke(Color.BLACK);
        graphicLeftWall.setStroke(Color.BLACK);
        graphicRightWall.setStroke(Color.BLACK);
        containerGroup = new Group(graphicLeftWall, graphicBottomWall, graphicRightWall);

        // create psychic bodies
        leftBody = new Body();
        rightBody = new Body();
        bottomBody = new Body();
        leftBody.addFixture(new BodyFixture(Geometry.createRectangle(wallThickness, height)));
        rightBody.addFixture(new BodyFixture(Geometry.createRectangle(wallThickness, height)));
        bottomBody.addFixture(new BodyFixture(Geometry.createRectangle(width, wallThickness)));

        leftBody.setMass(MassType.INFINITE);
        rightBody.setMass(MassType.INFINITE);
        bottomBody.setMass(MassType.INFINITE);

        leftBody.translate(x + wallThickness / 2, y + height / 2);
        rightBody.translate(x + width - wallThickness / 2, y + height / 2);
        bottomBody.translate(x + width / 2, y + height - wallThickness / 2);


    }

    public Rectangle getGraphicLeftWall() {
        return graphicLeftWall;
    }

    public Rectangle getGraphicRightWall() {
        return graphicRightWall;
    }

    public Rectangle getGraphicBottomWall() {
        return graphicBottomWall;
    }

    public Body getLeftBody() {
        return leftBody;
    }

    public Body getRightBody() {
        return rightBody;
    }

    public Body getBottomBody() {
        return bottomBody;
    }

    public Group getContainerGroup() {
        return containerGroup;
    }


    /**
     * a method that make the graphic part and the body rotate of an @angle in @duration sec
     */
    public void applyRotation(double angle, Duration duration) {
        if (rotateTransition != null) {
            rotateTransition.stop();
        }

        double targetRotation = containerGroup.getRotate() + angle;

        rotateTransition = new RotateTransition(duration, containerGroup);
        rotateTransition.setByAngle(angle);
        rotateTransition.setCycleCount(1);
        rotateTransition.setOnFinished(event -> {
            currentRotation = targetRotation;
            rotateTransition = null;
        });
        rotateTransition.play();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double deltaAngle = containerGroup.getRotate() - currentRotation;
                currentRotation = containerGroup.getRotate();
                rotateBody(leftBody, deltaAngle);
                rotateBody(rightBody, deltaAngle);
                rotateBody(bottomBody, deltaAngle);
            }
        };
        timer.start();
    }

    /**
     * make a body rotate of a @deltaAngle
     */
    private void rotateBody(Body body,double deltaAngle) {
        double angle = Math.toRadians(deltaAngle);
        double centerX = containerGroup.getBoundsInParent().getCenterX();
        double centerY = containerGroup.getBoundsInParent().getCenterY();
        body.rotate(angle, centerX, centerY);
    }

}

