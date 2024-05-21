package game.logic;

import game.gui.GameController;
import game.tool.Ball;
import game.tool.Box;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.List;

public class LogicGame {

    private static AnchorPane anchorPane;
    //  Point upperLeft = new Point(0, 370);
    // Point bottomRight = new Point(450, 0);
    @FXML
    private javafx.scene.shape.Rectangle table;
    private List<Ball> balls;


    public LogicGame(List<Ball> balls, AnchorPane anchorPane) {
        this.balls = balls;
        LogicGame.anchorPane = anchorPane;
        this.table=new Rectangle(0,470,150,30) ;

        startSimulation();// avvia la simulazione quando l'oggetto logicGame è creato
        mouseHandler(); // richiama eventi del mouse (gli ho spostati perchè ho cambiato constructor

    }

    public void mouseHandler() {
        for (Ball ball : balls) {
            ball.getCircle().setOnMouseDragged(this::onMouseDragged);
            ball.getCircle().setOnMouseReleased(this::onMouseReleased);
        }
    }

    public void startSimulation() {
        new AnimationTimer() { // questo metodo viene chiamatoad ogni frame consentendo l'aggiornamento dell'animazione
            @Override
            public void handle(long now) {//metodo dell'interfaccia animationtimer che viene chiamato ad ogni frame di aminazione
                //e dentro ci si mette cosa voglio che venga eseguito continuamente dentro l'animazione
                //nowè di base in nanosecondi
                updatePositions();
                checkCollisionBalls();
            }
        }.start();
    }

    private void updatePositions() {
        for (Ball ball : balls) {
            Point velocity = ball.getVelocity();
            Circle circle = ball.getCircle();
            double newX = circle.getCenterX() + velocity.x;
            double newY = circle.getCenterY() + velocity.y;

            if (newX + circle.getRadius() > anchorPane.getWidth()) {
                velocity.x = 0;
            }
            if (newY + circle.getRadius() > anchorPane.getHeight()) {
                velocity.y = 0;
            }

            circle.setCenterX(newX);
            circle.setCenterY(newY);
        }
    }

    public void checkCollisionBalls() { // cosi riesco a controllare più di due palle
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ball1 = balls.get(i); // inizializzo variabile in base a indice
                Ball ball2 = balls.get(j);
                if (isColliding(ball1, ball2)) {
                    handleCollision(ball1, ball2);// chiamo metodo gestisce collisioni
                }
            }
        }
    }

    private boolean isColliding(Ball ball1, Ball ball2) { //era nel collisionManager che avevo fatto io
        double radiusSum = ball1.getRadius() + ball2.getRadius();
        double distance = Math.sqrt(Math.pow(ball1.getCircle().getCenterX() - ball2.getCircle().getCenterX(), 2) +
                Math.pow(ball1.getCircle().getCenterY() - ball2.getCircle().getCenterY(), 2)); // radice di x^2+y^2
        return distance <= radiusSum;
        // se la distanza tra i due centri è minore della somma dei due raggi allora si compenetrano o toccano se =
    }

    /**
     * Graficamente, ciò che accade è che la velocità di ciascuna pallina viene scomposta in due componenti:
     * una lungo la linea che collega i centri delle due palline (normale) e una perpendicolare ad essa (tangente).
     * Nell'urto, tra le due sfere si scambiano le componenti lungo la direzione normale (poiché hanno la stessa massa),
     * mentre le componenti tangenti rimangono invariate.
     * Ciò si traduce in un cambio di direzione e velocità per ciascuna palla secondo le leggi dell'urto elastico.
     */
    private void handleCollision(Ball ball1, Ball ball2) {// gestisce collisioni
        Point vel1 = ball1.getVelocity();
        Point vel2 = ball2.getVelocity();

        Point center1 = ball1.getCenter();
        Point center2 = ball2.getCenter();

        //calcolo la distanza tra ball
        double dx = center2.getX() - center2.getX();
        double dy = center2.getY() - center1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        //creo il vettore normale dei componenti che punta dal centro di una a quello dell'altra
        double nx = dx / distance;
        double ny = dy / distance;

        //vettore tangente al vettore normale
        double tx = -ny;
        double ty = -nx;

        //proietto velocità sui due vettori
        double dpTan1 = vel1.x * tx + vel1.y * ty;
        double dpTan2 = vel2.x * tx + vel2.y * ty;

        double dpNorm1 = vel1.x * nx + vel1.y * ny;
        double dpNorm2 = vel2.x * nx + vel2.y * ny;

        //scambio i componenti del vettore velocità normale assumedo che abbiano massa uguale dato che ball sono grandi uguali
        double m1 = dpNorm2;
        double m2 = dpNorm1;

        //aggiorno la velocità delle palle combinando il vettore tangente e i nuovi componenti nel normale
        ball1.setVelocity(new Point((int) (tx * dpTan1 + nx * m1), (int) (ty * dpTan1 + ny * m1)));
        ball2.setVelocity(new Point((int) (tx * dpTan2 + nx * m2), (int) (ty * dpTan2 + ny * m2)));

    }


    /**
     * MOUSE EVENT
     */

    private Ball findBallByCircle(Circle circle) { // metodo più preciso per dragg
        /* lo stream serve per fare operazioni fi filtraggio su elementi della lista
         Questo filtro si applica a ciascuna pallina (ball) nella lista. ball.getCircle().equals(circle)
          confronta il cerchio della pallina corrente con il cerchio specificato come argomento (circle).
          dà true se i cerchi sono uguali, altrimenti false.
          .findFirst(): Una volta applicato il filtro, questo restituisce il primo elemento che soddisfa
          il criterio del filtro. In questo caso, restituirà la prima pallina il cui cerchio corrisponde al cerchio specificato.
     */
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


        if (newCenterY - draggedBall.getCircle().getRadius() < 0) { //basso
            newCenterY = draggedBall.getCircle().getRadius();
        } else if (newCenterY + draggedBall.getCircle().getRadius() > anchorPane.getHeight()) {//alto
            newCenterY = anchorPane.getHeight() - draggedBall.getCircle().getRadius();
        }

        // Imposta la nuova posizione della palla
        draggedBall.getCircle().setCenterX(newCenterX);
        draggedBall.getCircle().setCenterY(newCenterY);
        checkCollisionBalls();

    }

    @FXML
    private void onMouseReleased(MouseEvent event) {
        Ball releasedBall = findBallByCircle((Circle) event.getSource());
        if (releasedBall != null) {
            releasedBall.fallAnimation(anchorPane.getHeight()); // Avvia l'animazione di caduta
        }

    }


}
