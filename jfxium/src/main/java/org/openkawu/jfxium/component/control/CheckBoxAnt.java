package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.Bindings;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium CheckBox 组件 - 对标 Ant Design Checkbox（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：多选控件，继承自 {@link CheckBox}，
 * 跟 {@link org.openkawu.jfxium.component.layout.VBoxAnt VBoxAnt} /
 * {@link LabelAnt} / {@link InputAnt} 同款「双工厂模式」。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * CheckBox cb = CheckBoxAnt.create("记住我")
 *     .selected(true)
 *     .size(Size.SMALL)
 *     .onChange(checked -> System.out.println("Checked: " + checked))
 *     .build();
 *
 * // build 后再改状态（继承式核心优势）
 * cb.size(Size.LARGE).disabled(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class AgreeCheckBox extends CheckBoxAnt {
 *     public AgreeCheckBox() {
 *         text("我已阅读并同意");
 *         size(Size.SMALL);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link CheckBox} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size(SMALL)} / {@code shape(CIRCLE)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class CheckBoxAnt extends CheckBox implements LayoutCommon<CheckBoxAnt>, DisabledSupport<CheckBoxAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    /**
     * 形状枚举。
     * <ul>
     *   <li>{@link #DEFAULT}：方形小圆角（4px，Ant Design 默认）</li>
     *   <li>{@link #CIRCLE}：圆形</li>
     *   <li>{@link #SQUARE}：直角方形（0 圆角）</li>
     *   <li>{@link #ROUNDED}：大圆角（适合卡片式选择）</li>
     * </ul>
     */
    public enum Shape {
        DEFAULT, CIRCLE, SQUARE, ROUNDED
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（无文本）。 */
    public static CheckBoxAnt create() {
        return new CheckBoxAnt();
    }

    /** 工厂入口（带文本）。 */
    public static CheckBoxAnt create(String text) {
        return new CheckBoxAnt(text);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public CheckBoxAnt() {
        super();
    }

    public CheckBoxAnt(String text) {
        super(text);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置文本（链式包装 setText）。 */
    public CheckBoxAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /** 设置选中状态。 */
    public CheckBoxAnt selected(boolean selected) {
        setSelected(selected);
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /** 设置不确定状态（"半选"）。 */
    public CheckBoxAnt indeterminate(boolean indeterminate) {
        setIndeterminate(indeterminate);
        return this;
    }

    /**
     * 是否允许用户点击在 selected ↔ indeterminate ↔ unselected 三态间循环。
     * <p>对应 Ant Design Checkbox 的 indeterminate 三态切换语义。</p>
     */
    public CheckBoxAnt allowIndeterminate(boolean allow) {
        setAllowIndeterminate(allow);
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂（与 ButtonAnt 行为一致）。
     */
    public CheckBoxAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /**
     * 设置形状。幂等——先清旧 shape styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public CheckBoxAnt shape(Shape shape) {
        getStyleClass().removeAll(
                JfxStyles.CHECKBOX_SHAPE_CIRCLE,
                JfxStyles.CHECKBOX_SHAPE_SQUARE,
                JfxStyles.CHECKBOX_SHAPE_ROUNDED);
        if (shape != null) {
            switch (shape) {
                case CIRCLE  -> getStyleClass().add(JfxStyles.CHECKBOX_SHAPE_CIRCLE);
                case SQUARE  -> getStyleClass().add(JfxStyles.CHECKBOX_SHAPE_SQUARE);
                case ROUNDED -> getStyleClass().add(JfxStyles.CHECKBOX_SHAPE_ROUNDED);
                default      -> { /* DEFAULT 不挂额外类 */ }
            }
        }
        return this;
    }

    /** 选中状态变化回调。 */
    public CheckBoxAnt onChange(Consumer<Boolean> handler) {
        Bindings.onChange(selectedProperty(), handler);
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public CheckBoxAnt bindValue(BooleanProperty property) {
        Bindings.bindBidirectional(selectedProperty(), property);
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>CheckBoxAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public CheckBoxAnt build() {
        return this;
    }
}
