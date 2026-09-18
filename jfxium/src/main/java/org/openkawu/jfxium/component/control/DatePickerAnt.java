package org.openkawu.jfxium.component.control;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.DatePicker;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.Bindings;
import org.openkawu.jfxium.core.util.TextUtils;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * JFXium DatePicker 组件 - 对标 Ant Design DatePicker（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：日期选择控件，继承自 {@link DatePicker}，
 * 跟 {@link InputAnt} / {@link ComboBoxAnt} 同款「双工厂模式」。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * DatePicker dp = DatePickerAnt.create()
 *     .placeholder("选择日期")
 *     .value(LocalDate.now())
 *     .size(Size.SMALL)
 *     .build();
 *
 * // build 后再改（继承式核心优势）
 * dp.size(Size.LARGE).disabled(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class BirthdayPicker extends DatePickerAnt {
 *     public BirthdayPicker() {
 *         placeholder("选择生日");
 *         size(Size.LARGE);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link DatePicker} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size(SMALL)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class DatePickerAnt extends DatePicker implements LayoutCommon<DatePickerAnt>, DisabledSupport<DatePickerAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口。 */
    public static DatePickerAnt create() {
        return new DatePickerAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public DatePickerAnt() {
        super();
        init();
    }

    public DatePickerAnt(LocalDate date) {
        super(date);
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.JFX_DATE_PICKER);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置占位提示文本。 */
    public DatePickerAnt placeholder(String placeholder) {
        setPromptText(TextUtils.safeText(placeholder));
        return this;
    }

    /** 设置日期值。 */
    public DatePickerAnt value(LocalDate value) {
        setValue(value);
        return this;
    }

    /** 设置可编辑状态。 */
    public DatePickerAnt editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /** 显示周数。 */
    public DatePickerAnt showWeekNumbers(boolean show) {
        setShowWeekNumbers(show);
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂。委托 {@link ApplySizeUtil#apply(javafx.scene.Node, Size)} 实现。
     */
    public DatePickerAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /** 监听日期变化。 */
    public DatePickerAnt onChange(Consumer<LocalDate> handler) {
        Bindings.onChange(valueProperty(), handler);
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public DatePickerAnt bindValue(ObjectProperty<LocalDate> property) {
        Bindings.bindBidirectional(valueProperty(), property);
        return this;
    }

    /** Builder 模式终结调用——返回自身（向后兼容）。 */
    public DatePickerAnt build() {
        return this;
    }
}
