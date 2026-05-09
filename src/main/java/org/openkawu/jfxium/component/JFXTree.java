package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Tree Component
 * Inspired by Ant Design Tree
 *
 * Usage:
 * <pre>{@code
 * TreeView<String> tree = JFXTree.<String>create()
 *     .root("Root",
 *         JFXTree.node("Child 1",
 *             JFXTree.leaf("Grandchild 1"),
 *             JFXTree.leaf("Grandchild 2")
 *         ),
 *         JFXTree.leaf("Child 2")
 *     )
 *     .onSelect(item -> System.out.println("Selected: " + item))
 *     .build();
 * }</pre>
 */
public class JFXTree<T> {

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

    public static class Builder<T> {
        private TreeItem<T> root;
        private boolean showRoot = true;
        private Consumer<T> onSelect;
        private String style = "";

        private Builder() {}

        /**
         * 设置根节点
         */
        public Builder<T> root(T value, TreeItem<T>... children) {
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

        public Builder<T> style(String style) {
            this.style = style;
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

            if (!style.isEmpty()) {
                treeView.setStyle(style);
            }

            return treeView;
        }
    }
}