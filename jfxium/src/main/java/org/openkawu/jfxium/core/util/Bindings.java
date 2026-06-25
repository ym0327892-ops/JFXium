package org.openkawu.jfxium.core.util;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Bindings —— 统一 Property ↔ Property / Property → Callback 绑定模板工具。
 *
 * <h2>解决的重复模式</h2>
 * <p>项目中 13 个 {@code *Ant} 组件类里都有形如下面的样板：</p>
 *
 * <h3>模式 D1：双向绑定（13 处）</h3>
 * <pre>{@code
 * // 原样板（出现在 ComboBoxAnt / CheckBoxAnt / TextAreaAnt / InputAnt /
 * //            SliderAnt / AutoCompleteAnt / MentionsAnt / ToggleButtonAnt /
 * //            RadioButtonAnt / ColorPickerAnt / ChoiceBoxAnt / SwitchAnt / DatePickerAnt）
 * if (property != null) {
 *     someProperty().bindBidirectional(property);
 * }
 * return this;
 * }</pre>
 *
 * <h3>模式 D2：变更回调（多处）</h3>
 * <pre>{@code
 * // 原样板（出现在 ComboBoxAnt / DatePickerAnt / MentionsAnt / ToggleButtonAnt 等）
 * if (handler != null) {
 *     someProperty().addListener((obs, oldVal, newVal) -> handler.accept(newVal));
 * }
 * return this;
 * }</pre>
 *
 * <p>用本工具类替换后，组件代码变为单行调用：</p>
 * <pre>{@code
 * Bindings.bindBidirectional(textProperty(), property);
 *
 * ChangeListener<String> listener = Bindings.onChange(textProperty(), handler);
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>null 安全</b>：所有方法对入参 null 做防御——不抛 NPE，让外部代码零负担。</li>
 *   <li><b>无状态</b>：所有方法为 {@code static}，工具类不允许实例化。</li>
 *   <li><b>桥接 JavaFX API</b>：仅在 JavaFX API 之上做 null 防御与统一签名，不改变语义。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 双向绑定
 * StringProperty myValue = new SimpleStringProperty("init");
 * Bindings.bindBidirectional(textArea.textProperty(), myValue);
 *
 * // 监听变化
 * ChangeListener<String> listener = Bindings.onChange(myValue, newVal -> updateUi(newVal));
 * // ... 业务侧不再需要时
 * Bindings.remove(myValue, listener);
 * }</pre>
 */
public final class Bindings {

    private Bindings() {
        // 工具类禁止实例化
    }

    // ============================================================
    // 双向绑定（模式 D1）
    // ============================================================

    /**
     * 双向绑定两个 Property。<b>任一参数为 null 时静默跳过</b>，不抛 NPE。
     *
     * <p>适用于{@code *Ant.bindValue(Property)} 链式方法中：
     * <pre>{@code
     * public MyAnt bindValue(StringProperty property) {
     *     Bindings.bindBidirectional(textProperty(), property);
     *     return this;
     * }
     * }</pre></p>
     *
     * @param a 第一个 Property（通常是组件自身的 property）
     * @param b 第二个 Property（通常是外部传入的 property）
     * @param <T> 绑定值的类型
     */
    public static <T> void bindBidirectional(Property<T> a, Property<T> b) {
        if (a != null && b != null) {
            a.bindBidirectional(b);
        }
    }

    /**
     * 解除双向绑定。<b>任一参数为 null 时静默跳过</b>，不抛 NPE。
     *
     * @param a 第一个 Property
     * @param b 第二个 Property
     * @param <T> 解绑值的类型
     */
    public static <T> void unbindBidirectional(Property<T> a, Property<T> b) {
        if (a != null && b != null) {
            a.unbindBidirectional(b);
        }
    }

    // ============================================================
    // 变更回调（模式 D2）
    // ============================================================

    /**
     * 监听 Property 变化，回调仅接收<b>新值</b>。返回 ChangeListener 以便后续 {@link #remove} 解绑。
     *
     * <p>等价于：
     * <pre>{@code
     * if (handler != null) {
     *     ChangeListener<T> listener = (obs, oldVal, newVal) -> handler.accept(newVal);
     *     source.addListener(listener);
     *     return listener;
     * }
     * return null;
     * }</pre></p>
     *
     * @param source 被监听的 ObservableValue（可为 null，此时返回 null 不做任何事）
     * @param handler 变化回调（可为 null，此时返回 null 不做任何事）
     * @return 注册成功的 ChangeListener（用于解绑）；任一参数为 null 时返回 null
     */
    public static <T> ChangeListener<T> onChange(ObservableValue<T> source, Consumer<? super T> handler) {
        if (source == null || handler == null) {
            return null;
        }
        ChangeListener<T> listener = (obs, oldVal, newVal) -> handler.accept(newVal);
        source.addListener(listener);
        return listener;
    }

    /**
     * 监听 Property 变化，回调接收<b>旧值与新值</b>。返回 ChangeListener 以便后续 {@link #remove} 解绑。
     *
     * <p>适用于需要 diff / undo / 动画过渡的场景。</p>
     *
     * @param source 被监听的 ObservableValue（可为 null）
     * @param handler 变化回调，参数顺序为 (oldVal, newVal)（可为 null）
     * @return 注册成功的 ChangeListener；任一参数为 null 时返回 null
     */
    public static <T> ChangeListener<T> onChangeWithOld(ObservableValue<T> source,
                                                          BiConsumer<? super T, ? super T> handler) {
        if (source == null || handler == null) {
            return null;
        }
        ChangeListener<T> listener = (obs, oldVal, newVal) -> handler.accept(oldVal, newVal);
        source.addListener(listener);
        return listener;
    }

    /**
     * 解绑 ChangeListener。<b>任一参数为 null 时静默跳过</b>，不抛 NPE。
     *
     * @param source ObservableValue（来自 {@link #onChange} / {@link #onChangeWithOld} 调用时的同一 source）
     * @param listener 由 {@link #onChange} / {@link #onChangeWithOld} 返回的 ChangeListener
     */
    public static <T> void remove(ObservableValue<T> source, ChangeListener<? super T> listener) {
        if (source != null && listener != null) {
            source.removeListener(listener);
        }
    }
}