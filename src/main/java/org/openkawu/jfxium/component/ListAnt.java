package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 高级列表组件 - 对标 Ant Design List
 *
 * 提供丰富的列表展示功能，支持头像、标题、描述、操作等
 *
 * 使用示例：
 * <pre>{@code
 * // 基础列表
 * VBox list = ListAnt.create()
 *     .item("标题1", "描述内容1")
 *     .item("标题2", "描述内容2")
 *     .build();
 *
 * // 带头像和操作的列表
 * VBox list = ListAnt.create()
 *     .item(avatar1, "张三", "工程师", editBtn)
 *     .item(avatar2, "李四", "设计师", editBtn)
 *     .bordered(true)
 *     .build();
 *
 * // 可点击的列表
 * VBox list = ListAnt.create()
 *     .item("选项1", () -> System.out.println("点击了1"))
 *     .item("选项2", () -> System.out.println("点击了2"))
 *     .build();
 * }</pre>
 */
public class ListAnt {

    public static class ListItem {
        private final Node avatar;
        private final String title;
        private final String description;
        private final Node action;
        private final Runnable onClick;

        public ListItem(String title, String description) {
            this(null, title, description, null, null);
        }

        public ListItem(String title, Runnable onClick) {
            this(null, title, null, null, onClick);
        }

        public ListItem(Node avatar, String title, String description) {
            this(avatar, title, description, null, null);
        }

        public ListItem(Node avatar, String title, String description, Node action) {
            this(avatar, title, description, action, null);
        }

        public ListItem(Node avatar, String title, String description, Node action, Runnable onClick) {
            this.avatar = avatar;
            this.title = title;
            this.description = description;
            this.action = action;
            this.onClick = onClick;
        }

        public Node getAvatar() { return avatar; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Node getAction() { return action; }
        public Runnable getOnClick() { return onClick; }
    }

    public static class Builder {
        private List<ListItem> items = new ArrayList<>();
        private boolean bordered = false;
        private boolean split = true;
        private String header = null;
        private String footer = null;
        private boolean loading = false;

        public Builder item(String title, String description) {
            this.items.add(new ListItem(title, description));
            return this;
        }

        public Builder item(String title, Runnable onClick) {
            this.items.add(new ListItem(title, onClick));
            return this;
        }

        public Builder item(Node avatar, String title, String description) {
            this.items.add(new ListItem(avatar, title, description));
            return this;
        }

        public Builder item(Node avatar, String title, String description, Node action) {
            this.items.add(new ListItem(avatar, title, description, action));
            return this;
        }

        public Builder items(List<ListItem> items) {
            this.items = items;
            return this;
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder bordered() {
            return bordered(true);
        }

        public Builder split(boolean split) {
            this.split = split;
            return this;
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder footer(String footer) {
            this.footer = footer;
            return this;
        }

        public Builder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        public VBox build() {
            VBox list = new VBox(0);
            list.getStyleClass().add("list");

            String style = "-fx-background-color: -color-bg-default;";
            if (bordered) {
                style += "-fx-border-color: -color-border-default; -fx-border-radius: 8px; -fx-background-radius: 8px;";
            }
            list.setStyle(style);

            // Header
            if (header != null) {
                Label headerLabel = new Label(header);
                headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default; -fx-padding: 12px 24px;");
                headerLabel.getStyleClass().add("list-header");
                list.getChildren().add(headerLabel);
            }

            // Items
            for (int i = 0; i < items.size(); i++) {
                ListItem item = items.get(i);

                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(12, 24, 12, 24));
                row.getStyleClass().add("list-item");

                if (item.getOnClick() != null) {
                    row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");
                    row.setOnMouseEntered(e -> row.setStyle("-fx-cursor: hand; -fx-background-color: -color-bg-subtle;"));
                    row.setOnMouseExited(e -> row.setStyle("-fx-cursor: hand; -fx-background-color: transparent;"));
                    row.setOnMouseClicked(e -> item.getOnClick().run());
                }

                // Avatar
                if (item.getAvatar() != null) {
                    row.getChildren().add(item.getAvatar());
                }

                // Content
                VBox content = new VBox(4);
                content.setAlignment(Pos.CENTER_LEFT);

                Label titleLabel = new Label(item.getTitle());
                titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: -color-fg-default;");
                content.getChildren().add(titleLabel);

                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    Label descLabel = new Label(item.getDescription());
                    descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted;");
                    content.getChildren().add(descLabel);
                }

                row.getChildren().add(content);
                HBox.setHgrow(content, javafx.scene.layout.Priority.ALWAYS);

                // Action
                if (item.getAction() != null) {
                    row.getChildren().add(item.getAction());
                }

                list.getChildren().add(row);

                // Splitter
                if (split && i < items.size() - 1) {
                    javafx.scene.layout.Region divider = new javafx.scene.layout.Region();
                    divider.setStyle("-fx-background-color: -color-border-default; -fx-min-height: 1px; -fx-pref-height: 1px;");
                    divider.setPadding(new Insets(0, 24, 0, 24));
                    list.getChildren().add(divider);
                }
            }

            // Footer
            if (footer != null) {
                Label footerLabel = new Label(footer);
                footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted; -fx-padding: 12px 24px;");
                footerLabel.getStyleClass().add("list-footer");
                list.getChildren().add(footerLabel);
            }

            if (loading) {
                list.setStyle(list.getStyle() + "-fx-opacity: 0.6;");
            }

            return list;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
