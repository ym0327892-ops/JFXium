package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium 树形组件（M19.50 重构）— 包装 JavaFX {@link TreeView}（继承式 + 双工厂模式）。
 *
 * <p><b>定位</b>：层级数据展示控件，提供节点工厂方法（{@link #leaf} / {@link #node}）实现声明式树结构构建。
 * 对标 Ant Design Tree。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>声明式节点构建（{@code leaf()} / {@code node()} 静态工厂）</li>
 *   <li>节点图标支持（{@code leaf(value, graphic)} / {@code node(value, graphic, children)}）</li>
 *   <li>控制根节点可见性（{@link #showRoot(boolean)}）</li>
 *   <li>节点选择回调（{@link #onSelect(Consumer)}）</li>
 *   <li>实现 {@link LayoutCommon} 接口，支持 {@code .styleClass()} / {@code .style()} / 尺寸 / 圆角 / 边框等</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>文件目录树（文件管理器）</li>
 *   <li>组织架构展示（公司部门树）</li>
 *   <li>分类导航（商品分类、文章分类）</li>
 *   <li>权限树（角色功能权限勾选）</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式</h2>
 * <pre>{@code
 * TreeView<String> tree = TreeAnt.<String>create()
 *     .root("项目",
 *         TreeAnt.node("src",
 *             TreeAnt.node("main",
 *                 TreeAnt.leaf("App.java"),
 *                 TreeAnt.leaf("Config.java")
 *             ),
 *             TreeAnt.leaf("test")
 *         ),
 *         TreeAnt.leaf("pom.xml")
 *     )
 *     .showRoot(true)
 *     .onSelect(item -> System.out.println("选中：" + item))
 *     .build();
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class FileTreeView extends TreeAnt<String> {
 *     public FileTreeView() {
 *         super();
 *         root("项目",
 *             TreeAnt.node("src",
 *                 TreeAnt.leaf("App.java")
 *             ),
 *             TreeAnt.leaf("pom.xml")
 *         );
 *         onSelect(path -> openFile(path));
 *     }
 * }
 * }</pre>
 *
 * <h2>与 TreeSelectAnt 的区别</h2>
 * <ul>
 *   <li>{@code TreeAnt} —— 嵌入式树形展示（始终可见）</li>
 *   <li>{@code TreeSelectAnt} —— 下拉式树形选择（点击后弹出）</li>
 * </ul>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link TreeView} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class TreeAnt<T> extends TreeView<T>
        implements LayoutCommon<TreeAnt<T>>, DisabledSupport<TreeAnt<T>> {

    // ============================================================
    // 状态字段
    // ============================================================

    /**
     * 从 TreeItem&lt;T&gt; 提取展示文本的函数（默认 {@code Object::toString}）。
     * <p>支持自定义业务类型（File / Dept / Department 等），不必依赖 toString。</p>
     */
    private Function<T, String> valueExtractor;

    /** cellFactory 安装标记 —— 保证 build() 幂等、继承式不重复挂载。 */
    private boolean cellFactoryInstalled = false;

    // ============================================================
    // 节点工厂（静态方法）
    // ============================================================

    /** 创建叶子节点。 */
    public static <T> TreeItem<T> leaf(T value) {
        return new TreeItem<>(value);
    }

    /** 创建叶子节点（带图标）。 */
    public static <T> TreeItem<T> leaf(T value, Node graphic) {
        return new TreeItem<>(value, graphic);
    }

    /** 创建父节点。 */
    @SafeVarargs
    public static <T> TreeItem<T> node(T value, TreeItem<T>... children) {
        TreeItem<T> item = new TreeItem<>(value);
        item.getChildren().addAll(children);
        item.setExpanded(true);
        return item;
    }

    /** 创建父节点（带图标）。 */
    @SafeVarargs
    public static <T> TreeItem<T> node(T value, Node graphic, TreeItem<T>... children) {
        TreeItem<T> item = new TreeItem<>(value, graphic);
        item.getChildren().addAll(children);
        item.setExpanded(true);
        return item;
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口。 */
    public static <T> TreeAnt<T> create() {
        return new TreeAnt<>();
    }

    /** 工厂入口（带根节点 value + children）。 */
    @SafeVarargs
    public static <T> TreeAnt<T> create(T rootValue, TreeItem<T>... children) {
        TreeAnt<T> tree = new TreeAnt<>();
        tree.root(rootValue, children);
        return tree;
    }

    // ============================================================
    // 构造函数（公开，便于业务继承）
    // ============================================================

    public TreeAnt() {
        super();
        // P1-1c 修复：显式挂载 jfx- 前缀样式类，避免 .tree-cell 全局污染
        installCellFactory();
    }

    /**
     * 安装 cellFactory（幂等）。
     * <p>cellFactory 内部读取 {@link #valueExtractor} 字段，避免闭包锁定初始值，
     * 这样 {@link #valueExtractor(Function)} 可以后续修改而无需重建 cellFactory。</p>
     */
    private void installCellFactory() {
        if (cellFactoryInstalled) return;
        cellFactoryInstalled = true;
        setCellFactory(tv -> {
            TreeCell<T> cell = new TreeCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(renderItemText(item, valueExtractor));
                    }
                }
            };
            cell.getStyleClass().add(JfxStyles.TREE_CELL);
            return cell;
        });
    }

    /**
     * 从 {@code item} 提取 cell 展示文本（纯函数，便于测试）。
     * <p>逻辑：{@code valueExtractor} 非空时调 extractor，否则回退到 {@code item.toString()}。</p>
     *
     * @param item       节点值（可能为 null —— null 时返回 null）
     * @param extractor  自定义提取函数（可为 null —— 回退到 toString）
     * @param <T>        item 类型
     * @return cell 显示的文本
     */
    static <T> String renderItemText(T item, Function<T, String> extractor) {
        if (item == null) return null;
        return extractor != null ? extractor.apply(item) : item.toString();
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /**
     * 设置根节点（value + children 模式）。
     */
    @SafeVarargs
    public final TreeAnt<T> root(T value, TreeItem<T>... children) {
        TreeItem<T> r = new TreeItem<>(value);
        r.getChildren().addAll(children);
        r.setExpanded(true);
        setRoot(r);
        return this;
    }

    /**
     * 设置根节点（直接传入 TreeItem）。
     */
    public TreeAnt<T> root(TreeItem<T> rootItem) {
        setRoot(rootItem);
        return this;
    }

    /**
     * 是否显示根节点。
     */
    public TreeAnt<T> showRoot(boolean show) {
        setShowRoot(show);
        return this;
    }

    /**
     * 设置节点文本提取函数。
     * <p>用于自定义业务类型（如 File / Dept / Department）展示，
     * 无需重写 toString。允许 null（回退到 {@code item.toString()}）。</p>
     */
    public TreeAnt<T> valueExtractor(Function<T, String> extractor) {
        this.valueExtractor = extractor;
        return this;
    }

    /**
     * 设置选择回调。
     */
    public TreeAnt<T> onSelect(Consumer<T> handler) {
        if (handler != null) {
            getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    handler.accept(newVal.getValue());
                }
            });
        }
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 构建
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。
     *
     * <p>TreeAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public TreeAnt<T> build() {
        installCellFactory();
        return this;
    }
}