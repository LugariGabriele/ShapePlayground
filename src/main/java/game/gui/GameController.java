package game.gui;

import game.item.Ball;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Button addButton;
    @FXML
    public ToggleButton deleteButton;
    @FXML
    public Ball newBall;
    @FXML
    public List<Ball> balls=new ArrayList<>();
    @FXML
    public boolean deleteButtonMode = true;

    @FXML
    public Group root;


    @FXML
    private Color getRandomColorExceptRed() {
        Random random = new Random();
        float red = 0, green = 0, blue = 0;
        do {
            red = random.nextFloat();
            green = random.nextFloat();
            blue = random.nextFloat();
        } while (red > 0.7); // Ensure red component is less than or equal to 0.7 (not red)
        return Color.color(red, green, blue);
    }
    @FXML
    private void initializeAddButtons() {
        addButton.setOnMouseClicked(event -> {
            Point center = new Point(70, 551);
            newBall = new Ball(20, getRandomColorExceptRed(), center);
            balls.add(newBall);
            anchorPane.getChildren().addAll(newBall.getCircle());
            newBall.getCircle().setOnMouseReleased(this::onMouseReleased);
            newBall.getCircle().setOnMouseDragged(this::onMouseDragged);
            newBall.getCircle().setOnMouseClicked(this::onMouseClicked);


        });


    }

    @FXML
    private void initializeDeleteButton() {
        deleteButton.setOnMouseClicked(actionEvent -> {
            if (deleteButton.isSelected()) {
                deleteSelectedBall();
            } else {
                disableDeleteSelectedBall();
            }
        });
    }

    @FXML
    private void deleteSelectedBall() {
        System.out.println("Delete Mode ON");
        for (Ball ball : balls) {
            Paint originalColor=  ball.getCircle().getFill(); //store original color
            ball.getCircle().setOnMouseEntered(mouseEvent -> ball.getCircle().setFill(Color.RED)); // se entra mouse diventa rossa
            ball.getCircle().setOnMouseExited(mouseEvent -> ball.getCircle().setFill(originalColor)); // se esce torna colore normale
            ball.getCircle().setOnMouseClicked(mouseEvent -> {
                anchorPane.getChildren().remove(ball.getCircle()); // cancella dalla scena
                balls.remove(ball); // rimuove dall' elenco
            });
        }
    }

    @FXML
    private void disableDeleteSelectedBall() {
        System.out.println("Delete Mode OFF");
        for (Ball ball : balls) {
            // Remove event handlers for delete mode
            ball.getCircle().setOnMouseEntered(null);
            ball.getCircle().setOnMouseExited(null);
            ball.getCircle().setOnMouseClicked(null);
        }
    }


    @FXML
    private void onMouseReleased(MouseEvent event) {
        if (newBall != null) {
            double fallSpeed = 5;
            new AnimationTimer() {
                private long lastUpdate = 0;

                @Override
                public void handle(long now) {
                    if (lastUpdate == 0) {
                        lastUpdate = now;
                        return;
                    }
                    // compute the time from the last update
                    double elapsedTime = (now - lastUpdate)/ 1.5e7;

                    // compute the new position of the ball
                    double newY = newBall.getCircle().getCenterY() + fallSpeed * elapsedTime;

                    //check if the ball reach the end of the scene
                    if (newY >= anchorPane.getHeight() - newBall.getRadius()) {
                        newY = anchorPane.getHeight() - newBall.getRadius();
                        stop(); //stop the fall animation
                    }
                    newBall.getCircle().setCenterY(newY);
                    lastUpdate = now;
                }
            }.start();
        }
    }


    @FXML
    private void onMouseDragged(javafx.scene.input.MouseEvent event) {
        if (newBall != null) {
            newBall.getCircle().setCenterX(event.getSceneX());
            newBall.getCircle().setCenterY(event.getSceneY());
        }
    }

    @FXML
    private void onMouseClicked(MouseEvent event) {
        for (Ball ball : balls) {
            if (ball.getCircle().contains(event.getX(), event.getY())) {
                newBall = ball;
                return;
            }
        }
        newBall = null;
    }




}
