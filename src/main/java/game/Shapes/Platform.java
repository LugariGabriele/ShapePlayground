package game.Shapes;

import javafx.scene.shape.Rectangle;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;


public class Platform {
    private final Rectangle graphicRectangle;
    private final Body body;
    private final BodyFixture fixture;


    public Platform(double upperLeftX, double upperLeftY, double width, double height) {
        //graphic body
        graphicRectangle = new Rectangle(upperLeftX, upperLeftY, width, height);

        //physic body
        body = new Body();
        org.dyn4j.geometry.Rectangle shape = new org.dyn4j.geometry.Rectangle(width, height);
        fixture = new BodyFixture(shape);
        fixture.setDensity(1.0);
        fixture.setFriction(0.5);
        fixture.setRestitution(0);
        body.addFixture(fixture);
        body.setMass(MassType.NORMAL);
        body.translate(upperLeftX + width / 2, upperLeftY + height / 2);
    }

    public Rectangle getGraphicRectangle() {
        return graphicRectangle;
    }

    public Body getBody() {
        return body;
    }

    public BodyFixture getFixture() {
        return fixture;
    }
}
