package game.logic;

import game.tool.Ball;
import game.tool.Box;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import java.awt.*;
import java.util.List;

public class LogicGame {
    private static Ball ball;
    private static AnchorPane anchorPane;
    @FXML
    private Box box;
    private List<Ball> balls;
    Point upperLeft = new Point(0, 370);
    Point bottomRight = new Point(450, 0);
    public LogicGame(Ball ball, AnchorPane anchorPane) {
        this.ball = ball;
        this.anchorPane = anchorPane;
       // this.box = new Box(box.getWidth(),box.getHeight(),box.getUpperLeft(),box.getBottomRight());

        ball.getCircle().setOnMouseDragged(this::onMouseDragged);
        ball.getCircle().setOnMouseReleased(this::onMouseReleased);
    }


    private void onMouseDragged(MouseEvent event) {
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        // Imposta le nuove coordinate della palla
        double newCenterX = mouseX;
        double newCenterY = mouseY;

        // Controlla se la ball raggiunge i bordi dell anchorPane
        if (newCenterX - ball.getCircle().getRadius() < 0) { //sx
            newCenterX = ball.getCircle().getRadius();
        } else if (newCenterX + ball.getCircle().getRadius() > anchorPane.getWidth()) { //dx
            newCenterX = anchorPane.getWidth() - ball.getCircle().getRadius();
        }


        if (newCenterY - ball.getCircle().getRadius() < 0) { //basso
            newCenterY = ball.getCircle().getRadius();
        } else if (newCenterY + ball.getCircle().getRadius() > anchorPane.getHeight()) {//alto
            newCenterY = anchorPane.getHeight() - ball.getCircle().getRadius();
        }

        // Imposta la nuova posizione della palla
        ball.getCircle().setCenterX(newCenterX);
        ball.getCircle().setCenterY(newCenterY);

    }

    private void onMouseReleased(MouseEvent event) {
        if (ball != null) {
            fallAnimation();
        }
    }

    public void fallAnimation() {
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
                double elapsedTime = (now - lastUpdate) / 1.5e7;

                // compute the new position of the ball
                double newY = ball.getCircle().getCenterY() + fallSpeed * elapsedTime;

                // check if the ball reaches the bottom of the scene
                if (newY >= anchorPane.getHeight() - ball.getCircle().getRadius()) {
                    newY = anchorPane.getHeight() - ball.getCircle().getRadius();
                    stop(); // stop the fall animation
                }
                ball.getCircle().setCenterY(newY);

                lastUpdate = now;
            }
        }.start();
    }


}
