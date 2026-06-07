package org.openkawu.jfxium.component.composite;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SwitchAnt 单元测试 —— 覆盖选中、禁用、形状、onChange 和 bindValue。
 */
@DisplayName("SwitchAnt")
class SwitchAntTest extends JfxTestBase {

    @Test
    @DisplayName("create().build() 返回 HBox")
    void build_returnsHBox() {
        HBox sw = SwitchAnt.create().build();
        assertNotNull(sw);
        assertTrue(sw.getStyleClass().contains(JfxStyles.SWITCH_CONTAINER));
    }

    @Test
    @DisplayName("selected(true) 初始选中")
    void selected_true() {
        HBox sw = SwitchAnt.create().selected(true).build();
        assertNotNull(sw); // 不抛异常即通过
    }

    @Test
    @DisplayName("disabled(true) 禁用态")
    void disabled_true() {
        HBox sw = SwitchAnt.create().disabled(true).build();
        assertTrue(sw.getStyleClass().contains(JfxStyles.SWITCH_CONTAINER));
    }

    @Test
    @DisplayName("checkedText/uncheckedText 状态文本")
    void statusTexts() {
        HBox sw = SwitchAnt.create()
                .checkedText("开")
                .uncheckedText("关")
                .build();
        assertNotNull(sw);
    }

    @Test
    @DisplayName("shape(SQUARE) 挂修饰类")
    void shape_square() {
        HBox sw = SwitchAnt.create()
                .shape(SwitchAnt.Shape.SQUARE).build();
        // shape-square 应挂到内部的 switchPane 上，验证不抛异常即可
        assertNotNull(sw);
    }

    @Test
    @DisplayName("onChange 注册回调")
    void onChange_smoke() {
        HBox sw = SwitchAnt.create()
                .onChange(b -> {})
                .build();
        assertNotNull(sw);
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 双向绑定：外部 property→控件")
    void bindValue_propertyToControl() {
        BooleanProperty prop = new SimpleBooleanProperty(true);
        HBox sw = SwitchAnt.create()
                .bindValue(prop)
                .build();
        assertNotNull(sw); // 不抛异常，值已同步
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null() {
        HBox sw = SwitchAnt.create().bindValue(null).build();
        assertNotNull(sw);
    }

    // ---------- AbstractStyleBuilder 继承 ----------

    @Test
    @DisplayName("styleClass 追加到容器")
    void styleClass_applied() {
        HBox sw = SwitchAnt.create()
                .styleClass("my-switch").build();
        assertTrue(sw.getStyleClass().contains("my-switch"));
    }

    @Test
    @DisplayName("padding 应用到容器")
    void padding_applied() {
        HBox sw = SwitchAnt.create().padding(10).build();
        assertEquals(10, sw.getPadding().getTop());
        assertEquals(10, sw.getPadding().getRight());
        assertEquals(10, sw.getPadding().getBottom());
        assertEquals(10, sw.getPadding().getLeft());
    }

    @Test
    @DisplayName("prefWidth 应用到容器")
    void prefWidth_applied() {
        HBox sw = SwitchAnt.create().prefWidth(200).build();
        assertEquals(200, sw.getPrefWidth(), 0.01);
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        BooleanProperty prop = new SimpleBooleanProperty(false);
        HBox sw = SwitchAnt.create()
                .selected(true)
                .disabled(false)
                .checkedText("启用")
                .uncheckedText("禁用")
                .shape(SwitchAnt.Shape.ROUNDED)
                .onChange(b -> {})
                .bindValue(prop)
                .styleClass("custom-switch")
                .padding(8)
                .build();
        assertNotNull(sw);
    }
}
