package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 高级列表 - 对标 Ant Design List。
 *
 * 重构：list 容器 / header / footer / item / 分隔线全部走 LESS（{@code list-*}），
 * hover 由 LESS {@code .list-item.list-item-clickable:hover} 控制，不再 inline 注入。
 */
public class ListAnt {

    public static class ListItem {
        private final Node avatar;
        private final String title;
        private final String description;
        private final Node action;
        private final Runnable onClick;

        public ListItem(String title, String description) { this(null, title, description, null, null); }
        public ListItem(String title, Runnable onClick) { this(null, title, null, null, onClick); }
        public ListItem(Node avatar, String title, String description) { this(avatar, title, description, null, null); }
        public ListItem(Node avatar, String title, String description, Node action) { this(avatar, title, description, action, null); }

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

        public Builder item(String title, String description) { items.add(new ListItem(title, description)); return this; }
        public Builder item(String title, Runnable onClick) { items.add(new ListItem(title, onClick)); return this; }
        public Builder item(Node avatar, String title, String description) { items.add(new ListItem(avatar, title, description)); return this; }
        public Builder item(Node avatar, String title, String description, Node action) { items.add(new ListItem(avatar, title, description, action)); return this; }
        public Builder items(List<ListItem> items) { this.items = items; return this; }
        public Builder bordered(boolean bordered) { this.bordered = bordered; return this; }
        public Builder bordered() { return bordered(true); }
        public Builder split(boolean split) { this.split = split; return this; }
        public Builder header(String header) { this.header = header; return this; }
        public Builder footer(String footer) { this.footer = footer; return this; }
        public Builder loading(boolean loading) { this.loading = loading; return this; }

        public VBox build() {
            VBox list = new VBox(0);
            list.getStyleClass().add(CssClasses.LIST);
            if (bordered) list.getStyleClass().add(CssClasses.LIST_BORDERED);
            if (loading) list.getStyleClass().add(CssClasses.LIST_LOADING);

            if (header != null) {
                Label headerLabel = new Label(header);
                headerLabel.getStyleClass().add(CssClasses.LIST_HEADER);
                list.getChildren().add(headerLabel);
            }

            for (int i = 0; i < items.size(); i++) {
                ListItem item = items.get(i);

                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getStyleClass().add(CssClasses.LIST_ITEM);
                if (item.getOnClick() != null) {
                    row.getStyleClass().add(CssClasses.LIST_ITEM_CLICKABLE);
                    row.setOnMouseClicked(e -> item.getOnClick().run());
                }

                if (item.getAvatar() != null) {
                    row.getChildren().add(item.getAvatar());
                }

                VBox content = new VBox(4);
                content.setAlignment(Pos.CENTER_LEFT);
                Label titleLabel = new Label(item.getTitle());
                titleLabel.getStyleClass().add(CssClasses.LIST_ITEM_TITLE);
                content.getChildren().add(titleLabel);
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    Label descLabel = new Label(item.getDescription());
                    descLabel.getStyleClass().add(CssClasses.LIST_ITEM_DESCRIPTION);
                    content.getChildren().add(descLabel);
                }
                row.getChildren().add(content);
                HBox.setHgrow(content, Priority.ALWAYS);

                if (item.getAction() != null) {
                    row.getChildren().add(item.getAction());
                }

                list.getChildren().add(row);

                if (split && i < items.size() - 1) {
                    Region divider = new Region();
                    divider.getStyleClass().add(CssClasses.LIST_DIVIDER);
                    // 分隔线左右留出与 item 一致的内边距，结构性 padding 保留 inline
                    divider.setPadding(new Insets(0, 24, 0, 24));
                    list.getChildren().add(divider);
                }
            }

            if (footer != null) {
                Label footerLabel = new Label(footer);
                footerLabel.getStyleClass().add(CssClasses.LIST_FOOTER);
                list.getChildren().add(footerLabel);
            }
            return list;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
