package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MenuAnt")
class MenuAntTest extends JfxTestBase {

    @Test
    @DisplayName("null text / title 构建安全")
    void nullText_safe() {
        Pane menu = MenuAnt.create()
                .item((String) null, (String) null, null)
                .subMenu((String) null, (String) null)
                    .item((String) null, (String) null, null)
                    .endSubMenu()
                .group((String) null)
                .divider()
                .build();

        assertNotNull(menu);
        assertTrue(menu.getStyleClass().contains(JfxStyles.MENU));

        for (Label label : collectLabels(menu)) {
            assertNotNull(label.getText());
            assertFalse("null".equals(label.getText()));
        }
    }

    @Test
    @DisplayName("运行时折叠可切换")
    void runtimeCollapse_toggle() {
        MenuAnt.Builder builder = MenuAnt.create()
                .mode(MenuAnt.Mode.INLINE)
                .item("home", "首页", () -> {})
                .subMenu("sub", "导航")
                    .item("item1", "选项 1", () -> {})
                    .endSubMenu()
                .selectedKey("home");

        Pane menu = builder.build();
        MenuAnt.Controller controller = builder.controller();

        assertFalse(controller.isCollapsed());
        assertFalse(menu.getStyleClass().contains(JfxStyles.MENU_COLLAPSED));

        controller.setCollapsed(true);
        assertTrue(controller.isCollapsed());
        assertTrue(menu.getStyleClass().contains(JfxStyles.MENU_COLLAPSED));

        controller.toggleCollapsed();
        assertFalse(controller.isCollapsed());
        assertFalse(menu.getStyleClass().contains(JfxStyles.MENU_COLLAPSED));
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
