package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
            FXMLLoader fx=new FXMLLoader(Main.class.getResource("box.fxml"));
            stage.setScene(new Scene(fx.load()));
            stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}