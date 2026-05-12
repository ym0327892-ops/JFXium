package org.openkawu.jfxium.component;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeSelectAnt {

    public static class TreeNode {
        private final String value;
        private final String label;
        private final List<TreeNode> children;
        private boolean disabled;
        private boolean expanded = true;

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

        public TreeNode expanded(boolean expanded) {
            this.expanded = expanded;
            return this;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public List<TreeNode> getChildren() { return children; }
        public boolean isDisabled() { return disabled; }
        public boolean isExpanded() { return expanded; }
        public boolean hasChildren() { return children != null && !children.isEmpty(); }
    }

    public static class Builder {
        private String placeholder = "请选择";
        private TreeNode root;
        private boolean disabled = false;
        private boolean multiple = false;
        private Consumer<TreeNode> onSelect = null;
        private Consumer<List<TreeNode>> onMultipleSelect = null;
        private TreeNode selectedNode = null;

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
                "-fx-padding: 8px 32px 8px 12px;" +
                "-fx-background-image: url('data:image/svg+xml;utf8,<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"%23888\"><path d=\"M7 10l5 5 5-5z\"/></svg>');" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-position: right 8px center;" +
                "-fx-background-size: 16px 16px;"
            );
            HBox.setHgrow(field, Priority.ALWAYS);

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setAutoFix(true);

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
                buildTreeNodes(treePanel, root, 0, popup, field);
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

        private void buildTreeNodes(VBox panel, TreeNode node, int depth, Popup popup, TextField field) {
            if (node == null) return;

            HBox itemContainer = new HBox(0);
            itemContainer.setAlignment(javafx.geometry.Pos.TOP_LEFT);

            VBox rowBox = new VBox();
            rowBox.setFillWidth(true);

            HBox row = new HBox(8);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 12, 6, 12 + depth * 16));
            row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");

            if (node.hasChildren()) {
                Label arrow = new Label(node.isExpanded() ? "\u25bc" : "\u25b6");
                arrow.setStyle("-fx-font-size: 8px; -fx-text-fill: -color-fg-muted;");
                arrow.setPadding(new Insets(0, 4, 0, 0));
                row.getChildren().add(arrow);

                arrow.setOnMouseClicked(e -> {
                    node.expanded = !node.isExpanded();
                    refreshTree(panel, field);
                });
            } else {
                Label spacer = new Label(" ");
                spacer.setMinWidth(12);
                row.getChildren().add(spacer);
            }

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
                    field.setText(node.getLabel());
                    selectedNode = node;
                    
                    if (onSelect != null) {
                        onSelect.accept(node);
                    }
                    
                    popup.hide();
                });
            }

            rowBox.getChildren().add(row);

            if (node.hasChildren() && node.isExpanded()) {
                VBox childrenBox = new VBox(0);
                for (TreeNode child : node.getChildren()) {
                    buildTreeNodes(childrenBox, child, depth + 1, popup, field);
                }
                rowBox.getChildren().add(childrenBox);
            }

            itemContainer.getChildren().add(rowBox);
            panel.getChildren().add(itemContainer);
        }

        private void refreshTree(VBox panel, TextField field) {
            panel.getChildren().clear();
            if (root != null) {
                buildTreeNodes(panel, root, 0, null, field);
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
