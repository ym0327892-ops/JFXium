package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.function.Consumer;

/**
 * JFXium Tree Component
 * Inspired by Ant Design Tree
 *
 * Usage:
 * <pre>{@code
 * TreeView<String> tree = TreeAnt.<String>create()
 *     .root("Root",
 *         TreeAnt.node("Child 1",
 *             TreeAnt.leaf("Grandchild 1"),
 *             TreeAnt.leaf("Grandchild 2")
 *         ),
 *         TreeAnt.leaf("Child 2")
 *     )
 *     .onSelect(item -> System.out.println("Selected: " + item))
 *     .build();
 * }</pre>
 */
public class TreeAnt<T> {

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    /**
     * 创建叶子节点
     */
    public static <T> TreeItem<T> leaf(T value) {
        return new TreeItem<>(value);
    }

    /**
     * 创建叶子节点（带图标）
     */
    public static <T> TreeItem<T> leaf(T value, Node graphic) {
        TreeItem<T> item = new TreeItem<>(value, graphic);
        return item;
    }

    /**
     * 创建父节点
     */
    @SafeVarargs
    public static <T> TreeItem<T> node(T value, TreeItem<T>... children) {
        TreeItem<T> item = new TreeItem<>(value);
        item.getChildren().addAll(children);
        item.setExpanded(true);
        return item;
    }

    /**
     * 创建父节点（带图标）
     */
    @SafeVarargs
    public static <T> TreeItem<T> node(T value, Node graphic, TreeItem<T>... children) {
        TreeItem<T> item = new TreeItem<>(value, graphic);
        item.getChildren().addAll(children);
        item.setExpanded(true);
        return item;
    }

    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        private TreeItem<T> root;
        private boolean showRoot = true;
        private Consumer<T> onSelect;

        private Builder() {}

        /**
         * 设置根节点
         */
        @SafeVarargs
        public final Builder<T> root(T value, TreeItem<T>... children) {
            this.root = new TreeItem<>(value);
            this.root.getChildren().addAll(children);
            this.root.setExpanded(true);
            return this;
        }

        /**
         * 设置根节点（直接传入 TreeItem）
         */
        public Builder<T> root(TreeItem<T> root) {
            this.root = root;
            return this;
        }

        /**
         * 是否显示根节点
         */
        public Builder<T> showRoot(boolean show) {
            this.showRoot = show;
            return this;
        }

        /**
         * 设置选择回调
         */
        public Builder<T> onSelect(Consumer<T> handler) {
            this.onSelect = handler;
            return this;
        }

        public TreeView<T> build() {
            TreeView<T> treeView = new TreeView<>(root);
            treeView.setShowRoot(showRoot);

            // Selection listener
            if (onSelect != null) {
                treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        onSelect.accept(newVal.getValue());
                    }
                });
            }

            applyStyles(treeView);
            return treeView;
        }
    }
}
