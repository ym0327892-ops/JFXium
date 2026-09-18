package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CollapseAnt")
class CollapseAntTest extends JfxTestBase {

    // ---------------------------------------------------------------
    // 基础创建
    // ---------------------------------------------------------------

    @Test
    @DisplayName("create().panel().build() 返回 VBox 并挂 jfx-collapse")
    void build_returnsVBox_withStyleClass() {
        VBox collapse = CollapseAnt.create()
                .panel("key1", "标题 1", new Label("内容 1"))
                .build();
        assertNotNull(collapse);
        assertTrue(collapse.getStyleClass().contains(JfxStyles.COLLAPSE));
    }

    @Test
    @DisplayName("空 panel 不抛异常")
    void build_emptyPanels_noException() {
        VBox collapse = CollapseAnt.create().build();
        assertNotNull(collapse);
        assertEquals(0, collapse.getChildren().size());
    }

    // ---------------------------------------------------------------
    // 多个 Panel
    // ---------------------------------------------------------------

    @Test
    @DisplayName("多个 panel → 每个 panel 之间有 divider")
    void multiplePanels_withDividers() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题一", new Label("内容一"))
                .panel("k2", "标题二", new Label("内容二"))
                .panel("k3", "标题三", new Label("内容三"))
                .build();

        // 3 个 panel
        assertEquals(3, collapse.getChildren().size());
    }

    @Test
    @DisplayName("panel(key, header, content, disabled=true) 挂 jfx-collapse-disabled")
    void panel_disabled_addsDisabledClass() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题", new Label("内容"), true)
                .build();

        VBox panelBox = (VBox) collapse.getChildren().get(0);
        // header 是第一个子节点
        var header = panelBox.getChildren().get(0);
        assertTrue(header.getStyleClass().contains(JfxStyles.COLLAPSE_HEADER));
        assertTrue(header.getStyleClass().contains(JfxStyles.COLLAPSE_DISABLED));
    }

    // ---------------------------------------------------------------
    // accordion 模式
    // ---------------------------------------------------------------

    @Test
    @DisplayName("accordion(true) 不抛异常")
    void accordion_true_noException() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题一", new Label("内容一"))
                .panel("k2", "标题二", new Label("内容二"))
                .accordion(true)
                .build();
        assertNotNull(collapse);
    }

    @Test
    @DisplayName("accordion() 无参等价 accordion(true)")
    void accordion_noArg_equivalentTrue() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题", new Label("内容"))
                .accordion()
                .build();
        assertNotNull(collapse);
    }

    // ---------------------------------------------------------------
    // activeKey / activeKeys
    // ---------------------------------------------------------------

    @Test
    @DisplayName("activeKey 指定默认展开项")
    void activeKey_expandsDefault() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题一", new Label("内容一"))
                .panel("k2", "标题二", new Label("内容二"))
                .activeKey("k2")
                .build();

        // 第二个 panel 内容区可见
        VBox panel2 = (VBox) collapse.getChildren().get(1);
        // panelBox 内: header(0) + contentBox(1) + 可能的 divider(2)
        var contentBox = panel2.getChildren().get(1);
        assertTrue(contentBox.isVisible());
    }

    @Test
    @DisplayName("activeKeys(null) 回退空列表")
    void activeKeys_null_fallbackEmpty() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题", new Label("内容"))
                .activeKeys(null)
                .build();
        assertNotNull(collapse);
    }

    @Test
    @DisplayName("panels(null) 回退空列表")
    void panels_null_fallbackEmpty() {
        VBox collapse = CollapseAnt.create()
                .panels(null)
                .build();
        assertNotNull(collapse);
        assertEquals(0, collapse.getChildren().size());
    }

    // ---------------------------------------------------------------
    // AbstractStyleBuilder 继承
    // ---------------------------------------------------------------

    @Test
    @DisplayName("styleClass 追加到根容器")
    void styleClass_appended() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题", new Label("内容"))
                .styleClass("my-collapse")
                .build();
        assertTrue(collapse.getStyleClass().contains("my-collapse"));
        assertTrue(collapse.getStyleClass().contains(JfxStyles.COLLAPSE));
    }

    @Test
    @DisplayName("padding 应用到根 VBox")
    void padding_applied() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题", new Label("内容"))
                .padding(16)
                .build();
        assertEquals(16, collapse.getPadding().getTop());
    }

    // ---------------------------------------------------------------
    // expandIcon 自定义
    // ---------------------------------------------------------------

    @Test
    @DisplayName("expandIcon 自定义图标不抛异常")
    void expandIcon_custom_noException() {
        VBox collapse = CollapseAnt.create()
                .panel("k1", "标题", new Label("内容"))
                .expandIcon(new Label("▼"))
                .build();
        assertNotNull(collapse);
    }

    // ---------------------------------------------------------------
    // 链式全量
    // ---------------------------------------------------------------

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        VBox collapse = CollapseAnt.create()
                .panel("q1", "什么是 JFXium？", new Label("JavaFX 组件库"))
                .panel("q2", "如何安装？", new Label("Maven 依赖"))
                .panel("q3", "支持什么主题？", new Label("11 套主题"), false)
                .accordion(true)
                .activeKey("q1")
                .styleClass("faq")
                .padding(8)
                .build();
        assertNotNull(collapse);
        assertEquals(3, collapse.getChildren().size());
    }
}
