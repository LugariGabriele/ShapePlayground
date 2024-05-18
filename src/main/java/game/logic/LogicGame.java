package game.logic;

import game.tool.Ball;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class LogicGame {
    private static Ball ball;
    private static AnchorPane anchorPane;
    private List<Ball> balls;

    public LogicGame(Ball ball, AnchorPane anchorPane) {
        this.ball = ball;
        this.anchorPane = anchorPane;

        ball.getCircle().setOnMouseDragged(this::onMouseDragged);
        ball.getCircle().setOnMouseReleased(this::onMouseReleased);
    }


    private void onMouseDragged(MouseEvent event) {
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        // Imposta le nuove coordinate della palla
        double newCenterX = mouseX;
        double newCenterY = mouseY;

        // Controlla se la palla raggiunge il bordo sinistro o destro dell'AnchorPane
        if (newCenterX - ball.getCircle().getRadius() < 0) {
            newCenterX = ball.getCircle().getRadius();
        } else if (newCenterX + ball.getCircle().getRadius() > anchorPane.getWidth()) {
            newCenterX = anchorPane.getWidth() - ball.getCircle().getRadius();
        }

        // Controlla se la palla raggiunge il bordo superiore o inferiore dell'AnchorPane
        if (newCenterY - ball.getCircle().getRadius() < 0) {
            newCenterY = ball.getCircle().getRadius();
        } else if (newCenterY + ball.getCircle().getRadius() > anchorPane.getHeight()) {
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