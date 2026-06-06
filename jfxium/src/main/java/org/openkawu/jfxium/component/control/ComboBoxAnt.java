package org.openkawu.jfxium.component.control;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium ComboBox 组件 - 对标 Ant Design Select（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：下拉选择控件，继承自 {@link ComboBox}，
 * 跟 {@link org.openkawu.jfxium.component.layout.VBoxAnt VBoxAnt} /
 * {@link LabelAnt} / {@link InputAnt} 同款「双工厂模式」。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * ComboBox<String> cb = ComboBoxAnt.<String>create()
 *     .items("选项一", "选项二", "选项三")
 *     .placeholder("请选择")
 *     .build();
 *
 * // build 后再改尺寸 / 状态（继承式核心优势）
 * cb.size(Size.SMALL).disabled(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class CitySelect extends ComboBoxAnt<String> {
 *     public CitySelect() {
 *         items("北京", "上海", "广州", "深圳");
 *         placeholder("请选择城市");
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link ComboBox} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size(SMALL)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class ComboBoxAnt<T> extends ComboBox<T> {

    /** 尺寸枚举，与 InputAnt/ButtonAnt 一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT, SMALL, LARGE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空选项列表）。 */
    public static <T> ComboBoxAnt<T> create() {
        return new ComboBoxAnt<>();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ComboBoxAnt() {
        super();
        getStyleClass().add("jfx-combo-box");
    }

    public ComboBoxAnt(ObservableList<T> items) {
        super(items);
        getStyleClass().add("jfx-combo-box");
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置选项列表（可变参数）。 */
    @SafeVarargs
    public final ComboBoxAnt<T> items(T... items) {
        setItems(FXCollections.observableArrayList(items));
        return this;
    }

    /** 设置选项列表（ObservableList）。 */
    public ComboBoxAnt<T> items(ObservableList<T> items) {
        setItems(items);
        return this;
    }

    /** 设置当前选中值。 */
    public ComboBoxAnt<T> value(T value) {
        setValue(value);
        return this;
    }

    /** 设置占位提示文本。 */
    public ComboBoxAnt<T> placeholder(String placeholder) {
        if (placeholder != null && !placeholder.isEmpty()) {
            setPromptText(placeholder);
        }
        return this;
    }

    /** 设置是否可编辑（允许用户输入文字过滤选项）。 */
    public ComboBoxAnt<T> editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    /** 设置禁用状态。 */
    public ComboBoxAnt<T> disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂（与 ButtonAnt 行为一致）。
     */
    public ComboBoxAnt<T> size(Size size) {
        getStyleClass().removeAll(JfxStyles.SIZE_SMALL, JfxStyles.SIZE_LARGE);
        if (size == Size.SMALL) {
            getStyleClass().add(JfxStyles.SIZE_SMALL);
        } else if (size == Size.LARGE) {
            getStyleClass().add(JfxStyles.SIZE_LARGE);
        }
        return this;
    }

    /** 选中值变化回调。 */
    public ComboBoxAnt<T> onChange(Consumer<T> handler) {
        if (handler != null) {
            valueProperty().addListener((obs, oldVal, newVal) -> handler.accept(newVal));
        }
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public ComboBoxAnt<T> bindValue(Property<T> property) {
        if (property != null) {
            valueProperty().bindBidirectional(property);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子（跟 *Ant 风格一致）
    // ============================================================

    /** 追加一个 styleClass（幂等——重复调不会重复挂）。 */
    public ComboBoxAnt<T> styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass。 */
    public ComboBoxAnt<T> styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    /** inline style（应急用，优先用 styleClass + LESS）。 */
    public ComboBoxAnt<T> style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>ComboBoxAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public ComboBoxAnt<T> build() {
        return this;
    }
}
