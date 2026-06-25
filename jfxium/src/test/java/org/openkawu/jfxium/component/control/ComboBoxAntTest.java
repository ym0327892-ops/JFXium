package org.openkawu.jfxium.component.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ComboBoxAnt 单元测试 —— 覆盖 items、value、size、editable、onChange 和 bindValue。
 */
@DisplayName("ComboBoxAnt")
class ComboBoxAntTest extends JfxTestBase {

    @Test
    @DisplayName("create() 挂默认 styleClass 且无可选值")
    void create_defaults() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create().build();
        assertTrue(cb.getStyleClass().contains(JfxStyles.JFX_COMBO_BOX));
        assertNull(cb.getValue());
    }

    @Test
    @DisplayName("items(varargs) 设置选项")
    void items_varargs() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items("A", "B", "C").build();
        assertEquals(3, cb.getItems().size());
        assertEquals("A", cb.getItems().get(0));
    }

    @Test
    @DisplayName("items(ObservableList) 设置选项")
    void items_observableList() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items(FXCollections.observableArrayList("X", "Y"))
                .build();
        assertEquals(2, cb.getItems().size());
    }

    @Test
    @DisplayName("value 设置当前选中值")
    void value() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items("A", "B", "C")
                .value("B").build();
        assertEquals("B", cb.getValue());
    }

    @Test
    @DisplayName("placeholder 设置 promptText")
    void placeholder() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .placeholder("请选择").build();
        assertEquals("请选择", cb.getPromptText());
    }

    @Test
    @DisplayName("size(SMALL) 挂 SIZE_SMALL")
    void size_small() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .size(Size.SMALL).build();
        assertTrue(cb.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    @Test
    @DisplayName("editable(true) 可编辑")
    void editable_true() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .editable(true).build();
        assertTrue(cb.isEditable());
    }

    @Test
    @DisplayName("disabled(true) 禁用")
    void disabled_true() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .disabled(true).build();
        assertTrue(cb.isDisable());
    }

    @Test
    @DisplayName("build() 返回自身")
    void build_returnsThis() {
        ComboBoxAnt<String> cb = ComboBoxAnt.create();
        assertSame(cb, cb.build());
    }

    // ---------- onChange ----------

    @Test
    @DisplayName("onChange 注册监听")
    void onChange_smoke() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items("A", "B")
                .onChange(v -> {})
                .build();
        assertNotNull(cb);
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 双向绑定：控件→Property")
    void bindValue_controlToProperty() {
        ObjectProperty<String> prop = new SimpleObjectProperty<>("");
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items("A", "B", "C")
                .bindValue(prop).build();
        cb.setValue("B");
        assertEquals("B", prop.get());
    }

    @Test
    @DisplayName("bindValue 双向绑定：Property→控件")
    void bindValue_propertyToControl() {
        ObjectProperty<String> prop = new SimpleObjectProperty<>("");
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items("A", "B", "C")
                .bindValue(prop).build();
        prop.set("C");
        assertEquals("C", cb.getValue());
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null() {
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .bindValue(null).build();
        assertNotNull(cb);
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        ObjectProperty<String> prop = new SimpleObjectProperty<>("A");
        ComboBoxAnt<String> cb = ComboBoxAnt.<String>create()
                .items("A", "B", "C")
                .value("B")
                .placeholder("选一个")
                .size(Size.LARGE)
                .editable(false)
                .disabled(false)
                .onChange(v -> {})
                .bindValue(prop)
                .styleClass("my-combo")
                .build();
        // bindBidirectional: 初始化时 prop 的值会同步到控件
        assertEquals("A", cb.getValue());
        assertEquals("A", prop.get());
        assertNotNull(cb);
    }
}
