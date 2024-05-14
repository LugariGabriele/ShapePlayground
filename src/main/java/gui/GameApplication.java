package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {


    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("box.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Address Application");
        stage.setScene(scene);
        stage.show();
    }
}

