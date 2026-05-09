package org.openkawu.jfxium;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JFXiumApp extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("JFXium Framework");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("JFXium");
        stage.setScene(scene);
        stage.show();
    }
}
