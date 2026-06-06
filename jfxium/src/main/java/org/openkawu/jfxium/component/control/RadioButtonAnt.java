package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.core.css.JfxStyles;

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
public class RadioButtonAnt extends RadioButton {

    /** 尺寸枚举，与 ButtonAnt/InputAnt 一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT, SMALL, LARGE
    }

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
        getStyleClass().add("jfx-radio-button");
    }

    public RadioButtonAnt(String text) {
        super(text);
        getStyleClass().add("jfx-radio-button");
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置文本（链式包装 setText）。 */
    public RadioButtonAnt text(String text) {
        setText(text != null ? text : "");
        return this;
    }

    /** 设置选中状态。 */
    public RadioButtonAnt selected(boolean selected) {
        setSelected(selected);
        return this;
    }

    /** 设置禁用状态。 */
    public RadioButtonAnt disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public RadioButtonAnt size(Size size) {
        getStyleClass().removeAll(JfxStyles.SIZE_SMALL, JfxStyles.SIZE_LARGE);
        if (size == Size.SMALL) {
            getStyleClass().add(JfxStyles.SIZE_SMALL);
        } else if (size == Size.LARGE) {
            getStyleClass().add(JfxStyles.SIZE_LARGE);
        }
        return this;
    }

    /**
     * 设置形状。幂等——先清旧 shape styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public RadioButtonAnt shape(Shape shape) {
        getStyleClass().removeAll("shape-square", "shape-rounded");
        if (shape == Shape.SQUARE) {
            getStyleClass().add("shape-square");
        } else if (shape == Shape.ROUNDED) {
            getStyleClass().add("shape-rounded");
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
        if (handler != null) {
            selectedProperty().addListener((obs, oldVal, newVal) -> handler.accept(newVal));
        }
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public RadioButtonAnt bindValue(BooleanProperty property) {
        if (property != null) {
            selectedProperty().bindBidirectional(property);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    /** 追加一个 styleClass（幂等）。 */
    public RadioButtonAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass。 */
    public RadioButtonAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    /** inline style（应急用，优先用 styleClass + LESS）。 */
    public RadioButtonAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /** Builder 模式终结调用——返回自身（向后兼容）。 */
    public RadioButtonAnt build() {
        return this;
    }
}
