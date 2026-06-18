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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ProjectReleaseTemplate")
class ProjectReleaseTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("发布节奏板构建与动作回调安全")
    void build_and_actionForwarding() {
        List<String> routes = new ArrayList<>();

        VBox root = ProjectReleaseTemplate.create()
                .title(null)
                .description(null)
                .status("稳定", TagAnt.Type.SUCCESS)
                .meta("版本", "1.0-SNAPSHOT")
                .meta("分支", "main")
                .release("1.0-SNAPSHOT", "当前演示版本", TimelineAnt.DotColor.GREEN)
                .release("1.0.1", "引入项目概览板与快捷入口", TimelineAnt.DotColor.BLUE)
                .change("workspace-template", "工作台模板", "继续收口 header / sider / footer 的工程壳层", "查看")
                .change("release-template", "发布节奏模板", "展示版本、变更和发布准备度", "查看")
                .readiness("当前发布准备度", 0.78, ProgressAnt.Status.SUCCESS, "核心展示链路已完整")
                .onAction(routes::add)
                .build();

        assertNotNull(root);
        assertEquals(4, root.getChildren().size());

        Button action = collectButtons(root).stream()
                .filter(button -> "查看".equals(button.getText()))
                .findFirst()
                .orElseThrow();

        action.fire();

        assertEquals(1, routes.size());
        assertEquals("workspace-template", routes.getFirst());
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
