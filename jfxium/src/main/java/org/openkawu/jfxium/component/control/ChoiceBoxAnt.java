package org.openkawu.jfxium.component.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium ChoiceBox 组件 - 简易下拉选择（继承式，双工厂模式）。
 *
 * <p><b>定位</b>：比 {@link ComboBoxAnt} 更轻量的下拉选择控件，继承自 {@link ChoiceBox}，
 * 跟 {@link ComboBoxAnt} / {@link InputAnt} 同款「双工厂模式」——
 * 既能当工厂链式构建，也能被业务继承。</p>
 *
 * <p>与 {@link ComboBoxAnt} 的区别：ChoiceBox 不支持可编辑输入、不支持多选、
 * Popup 渲染更轻量。适合选项固定且数量较少的场景（如主题选择、语言切换）。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式</h3>
 * <pre>{@code
 * ChoiceBox<String> cb = ChoiceBoxAnt.<String>create()
 *     .items("浅色", "深色", "紧凑")
 *     .value("浅色")
 *     .onSelect(theme -> applyTheme(theme))
 *     .build();
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class ThemeSelect extends ChoiceBoxAnt<String> {
 *     public ThemeSelect() {
 *         items("light", "dark", "compact");
 *         value("light");
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link ChoiceBox} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size(SMALL)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身</li>
 * </ul>
 */
public class ChoiceBoxAnt<T> extends ChoiceBox<T> {

    /** 尺寸枚举，与 InputAnt/ButtonAnt 一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT, SMALL, LARGE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空选项列表）。 */
    public static <T> ChoiceBoxAnt<T> create() {
        return new ChoiceBoxAnt<>();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ChoiceBoxAnt() {
        super();
        getStyleClass().add(JfxStyles.JFX_CHOICE_BOX);
    }

    public ChoiceBoxAnt(ObservableList<T> items) {
        super(items);
        getStyleClass().add(JfxStyles.JFX_CHOICE_BOX);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置选项列表（可变参数）。 */
    @SafeVarargs
    public final ChoiceBoxAnt<T> items(T... items) {
        setItems(FXCollections.observableArrayList(items));
        return this;
    }

    /** 设置选项列表（ObservableList）。 */
    public ChoiceBoxAnt<T> items(ObservableList<T> items) {
        setItems(items);
        return this;
    }

    /** 设置当前选中值。 */
    public ChoiceBoxAnt<T> value(T value) {
        setValue(value);
        return this;
    }

    /** 设置选中项变更回调。 */
    @SuppressWarnings("unchecked")
    public ChoiceBoxAnt<T> onSelect(Consumer<T> handler) {
        getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (handler != null) handler.accept(newVal);
                });
        return this;
    }

    /** 设置禁用状态。 */
    public ChoiceBoxAnt<T> disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT 仅清不挂（与 ButtonAnt 行为一致）。
     */
    public ChoiceBoxAnt<T> size(Size size) {
        getStyleClass().removeAll(JfxStyles.SIZE_SMALL, JfxStyles.SIZE_LARGE);
        if (size == Size.SMALL) {
            getStyleClass().add(JfxStyles.SIZE_SMALL);
        } else if (size == Size.LARGE) {
            getStyleClass().add(JfxStyles.SIZE_LARGE);
        }
        return this;
    }

    // ============================================================
    // Builder 终结
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     */
    public ChoiceBoxAnt<T> build() {
        return this;
    }
}
