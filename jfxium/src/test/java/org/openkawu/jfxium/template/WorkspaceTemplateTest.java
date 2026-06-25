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
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt;
import org.openkawu.jfxium.component.overlay.DropdownAnt.DropdownResult;
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

    @Test
    @DisplayName("headerTop 可承载双层头部")
    void headerTop_supportsTopBar() {
        WorkspaceTemplate.Result result = WorkspaceTemplate.create()
                .brand("Workbench", "高可用模板")
                .headerTop(new Label("Menu"), new Label("Tools"))
                .headerCenter(new Label("首页 / 工作台"))
                .sider(new VBox(), 240)
                .content(new VBox())
                .buildResult();

        BorderPane root = result.getRoot();
        assertNotNull(root.getTop());
        assertTrue(root.getTop() instanceof VBox, "带 headerTop 时 top 应包装成 VBox");
    }

    @Test
    @DisplayName("用户菜单 helper 复用头像下拉")
    void userMenu_buildsDropdownTrigger() {
        DropdownResult result = WorkspaceTemplate.userMenu(null, key -> {});

        assertNotNull(result);
        assertTrue(result.getTrigger() instanceof HBox);
    }

    @Test
    @DisplayName("brandIcon 可与品牌区组合")
    void brandIcon_supportsBrandIcon() {
        WorkspaceTemplate.Result result = WorkspaceTemplate.create()
                .brand("Workbench", "高可用模板")
                .brandIcon(IconAnt.Path.DASHBOARD)
                .sider(new VBox(), 240)
                .content(new VBox())
                .buildResult();

        assertNotNull(result.getRoot().getTop());
        assertTrue(result.getRoot().getTop() instanceof HBarAnt);
    }
}
