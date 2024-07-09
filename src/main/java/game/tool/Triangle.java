package game.tool;

import javafx.scene.shape.Polygon;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class Triangle {
    private final Polygon graphicTriangle;
    private final Body body;
    private final BodyFixture fixture;

    public Triangle(double p1X, double p1Y, double p2X, double p2Y, double p3X, double p3Y) {
        //graphic body
        Vector2 vertex1 = new Vector2(p1X, p1Y);
        Vector2 vertex2 = new Vector2(p2X, p2Y);
        Vector2 vertex3 = new Vector2(p3X, p3Y);
        this.graphicTriangle = new Polygon(p1X, p1Y, p2X, p2Y, p3X, p3Y);

        //physic body
        this.body = new Body();
        org.dyn4j.geometry.Triangle shape = new org.dyn4j.geometry.Triangle(vertex1, vertex2, vertex3);
        fixture = new BodyFixture(shape);
        fixture.setDensity(1.0);
        fixture.setFriction(0.2);
        fixture.setRestitution(1);
        body.addFixture(fixture);

    }

    public Body getBody() {
        return body;
    }

    public Polygon getGraphicTriangle() {
        return graphicTriangle;
    }

    public BodyFixture getFixture() {
        return fixture;
    }
}
