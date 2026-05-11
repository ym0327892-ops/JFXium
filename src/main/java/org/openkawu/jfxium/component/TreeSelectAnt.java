package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 树形选择器组件 - 对标 Ant Design TreeSelect
 *
 * 支持下拉树形结构选择
 *
 * 使用示例：
 * <pre>{@code
 * // 基础树形选择
 * HBox treeSelect = TreeSelectAnt.create()
 *     .placeholder("请选择")
 *     .tree(treeRoot)
 *     .onSelect(node -> System.out.println(node.getValue()))
 *     .build();
 * }</pre>
 */
public class TreeSelectAnt {

    public static class TreeNode {
        private final String value;
        private final String label;
        private final List<TreeNode> children;
        private boolean disabled;

        public TreeNode(String value, String label) {
            this.value = value;
            this.label = label;
            this.children = new ArrayList<>();
            this.disabled = false;
        }

        public TreeNode(String value, String label, List<TreeNode> children) {
            this.value = value;
            this.label = label;
            this.children = children != null ? children : new ArrayList<>();
            this.disabled = false;
        }

        public TreeNode disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public List<TreeNode> getChildren() { return children; }
        public boolean isDisabled() { return disabled; }
        public boolean hasChildren() { return children != null && !children.isEmpty(); }
    }

    public static class Builder {
        private String placeholder = "请选择";
        private TreeNode root;
        private boolean disabled = false;
        private boolean multiple = false;
        private Consumer<TreeNode> onSelect = null;
        private Consumer<List<TreeNode>> onMultipleSelect = null;

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder tree(TreeNode root) {
            this.root = root;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder multiple(boolean multiple) {
            this.multiple = multiple;
            return this;
        }

        public Builder onSelect(Consumer<TreeNode> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder onMultipleSelect(Consumer<List<TreeNode>> onMultipleSelect) {
            this.onMultipleSelect = onMultipleSelect;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.getStyleClass().add("tree-select");

            TextField field = new TextField();
            field.setPromptText(placeholder);
            field.setEditable(false);
            field.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-padding: 8px 12px;"
            );
            HBox.setHgrow(field, Priority.ALWAYS);

            Popup popup = new Popup();
            popup.setAutoHide(true);

            VBox treePanel = new VBox(0);
            treePanel.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 8px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);"
            );
            treePanel.setPadding(new Insets(4, 0, 4, 0));
            treePanel.setPrefWidth(240);

            if (root != null) {
                buildTreeNodes(treePanel, root, 0);
            }

            popup.getContent().add(treePanel);

            field.setOnMouseClicked(e -> {
                if (!disabled) {
                    if (popup.isShowing()) {
                        popup.hide();
                    } else {
                        javafx.geometry.Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                        popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
                    }
                }
            });

            container.getChildren().add(field);

            if (disabled) {
                field.setDisable(true);
                container.setStyle("-fx-opacity: 0.6;");
            }

            return container;
        }

        private void buildTreeNodes(VBox panel, TreeNode node, int depth) {
            HBox row = new HBox(8);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 12, 6, 12 + depth * 16));
            row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");

            Label label = new Label(node.getLabel());
            label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + (node.isDisabled() ? "-color-fg-subtle" : "-color-fg-default") + ";"
            );
            row.getChildren().add(label);

            if (!node.isDisabled()) {
                row.setOnMouseEntered(e -> {
                    row.setStyle("-fx-cursor: hand; -fx-background-color: -color-bg-subtle;");
                });
                row.setOnMouseExited(e -> {
                    row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");
                });
                row.setOnMouseClicked(e -> {
                    if (onSelect != null) {
                        onSelect.accept(node);
                    }
                });
            }

            panel.getChildren().add(row);

            if (node.hasChildren()) {
                for (TreeNode child : node.getChildren()) {
                    buildTreeNodes(panel, child, depth + 1);
                }
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
