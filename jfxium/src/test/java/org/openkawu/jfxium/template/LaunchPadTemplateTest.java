package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LaunchPadTemplate")
class LaunchPadTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("构建布局 / 回调转发安全")
    void build_and_actionForwarding() {
        List<String> routes = new ArrayList<>();

        VBox root = LaunchPadTemplate.create()
                .title(null)
                .description(null)
                .columns(0)
                .gap(Double.NaN)
                .onAction(routes::add)
                .item("workspace", "工作台模板", "查看完整工程壳层", null, "跳转")
                .build();

        assertNotNull(root);
        assertEquals(2, root.getChildren().size());

        Button action = collectButtons(root).stream()
                .filter(button -> "跳转".equals(button.getText()))
                .findFirst()
                .orElseThrow();

        action.fire();

        assertEquals(1, routes.size());
        assertEquals("workspace", routes.getFirst());
        assertTrue(root.getChildren().get(1) instanceof Pane);
    }

    private static List<Button> collectButtons(Node node) {
        List<Button> buttons = new ArrayList<>();
        collectButtons(node, buttons);
        return buttons;
    }

    private static void collectButtons(Node node, List<Button> buttons) {
        if (node instanceof Button button) {
            buttons.add(button);
        }
        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                collectButtons(child, buttons);
            }
        }
    }
}
