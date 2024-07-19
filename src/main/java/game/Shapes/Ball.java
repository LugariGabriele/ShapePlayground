package game.Shapes;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;


public class Ball {
    private final Circle graphicCircle;
    private final Body body;
    private final BodyFixture fixture;
    private final Line radiusLine;
    private boolean isDragging = false;
    private double gravityScale;



    public Ball(double radius, double centerX, double centerY) {

        //create a graphic body
        graphicCircle = new Circle(radius, getRandomColorExceptRed());
        graphicCircle.setStroke(Color.BLACK);
        radiusLine = new Line();
        radiusLine.setStroke(Color.BLACK);
        radiusLine.setStrokeWidth(1);

        //create a psychic body
        body = new Body();
        org.dyn4j.geometry.Circle shape = new org.dyn4j.geometry.Circle(radius);
        fixture = new BodyFixture(shape);

        //psychical properties
        fixture.setDensity(1.0);
        fixture.setFriction(0.5);
        fixture.setRestitution(0.5);
        body.addFixture(fixture);
        body.setMass(MassType.NORMAL);
        body.translate(centerX, centerY);
        body.setAngularDamping(0.2);
        updateRadiusLine();
        eventMouseHandler();

    }

    public BodyFixture getFixture() {
        return fixture;
    }


    public Circle getGraphicCircle() {
        return graphicCircle;
    }

    public Body getBody() {
        return body;
    }

    /**
     * a method that create a random color until the condition is false
     *
     * @return a random color that can't be red
     */
    private Color getRandomColorExceptRed() {
        double red, green, blue;
        do {
            red = Math.random();
            green = Math.random();
            blue = Math.random();
        } while (red > 0.7);
        return Color.color(red, green, blue);
    }

    public Line getRadiusLine() {
        return radiusLine;
    }

    /**
     * update the radius line in base of the rotation of the body
     */
    public void updateRadiusLine() {

        double radius = graphicCircle.getRadius();
        double angle = body.getTransform().getRotation().toRadians();

        double endX = graphicCircle.getCenterX() + radius * Math.cos(angle);
        double endY = graphicCircle.getCenterY() + radius * Math.sin(angle);

        radiusLine.setStartX(graphicCircle.getCenterX());
        radiusLine.setStartY(graphicCircle.getCenterY());
        radiusLine.setEndX(endX);
        radiusLine.setEndY(endY);
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public void eventMouseHandler() {
        graphicCircle.setOnMousePressed(event -> {
            setDragging(true);
            gravityScale = 0.0;
            body.setGravityScale(gravityScale);
            body.setLinearVelocity(0,0);
            body.rotate(0);

        });

        graphicCircle.setOnMouseDragged(event -> {
            if (isDragging) {
                gravityScale = 0.0;
                body.setGravityScale(gravityScale);
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();

                graphicCircle.setCenterX(mouseX);
                graphicCircle.setCenterY(mouseY);

                body.getTransform().setTranslation(graphicCircle.getCenterX(),graphicCircle.getCenterY());
                updateRadiusLine();
            }
        });

        graphicCircle.setOnMouseReleased(event -> {
            setDragging(false);
            gravityScale = 1.0;
            body.setGravityScale(gravityScale);
        });
    }
}
