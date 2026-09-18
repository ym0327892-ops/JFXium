package org.openkawu.jfxium.component.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DatePickerAnt 单元测试 —— 覆盖 value、placeholder、size、onChange 和 bindValue。
 */
@DisplayName("DatePickerAnt")
class DatePickerAntTest extends JfxTestBase {

    @Test
    @DisplayName("create() 挂默认 styleClass")
    void create_defaults() {
        DatePickerAnt dp = DatePickerAnt.create().build();
        assertTrue(dp.getStyleClass().contains(JfxStyles.JFX_DATE_PICKER));
    }

    @Test
    @DisplayName("create(LocalDate) 设置初始日期")
    void createWithDate() {
        LocalDate date = LocalDate.of(2025, 6, 6);
        DatePickerAnt dp = new DatePickerAnt(date);
        assertEquals(date, dp.getValue());
    }

    @Test
    @DisplayName("build() 返回自身")
    void build_returnsThis() {
        DatePickerAnt dp = DatePickerAnt.create();
        assertSame(dp, dp.build());
    }

    @Test
    @DisplayName("value 设置日期")
    void value() {
        LocalDate date = LocalDate.of(2025, 12, 25);
        DatePickerAnt dp = DatePickerAnt.create().value(date).build();
        assertEquals(date, dp.getValue());
    }

    @Test
    @DisplayName("placeholder 设置提示文本")
    void placeholder() {
        DatePickerAnt dp = DatePickerAnt.create()
                .placeholder("选择日期").build();
        assertEquals("选择日期", dp.getPromptText());
    }

    @Test
    @DisplayName("editable(true) 可编辑")
    void editable_true() {
        DatePickerAnt dp = DatePickerAnt.create().editable(true).build();
        assertTrue(dp.isEditable());
    }

    @Test
    @DisplayName("disabled(true) 禁用")
    void disabled_true() {
        DatePickerAnt dp = DatePickerAnt.create().disabled(true).build();
        assertTrue(dp.isDisable());
    }

    @Test
    @DisplayName("showWeekNumbers(true) 显示周数")
    void showWeekNumbers() {
        DatePickerAnt dp = DatePickerAnt.create()
                .showWeekNumbers(true).build();
        assertTrue(dp.isShowWeekNumbers());
    }

    @Test
    @DisplayName("size(SMALL) 挂 SIZE_SMALL")
    void size_small() {
        DatePickerAnt dp = DatePickerAnt.create()
                .size(Size.SMALL).build();
        assertTrue(dp.getStyleClass().contains(JfxStyles.SIZE_SMALL));
    }

    // ---------- onChange ----------

    @Test
    @DisplayName("onChange 注册监听")
    void onChange_smoke() {
        DatePickerAnt dp = DatePickerAnt.create()
                .onChange(d -> {})
                .build();
        assertNotNull(dp);
    }

    // ---------- bindValue ----------

    @Test
    @DisplayName("bindValue 双向绑定：控件→Property")
    void bindValue_controlToProperty() {
        ObjectProperty<LocalDate> prop = new SimpleObjectProperty<>(null);
        DatePickerAnt dp = DatePickerAnt.create().bindValue(prop).build();
        LocalDate date = LocalDate.of(2025, 6, 1);
        dp.setValue(date);
        assertEquals(date, prop.get());
    }

    @Test
    @DisplayName("bindValue 双向绑定：Property→控件")
    void bindValue_propertyToControl() {
        ObjectProperty<LocalDate> prop = new SimpleObjectProperty<>(null);
        DatePickerAnt dp = DatePickerAnt.create().bindValue(prop).build();
        LocalDate date = LocalDate.of(2025, 7, 1);
        prop.set(date);
        assertEquals(date, dp.getValue());
    }

    @Test
    @DisplayName("bindValue(null) 不抛异常")
    void bindValue_null() {
        DatePickerAnt dp = DatePickerAnt.create().bindValue(null).build();
        assertNotNull(dp);
    }

    // ---------- 链式 ----------

    @Test
    @DisplayName("全链式串联")
    void fullChain() {
        ObjectProperty<LocalDate> prop = new SimpleObjectProperty<>(null);
        DatePickerAnt dp = DatePickerAnt.create()
                .value(LocalDate.now())
                .placeholder("请选择")
                .editable(false)
                .disabled(false)
                .showWeekNumbers(false)
                .size(Size.DEFAULT)
                .onChange(d -> {})
                .bindValue(prop)
                .styleClass("my-dp")
                .build();
        assertNotNull(dp);
    }
}
