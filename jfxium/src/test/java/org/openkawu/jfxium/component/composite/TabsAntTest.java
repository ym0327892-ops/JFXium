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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        HBox tabBar = (HBox) wrapper.getChildren().get(0);
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
    @DisplayName("BUG #144：LEFT 垂直形态带指示条容器")
    void verticalLine_hasIndicatorPane() {
        VBox root = (VBox) TabsAnt.create()
                .tab("a", "A", new Region())
                .tab("b", "B", new Region())
                .tabPlacement(TabsAnt.TabPlacement.LEFT)
                .build();
        TabsAnt.Controller ctrl = TabsAnt.controllerOf(root);
        assertNotNull(ctrl.indicatorRef);
        assertNotNull(ctrl.indicatorPaneRef);
        assertTrue(ctrl.verticalIndicator);

        // 结构：root > HBox layout > HBox wrapper（tabBar + indicatorPane）
        HBox layout = (HBox) root.getChildren().get(0);
        HBox wrapper = (HBox) layout.getChildren().get(0);
        assertTrue(wrapper.getChildren().contains(ctrl.indicatorPaneRef));
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
            // 结构：root > VBox wrapper(tabBar+indicator) > HBox tabBar > Label×2
            VBox wrapper = (VBox) root.getChildren().get(0);
            HBox tabBar = (HBox) wrapper.getChildren().get(0);
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
}
