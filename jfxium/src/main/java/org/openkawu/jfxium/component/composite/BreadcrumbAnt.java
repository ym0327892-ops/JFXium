package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 面包屑导航组件 - 对标 Ant Design Breadcrumb。
 *
 * <p><b>定位</b>：显示当前页面在导航层级中的位置，常用于顶部导航路径展示。
 * 最后一项自动高亮为当前位置。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>导航项</b>：item(title) 纯文本 / item(title, href, onClick) 可点击链接</li>
 *   <li><b>分隔符</b>：默认 "/"，可自定义</li>
 *   <li><b>自动高亮</b>：最后一项通过 CSS 修饰类高亮</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * HBox breadcrumb = BreadcrumbAnt.create()
 *     .item("首页", "/", item -> navigateHome())
 *     .item("用户管理", "/users", item -> navigateUsers())
 *     .item("张三")  // 当前页，不可点击
 *     .build();
 * }</pre>
 */
public class BreadcrumbAnt {

    public static class Item {
        String title;
        String href;
        Consumer<Item> onClick;

        public Item(String title) { this.title = title; }
        public Item(String title, String href) { this.title = title; this.href = href; }
        public Item(String title, Consumer<Item> onClick) { this.title = title; this.onClick = onClick; }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<Item> items = new ArrayList<>();
        private String separator = "/";

        public Builder separator(String separator) { this.separator = separator; return this; }

        public Builder item(String title) { items.add(new Item(title)); return this; }
        public Builder item(String title, String href) { items.add(new Item(title, href)); return this; }
        public Builder item(String title, Consumer<Item> onClick) { items.add(new Item(title, onClick)); return this; }

        public Builder items(String... titles) {
            for (String title : titles) {
                items.add(new Item(title));
            }
            return this;
        }

        public HBox build() {
            HBox breadcrumb = new HBox(4);
            breadcrumb.setAlignment(Pos.CENTER_LEFT);
            breadcrumb.getStyleClass().add(JfxStyles.BREADCRUMB);

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                boolean isLast = i == items.size() - 1;

                if (isLast) {
                    Label lastLabel = new Label(item.title);
                    lastLabel.getStyleClass().addAll(JfxStyles.BREADCRUMB_ITEM, JfxStyles.BREADCRUMB_LAST);
                    breadcrumb.getChildren().add(lastLabel);
                } else {
                    if (item.onClick != null) {
                        Hyperlink link = new Hyperlink(item.title);
                        link.getStyleClass().add(JfxStyles.BREADCRUMB_LINK);
                        link.setOnAction(e -> item.onClick.accept(item));
                        breadcrumb.getChildren().add(link);
                    } else if (item.href != null) {
                        Hyperlink link = new Hyperlink(item.title);
                        link.getStyleClass().add(JfxStyles.BREADCRUMB_LINK);
                        breadcrumb.getChildren().add(link);
                    } else {
                        Label label = new Label(item.title);
                        label.getStyleClass().add(JfxStyles.BREADCRUMB_ITEM);
                        breadcrumb.getChildren().add(label);
                    }

                    Label sepLabel = new Label(separator);
                    sepLabel.getStyleClass().add(JfxStyles.BREADCRUMB_SEPARATOR);
                    breadcrumb.getChildren().add(sepLabel);
                }
            }
            return breadcrumb;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
