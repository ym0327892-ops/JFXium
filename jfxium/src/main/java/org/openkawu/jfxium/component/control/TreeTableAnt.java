package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 树形表格组件 - 对标 Ant Design TreeTable。
 *
 * <p><b>定位</b>：表格 + 树形结构的结合体，支持层级展开/折叠，
 * 常用于文件管理器、组织架构展示、分类属性面板等场景。
 * JavaFX 原生有 {@link TreeTableView}，但 JFXium 提供 Builder 流式 API + 主题化封装。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>列定义</b>：column(title, property) 定义表格列</li>
 *   <li><b>树形节点</b>：TreeNode 支持 children 嵌套（无限层级）</li>
 *   <li><b>展开/折叠</b>：expandAll() / collapseAll() / expandToLevel(n)</li>
 *   <li><b>箭头点击热区</b>：整行高度 + 箭头左右各 6px（JavaFX 默认点箭头无反应，只能双击）</li>
 *   <li><b>选择回调</b>：onSelect(node) 选中时触发</li>
 *   <li><b>多选</b>：multiSelect(true) 启用多选</li>
 *   <li><b>视觉</b>：走 {@link JfxStyles#TREE_TABLE} 系列 LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 文件管理器风格
 * TreeTableAnt<FileNode> treeTable = TreeTableAnt.<FileNode>create()
 *     .column("名称", "name")
 *     .column("大小", "size")
 *     .column("修改时间", "modifiedTime")
 *     .root(new TreeNode("根目录", "-", "-"))
 *         .child(new TreeNode("src", "-", "2024-01-01"))
 *             .child(new TreeNode("Main.java", "2KB", "2024-01-02"))
 *             .endChild()
 *         .endChild()
 *         .child(new TreeNode("pom.xml", "1KB", "2024-01-01"))
 *         .endChild()
 *     .onSelect(node -> System.out.println("选中：" + node.getName()))
 *     .build();
 * }</pre>
 *
 * @see TreeAnt 纯树形控件（无表格列）
 * @see TableAnt 纯表格控件（无树形层级）
 */
public class TreeTableAnt<T> extends TreeTableView<T> {

    /** 展开热区左右各放宽的像素（纵向 = 整行高度）。 */
    private static final double HOT_ZONE_PX = 6;

    private final List<TreeTableColumn<T, ?>> columns = new ArrayList<>();
    private TreeItem<T> rootItem;
    private Consumer<T> onSelect;

    // ============================================================
    // 工厂入口
    // ============================================================

    public static <T> TreeTableAnt<T> create() {
        return new TreeTableAnt<>();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public TreeTableAnt() {
        super();
        getStyleClass().add(JfxStyles.TREE_TABLE);
        setShowRoot(true);

        // 展开热区：JavaFX 的 TreeTableRowBehavior 没有实现 handleDisclosureNode，
        // 点 disclosure 箭头完全没有反应（只能双击行）。这里和 TreeAnt 保持一致：
        // 箭头左右各放宽 6px、纵向整行都可点，命中即切换展开并 consume（不选中该行）。
        setRowFactory(tv -> createRow());

        // 选择监听
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && val.getValue() != null && onSelect != null) {
                onSelect.accept(val.getValue());
            }
        });
    }

    /** 行工厂：挂展开热区事件过滤器。 */
    private TreeTableRow<T> createRow() {
        TreeTableRow<T> row = new TreeTableRow<>();
        row.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            TreeItem<T> item = row.getTreeItem();
            if (item == null || item.isLeaf()) return;

            Node disclosure = row.getDisclosureNode();
            if (disclosure == null) return;

            double left = disclosure.getLayoutX() - HOT_ZONE_PX;
            double right = disclosure.getLayoutX()
                    + disclosure.getLayoutBounds().getWidth() + HOT_ZONE_PX;
            if (e.getX() >= left && e.getX() <= right) {
                item.setExpanded(!item.isExpanded());
                e.consume();
            }
        });
        return row;
    }

    // ============================================================
    // 列定义
    // ============================================================

    public TreeTableAnt<T> column(String title, String property) {
        return column(title, property, -1);
    }

    public TreeTableAnt<T> column(String title, String property, double width) {
        TreeTableColumn<T, Object> col = new TreeTableColumn<>(title);
        col.setCellValueFactory(new TreeItemPropertyValueFactory<>(property));
        if (width >= 0) {
            col.setPrefWidth(width);
        }
        col.getStyleClass().add(JfxStyles.TREE_TABLE_HEADER);
        columns.add(col);
        return this;
    }

    // ============================================================
    // 根节点
    // ============================================================

    public TreeNodeBuilder<T> root(T value) {
        rootItem = new TreeItem<>(value);
        rootItem.setExpanded(true);
        setRoot(rootItem);
        return new TreeNodeBuilder<>(rootItem, this);
    }

    // ============================================================
    // 选择
    // ============================================================

    public TreeTableAnt<T> onSelect(Consumer<T> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public TreeTableAnt<T> multiSelect(boolean multi) {
        getSelectionModel().setSelectionMode(
            multi ? javafx.scene.control.SelectionMode.MULTIPLE
                  : javafx.scene.control.SelectionMode.SINGLE
        );
        return this;
    }

    // ============================================================
    // 展开/折叠
    // ============================================================

    public TreeTableAnt<T> expandAll() {
        expandAll(rootItem);
        return this;
    }

    public TreeTableAnt<T> collapseAll() {
        collapseAll(rootItem);
        return this;
    }

    public TreeTableAnt<T> expandToLevel(int level) {
        expandToLevel(rootItem, 0, level);
        return this;
    }

    private void expandAll(TreeItem<T> item) {
        item.setExpanded(true);
        for (TreeItem<T> child : item.getChildren()) {
            expandAll(child);
        }
    }

    private void collapseAll(TreeItem<T> item) {
        item.setExpanded(false);
        for (TreeItem<T> child : item.getChildren()) {
            collapseAll(child);
        }
    }

    private void expandToLevel(TreeItem<T> item, int current, int target) {
        item.setExpanded(current < target);
        for (TreeItem<T> child : item.getChildren()) {
            expandToLevel(child, current + 1, target);
        }
    }

    // ============================================================
    // 构建
    // ============================================================

    public TreeTableAnt<T> build() {
        getColumns().addAll(columns);
        return this;
    }

    // ============================================================
    // TreeNodeBuilder：用于构建树形层级
    // ============================================================

    public static class TreeNodeBuilder<T> {
        private final TreeItem<T> parent;
        private final TreeTableAnt<T> treeTable;
        private TreeItem<T> current;

        TreeNodeBuilder(TreeItem<T> parent, TreeTableAnt<T> treeTable) {
            this.parent = parent;
            this.treeTable = treeTable;
            this.current = parent;
        }

        public TreeNodeBuilder<T> child(T value) {
            TreeItem<T> child = new TreeItem<>(value);
            current.getChildren().add(child);
            this.current = child;
            return this;
        }

        public TreeNodeBuilder<T> endChild() {
            if (current != parent) {
                this.current = current.getParent();
            }
            return this;
        }

        public TreeTableAnt<T> endRoot() {
            return treeTable;
        }
    }
}
