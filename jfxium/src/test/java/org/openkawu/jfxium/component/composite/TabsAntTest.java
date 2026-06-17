package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TabsAnt")
class TabsAntTest extends JfxTestBase {

    @Test
    @DisplayName("null label / null content 构建安全")
    void nullValues_safe() {
        VBox root = (VBox) TabsAnt.create()
                .tab("key", null, null)
                .build();

        assertNotNull(root);
        assertTrue(root.getStyleClass().contains(JfxStyles.TABS_ROOT));
        assertEquals(2, root.getChildren().size());

        VBox wrapper = (VBox) root.getChildren().get(0);
        HBox tabBar = (HBox) wrapper.getChildren().get(0);
        Label tabLabel = (Label) tabBar.getChildren().get(0);
        assertEquals("", tabLabel.getText());

        StackPane contentArea = (StackPane) root.getChildren().get(1);
        assertEquals(1, contentArea.getChildren().size());
        Node content = contentArea.getChildren().get(0);
        assertTrue(content instanceof Region);
    }
}
