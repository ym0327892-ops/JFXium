package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * JFXium 页面布局组件 - 对标 Ant Design Layout
 *
 * 提供经典的页面布局结构：Header（顶部）、Sider（侧边栏）、Content（内容区）、Footer（底部）
 *
 * 使用示例：
 * <pre>{@code
 * // 基础布局：Header + Content + Footer
 * BorderPane layout = LayoutAnt.create()
 *     .header(new Label("顶部导航"))
 *     .content(new Label("主要内容"))
 *     .footer(new Label("底部版权"))
 *     .build();
 *
 * // 带侧边栏的布局
 * BorderPane layout = LayoutAnt.create()
 *     .sider(new Label("侧边菜单"), 200)
 *     .header(new Label("顶部"))
 *     .content(new Label("内容"))
 *     .build();
 * }</pre>
 */
public class LayoutAnt {

    public static class Builder {
        private Node header;
        private Node sider;
        private double siderWidth = 200;
        private Node content;
        private Node footer;
        private boolean hasSider = false;

        /**
         * 设置顶部区域
         */
        public Builder header(Node header) {
            this.header = header;
            return this;
        }

        /**
         * 设置侧边栏
         * @param sider 侧边栏内容
         * @param width 侧边栏宽度（默认 200）
         */
        public Builder sider(Node sider, double width) {
            this.sider = sider;
            this.siderWidth = width;
            this.hasSider = true;
            return this;
        }

        public Builder sider(Node sider) {
            return sider(sider, 200);
        }

        /**
         * 设置内容区域
         */
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        /**
         * 设置底部区域
         */
        public Builder footer(Node footer) {
            this.footer = footer;
            return this;
        }

        public BorderPane build() {
            BorderPane layout = new BorderPane();
            layout.getStyleClass().add("layout");

            if (header != null) {
                header.getStyleClass().add("layout-header");
                layout.setTop(header);
            }

            if (footer != null) {
                footer.getStyleClass().add("layout-footer");
                layout.setBottom(footer);
            }

            if (hasSider && sider != null) {
                sider.getStyleClass().add("layout-sider");
                HBox centerArea = new HBox();
                centerArea.getChildren().add(sider);
                if (content != null) {
                    content.getStyleClass().add("layout-content");
                    centerArea.getChildren().add(content);
                    HBox.setHgrow(content, javafx.scene.layout.Priority.ALWAYS);
                }
                layout.setCenter(centerArea);
            } else {
                if (content != null) {
                    content.getStyleClass().add("layout-content");
                    layout.setCenter(content);
                }
            }

            return layout;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
