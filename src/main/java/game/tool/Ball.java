package game.tool;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rotation;


public class Ball {
    private Circle graphicCircle;
    private Body body;
    private BodyFixture fixture;
    private org.dyn4j.geometry.Circle shape;
    private Line radiusLine;
    private boolean isDragging = false; // Flag per indicare se la palla è trascinata
    private double gravityScale = 1.0; // è una scala che va da 0.0 a 1.0 e serve per dare la forza con cui è affetta da gravità

    public Ball(double radius, double centerX, double centerY) {
        /**
         * parte grafica
         */
        graphicCircle = new Circle(radius, getRandomColorExceptRed());
        graphicCircle.setStroke(Color.BLACK); // bordi
        radiusLine = new Line(); // linea grafica per vedere se la ball ruotano
        radiusLine.setStroke(Color.BLACK);
        radiusLine.setStrokeWidth(1);
        /**
         * parte fisica
         */
        body = new Body();
        shape = new org.dyn4j.geometry.Circle(radius);
        fixture = new BodyFixture(shape);
        /**
         * propietà della ball (a sentimento)
         */
        fixture.setDensity(1.0);    // su uno è in modo che palla piena( serve per gravità e urti) se più basso palla non piena
        fixture.setFriction(0.5); // coeff attrito(più alto è più scorre male tra oggetti)
        fixture.setRestitution(0.5); // se alto ball fanno mega rimbalzi su altri oggetti
        body.addFixture(fixture);
        body.setMass(MassType.NORMAL);
        body.translate(centerX, centerY); // posizione iniziale corpo fisico


        // Aggiungi gestori degli eventi del mouse per il trascinamento della palla
        eventMouseHandler();
        updateRadiusLine();
    }

    public Circle getGraphicCircle() {
        return graphicCircle;
    }

    public void eventMouseHandler() {
        graphicCircle.setOnMousePressed(event -> {
            isDragging = true;
            gravityScale = 0.0;
            body.setGravityScale(gravityScale); // rimuovo la gravità della palla quando voglio draggarla
        });

        graphicCircle.setOnMouseDragged(event -> {
            if (isDragging) {
                gravityScale = 0.0; // lo rimetto tanto per essere sicuro
                body.setGravityScale(gravityScale);
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();

                // Imposta le nuove coordinate della palla
                double newCenterX = mouseX;
                double newCenterY = mouseY;

                // Aggiorna la posizione della palla
                graphicCircle.setCenterX(newCenterX);
                graphicCircle.setCenterY(newCenterY);

                // Memorizza la nuova posizione del trascinamento come posizione iniziale per il prossimo spostamento

                // Aggiorna la posizione del corpo fisico
                body.getTransform().setTranslation(graphicCircle.getCenterX(), graphicCircle.getCenterY());
                updateRadiusLine();
            }
        });

        graphicCircle.setOnMouseReleased(event -> {
            /**
             * quando mollo riattivo gravità
             */
            isDragging = false;
            gravityScale = 1.0;
            body.setGravityScale(gravityScale);
        });
    }

    public Body getBody() {
        return body;
    }

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
     * aggiorna linea del raggio
     */
    public void updateRadiusLine() {
        double radius = graphicCircle.getRadius();
        double angle = body.getTransform().getRotation().toRadians(); // Ottieni l'angolo in radianti

        double endX = graphicCircle.getCenterX() + radius * Math.cos(angle); // dovrebbe far muovere la linea
        double endY = graphicCircle.getCenterY() + radius * Math.sin(angle);

        radiusLine.setStartX(graphicCircle.getCenterX());
        radiusLine.setStartY(graphicCircle.getCenterY());
        radiusLine.setEndX(endX);
        radiusLine.setEndY(endY);
    }




}
