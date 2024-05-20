package game.gui;

import game.logic.LogicGame;
import game.tool.Ball;
import game.tool.Box;
import javafx.fxml.FXML;
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
    public Box table;
    @FXML
    public List<Ball> balls = new ArrayList<>();
    @FXML
    boolean deleteButtonOn = false;
    @FXML
    private List<LogicGame> logic = new ArrayList<>();

    public void initialize(){
        /*
        In a few words: The constructor is called first, then any @FXML annotated fields are populated, then initialize() is called.

        This means the constructor does not have access to @FXML fields referring to components defined in the .fxml file, while initialize() does have access to them. STACK OVERFLOW
        */

        initializeAddButton();
        initializeDeleteButton();
       // initializeTable();
    }
    /*@FXML
    public void initializeTable() {
        // Create a Box instance and add it to the AnchorPane (example positioning)
        Point upperLeft = new Point(0, 370);
        Point bottomRight = new Point(450, 0);
        table = new Box(150, 30, upperLeft, bottomRight);

        //Initialize LogicGame with the box
        if (newBall != null) {
            LogicGame logicTable = new LogicGame(newBall, anchorPane, table);
            logic.add(logicTable);
        }
    }
*/
    @FXML
    private Color getRandomColorExceptRed() {
        Random random = new Random();
        float red = 0, green = 0, blue = 0;
        do { //gives a random value between 0.0 and 1.0 of colors components
            red = random.nextFloat();
            green = random.nextFloat();
            blue = random.nextFloat();
        } while (red > 0.7); //(ciclo whilesi ripete fino a che condizione nella parentesi non è falsa)
        // fa in modo che se red> 0.7 il ciclo si ripete la parte del Do (colore rosso è superiore a 0.7)
        return Color.color(red, green, blue);
    }

    @FXML
    private void initializeAddButton() {

        addButton.setOnMouseClicked(event -> {
            if (deleteButtonOn == false) { // controllo che il delete button non sia attivo
                Point spawnPoint = new Point(70, 400);
                newBall = new Ball(20, getRandomColorExceptRed(), spawnPoint);
                balls.add(newBall);
                newBall.getCircle().setOnMouseClicked(this::MouseClicked);
                System.out.println("ball:"+balls);
                if (newBall != null) {
                    LogicGame logicAdd = new LogicGame(newBall, anchorPane);
                    logic.add(logicAdd);
                    logicAdd.fallAnimation();
                    anchorPane.getChildren().addAll(newBall.getCircle());
                }
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
            Paint originalColor = ball.getCircle().getFill(); //store original color
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

    private void MouseClicked(MouseEvent event) {
        for (Ball ball : balls) {
            System.out.println("ball presa"+ball);
            if (ball.getCircle().contains(event.getX(), event.getY())) {
                newBall = ball;
                System.out.println(""+ball);
                return;
            }
        }
        newBall = null;
    }
}