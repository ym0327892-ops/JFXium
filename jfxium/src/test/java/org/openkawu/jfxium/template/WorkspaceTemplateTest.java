package org.openkawu.jfxium.template;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.component.composite.HBarAnt;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("WorkspaceTemplate")
class WorkspaceTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("结构 / 尺寸兜底 / 折叠控制")
    void structure_and_control() {
        WorkspaceTemplate.Result result = WorkspaceTemplate.create()
                .brand("Workbench", "高可用模板")
                .headerCenter(new Label("首页 / 工作台"))
                .headerRight(new Label("刷新"))
                .sider(new VBox(), Double.NaN)
                .content(new VBox())
                .footer(new Label("Ready"))
                .collapsible(true)
                .collapsedWidth(Double.NaN)
                .headerGap(Double.NaN)
                .buildResult();

        BorderPane root = result.getRoot();
        assertNotNull(root);
        assertTrue(root.getStyleClass().contains(JfxStyles.WORKSPACE_TEMPLATE));

        HBarAnt header = (HBarAnt) root.getTop();
        assertEquals(12.0, header.getSpacing(), 0.001);

        HBox center = (HBox) root.getCenter();
        Region sider = (Region) center.getChildren().get(0);
        assertEquals(240.0, sider.getPrefWidth(), 0.001);

        result.toggle();
        assertTrue(result.isCollapsed());
        assertEquals(64.0, sider.getPrefWidth(), 0.001);
    }
}
