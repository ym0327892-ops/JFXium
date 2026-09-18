package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.Bindings;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium RadioButton 组件 - 对标 Ant Design Radio（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：单选控件，继承自 {@link RadioButton}，
 * 跟 {@link CheckBoxAnt} 同款「双工厂模式」。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * ToggleGroup group = new ToggleGroup();
 *
 * RadioButton r1 = RadioButtonAnt.create("选项 A")
 *     .toggleGroup(group)
 *     .selected(true)
 *     .size(Size.SMALL)
 *     .build();
 *
 * // build 后再改（继承式核心优势）
 * r1.size(Size.LARGE).disabled(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class YesRadio extends RadioButtonAnt {
 *     public YesRadio() {
 *         text("是");
 *         size(Size.SMALL);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link RadioButton} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size(SMALL)} / {@code shape(SQUARE)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class RadioButtonAnt extends RadioButton implements LayoutCommon<RadioButtonAnt>, DisabledSupport<RadioButtonAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    /**
     * 形状枚举。
     * <ul>
     *   <li>{@link #DEFAULT}：圆形（Ant Design 默认）</li>
     *   <li>{@link #SQUARE}：方形</li>
     *   <li>{@link #ROUNDED}：圆角方形</li>
     * </ul>
     */
    public enum Shape {
        DEFAULT, SQUARE, ROUNDED
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（无文本）。 */
    public static RadioButtonAnt create() {
        return new RadioButtonAnt();
    }

    /** 工厂入口（带文本）。 */
    public static RadioButtonAnt create(String text) {
        return new RadioButtonAnt(text);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public RadioButtonAnt() {
        super();
    }

    public RadioButtonAnt(String text) {
        super(text);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置文本（链式包装 setText）。 */
    public RadioButtonAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /** 设置选中状态。 */
    public RadioButtonAnt selected(boolean selected) {
        setSelected(selected);
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public RadioButtonAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /**
     * 设置形状。幂等——先清旧 shape styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public RadioButtonAnt shape(Shape shape) {
        getStyleClass().removeAll(JfxStyles.CHECKBOX_SHAPE_SQUARE, JfxStyles.CHECKBOX_SHAPE_ROUNDED);
        if (shape == Shape.SQUARE) {
            getStyleClass().add(JfxStyles.CHECKBOX_SHAPE_SQUARE);
        } else if (shape == Shape.ROUNDED) {
            getStyleClass().add(JfxStyles.CHECKBOX_SHAPE_ROUNDED);
        }
        return this;
    }

    /** 设置 ToggleGroup。 */
    public RadioButtonAnt toggleGroup(ToggleGroup toggleGroup) {
        if (toggleGroup != null) {
            setToggleGroup(toggleGroup);
        }
        return this;
    }

    /** 监听选中状态变化。 */
    public RadioButtonAnt onChange(Consumer<Boolean> handler) {
        Bindings.onChange(selectedProperty(), handler);
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public RadioButtonAnt bindValue(BooleanProperty property) {
        Bindings.bindBidirectional(selectedProperty(), property);
        return this;
    }

    /** Builder 模式终结调用——返回自身（向后兼容）。 */
    public RadioButtonAnt build() {
        return this;
    }
}
