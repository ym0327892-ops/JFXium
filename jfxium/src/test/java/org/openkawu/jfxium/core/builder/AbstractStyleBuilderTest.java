package org.openkawu.jfxium.core.builder;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AbstractStyleBuilder 单元测试 —— 通过一个最小的具体 Builder 子类
 * 验证 style/styleClass/padding/尺寸/边框/节点属性等方法。
 */
@DisplayName("AbstractStyleBuilder")
class AbstractStyleBuilderTest extends JfxTestBase {

    /** 最小具体实现：extends AbstractStyleBuilder，build() 返回 VBox。 */
    static class TestBuilder extends AbstractStyleBuilder<TestBuilder> {
        VBox build() {
            VBox box = new VBox();
            applyStyles(box);
            return box;
        }
    }

    private static TestBuilder newBuilder() {
        return new TestBuilder();
    }

    // ---------- style ----------

    @Test
    @DisplayName("style 设置 inline style，build 后应用到节点")
    void style_string() {
        VBox box = newBuilder().style("-fx-background-color: red;").build();
        assertEquals("-fx-background-color: red;", box.getStyle());
    }

    @Test
    @DisplayName("style(null) 设为空字符串")
    void style_null() {
        VBox box = newBuilder().style(null).build();
        assertTrue(box.getStyle().isEmpty());
    }

    @Test
    @DisplayName("style 多次调用，最后一次覆盖")
    void style_lastWins() {
        VBox box = newBuilder()
                .style("-fx-background-color: red;")
                .style("-fx-background-color: blue;")
                .build();
        assertEquals("-fx-background-color: blue;", box.getStyle());
    }

    // ---------- styleClass ----------

    @Test
    @DisplayName("styleClass 追加")
    void styleClass_append() {
        VBox box = newBuilder().styleClass("a").styleClass("b").build();
        assertTrue(box.getStyleClass().contains("a"));
        assertTrue(box.getStyleClass().contains("b"));
    }

    @Test
    @DisplayName("styleClass 空字符串不追加")
    void styleClass_empty() {
        VBox box = newBuilder().styleClass("").build();
        // 不应增加空字符串
        assertFalse(box.getStyleClass().contains(""));
    }

    // ---------- padding ----------

    @Test
    @DisplayName("padding(double) 四边相同")
    void padding_uniform() {
        VBox box = newBuilder().padding(12).build();
        assertEquals(new Insets(12), box.getPadding());
    }

    @Test
    @DisplayName("padding(top, right, bottom, left) 四边独立")
    void padding_directional() {
        VBox box = newBuilder().padding(1, 2, 3, 4).build();
        assertEquals(1, box.getPadding().getTop());
        assertEquals(2, box.getPadding().getRight());
        assertEquals(3, box.getPadding().getBottom());
        assertEquals(4, box.getPadding().getLeft());
    }

    @Test
    @DisplayName("padding(Insets) Insets 对象")
    void padding_insets() {
        Insets ins = new Insets(5, 10, 15, 20);
        VBox box = newBuilder().padding(ins).build();
        assertEquals(ins, box.getPadding());
    }

    // ---------- 尺寸属性 ----------

    @Test
    @DisplayName("maxWidth/minWidth/prefWidth 应用到 Region")
    void size_properties() {
        VBox box = newBuilder()
                .maxWidth(500).minWidth(100).prefWidth(250)
                .maxHeight(400).minHeight(50).prefHeight(200)
                .build();
        assertEquals(500, box.getMaxWidth(), 0.01);
        assertEquals(100, box.getMinWidth(), 0.01);
        assertEquals(250, box.getPrefWidth(), 0.01);
        assertEquals(400, box.getMaxHeight(), 0.01);
        assertEquals(50, box.getMinHeight(), 0.01);
        assertEquals(200, box.getPrefHeight(), 0.01);
    }

    @Test
    @DisplayName("prefSize 同时设置首选宽高")
    void prefSize() {
        VBox box = newBuilder().prefSize(300, 200).build();
        assertEquals(300, box.getPrefWidth(), 0.01);
        assertEquals(200, box.getPrefHeight(), 0.01);
    }

    @Test
    @DisplayName("maxSize 同时设置最大宽高")
    void maxSize() {
        VBox box = newBuilder().maxSize(600, 500).build();
        assertEquals(600, box.getMaxWidth(), 0.01);
        assertEquals(500, box.getMaxHeight(), 0.01);
    }

    @Test
    @DisplayName("minSize 同时设置最小宽高")
    void minSize() {
        VBox box = newBuilder().minSize(80, 60).build();
        assertEquals(80, box.getMinWidth(), 0.01);
        assertEquals(60, box.getMinHeight(), 0.01);
    }

    // ---------- 边框线 ----------

    @Test
    @DisplayName("borderTop 追加 jfx-border-top styleClass")
    void borderTop() {
        VBox box = newBuilder().borderTop().build();
        assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
    }

    @Test
    @DisplayName("borderBottom 追加 jfx-border-bottom styleClass")
    void borderBottom() {
        VBox box = newBuilder().borderBottom().build();
        assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
    }

