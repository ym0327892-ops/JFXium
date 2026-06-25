package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CheckBoxAnt 单元测试 —— 覆盖选中、不确定态、形状、尺寸、onChange 和 bindValue。
 */
@DisplayName("CheckBoxAnt")
class CheckBoxAntTest extends JfxTestBase {

    @Test
    @DisplayName("create() 挂默认 styleClass")
    void create_hasDefaultClass() {
        CheckBoxAnt cb = CheckBoxAnt.create().build();
        // 父类 CheckBox 默认挂 modena "check-box" class（jfx-check-box 是死代码已删，BUG #104）
        assertTrue(cb.getStyleClass().contains("check-box"));
    }

    @Test
    @DisplayName("create(text) 设置文本")
    void createWithText() {
        CheckBoxAnt cb = CheckBoxAnt.create("记住我").build();
        assertEquals("记住我", cb.getText());
    }

    @Test
    @DisplayName("build() 返回自身")
    void build_returnsThis() {
        CheckBoxAnt cb = CheckBoxAnt.create();
        assertSame(cb, cb.build());
    }

    // ---------- selected ----------

    @Test
    @DisplayName("selected(true) 选中")
    void selected_true() {
        CheckBoxAnt cb = CheckBoxAnt.create().selected(true).build();
        assertTrue(cb.isSelected());
    }

    // ---------- indeterminate ----------

    @Test
    @DisplayName("indeterminate(true) 设置不确定态")
    void indeterminate_true() {
        CheckBoxAnt cb = CheckBoxAnt.create().indeterminate(true).build();
        assertTrue(cb.isIndeterminate());
    }

    @Test
    @DisplayName("allowIndeterminate(true) 允许三态")
    void allowIndeterminate() {
        CheckBoxAnt cb = CheckBoxAnt.create().allowIndeterminate(true).build();
        assertTrue(cb.isAllowIndeterminate());
    }

    // ---------- size ----------

    @Test
    @DisplayName("size(SMALL) 挂 SIZE_SMALL")
    void size_small() {
        CheckBoxAnt cb = CheckBoxAnt.create().size(Size.SMALL).build();
        assertTrue(cb.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    // ---------- shape ----------

    @Test
    @DisplayName("shape(CIRCLE) 挂 jfx-shape-circle")
    void shape_circle() {
        CheckBoxAnt cb = CheckBoxAnt.create().shape(CheckBoxAnt.Shape.CIRCLE).build();
        assertTrue(cb.getStyleClass().contains(JfxStyles.CHECKBOX_SHAPE_CIRCLE));
    }

    @Test
    @DisplayName("shape 幂等")
    void shape_idempotent() {
        CheckBoxAnt cb = CheckBoxAnt.create()
                .shape(CheckBoxAnt.Shape.CIRCLE)
                .shape(CheckBoxAnt.Shape.ROUNDED)
                .build();
        assertFalse(cb.getStyleClass().contains(JfxStyles.CHECKBOX_SHAPE_CIRCLE));
        assertTrue(cb.getStyleClass().contains(JfxStyles.CHECKBOX_SHAPE_ROUNDED));
    }

    // ---------- onChange ----------

    @Test
    @DisplayName("onChange 注册监听")
    void onChange_smoke() {
        CheckBoxAnt cb = CheckBoxAnt.create()
                .onChange(b -> {})
                .build();
        assertNotNull(cb); // 不抛异常
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 双向绑定：控件→Property")
    void bindValue_controlToProperty() {
        BooleanProperty prop = new SimpleBooleanProperty(false);
        CheckBoxAnt cb = CheckBoxAnt.create().bindValue(prop).build();
        cb.setSelected(true);
        assertTrue(prop.get());
    }

    @Test
    @DisplayName("bindValue 双向绑定：Property→控件")
    void bindValue_propertyToControl() {
        BooleanProperty prop = new SimpleBooleanProperty(false);
        CheckBoxAnt cb = CheckBoxAnt.create().bindValue(prop).build();
        prop.set(true);
        assertTrue(cb.isSelected());
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null() {
        CheckBoxAnt cb = CheckBoxAnt.create().bindValue(null).build();
        assertNotNull(cb);
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        BooleanProperty prop = new SimpleBooleanProperty(true);
        CheckBoxAnt cb = CheckBoxAnt.create("同意")
                .selected(false)
                .size(Size.LARGE)
                .shape(CheckBoxAnt.Shape.CIRCLE)
                .indeterminate(false)
                .disabled(false)
                .onChange(b -> {})
                .bindValue(prop)
                .styleClass("my-check")
                .build();
        assertNotNull(cb);
        // bindValue 双向同步后，prop 的 true 应已设到控件
        assertTrue(cb.isSelected(), "prop(true) 应同步到控件");
    }
}
