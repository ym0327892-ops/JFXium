package org.openkawu.jfxium.component.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium ListView 组件 - 对标 Ant Design List（继承式，双工厂模式）。
 *
 * <p><b>定位</b>：列表选择控件，继承自 {@link ListView}，
 * 跟 {@link ComboBoxAnt} / {@link InputAnt} 同款「双工厂模式」——
 * 既能当工厂链式构建，也能被业务继承。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式</h3>
 * <pre>{@code
 * ListView<String> list = ListViewAnt.<String>create()
 *     .items("项目一", "项目二", "项目三")
 *     .selectionMode(SelectionMode.SINGLE)
 *     .onSelect(item -> System.out.println("选中: " + item))
 *     .build();
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class FileList extends ListViewAnt<String> {
 *     public FileList() {
 *         items("file1.txt", "file2.txt", "file3.txt");
 *         selectionMode(SelectionMode.MULTIPLE);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link ListView} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身</li>
 * </ul>
 */
public class ListViewAnt<T> extends ListView<T> implements DisabledSupport<ListViewAnt<T>> {

    /** 双击事件所需的点击次数。 */
    private static final int DOUBLE_CLICK_COUNT = 2;

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（空选项列表）。 */
    public static <T> ListViewAnt<T> create() {
        return new ListViewAnt<>();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ListViewAnt() {
        super();
        init();
    }

    public ListViewAnt(ObservableList<T> items) {
        super(items);
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.JFX_LIST_VIEW);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置选项列表（可变参数）。 */
    @SafeVarargs
    public final ListViewAnt<T> items(T... items) {
        setItems(FXCollections.observableArrayList(items));
        return this;
    }

    /** 设置选项列表（ObservableList）。 */
    public ListViewAnt<T> items(ObservableList<T> items) {
        setItems(items);
        return this;
    }

    /** 设置空白占位符（列表为空时显示）。 */
    public ListViewAnt<T> placeholder(Node placeholder) {
        setPlaceholder(placeholder);
        return this;
    }

    /** 设置选择模式（SINGLE / MULTIPLE）。 */
    public ListViewAnt<T> selectionMode(SelectionMode mode) {
        getSelectionModel().setSelectionMode(mode);
        return this;
    }

    /** 设置选中项变更回调。 */
    @SuppressWarnings("unchecked")
    public ListViewAnt<T> onSelect(Consumer<T> handler) {
        getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (handler != null) handler.accept(newVal);
                });
        return this;
    }

    /** 设置双击回调。 */
    public ListViewAnt<T> onDoubleClick(Consumer<T> handler) {
        setOnMouseClicked(e -> {
            if (e.getClickCount() == DOUBLE_CLICK_COUNT && handler != null) {
                T item = getSelectionModel().getSelectedItem();
                if (item != null) handler.accept(item);
            }
        });
        return this;
    }

    /** 设置固定单元格高度（用于虚拟化性能优化）。 */
    public ListViewAnt<T> fixedCellSize(double size) {
        setFixedCellSize(size);
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /** 设置可编辑（配合 cellFactory 使用）。 */
    public ListViewAnt<T> editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    // ============================================================
    // Builder 终结
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>ListViewAnt 既是工厂也是节点：调用 {@link #build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了跟老式 Builder API 完全对齐。</p>
     */
    public ListViewAnt<T> build() {
        return this;
    }
}
