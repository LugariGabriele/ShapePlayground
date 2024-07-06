package game.gui.controller;


import game.tool.Ball;
import game.tool.Container;
import game.tool.Platform;
import game.tool.Triangle;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GameController {

    @FXML
    Label deleteLabel;
    @FXML
    private Label addLabel;
    private World world;
    @FXML
    private AnchorPane anchorPane;
    private Ball ball;
    @FXML
    private Button addButton;
    @FXML
    private Label timerLabel;
    private double timer = 0.8;
    @FXML
    private ToggleButton deleteButton;
    @FXML
    private Button popUpButton;
    private boolean deleteButtonOn = false;
    @FXML
    private Label deleteStatusLabel;
    private Platform bouncingSurface;
    private Triangle leftSlide;
    private Triangle rightSlide;
    private Container container;
    private List<Ball> balls = new ArrayList<>();
    private Boolean canSpawnBall = true;
    private boolean isContainerRotatingRigth = false;
    private boolean isContainerRotatingLeft = false;

    /**
     * create a world with all its properties
     */
    public void initialize() {
        world = new World<>();
        world.setGravity(new Vector2(0, 300));
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
        initializeFloor();
        initializeButtonsBody();
        anchorPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
    }

    /**
     * a method that set the bottom border of the anchorPane as a physical body
     */
    void initializeFloor() {
        double anchorPaneX = anchorPane.getLayoutX();
        double anchorPaneY = anchorPane.getLayoutY();
        double anchorPaneWidth = anchorPane.getPrefWidth();
        double anchorPaneHeight = anchorPane.getPrefHeight();

        Body anchorPaneBorder = new Body();
        anchorPaneBorder.addFixture(Geometry.createRectangle(anchorPaneWidth, 1));
        anchorPaneBorder.setMass(MassType.INFINITE);
        anchorPaneBorder.translate(anchorPaneX, anchorPaneY + anchorPaneHeight);
        world.addBody(anchorPaneBorder);
    }

    /**
     * method to manage the inputs of certain keyboard keys
     *
     * @param event
     */
    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case DOWN:
                toggleBouncingSurface();
                break;
            case UP:
                toggleContainer();
                break;
            case D:
                if (container != null) {
                    isContainerRotatingRigth = true;
                    rotateContainer();
                }
                break;
            case A:
                if (container != null) {
                    isContainerRotatingLeft = true;
                    rotateContainer();
                }
                break;
            case LEFT:
                toggleLeftSlide();
                break;
            case RIGHT:
                toggleRightSlide();
                break;

        }
    }

    public void toggleBouncingSurface() {
        if (bouncingSurface == null) {
            initializeBouncingSurface();
        } else {
            removeBouncingSurface();
        }
    }

    public void toggleLeftSlide() {
        if (leftSlide == null) {
            initilizeLeftSlide();
        } else {
            removeLeftSlide();
        }
    }

    public void toggleRightSlide() {
        if (rightSlide == null) {
            initilizeRigthSlide();
        } else {
            removeRigthSlide();
        }
    }

    public void toggleContainer() {
        if (container == null) {
            initializeContainer();
        } else {
            removeContainer();
        }
    }

    /*
     * create a right triangle on the left side of the anchorPane
     */
    public void initilizeLeftSlide() {
        leftSlide = new Triangle(0, 300, 220, 350, 0, 350);
        leftSlide.getGraphicTriangle().setFill(Color.GOLD);
        leftSlide.getGraphicTriangle().setStroke(Color.BLACK);
        world.addBody(leftSlide.getBody());
        leftSlide.getBody().setMass(MassType.INFINITE);
        anchorPane.getChildren().add(leftSlide.getGraphicTriangle());
    }

    /**
     * create a right triangle on the left side of the anchorPane
     */
    public void initilizeRigthSlide() {
        rightSlide = new Triangle(580, 350, 800, 300, 800, 350);
        rightSlide.getGraphicTriangle().setFill(Color.GOLD);
        rightSlide.getGraphicTriangle().setStroke(Color.BLACK);
        world.addBody(rightSlide.getBody());
        rightSlide.getBody().setMass(MassType.INFINITE);
        anchorPane.getChildren().add(rightSlide.getGraphicTriangle());
    }

    /**
     * create a platform that when a dynamic body touch the upper part of it makes the body bounce
     */
    private void initializeBouncingSurface() {
        bouncingSurface = new Platform(275, 490, 250, 10);
        world.addBody(bouncingSurface.getBody());
        bouncingSurface.getGraphicRectangle().setFill(Color.GREEN);
        bouncingSurface.getGraphicRectangle().setStroke(Color.BLACK);
        bouncingSurface.getBody().setMass(MassType.INFINITE);
        bouncingSurface.getFixture().setFriction(0.2);
        anchorPane.getChildren().add(bouncingSurface.getGraphicRectangle());
    }

    /**
     * create a similar box which dynamic bodies can be stored
     */
    private void initializeContainer() {
        container = new Container(325, 150, 150, 150, 10);
        container.getGraphicLeftWall().setFill(Color.BLUE);
        container.getGraphicRightWall().setFill(Color.BLUE);
        container.getGraphicBottomWall().setFill(Color.BLUE);
        world.addBody(container.getBottomBody());
        world.addBody(container.getRightBody());
        world.addBody(container.getLeftBody());

        anchorPane.getChildren().addAll(container.getContainerGroup());
    }

    /**
     * set the action of the Add button when is clicked
     */
    @FXML
    private void initilizeAddButton() {
        addButton.setOnMouseClicked(event -> {
            if (!deleteButtonOn && canSpawnBall) {
                double spawnX = 400;
                double spawnY = 100;
                ball = new Ball(20, spawnX, spawnY);
                world.addBody(ball.getBody());
                balls.add(ball);
                anchorPane.getChildren().addAll(ball.getGraphicCircle(), ball.getRadiusLine());
                startTimer();
            }
        });
    }

    /**
     * update the FX's timer label
     */
    private void updateTimerLabel() {
        timerLabel.setText(String.format("%.1fs", timer));
    }

    /**
     * create a timer that start a countdown to be able to spawn a new ball
     */
    private void startTimer() {
        canSpawnBall = false;
        timer = 0.8;
        updateTimerLabel();


        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    timer -= 0.1;
                    if (timer < 0.8 && timer > 0.0) {
                        timerLabel.setTextFill(Color.RED);
                        addLabel.setTextFill(Color.RED);
                    }

                    if (timer <= 0.0) {
                        timer = 0;
                    }
                    updateTimerLabel();
                })
        );
        timeline.setCycleCount(8);
        timeline.setOnFinished(event -> {

            canSpawnBall = true;
            timerLabel.setTextFill(Color.DARKGREEN);
            addLabel.setTextFill(Color.DARKGREEN);
        });
        timeline.play();
    }

    /**
     * set the action of the Delete button when is clicked
     */
    @FXML
    private void initializeDeleteButton() {
        deleteButton.setOnMouseClicked(actionEvent -> {

            if (deleteButton.isSelected()) {
                deleteSelectedBall();
                addLabel.setTextFill(Color.RED);
                timerLabel.setTextFill(Color.RED);

                deleteButtonOn = true;
                deleteStatusLabel.setText("ON");
                deleteStatusLabel.setTextFill(Color.RED);

            } else {
                disableDeleteSelectedBall();
                deleteButtonOn = false;
                deleteStatusLabel.setText("OFF");
                deleteStatusLabel.setTextFill(Color.BLACK);
                timerLabel.setTextFill(Color.DARKGREEN);
                addLabel.setTextFill(Color.DARKGREEN);


            }
        });
    }

    /**
     * create a bodies around the buttons to make the dynamic bodies bang on them
     */
    private void initializeButtonsBody() {
        Body buttonBody = new Body();
        Rectangle rectangle = new Rectangle(161, 52);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setDensity(1);
        fixture.setFriction(0.5);
        fixture.setRestitution(1);
        buttonBody.addFixture(fixture);
        buttonBody.setMass(MassType.INFINITE);
        buttonBody.translate(624, 24);

    }

    /**
     * methods for remove the FX figures and the bodies from the scene
     */
    @FXML
    private void removeBouncingSurface() {
        world.removeBody(bouncingSurface.getBody());
        anchorPane.getChildren().remove(bouncingSurface.getGraphicRectangle());
        bouncingSurface = null;
    }

    @FXML
    private void removeLeftSlide() {
        world.removeBody(leftSlide.getBody());
        anchorPane.getChildren().remove(leftSlide.getGraphicTriangle());
        leftSlide = null;
    }

    @FXML
    private void removeRigthSlide() {
        world.removeBody(rightSlide.getBody());
        anchorPane.getChildren().remove(rightSlide.getGraphicTriangle());
        rightSlide = null;
    }

    @FXML
    private void removeContainer() {
        world.removeBody(container.getBottomBody());
        world.removeBody(container.getLeftBody());
        world.removeBody(container.getRightBody());
        anchorPane.getChildren().removeAll(container.getContainerGroup());
        container = null;

    }

    @FXML
    private void deleteSelectedBall() {
        for (Ball ball : balls) {
            Paint originalColor = ball.getGraphicCircle().getFill();
            ball.getGraphicCircle().setOnMouseEntered(mouseEvent -> ball.getGraphicCircle().setFill(Color.RED));
            ball.getGraphicCircle().setOnMouseExited(mouseEvent -> ball.getGraphicCircle().setFill(originalColor));
            ball.getGraphicCircle().setOnMouseClicked(mouseEvent -> {
                world.removeBody(ball.getBody());
                anchorPane.getChildren().removeAll(ball.getGraphicCircle(), ball.getRadiusLine());
                balls.remove(ball);
            });
        }
    }

    @FXML
    private void disableDeleteSelectedBall() {
        for (Ball ball : balls) {
            ball.getGraphicCircle().setOnMouseEntered(null);
            ball.getGraphicCircle().setOnMouseExited(null);
            ball.getGraphicCircle().setOnMouseClicked(null);
        }
    }

    /**
     * update the world and his bodies position
     */
    private void update() {
        world.update(1.0 / 80); //update the world 60 times per second
        for (Ball ball : balls) {
            Vector2 position = ball.getBody().getTransform().getTranslation();

            ball.getGraphicCircle().setCenterX(position.x);
            ball.getGraphicCircle().setCenterY(position.y);

            double radius = ball.getGraphicCircle().getRadius();
            double anchorPaneWidth = anchorPane.getWidth();
            double anchorPaneHeight = anchorPane.getHeight();

            double newX = Math.min(Math.max(radius, position.x), anchorPaneWidth - radius);
            double newY = Math.min(Math.max(radius, position.y), anchorPaneHeight - radius);

            //check if the dynamic bodies are touching the border of the anchorPane
            if (newX != position.x || newY != position.y) {
                ball.getGraphicCircle().setCenterX(newX);
                ball.getGraphicCircle().setCenterY(newY);
                ball.getBody().getTransform().setTranslation(new Vector2(newX, newY));
                ball.updateRadiusLine();
            }



            //set the force given from the bouncingSurface to make the bodies bounce
            if (bouncingSurface != null) {
                handleBounceOnSurface(ball);
            }

            rotateContainer();
            ball.getFixture().setRestitution(0.5);
            ball.updateRadiusLine();
        }

    }

    /**
     * create the impulse given to the ball by the bouncing surface
     */
    private void handleBounceOnSurface(Ball ball) {
        double bouncingSurfaceTop = bouncingSurface.getGraphicRectangle().getY();
        double bouncingSurfaceLeft = bouncingSurface.getGraphicRectangle().getX();
        double elasticSurfaceRight = bouncingSurface.getGraphicRectangle().getX() + bouncingSurface.getGraphicRectangle().getWidth();

        double ballX = ball.getGraphicCircle().getCenterX();

        double ballBottom = ball.getGraphicCircle().getCenterY() + ball.getGraphicCircle().getRadius() + ball.getGraphicCircle().getStrokeWidth();

        if (ballBottom >= bouncingSurfaceTop &&
                ballX >= bouncingSurfaceLeft && ballX <= elasticSurfaceRight) {
            
            Vector2 velocity = ball.getBody().getLinearVelocity();

            Vector2 normal = new Vector2(0, -1); //normal of the horizontal bouncing surface

            double velocityAlongNormal = velocity.dot(normal);

            //set the new velocity of the ball mirroring the direction before bounce
            Vector2 velocityReflectedAlongNormal = normal.multiply(-3 * velocityAlongNormal);

            Vector2 reflectedVelocity = velocity.add(velocityReflectedAlongNormal);

            ball.getBody().setLinearVelocity(reflectedVelocity);

        }
    }

    /**
     * make the container's FX and body rotate through an angle of 45°
     */
    private void rotateContainer() {

        if (isContainerRotatingRigth) {
            container.applyRotation(45, Duration.seconds(4.5));
            isContainerRotatingRigth = false;
        }
        if (isContainerRotatingLeft) {
            container.applyRotation(-45, Duration.seconds(4.5));
            isContainerRotatingLeft = false;

        }
    }

    /**
     * initialize the creation of a new scene when the popUpButton is clicked
     */
    @FXML
    private void openPopup() {
        popUpButton.setOnMouseClicked(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PopUp.fxml"));
            Parent rootPopUp;
            try {
                rootPopUp = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scene popUpScene = new Scene(rootPopUp, 350, 180);
            popUpScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("PopUp.css")).toExternalForm());
            Stage newStage = new Stage();
            newStage.setTitle("Commands info");
            newStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("info_logo.jpg"))));
            newStage.setScene(popUpScene);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(anchorPane.getScene().getWindow());
            newStage.show();
        });
    }
}

