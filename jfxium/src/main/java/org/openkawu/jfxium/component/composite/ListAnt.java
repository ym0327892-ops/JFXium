package org.openkawu.jfxium.component.composite;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 列表组件 - 对标 Ant Design List。
 *
 * <p><b>定位</b>：通用列表容器，支持头像 + 标题 + 描述 + 操作按钮的列表项，
 * 常用于用户列表、消息列表、商品列表等场景。视觉走 LESS。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>列表项</b>：支持 avatar / title / description / action 完整配置</li>
 *   <li><b>头部/底部</b>：header(Node) / footer(Node)</li>
 *   <li><b>可点击</b>：onClick(Runnable) 启用 hover 高亮效果</li>
 *   <li><b>分隔线</b>：split(true) 显示项间分隔线</li>
 *   <li><b>边框</b>：bordered(true) 启用外边框</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox list = ListAnt.create()
 *     .header(new Label("团队成员"))
 *     .bordered(true)
 *     .item(avatarImg, "张三", "前端工程师", editBtn, () -> showDetail())
 *     .item(avatarImg, "李四", "后端工程师", editBtn, () -> showDetail())
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

    public static class Builder extends AbstractStyleBuilder<Builder> {
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
            list.getStyleClass().add(JfxStyles.LIST);
            if (bordered) list.getStyleClass().add(JfxStyles.LIST_BORDERED);
            if (loading) list.getStyleClass().add(JfxStyles.LIST_LOADING);

            if (header != null) {
                Label headerLabel = new Label(header);
                headerLabel.getStyleClass().add(JfxStyles.LIST_HEADER);
                list.getChildren().add(headerLabel);
            }

            for (int i = 0; i < items.size(); i++) {
                ListItem item = items.get(i);

                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getStyleClass().add(JfxStyles.LIST_ITEM);
                if (item.getOnClick() != null) {
                    row.getStyleClass().add(JfxStyles.LIST_ITEM_CLICKABLE);
                    row.setOnMouseClicked(e -> item.getOnClick().run());
                }

                if (item.getAvatar() != null) {
                    row.getChildren().add(item.getAvatar());
                }

                VBox content = new VBox(4);
                content.setAlignment(Pos.CENTER_LEFT);
                Label titleLabel = new Label(item.getTitle());
                titleLabel.getStyleClass().add(JfxStyles.LIST_ITEM_TITLE);
                content.getChildren().add(titleLabel);
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    Label descLabel = new Label(item.getDescription());
                    descLabel.getStyleClass().add(JfxStyles.LIST_ITEM_DESCRIPTION);
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
                    divider.getStyleClass().add(JfxStyles.LIST_DIVIDER);
                    // 分隔线左右留出与 item 一致的内边距，结构性 padding 保留 inline
                    divider.setPadding(new Insets(0, 24, 0, 24));
                    list.getChildren().add(divider);
                }
            }

            if (footer != null) {
                Label footerLabel = new Label(footer);
                footerLabel.getStyleClass().add(JfxStyles.LIST_FOOTER);
                list.getChildren().add(footerLabel);
            }
            return list;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
