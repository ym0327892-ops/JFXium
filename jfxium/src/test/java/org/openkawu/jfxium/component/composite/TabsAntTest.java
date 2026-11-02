package org.openkawu.jfxium.component.composite;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
        StackPane barHost = (StackPane) wrapper.getChildren().get(0);
        HBox tabBar = (HBox) barHost.getChildren().get(0);
        Label tabLabel = (Label) tabBar.getChildren().get(0);
        assertEquals("", tabLabel.getText());

        StackPane contentArea = (StackPane) root.getChildren().get(1);
        assertEquals(1, contentArea.getChildren().size());
        Node content = contentArea.getChildren().get(0);
        assertTrue(content instanceof Region);
    }

    @Test
    @DisplayName("BUG #144：activeIndex 指定初始激活的 tab")
    void activeIndex_applies() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .tab("c", "C", new Region())
                .activeIndex(2)
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        assertEquals(2, ctrl.getCurrent());
        assertEquals("c", ctrl.getCurrentKey());
    }

    @Test
    @DisplayName("BUG #144：activeKey 优先于 activeIndex")
    void activeKey_priorityOverIndex() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .activeIndex(0)
                .activeKey("b")
                .build();
        assertEquals(1, TabsAnt.controllerOf(root).getCurrent());
    }

    @Test
    @DisplayName("BUG #144：初始下标指向禁用项时自动让位")
    void initialDisabled_skippedToNextEnabled() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region(), true)
                .tab("b", "B", new Region())
                .activeIndex(0)
                .build();
        assertEquals(1, TabsAnt.controllerOf(root).getCurrent());
    }

    @Test
    @DisplayName("BUG #144：activeIndex 越界钳制到 0")
    void activeIndex_outOfRange_clampedToZero() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .activeIndex(99)
                .build();
        assertEquals(0, TabsAnt.controllerOf(root).getCurrent());
    }

    @Test
    @DisplayName("BUG #144：二次 build() 复用同一 content 不抛单亲异常")
    void rebuild_sameContent_releasesOldParent() {
        Region content = new Region();
        TabsAnt.Builder builder = TabsAnt.create().tab("a", "A", content);
        Node first = builder.build();
        Node second = builder.build();
        assertNotNull(first);
        assertNotNull(second);
        assertNotNull(content.getParent());
    }

    @Test
    @DisplayName("BUG #144-P2：next() 跳过禁用 tab")
    void next_skipsDisabled() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region(), true)  // disabled
                .tab("c", "C", new Region())
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        assertEquals(0, ctrl.getCurrent());
        assertTrue(ctrl.next());
        assertEquals(2, ctrl.getCurrent()); // skipped index 1 (disabled)
    }

    @Test
    @DisplayName("BUG #144-P2：prev() 跳过禁用 tab")
    void prev_skipsDisabled() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region(), true)  // disabled
                .tab("c", "C", new Region())
                .activeIndex(2)
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        assertEquals(2, ctrl.getCurrent());
        assertTrue(ctrl.prev());
        assertEquals(0, ctrl.getCurrent()); // skipped index 1 (disabled)
    }

    @Test
    @DisplayName("BUG #144-P2：next() 已是末尾返回 false")
    void next_atEnd_returnsFalse() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .activeIndex(1)
                .build();
        assertFalse(TabsAnt.controllerOf(root).next());
    }

    @Test
    @DisplayName("BUG #144-P2：selectByKey(null) 返回 false")
    void selectByKey_null_returnsFalse() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .build();
        assertFalse(TabsAnt.controllerOf(root).selectByKey(null));
    }

    @Test
    @DisplayName("BUG #144-P2：selectByKey(空串) 返回 false")
    void selectByKey_empty_returnsFalse() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .build();
        assertFalse(TabsAnt.controllerOf(root).selectByKey(""));
    }

    @Test
    @DisplayName("标签间应有间距：CSS padding 生效后 label 有横向 insets")
    void tabLabels_haveHorizontalInsets() {
        runOnFxThreadAndWait(() -> {
            VBox root = (VBox) TabsAnt.create()
                    .tab("a", "标签一", new Region())
                    .tab("b", "标签二", new Region())
                    .build();
            // 结构：root > VBox wrapper(barHost+indicator) > StackPane barHost > HBox tabBar > Label×2
            VBox wrapper = (VBox) root.getChildren().get(0);
            StackPane barHost = (StackPane) wrapper.getChildren().get(0);
            HBox tabBar = (HBox) barHost.getChildren().get(0);
            Label first = (Label) tabBar.getChildren().get(0);
            Label second = (Label) tabBar.getChildren().get(1);

            // 放进 Scene 并 applyCss，让 CSS padding 生效
            Scene scene = new Scene(root, 600, 200);
            // 加载真实的 JFXium 主题 CSS，精确复现 demo 渲染（LESS 编译产物）
            java.net.URL url = getClass().getResource("/org/openkawu/jfxium/css/theme-light.css");
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            }
            root.applyCss();
            root.layout();

            double padR = first.getInsets().getRight();
            System.out.println("[TabsAnt-test] label1 styleClass=" + first.getStyleClass());
            System.out.println("[TabsAnt-test] label1 insets L=" + first.getInsets().getLeft()
                    + " R=" + padR + " label1.maxX=" + first.getBoundsInParent().getMaxX()
                    + " label2.minX=" + second.getBoundsInParent().getMinX());
            // 标签间水平应有间隙（CSS 16px padding 提供）
            assertTrue(padR > 0, "标签应有横向内边距，实际 R=" + padR);
            double gap = second.getBoundsInParent().getMinX() - first.getBoundsInParent().getMaxX();
            assertTrue(gap >= 0, "标签间不应重叠，间距=" + gap);
        });
    }

    // ============================================================
    // 可关闭 / 动态增删 / 右侧操作菜单
    // ============================================================

    @Test
    @DisplayName("closable：标签渲染 × 关闭按钮")
    void closable_rendersCloseGraphic() {
        VBox root = (VBox) TabsAnt.create()
                .closable(true)
                .tab("a", "A", new Region())
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        assertNotNull(ctrl.tabLabels.get(0).getGraphic());
    }

    @Test
    @DisplayName("closeTab：移除 tab 并触发 onClose")
    void closeTab_removesAndNotifies() {
        List<String> closed = new ArrayList<>();
        VBox root = (VBox) TabsAnt.create()
                .closable(true)
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .onClose(closed::add)
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertTrue(ctrl.closeTab("a"));
        assertEquals(1, ctrl.getTotal());
        assertEquals(List.of("b"), ctrl.getKeys());
        assertEquals(List.of("a"), closed);
        assertEquals("b", ctrl.getCurrentKey());
    }

    @Test
    @DisplayName("onBeforeClose 返回 false：阻止关闭")
    void onBeforeClose_veto_keepsTab() {
        VBox root = (VBox) TabsAnt.create()
                .closable(true)
                .tab("a", "A", new Region())
                .onBeforeClose(key -> false)
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertFalse(ctrl.closeTab("a"));
        assertEquals(1, ctrl.getTotal());
        assertEquals(List.of("a"), ctrl.getKeys());
    }

    @Test
    @DisplayName("动态 addTab：追加 tab / 拒绝重复 key / 可选中")
    void addTab_dynamic_and_duplicateRejected() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertTrue(ctrl.addTab("b", "B", new Region()));
        assertEquals(2, ctrl.getTotal());
        assertEquals(List.of("a", "b"), ctrl.getKeys());

        assertFalse(ctrl.addTab("a", "A2", new Region()), "重复 key 应被拒绝");
        assertEquals(2, ctrl.getTotal());

        assertTrue(ctrl.selectByKey("b"));
        assertEquals("b", ctrl.getCurrentKey());
    }

    @Test
    @DisplayName("空 TabsAnt build 后可动态 addTab")
    void emptyBuild_canAddTab() {
        VBox root = (VBox) TabsAnt.create().build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertTrue(ctrl.isEmpty());
        assertEquals(-1, ctrl.getCurrent());

        assertTrue(ctrl.addTab("a", "A", new Region()));
        assertEquals(1, ctrl.getTotal());
        assertEquals("a", ctrl.getCurrentKey());
    }

    @Test
    @DisplayName("removeTab：删除当前 tab 后选中右邻")
    void removeTab_active_selectsNeighbor() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .tab("c", "C", new Region())
                .activeKey("b")
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertTrue(ctrl.removeTab("b"));
        assertEquals(List.of("a", "c"), ctrl.getKeys());
        assertEquals("c", ctrl.getCurrentKey());
    }

    @Test
    @DisplayName("setTabClosable：运行时增删 × 按钮")
    void setTabClosable_togglesGraphic() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertNull(ctrl.tabLabels.get(0).getGraphic());
        assertTrue(ctrl.setTabClosable("a", true));
        assertNotNull(ctrl.tabLabels.get(0).getGraphic());
        assertTrue(ctrl.setTabClosable("a", false));
        assertNull(ctrl.tabLabels.get(0).getGraphic());
    }

    @Test
    @DisplayName("closeAll 只关闭 closable 的 tab")
    void closeAll_respectsClosableFlag() {
        List<String> closed = new ArrayList<>();
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region(), false, true)  // closable
                .tab("b", "B", new Region())               // not closable
                .onClose(closed::add)
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);

        assertEquals(1, ctrl.closeAll());
        assertEquals(List.of("b"), ctrl.getKeys());
        assertEquals(List.of("a"), closed);
    }

    @Test
    @DisplayName("动态删除前面的 tab 后，指示条跟随当前标签位置")
    void indicator_followsCurrentAfterRemove() {
        runOnFxThreadAndWait(() -> {
            VBox root = (VBox) TabsAnt.create()
                    .tab("a", "A", new Region())
                    .tab("b", "B", new Region())
                    .tab("c", "C", new Region())
                    .activeKey("c")
                    .build();
            Scene scene = new Scene(root, 600, 200);
            java.net.URL url = getClass().getResource("/org/openkawu/jfxium/css/theme-light.css");
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            }
            root.applyCss();
            root.layout();

            TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
            ctrl.removeTab("a");
            ctrl.removeTab("b");
            root.applyCss();
            root.layout();

            Label current = ctrl.tabLabels.get(0);
            assertEquals(current.getBoundsInParent().getMinX(),
                    ctrl.indicatorRef.getLayoutX(), 0.5,
                    "删除前面的 tab 后，指示条应跟随当前标签左移");
        });
    }

    @Test
    @DisplayName("右侧菜单关闭动作可单独控制显示")
    void actionsMenu_closeActionsConfigurable() {
        VBox single = (VBox) TabsAnt.create()
                .showCloseCurrent(false)
                .showCloseOthers(false)
                .showCloseAll(false)
                .tab("a", "A", new Region())
                .build();
        assertFalse(TabsAnt.controllerOf(single).actionsHost.isVisible(),
                "单 tab + 无关闭动作时右侧菜单应隐藏");

        VBox multi = (VBox) TabsAnt.create()
                .showCloseCurrent(false)
                .showCloseOthers(false)
                .showCloseAll(false)
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .build();
        assertTrue(TabsAnt.controllerOf(multi).actionsHost.isVisible(),
                "多 tab 时仍应显示标签列表菜单");
    }

    @Test
    @DisplayName("updateTab：更新标题与内容")
    void updateTab_labelAndContent() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        Region newContent = new Region();

        assertTrue(ctrl.updateTab("a", "A2", newContent));
        assertEquals("A2", ctrl.tabLabels.get(0).getText());
        assertSame(newContent, ctrl.contentAreaRef.getChildren().get(0));
    }

    @Test
    @DisplayName("右侧操作菜单：默认渲染，overflowMenu(false) 隐藏")
    void actionsMenu_defaultVisible_canDisable() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        assertNotNull(ctrl.actionsHost);
        assertTrue(ctrl.actionsHost.isVisible());
        assertFalse(ctrl.actionsHost.getChildren().isEmpty());

        VBox hidden = (VBox) TabsAnt.create()
                .overflowMenu(false)
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .build();
        assertFalse(TabsAnt.controllerOf(hidden).actionsHost.isVisible());
    }
}
