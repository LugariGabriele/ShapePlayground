module game{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // Application class' package
    opens game to javafx.fxml;
    exports game;
}