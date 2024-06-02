package game.logic;
/*
import game.tool.Ball;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import java.awt.*;
import java.util.List;

public class LogicGame {
    private static final double GRAVITY = 9.8; // accelerazione di gravità in m/s^2
    private static final double TIME_STEP = 1.0 / 60.0; // durata di ogni passo di simulazione in secondi
    private static final double DAMPING = 0.99; // coefficiente di smorzamento dell'attrito dell'aria
    private static AnchorPane anchorPane;
    private final World world;


    private Rectangle table;
    private List<Ball> balls;


    public LogicGame(List<Ball> balls, AnchorPane anchorPane) {
        this.balls = balls;
        this.anchorPane = anchorPane;
        this.table = new Rectangle(0, 470, 150, 30);
        this.world = new World();
        this.world.setGravity(new Vector2(0, GRAVITY));

        for (Ball ball : balls) {
            this.world.addBody(ball.getBody());
        }

        startSimulation(); // avvia la simulazione quando l'oggetto logicGame è creato
        mouseHandler(); // richiama eventi del mouse
    }


    public void mouseHandler() {
        for (Ball ball : balls) {
            ball.getCircle().setOnMouseDragged(this::onMouseDragged);
            ball.getCircle().setOnMouseReleased(this::onMouseReleased);
        }
    }

    public void startSimulation() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                world.update(TIME_STEP);
                for (Ball ball : balls) {
                    updateBallPosition(ball);
                    checkCollisionWithTable(ball);
                    checkCollisionWithAnchorPane(ball);
                }
            }
        }.start();
    }

    private void checkCollisionWithTable(Ball ball) {
        Body body = ball.getBody();
        if (body.getTransform().getTranslationY() + ball.getRadius() >= table.getY() &&
                body.getTransform().getTranslationX() <= table.getWidth() + ball.getRadius()) {
            body.getTransform().setTranslation(body.getTransform().getTranslationX(), table.getY() - ball.getRadius());
            body.setLinearVelocity(body.getLinearVelocity().multiply(-DAMPING));
        }
    }

    private void updateBallPosition(Ball ball) {
        Body body = ball.getBody();
        ball.setCenter(new Point2D(body.getTransform().getTranslationX(), body.getTransform().getTranslationY()));
    }

    private void checkCollisionWithAnchorPane(Ball ball) {
        Body body = ball.getBody();
        double radius = ball.getRadius();
        double x = body.getTransform().getTranslationX();
        double y = body.getTransform().getTranslationY();
        if (x - radius < 0) {
            body.getTransform().setTranslation(radius, y);
            body.setLinearVelocity(body.getLinearVelocity().multiply(-DAMPING));
        } else if (x + radius > anchorPane.getWidth()) {
            body.getTransform().setTranslation(anchorPane.getWidth() - radius, y);
            body.setLinearVelocity(body.getLinearVelocity().multiply(-DAMPING));
        }

        if (y - radius < 0) {
            body.getTransform().setTranslation(x, radius);
            body.setLinearVelocity(body.getLinearVelocity().multiply(-DAMPING));
        } else if (y + radius > anchorPane.getHeight()) {
            body.getTransform().setTranslation(x, anchorPane.getHeight() - radius);
            body.setLinearVelocity(body.getLinearVelocity().multiply(-DAMPING));
        }
    }
    /**
     * MOUSE EVENT
     */
/*
    private Ball findBallByCircle(Circle circle) { // metodo più preciso per dragg
        /* lo stream serve per fare operazioni fi filtraggio su elementi della lista
         Questo filtro si applica a ciascuna pallina (ball) nella lista. ball.getCircle().equals(circle)
          confronta il cerchio della pallina corrente con il cerchio specificato come argomento (circle).
          dà true se i cerchi sono uguali, altrimenti false.
          .findFirst(): Una volta applicato il filtro, questo restituisce il primo elemento che soddisfa
          il criterio del filtro. In questo caso, restituirà la prima pallina il cui cerchio corrisponde al cerchio specificato.
     */
/*
        return balls.stream().filter(ball -> ball.getCircle().equals(circle)).findFirst().orElse(null);
    }

    @FXML
    private void onMouseDragged(MouseEvent event) {
        Ball draggedBall = findBallByCircle((Circle) event.getSource());

        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        // Imposta le nuove coordinate della palla
        double newCenterX = mouseX;
        double newCenterY = mouseY;


        // Controlla se la ball raggiunge i bordi dell anchorPane
        if (newCenterX - draggedBall.getCircle().getRadius() < 0) { //sx
            newCenterX = draggedBall.getCircle().getRadius();
        } else if (newCenterX + draggedBall.getCircle().getRadius() > anchorPane.getWidth()) { //dx
            newCenterX = anchorPane.getWidth() - draggedBall.getCircle().getRadius();
        }


        if (newCenterY + draggedBall.getCircle().getRadius() < 0) { //basso
            newCenterY = draggedBall.getCircle().getRadius();
        } else if (newCenterY + draggedBall.getCircle().getRadius() > anchorPane.getHeight()) {//alto
            newCenterY = anchorPane.getHeight() - draggedBall.getCircle().getRadius();
        }

        // Controllo del bordo superiore del tavolo
       if (newCenterY + draggedBall.getCircle().getRadius() >= anchorPane.getHeight() - table.getHeight()
                && newCenterX <= table.getWidth() + draggedBall.getCircle().getRadius()) {
            newCenterY = anchorPane.getHeight() - table.getHeight() - draggedBall.getCircle().getRadius();
        }

        // Imposta la nuova posizione della palla
        draggedBall.getCircle().setCenterX(newCenterX);
        draggedBall.getCircle().setCenterY(newCenterY);
        draggedBall.getBody().getTransform().setTranslation(newCenterX, newCenterY);


    }

    @FXML
    private void onMouseReleased(MouseEvent event) {
        Ball releasedBall = findBallByCircle((Circle) event.getSource());
        if (releasedBall != null) {
            if (releasedBall.getCircle().getCenterX() + releasedBall.getRadius() <= table.getWidth()) {
                releasedBall.fallAnimation(table.getY()); // se la palla e nella zona del tavolo si deve fermare prima
            } else {
                releasedBall.fallAnimation(anchorPane.getHeight()); // Avvia l'animazione di caduta
            }

        }


    }
}

*/