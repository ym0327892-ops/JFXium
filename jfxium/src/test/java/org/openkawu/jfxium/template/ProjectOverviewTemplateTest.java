package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.component.composite.ProgressAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TimelineAnt;
import org.openkawu.jfxium.component.control.IconAnt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ProjectOverviewTemplate")
class ProjectOverviewTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("项目总览板构建与活动回调安全")
    void build_and_actionForwarding() {
        List<String> routes = new ArrayList<>();

        VBox root = ProjectOverviewTemplate.create()
                .title(null)
                .description(null)
                .status("稳定", TagAnt.Type.SUCCESS)
                .meta("版本", "1.0-SNAPSHOT")
                .meta("分支", "main")
                .tech("Java 21")
                .metric("主题", "Light", "当前主题状态", IconAnt.Path.SETTINGS)
                .progress("工程展示完成度", 0.5, ProgressAnt.Status.SUCCESS, "一半已完成")
                .milestone("完成首页改造", "Done", TimelineAnt.DotColor.GREEN)
                .activity("workspace", "查看工作台模板", "header / sider / footer 的完整壳层", "打开")
                .onAction(routes::add)
                .build();

        assertNotNull(root);
        assertEquals(5, root.getChildren().size());

        Button action = collectButtons(root).stream()
                .filter(button -> "打开".equals(button.getText()))
                .findFirst()
                .orElseThrow();

        action.fire();

        assertEquals(1, routes.size());
        assertEquals("workspace", routes.getFirst());
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
