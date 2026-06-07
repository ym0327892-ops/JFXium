package org.openkawu.jfxium.demo.border;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.composite.BarAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.Theme;
import org.openkawu.jfxium.core.theme.ThemeManager;

/**
 * Demonstrates how padding interacts with border in JavaFX.
 * border renders at the outer edge of padding — so padding
 * pushes the border line outward, increasing layout bounds.
 */
public class BorderPaddingDemo extends Application {

    @Override
    public void start(Stage stage) {

        VsCodeEditor vsCodeEditor = new VsCodeEditor();

        Scene scene = new Scene(vsCodeEditor, 600, 700);
        ThemeManager instance = ThemeManager.getInstance();
        instance.registerScene(scene);
        instance.applyTheme(new LightTheme());
        stage.setTitle("Border + Padding Interaction Demo");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }

}

