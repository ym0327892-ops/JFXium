package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AutoCompleteAnt 边界")
class AutoCompleteAntEdgeTest extends JfxTestBase {

    @Test
    @DisplayName("null optionToString / null option / null filter 返回安全")
    void nullValues_safe() {
        HBox autoComplete = AutoCompleteAnt.<String>create()
                .options(new ArrayList<>(Arrays.asList("A", null, "B")))
                .optionToString(v -> null)
                .filter(q -> null)
                .placeholder(null)
                .build();

        assertNotNull(autoComplete);
        assertTrue(autoComplete.getStyleClass().contains(JfxStyles.AUTO_COMPLETE));

        TextField field = findTextField(autoComplete);
        assertNotNull(field);
        assertDoesNotThrow(() -> runOnFxThreadAndWait(() -> field.setText("a")));
    }

    private static TextField findTextField(Node node) {
        if (node instanceof TextField textField) {
            return textField;
        }
        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                TextField found = findTextField(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
