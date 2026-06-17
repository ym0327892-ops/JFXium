package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CascaderAnt")
class CascaderAntTest extends JfxTestBase {

    @Test
    @DisplayName("Option null value/label 构建安全")
    void option_nullSafe() {
        CascaderAnt.Option option = new CascaderAnt.Option(null, null, null);

        assertEquals("", option.getValue());
        assertEquals("", option.getLabel());
        assertNotNull(option.getChildren());
    }

    @Test
    @DisplayName("null path / null option 构建安全")
    void build_nullValues_safe() {
        HBox cascader = CascaderAnt.create()
                .options(List.of(
                        new CascaderAnt.Option(null, null, List.of(
                                new CascaderAnt.Option(null, null)
                        ))
                ))
                .value(new ArrayList<>(Arrays.asList(null, "")))
                .placeholder(null)
                .showSearch(true)
                .build();

        assertNotNull(cascader);
        assertTrue(cascader.getStyleClass().contains(JfxStyles.CASCADER));
        TextField field = findTextField(cascader);
        assertNotNull(field);

        List<Label> labels = collectLabels(cascader);
        for (Label label : labels) {
            assertNotNull(label.getText());
        }
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

    private static List<Label> collectLabels(Node node) {
        List<Label> labels = new ArrayList<>();
        collectLabels(node, labels);
        return labels;
    }

    private static void collectLabels(Node node, List<Label> labels) {
        if (node instanceof Label label) {
            labels.add(label);
        }
        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                collectLabels(child, labels);
            }
        }
    }
}
