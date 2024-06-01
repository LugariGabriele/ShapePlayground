package game.tool;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

public class Ball extends Circle {
    public Circle circle;
    public World world;
    private double radius;
    private Paint color;
    private Point2D center;
    private Body body;
    private Point2D velocity;
    private AnimationTimer animationTimer;

    public Ball(double radius, Paint color, Point2D center, World world) {
        validate(radius);
        this.radius = radius;
        this.center = center;
        this.color = color;
        this.circle = new Circle(center.getX(), center.getY(), radius);
        this.world = world;
        this.body = createBody();
        this.velocity = new Point2D(0, 0);

        setCenterX(center.getX());
        setCenterY(center.getY());
        setRadius(radius);
        setFill(color);

        fallAnimation(500);
    }

    private static boolean validate(double radius) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("radius can't be a negative value");
        } else {
            return true;
        }
    }
    private Body createBody() {
        Body body = new Body();
        org.dyn4j.geometry.Circle geomCircle = new org.dyn4j.geometry.Circle(radius);
        BodyFixture fixture = new BodyFixture(geomCircle);
        fixture.setDensity(1);
        fixture.setFriction(1);
        body.addFixture(fixture);
        body.setMass(MassType.NORMAL);
        body.getTransform().setTranslation(center.getX(), center.getY());
        world.addBody(body);
        return body;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
        setFill(color);
    }

    public Point2D getCenter() {
        return center;
    }

    public void setCenter(Point2D center) {
        this.center = center;
        setCenterX(center.getX());
        setCenterY(center.getY());
    }

    public Circle getCircle() {
        return circle;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void fallAnimation(double finalY) {

        world.setGravity(new Vector2(0, 9.8)); // set gravity to 9.8 m/s^2

        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double elapsedTime = (now - lastUpdate) / 1.5e7;

                world.update(elapsedTime); // update the physics world

                setCenterX(body.getTransform().getTranslationX());
                setCenterY(body.getTransform().getTranslationY());

                if (body.getTransform().getTranslationY() >= finalY - getRadius()) {
                    stop();
                }

                lastUpdate = now;
            }
        };
        animationTimer.start();
    }


}