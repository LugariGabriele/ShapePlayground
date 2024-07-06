package game.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class GameApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Simulation");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Playground.fxml")));
        root.setStyle("-fx-background-color: #C6A664;");
        Scene scene = new Scene(root);
        stage.setTitle("Playground");
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.jpg"))));
        stage.setScene(scene);
        stage.show();

    }
}
