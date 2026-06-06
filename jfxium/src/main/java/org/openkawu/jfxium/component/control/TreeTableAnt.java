package org.openkawu.jfxium.component.control;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.openkawu.jfxium.core.css.JfxStyles;

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

        // 选择监听
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && val.getValue() != null && onSelect != null) {
                onSelect.accept(val.getValue());
            }
        });
    }

    // ============================================================
    // 列定义
    // ============================================================

    public TreeTableAnt<T> column(String title, String property) {
        TreeTableColumn<T, Object> col = new TreeTableColumn<>(title);
        col.setCellValueFactory(new TreeItemPropertyValueFactory<>(property));
        col.getStyleClass().add(JfxStyles.TREE_TABLE_HEADER);
        columns.add(col);
        return this;
    }

    public TreeTableAnt<T> column(String title, String property, double width) {
        TreeTableColumn<T, Object> col = new TreeTableColumn<>(title);
        col.setCellValueFactory(new TreeItemPropertyValueFactory<>(property));
        col.setPrefWidth(width);
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
