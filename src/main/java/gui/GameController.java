package gui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.example.Ball;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;


public class GameController {

    private Button addButton;
    private Ball newBall;
    private List<Ball> balls;

    private Button deleteButton;
    private Group root;

    /**
     * initialize the action of add button
     */
    @FXML
    private void initializeAddButtons() {
        addButton.setOnAction(event -> {
            Point center = new Point(70, 551);
            newBall = new Ball(20, Color.blue, center);
            balls.add(newBall);
            root.getChildren().add(newBall.getCircle());
            System.out.println("New ball created: " + newBall);
        });
    }

    private void ballController() {
        root = new Group();
        Scene scene = new Scene(root, 800, 600);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("box.fxml"));
        try {
            AnchorPane boxPane = loader.load();
            root.getChildren().add(boxPane);
        } catch (IOException e) {
            e.printStackTrace();

        }
        Button addButton = (Button) root.lookup("#addButton");
        addButton.setOnAction(event -> initializeAddButtons());

        scene.setOnMousePressed(this::onMousePressed);
        scene.setOnMouseDragged(this::onMouseDragged);
        scene.setOnMouseReleased(this::onMouseReleased);
    }

    private void onMouseReleased(javafx.scene.input.MouseEvent event) {
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
                    // compute the time frome the last update in milliseconds
                    double elapsedTime = (now - lastUpdate) / 1e9;

                    // compute the new position of the ball
                    double newY = newBall.getCircle().getCenterY() + fallSpeed * elapsedTime;

                    //check if the ball reach the ned of the scene
                    if (newY >= root.getScene().getHeight() - newBall.getRadius()) {
                        newY = root.getScene().getHeight() - newBall.getRadius();
                        stop(); //stop the fall animation
                    }
                    newBall.getCircle().setCenterY(newY);
                    lastUpdate=now;
                }
            }.start();
        }
    }

    ;

    private void onMouseDragged(javafx.scene.input.MouseEvent event) {
        if (newBall != null) {
            newBall.getCircle().setCenterX(event.getSceneX());
            newBall.getCircle().setCenterY(event.getSceneY());
        }
    }

    private void onMousePressed(javafx.scene.input.MouseEvent event) {
        for (Ball ball : balls) {
            if (ball.getCircle().contains(event.getX(), event.getY())) {
                newBall = ball;
                return;
            }
        }
        newBall = null;
    }

    private void onMouseReleased(MouseEvent event) {
        if (newBall != null) {
            //set fall speed and direction
            double fallSpeed = 5;
            int direction = 1; // 1 for the bottom fall
            newBall.getCircle().setCenterY(newBall.getCircle().getCenterY() + fallSpeed * direction);
        }
    }

    public Scene getScene() {
        return root.getScene();
    }
}
