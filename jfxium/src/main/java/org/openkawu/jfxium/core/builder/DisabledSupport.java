package org.openkawu.jfxium.core.builder;

/**
 * 统一的 disabled API 契约（P2-S7 抽取）——
 * 消除控件类 + Builder 类中重复的 {@code disabled(boolean)} / {@code disabled()} 方法实现。
 *
 * <h2>背景</h2>
 * <p>此前 9 个控件类（{@link org.openkawu.jfxium.component.control.ButtonAnt ButtonAnt} /
 * {@link org.openkawu.jfxium.component.control.CheckBoxAnt CheckBoxAnt} /
 * {@link org.openkawu.jfxium.component.control.ComboBoxAnt ComboBoxAnt} /
 * {@link org.openkawu.jfxium.component.control.RadioButtonAnt RadioButtonAnt} /
 * {@link org.openkawu.jfxium.component.control.ColorPickerAnt ColorPickerAnt} /
 * {@link org.openkawu.jfxium.component.control.ChoiceBoxAnt ChoiceBoxAnt} /
 * {@link org.openkawu.jfxium.component.control.SplitMenuButtonAnt SplitMenuButtonAnt} /
 * {@link org.openkawu.jfxium.component.control.DatePickerAnt DatePickerAnt} /
 * {@link org.openkawu.jfxium.component.control.InputAnt InputAnt}）各自重复实现：</p>
 * <pre>{@code
 * public ButtonAnt disabled(boolean disabled) {
 *     setDisable(disabled);
 *     return this;
 * }
 * }</pre>
 * <p>每处 4 行模板，总计 36 行样板代码。新增控件类时也需要拷贝这套逻辑。</p>
 *
 * <h2>方案</h2>
 * <ul>
 *   <li><b>Pattern A（控件类）</b>：实现 {@code DisabledSupport<Self>}，默认方法直接调用
 *       {@link javafx.scene.Node#setDisable(boolean)}（控件类继承自 JavaFX 控件，已有此方法）。</li>
 *   <li><b>Pattern B（Builder 继承 {@link AbstractStyleBuilder}）</b>：基类已提供
 *       {@code disabled(boolean)} / {@code disabled()} 方法（内部委托到 {@code disable} 字段，
 *       build() 时通过 {@link AbstractStyleBuilder#applyStyles(javafx.scene.Node)} 自动应用）。
 *       子类无需重复定义。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // Pattern A：控件类
 * public class ButtonAnt extends Button implements DisabledSupport<ButtonAnt> {
 *     // disabled(boolean) / disabled() 由接口默认提供
 * }
 *
 * // Pattern B：Builder 类（继承 AbstractStyleBuilder）
 * public static class Builder extends AbstractStyleBuilder<Builder> {
 *     // disabled(boolean) / disabled() 由父类提供
 *     public Slider build() {
 *         Slider slider = new Slider();
 *         slider.setDisable(disable);  // 或 applyStyles(slider) 由父类自动处理
 *         return slider;
 *     }
 * }
 * }</pre>
 *
 * <h2>注意</h2>
 * <p>本接口的 {@link #disabled(boolean)} 默认方法直接调用 {@code setDisable(disabled)}，
 * 要求实现类必须是 {@link javafx.scene.Node} 的子类（拥有继承自 {@code Node} 的
 * {@code setDisable(boolean)} 方法）。如需 Builder 类实现，请改用
 * {@link AbstractStyleBuilder}，其内部通过 {@code disable} 字段延迟应用。</p>
 *
 * @param <SELF> 子类自身类型（用于链式调用协变返回）
 * @see AbstractStyleBuilder
 */
public interface DisabledSupport<SELF extends DisabledSupport<SELF>> {

    /**
     * 实际写入禁用状态到底层节点。
     *
     * <p>默认实现通过 {@code instanceof} 模式匹配检查 {@code this} 是否为
     * {@link javafx.scene.Node}，若是则直接调用 {@code setDisable(disabled)}。
     * 这是 Java 16+ 支持的惯用写法，消除了此前 23 个控件类中完全相同的
     * {@code @Override public void applyDisable(boolean) { setDisable(disabled); }} 样板。</p>
     *
     * <p>非 Node 实现类（罕见）需要覆写本方法提供自己的禁用逻辑。</p>
     */
    default void applyDisable(boolean disabled) {
        if (this instanceof javafx.scene.Node node) {
            node.setDisable(disabled);
        }
    }

    /**
     * 设置禁用状态。默认实现：调用 {@link #applyDisable(boolean)}。
     *
     * @param disabled {@code true} 禁用，{@code false} 启用
     * @return {@code this}（子类自身类型，便于链式）
     */
    @SuppressWarnings("unchecked")
    default SELF disabled(boolean disabled) {
        applyDisable(disabled);
        return (SELF) this;
    }

    /**
     * 设置禁用状态（无参语法糖，等价 {@code disabled(true)}）。
     *
     * @return {@code this}（子类自身类型，便于链式）
     */
    default SELF disabled() {
        return disabled(true);
    }
}