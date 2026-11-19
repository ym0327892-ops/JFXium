package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.TreeCellSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.List;
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
 *   <li>展开/折叠指示器风格（{@link #indicatorStyle(IndicatorStyle)}）：三角箭头 / V 形箭头 / 加减号</li>
 *   <li>缩进参考线开关（{@link #showLine(boolean)}，默认关闭）</li>
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
    // 枚举
    // ============================================================

    /**
     * 展开/折叠指示器风格（单元格左侧的展开开关符号）。
     *
     * <p>纯视觉风格，不改变树的展开/折叠行为；通过 {@link #indicatorStyle(IndicatorStyle)} 切换，
     * 可重复调用（幂等）。默认 {@link #ARROW}。</p>
     */
    public enum IndicatorStyle {
        /** 三角箭头（默认，Ant Design 风格）。 */
        ARROW,
        /** V 形箭头（现代桌面工具风格，如 JetBrains / VS Code）。 */
        CHEVRON,
        /** 加号 / 减号（资源管理器风格）。 */
        PLUS_MINUS
    }

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
            GuideTreeCell cell = new GuideTreeCell();
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
     * 设置展开/折叠指示器风格。幂等——先清旧风格 styleClass，再按需挂新。
     * <p>传 {@code null} 等价于 {@link IndicatorStyle#ARROW}（默认三角箭头，只清不挂）。</p>
     *
     * <pre>{@code
     * TreeAnt.<String>create()
     *     .root("根", TreeAnt.node("子节点"))
     *     .indicatorStyle(TreeAnt.IndicatorStyle.PLUS_MINUS)
     *     .build();
     * }</pre>
     */
    public TreeAnt<T> indicatorStyle(IndicatorStyle style) {
        getStyleClass().removeAll(
                JfxStyles.TREE_INDICATOR_CHEVRON,
                JfxStyles.TREE_INDICATOR_PLUS_MINUS);
        if (style != null) {
            switch (style) {
                case CHEVRON    -> getStyleClass().add(JfxStyles.TREE_INDICATOR_CHEVRON);
                case PLUS_MINUS -> getStyleClass().add(JfxStyles.TREE_INDICATOR_PLUS_MINUS);
                default         -> { /* ARROW：不挂额外类，使用默认三角箭头 */ }
            }
        }
        return this;
    }

    /**
     * 是否显示缩进参考线（每一层一条竖向细线，VS Code 缩进参考线风格）。
     * <p>默认关闭；可运行时开关，调用后会立即刷新所有已渲染的 cell。</p>
     *
     * <pre>{@code
     * TreeAnt.<String>create()
     *     .root("根", TreeAnt.node("子节点"))
     *     .showLine(true)
     *     .build();
     * }</pre>
     *
     * <p><b>实现说明</b>：JavaFX 的缩进由 {@link TreeCellSkin#layoutChildren} 在 Java 侧按
     * {@code indent * level} 计算，CSS 没有层级选择器，无法用纯样式画线。因此由
     * {@link GuideTreeCellSkin} 在每行底层铺一个线图层，按层级画整行高的竖线；
     * 线宽/线色仍走 styleClass + LESS（{@link JfxStyles#TREE_GUIDE}）。</p>
     */
    public TreeAnt<T> showLine(boolean show) {
        if (show) {
            if (!getStyleClass().contains(JfxStyles.TREE_SHOW_LINE)) {
                getStyleClass().add(JfxStyles.TREE_SHOW_LINE);
            }
        } else {
            getStyleClass().remove(JfxStyles.TREE_SHOW_LINE);
        }
        // 已渲染的 cell 需要重建才会重新计算参考线；未挂 skin 时 refresh() 也是安全的（只打标记）
        refresh();
        return this;
    }

    /** 是否显示缩进参考线（默认 {@code false}）。 */
    public boolean isShowLine() {
        return getStyleClass().contains(JfxStyles.TREE_SHOW_LINE);
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

    // ============================================================
    // 缩进参考线（cell + skin）
    // ============================================================

    /**
     * TreeAnt 专用 TreeCell —— 文本走 {@link TreeAnt#valueExtractor}，
     * 皮肤换成 {@link GuideTreeCellSkin} 以提供缩进参考线图层。
     */
    private final class GuideTreeCell extends TreeCell<T> {

        /**
         * 展开热区：披露节点左右各放宽一点，纵向放宽到整行高度。
         * <p>JavaFX 默认只有披露节点自身 bounds（约 29×15）可点，行高 32 时上下都差一截，
         * 用户瞄着箭头点很容易点到文字区变成"选中"而不是"展开"。这里用事件过滤器接管：
         * 命中热区 → 直接切换展开并 consume（与原生语义一致：点箭头不选中该行）。</p>
         */
        private static final double HOT_ZONE_PX = 6;

        GuideTreeCell() {
            addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (e.getButton() != MouseButton.PRIMARY) return;
                if (isEmpty() || getTreeItem() == null || getTreeItem().isLeaf()) return;
                Node disclosure = getDisclosureNode();
                if (disclosure == null) return;

                double x = e.getX();
                double left = disclosure.getLayoutX() - HOT_ZONE_PX;
                double right = disclosure.getLayoutX() + disclosure.getLayoutBounds().getWidth() + HOT_ZONE_PX;
                if (x >= left && x <= right) {
                    getTreeItem().setExpanded(!getTreeItem().isExpanded());
                    e.consume();
                }
            });
        }

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

        @Override
        protected Skin<?> createDefaultSkin() {
            return new GuideTreeCellSkin<>(this);
        }
    }

    /**
     * 带「缩进参考线」图层的 TreeCell 皮肤。
     *
     * <p>参考线 = 每行底层一个 {@link Pane}，按当前行的可视层级画 N 条整行高的竖线
     * （N = {@code indent * level} 里的 level）。整行高绘制让相邻行的线首尾相接，
     * 不需要额外判断兄弟节点。</p>
     *
     * <p>不改写缩进本身（仍由 {@link TreeCellSkin#layoutChildren} 负责），只叠加图层，
     * 所以展开/折叠、滚动回收、showRoot 切换都与原生行为一致。</p>
     */
    private static final class GuideTreeCellSkin<T> extends TreeCellSkin<T> {

        /** 线图层：始终位于 cell 子节点第 0 位（最底层），不参与布局。 */
        private final Pane guideLayer = new Pane();

        /** 竖线节点池 —— cell 滚动回收时复用，避免每次 layout 反复创建节点。 */
        private final List<Line> guides = new ArrayList<>();

        GuideTreeCellSkin(TreeCell<T> cell) {
            super(cell);
            guideLayer.getStyleClass().add(JfxStyles.TREE_GUIDE_LAYER);
            guideLayer.setManaged(false);
            guideLayer.setMouseTransparent(true);
            getChildren().add(0, guideLayer);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);

            final TreeCell<T> cell = getSkinnable();
            final TreeView<T> tree = cell.getTreeView();
            final int level = resolveLevel(tree, cell);

            if (level <= 0) {
                hideGuidesFrom(0);
                return;
            }

            // skin 会把 disclosureNode 挪到最底层，这里重新把线图层压回去（仅在需要时改子节点顺序）
            if (getChildren().indexOf(guideLayer) != 0) {
                getChildren().remove(guideLayer);
                getChildren().add(0, guideLayer);
            }
            // 铺满整个 cell（含 padding），保证相邻行竖线连续
            guideLayer.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());

            final double indent = getIndent();
            ensureGuides(level);
            final double height = cell.getHeight();
            for (int i = 0; i < level; i++) {
                Line line = guides.get(i);
                // 第 i 个缩进列的列中心；+0.5 让 1px 描边落在像素中心，避免发虚
                double lx = Math.round(i * indent + indent / 2) + 0.5;
                line.setStartX(lx);
                line.setEndX(lx);
                line.setStartY(0);
                line.setEndY(height);
                line.setVisible(true);
            }
            hideGuidesFrom(level);
        }

        /** 当前行需要几条参考线：= 可视层级；未开启 / 空行 / 根行返回 -1 或 0。 */
        private int resolveLevel(TreeView<T> tree, TreeCell<T> cell) {
            if (tree == null || cell.isEmpty()) return -1;
            if (!tree.getStyleClass().contains(JfxStyles.TREE_SHOW_LINE)) return -1;
            TreeItem<T> item = cell.getTreeItem();
            if (item == null) return -1;
            int level = tree.getTreeItemLevel(item);
            if (level < 0) return -1;
            if (!tree.isShowRoot()) level--;   // 与 TreeCellSkin 的缩进算法保持一致
            return level;
        }

        private void ensureGuides(int count) {
            while (guides.size() < count) {
                Line line = new Line();
                line.getStyleClass().add(JfxStyles.TREE_GUIDE);
                line.setManaged(false);
                line.setMouseTransparent(true);
                guides.add(line);
                guideLayer.getChildren().add(line);
            }
        }

        private void hideGuidesFrom(int from) {
            for (int i = from; i < guides.size(); i++) {
                guides.get(i).setVisible(false);
            }
        }
    }
}