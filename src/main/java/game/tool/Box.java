package game.tool;

import javafx.scene.shape.Rectangle;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;

public class Box {
     private Rectangle graphicRectangle;
     private Body body;
    private BodyFixture fixture;
    private org.dyn4j.geometry.Rectangle shape;

    /**
     * fatta generica in modo che se vogliamo mettere rettangoli non fermi è già pronto
     * @param upperLeftX
     * @param upperLeftY
     * @param width
     * @param height
     */
    public Box(double upperLeftX,double upperLeftY, double width, double height){
        graphicRectangle = new Rectangle(upperLeftX,upperLeftY,width,height);
        body= new Body();
        shape = new org.dyn4j.geometry.Rectangle(width, height);
        fixture= new BodyFixture(shape);
        fixture.setDensity(1.0);
        fixture.setFriction(0.5);
        fixture.setRestitution(0.2); // così ball non fanno ancora meno mega rimbalzi
        body.addFixture(fixture);
        body.setMass(MassType.NORMAL);
        body.translate(upperLeftX + width / 2, upperLeftY + height / 2); // messo così perchè senno body era messo diverso da grafic


    }

    public Rectangle getGraphicRectangle() {
        return graphicRectangle;
    }

    public Body getBody() {
        return body;
    }
    public void updateGraphicRectangle() {
        // Sincronizza la posizione del rettangolo grafico con il corpo fisico dato che body fatto diverso da quell di fx
        // serve se voglio fare rettangolo dinamico
        graphicRectangle.setX(body.getTransform().getTranslationX() - graphicRectangle.getWidth() / 2);
        graphicRectangle.setY(body.getTransform().getTranslationY() - graphicRectangle.getHeight() / 2);
    }
}
