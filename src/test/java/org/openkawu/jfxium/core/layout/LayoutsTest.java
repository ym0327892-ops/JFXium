package org.openkawu.jfxium.core.layout;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Layouts DSL 测试
 */
public class LayoutsTest extends org.openkawu.jfxium.JavaFXTestBase {

    @Test
    public void testVBoxBuilder() {
        VBox vbox = Layouts.vbox()
            .spacing(16)
            .padding(20)
            .children(
                new Label("Test"),
                new Button("Click")
            )
            .build();

        assertNotNull(vbox);
        assertEquals(16, vbox.getSpacing(), 0.01);
        assertEquals(2, vbox.getChildren().size());
    }

    @Test
    public void testHBoxBuilder() {
        HBox hbox = Layouts.hbox()
            .spacing(12)
            .children(
                new Label("Left"),
                new Button("Right")
            )
            .build();

        assertNotNull(hbox);
        assertEquals(12, hbox.getSpacing(), 0.01);
        assertEquals(2, hbox.getChildren().size());
    }

    @Test
    public void testGridBuilder() {
        GridPane grid = Layouts.grid()
            .cols(2)
            .gap(8)
            .children(
                new Button("1"),
                new Button("2"),
                new Button("3"),
                new Button("4")
            )
            .build();

        assertNotNull(grid);
        assertEquals(8, grid.getHgap(), 0.01);
        assertEquals(4, grid.getChildren().size());
    }

    @Test
    public void testGrow() {
        javafx.scene.layout.Region grow = Layouts.grow();
        assertNotNull(grow);
    }

    @Test
    public void testSpacer() {
        javafx.scene.layout.Region spacer = Layouts.spacer(20, 10);
        assertNotNull(spacer);
        assertEquals(20, spacer.getPrefWidth(), 0.01);
        assertEquals(10, spacer.getPrefHeight(), 0.01);
    }
}