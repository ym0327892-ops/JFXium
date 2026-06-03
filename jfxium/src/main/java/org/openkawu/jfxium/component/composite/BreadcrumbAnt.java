package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Breadcrumb - 对标 Ant Design Breadcrumb。
 *
 * 重构：breadcrumb 容器 / item / link / 分隔符全部走 LESS（{@code breadcrumb-*}），
 * link hover 由 LESS 伪类控制，不再用 setOnMouseEntered/Exited 拼字符串。
 * 最后一项通过 {@link CssClasses#BREADCRUMB_LAST} 修饰类高亮当前位置。
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

    public static class Builder {
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
            breadcrumb.getStyleClass().add(CssClasses.BREADCRUMB);

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                boolean isLast = i == items.size() - 1;

                if (isLast) {
                    Label lastLabel = new Label(item.title);
                    lastLabel.getStyleClass().addAll(CssClasses.BREADCRUMB_ITEM, CssClasses.BREADCRUMB_LAST);
                    breadcrumb.getChildren().add(lastLabel);
                } else {
                    if (item.onClick != null) {
                        Hyperlink link = new Hyperlink(item.title);
                        link.getStyleClass().add(CssClasses.BREADCRUMB_LINK);
                        link.setOnAction(e -> item.onClick.accept(item));
                        breadcrumb.getChildren().add(link);
                    } else if (item.href != null) {
                        Hyperlink link = new Hyperlink(item.title);
                        link.getStyleClass().add(CssClasses.BREADCRUMB_LINK);
                        breadcrumb.getChildren().add(link);
                    } else {
                        Label label = new Label(item.title);
                        label.getStyleClass().add(CssClasses.BREADCRUMB_ITEM);
                        breadcrumb.getChildren().add(label);
                    }

                    Label sepLabel = new Label(separator);
                    sepLabel.getStyleClass().add(CssClasses.BREADCRUMB_SEPARATOR);
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
