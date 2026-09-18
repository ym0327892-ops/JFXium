package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TransferAnt")
class TransferAntTest extends JfxTestBase {

    @Test
    @DisplayName("null dataSource / targetKeys / render 构建安全")
    void nullInputs_safe() {
        HBox transfer = TransferAnt.<String>create()
                .dataSource(null)
                .targetKeys(null)
                .titles(null, null)
                .render(null)
                .showSearch(true)
                .build();

        assertNotNull(transfer);
        assertTrue(transfer.getStyleClass().contains(JfxStyles.TRANSFER));

        List<Label> labels = collectLabels(transfer);
        assertTrue(labels.stream().anyMatch(label -> Messages.get("transfer.source").equals(label.getText())));
        assertTrue(labels.stream().anyMatch(label -> Messages.get("transfer.target").equals(label.getText())));
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
