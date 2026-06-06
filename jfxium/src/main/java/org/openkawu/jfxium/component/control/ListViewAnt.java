package org.openkawu.jfxium.component.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.function.Consumer;

/**
 * JFXium ListView 组件 - 对标 Ant Design List（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：列表控件，继承自 {@link ListView}，
 * 跟 {@link ComboBoxAnt} 同款「双工厂模式」。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * ListView<String> list = ListViewAnt.<String>create()
 *     .items("Item 1", "Item 2", "Item 3")
 *     .onSelect(item -> System.out.println("Selected: " + item))
 *     .build();
 *
 * // build 后再改（继承式核心优势）
 * list.disabled(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class CityList extends ListViewAnt<String> {
 *     public CityList() {
 *         items("北京", "上海", "广州", "深圳");
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link ListView} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class ListViewAnt<T> extends ListView<T> {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口。 */
    public static <T> ListViewAnt<T> create() {
        return new ListViewAnt<>();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ListViewAnt() {
        super();
        getStyleClass().add("jfx-list-view");
    }

    public ListViewAnt(ObservableList<T> items) {
        super(items);
        getStyleClass().add("jfx-list-view");
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置列表项（可变参数）。 */
    @SafeVarargs
    public final ListViewAnt<T> items(T... items) {
        setItems(FXCollections.observableArrayList(items));
        return this;
    }

    /** 设置列表项（ObservableList）。 */
    public ListViewAnt<T> items(ObservableList<T> items) {
        setItems(items);
        return this;
    }

    /** 设置可编辑状态。 */
    public ListViewAnt<T> editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    /** 设置禁用状态。 */
    public ListViewAnt<T> disabled(boolean disabled) {
        setDisable(disabled);
        return this;
    }

    /** 监听选中项变化。 */
    public ListViewAnt<T> onSelect(Consumer<T> handler) {
        if (handler != null) {
            getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    handler.accept(newVal);
                }
            });
        }
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    /** 追加一个 styleClass（幂等）。 */
    public ListViewAnt<T> styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass。 */
    public ListViewAnt<T> styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    /** inline style（应急用，优先用 styleClass + LESS）。 */
    public ListViewAnt<T> style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /** Builder 模式终结调用——返回自身（向后兼容）。 */
    public ListViewAnt<T> build() {
        return this;
    }
}