    @Test
    @DisplayName("borderLeft 追加 jfx-border-left styleClass")
    void borderLeft() {
        VBox box = newBuilder().borderLeft().build();
        assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_LEFT));
    }

    @Test
    @DisplayName("borderRight 追加 jfx-border-right styleClass")
    void borderRight() {
        VBox box = newBuilder().borderRight().build();
        assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_RIGHT));
    }

    @Test
    @DisplayName("borderTop(false) 不添加")
    void borderTop_false() {
        VBox box = newBuilder().borderTop(false).build();
        assertFalse(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
    }

    // ---------- 圆角档位（M24 任务：radius 下沉到 AbstractStyleBuilder） ----------

    @Test
    @DisplayName("radius(NONE) 追加 jfx-radius-none")
    void radius_none() {
        VBox box = newBuilder().radius(Radius.NONE).build();
        assertTrue(box.getStyleClass().contains(JfxStyles.RADIUS_NONE));
    }

    @Test
    @DisplayName("radius(SM) 追加 jfx-radius-sm")
    void radius_sm() {
        VBox box = newBuilder().radius(Radius.SM).build();
        assertTrue(box.getStyleClass().contains(JfxStyles.RADIUS_SM));
    }

    @Test
    @DisplayName("radius(LG) 追加 jfx-radius-lg")
    void radius_lg() {
        VBox box = newBuilder().radius(Radius.LG).build();
        assertTrue(box.getStyleClass().contains(JfxStyles.RADIUS_LG));
    }

    @Test
    @DisplayName("radius(MD) 不追加 class（默认走 LESS @border-radius-md）")
    void radius_md_noClass() {
        VBox box = newBuilder().radius(Radius.MD).build();
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_SM));
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_LG));
    }

    @Test
    @DisplayName("未调 radius 时不追加任何 RADIUS_* class（默认 null 语义）")
    void radius_default_null() {
        VBox box = newBuilder().build();
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_NONE));
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_SM));
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_LG));
    }

    @Test
    @DisplayName("radius 重复调用：清旧挂新（幂等性）")
    void radius_idempotent() {
        VBox box = newBuilder()
                .radius(Radius.SM)
                .radius(Radius.LG)
                .build();
        assertFalse(box.getStyleClass().contains(JfxStyles.RADIUS_SM));
        assertTrue(box.getStyleClass().contains(JfxStyles.RADIUS_LG));
    }

    @Test
    @DisplayName("radius 返回 self，支持链式")
    void radius_fluent() {
        TestBuilder b = newBuilder();
        assertSame(b, b.radius(Radius.MD));
    }

    // ---------- 高频节点属性 ----------

    @Test
    @DisplayName("visible 设置可见性")
    void visible() {
        VBox box = newBuilder().visible(false).build();
        assertFalse(box.isVisible());
    }

    @Test
    @DisplayName("disable 设置禁用")
    void disable() {
        VBox box = newBuilder().disable(true).build();
        assertTrue(box.isDisable());
    }

    @Test
    @DisplayName("managed 设置布局管理")
    void managed() {
        VBox box = newBuilder().managed(false).build();
        assertFalse(box.isManaged());
    }

    @Test
    @DisplayName("opacity 设置透明度")
    void opacity() {
        VBox box = newBuilder().opacity(0.5).build();
        assertEquals(0.5, box.getOpacity(), 0.01);
    }

    @Test
    @DisplayName("cursor 设置鼠标光标")
    void cursor() {
        VBox box = newBuilder().cursor(Cursor.HAND).build();
        assertEquals(Cursor.HAND, box.getCursor());
    }

    @Test
    @DisplayName("id 设置节点 ID")
    void id() {
        VBox box = newBuilder().id("my-box").build();
        assertEquals("my-box", box.getId());
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("所有属性串联不抛异常")
    void fullChain() {
        VBox box = newBuilder()
                .style("-fx-background-color: #fff;")
                .styleClass("panel")
                .styleClass("bordered")
                .padding(8, 12, 8, 12)
                .maxWidth(600).minWidth(200).prefWidth(400)
                .maxHeight(500).minHeight(100).prefHeight(300)
                .prefSize(400, 300)
                .maxSize(600, 500)
                .minSize(200, 100)
                .borderTop().borderBottom()
                .radius(Radius.LG)
                .visible(true)
                .disable(false)
                .managed(true)
                .opacity(1.0)
                .cursor(Cursor.DEFAULT)
                .id("root-panel")
                .build();

        assertNotNull(box);
        assertTrue(box.getStyleClass().contains("panel"));
        assertTrue(box.getStyleClass().contains("bordered"));
        assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_TOP));
        assertTrue(box.getStyleClass().contains(JfxStyles.BORDER_BOTTOM));
        assertTrue(box.getStyleClass().contains(JfxStyles.RADIUS_LG));
        assertEquals(8, box.getPadding().getTop());
        assertTrue(box.isVisible());
        assertFalse(box.isDisable());
        assertEquals("root-panel", box.getId());
    }
}
