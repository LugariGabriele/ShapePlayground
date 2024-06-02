package game.gui;


import game.tool.Ball;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import java.util.ArrayList;
import java.util.List;


public class GameController {

    private World world;
    @FXML
    private AnchorPane anchorPane;
    private Ball ball;
    @FXML
    private Button addButton;
    @FXML
    private ToggleButton deleteButton;
    @FXML
    private boolean deleteButtonOn = false;
    private List<Ball> balls = new ArrayList<>();

    @FXML
    public void initialize() {
        world = new World<>(); // creo mondo fisica
        world.setGravity(new Vector2(0, 100)); // 100 è la velocità con cui vanno verso basso(ho messo 9.8 ma va lentissimo)
        AnimationTimer timer = new AnimationTimer() { //uso per aggiornare mondo dopo
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    @FXML
    private void initilizeAddButton() {

        addButton.setOnMouseClicked(event -> {
            if (!deleteButtonOn) {
                ball = new Ball(20, 400, 100);
                world.addBody(ball.getBody());
                balls.add(ball);
                anchorPane.getChildren().add(ball.getGraphicCircle());
            }
        });
    }

    @FXML
    private void initializeDeleteButton() {

        deleteButton.setOnMouseClicked(actionEvent -> {

            if (deleteButton.isSelected()) {
                deleteSelectedBall();
                deleteButtonOn = true;
            } else {
                disableDeleteSelectedBall();
                deleteButtonOn = false;

            }
        });
    }

    @FXML
    private void deleteSelectedBall() {
        System.out.println("Delete Mode ON");
        for (Ball ball : balls) {
            Paint originalColor = ball.getGraphicCircle().getFill(); //immagazzina colore base della palla
            ball.getGraphicCircle().setOnMouseEntered(mouseEvent -> ball.getGraphicCircle().setFill(Color.RED)); // se entra mouse diventa rossa
            ball.getGraphicCircle().setOnMouseExited(mouseEvent -> ball.getGraphicCircle().setFill(originalColor)); // se esce torna colore normale
            ball.getGraphicCircle().setOnMouseClicked(mouseEvent -> {
                world.removeBody(ball.getBody()); // rimuovo dal mondo della simulazione fisica
                anchorPane.getChildren().remove(ball.getGraphicCircle()); // cancella dalla scena
                balls.remove(ball); // rimuove dall' elenco
            });
        }
    }

    @FXML
    private void disableDeleteSelectedBall() {
        System.out.println("Delete Mode OFF");
        for (Ball ball : balls) {
            // disattivo eventi mouse
            ball.getGraphicCircle().setOnMouseEntered(null);
            ball.getGraphicCircle().setOnMouseExited(null);
            ball.getGraphicCircle().setOnMouseClicked(null);
        }
    }

    private void update() {
        //faccio animazione per aggiornare il mondo fisico
        world.update(1.0 / 6.0); //aggiorna il mondo a 6FPS
        for (Ball ball : balls) {
            // posizione della palla nel mondo fisico
            Vector2 position = ball.getBody().getTransform().getTranslation();

            // aggiorno partegrafica
            ball.getGraphicCircle().setCenterX(position.x);
            ball.getGraphicCircle().setCenterY(position.y);

            // controllo bordi anchorpane
            double radius = ball.getGraphicCircle().getRadius();
            double anchorPaneWidth = anchorPane.getWidth();
            double anchorPaneHeight = anchorPane.getHeight();
            /**
             * Math.max(radius, position.x) assicura che la palla non vada a sinistra oltre il bordo sinistro dell'AnchorPane.
             * La funzione Math.min(..., anchorPaneWidth - radius) assicura che la palla non vada a destra oltre il bordo destro
             */
            double newX = Math.min(Math.max(radius, position.x), anchorPaneWidth - radius);
            /**
             * simile a sopra
             */
            double newY = Math.min(Math.max(radius, position.y), anchorPaneHeight - radius);

            if (newX != position.x || newY != position.y) { // controlla se tocca bordo
                ball.getBody().setLinearVelocity(0, 0); // ferma la palla azzerando velocità
                ball.getBody().setAngularVelocity(0); // ferma rotazione
                ball.getGraphicCircle().setCenterX(newX); // setto posizione grafica nuove
                ball.getGraphicCircle().setCenterY(newY);
                ball.getBody().getTransform().setTranslation(new Vector2(newX, newY)); // setto anche nuove per mondo fisico
                continue; // serve perchè quando arrivavano in fondo quando spawnate collidevano ma piano piano dopo
                // compenetravano il pavimento
            }

            // se ball non tocca bordo......
        }
    }
}

