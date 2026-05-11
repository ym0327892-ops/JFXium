package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Breadcrumb Component
 * Inspired by Ant Design Breadcrumb
 * A navigation aid to help users understand where they are in the application.
 */
public class BreadcrumbAnt {

    public static class Item {
        String title;
        String href;
        Consumer<Item> onClick;

        public Item(String title) {
            this.title = title;
        }

        public Item(String title, String href) {
            this.title = title;
            this.href = href;
        }

        public Item(String title, Consumer<Item> onClick) {
            this.title = title;
            this.onClick = onClick;
        }
    }

    public static class Builder {
        private List<Item> items = new ArrayList<>();
        private String separator = "/";

        public Builder separator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder item(String title) {
            items.add(new Item(title));
            return this;
        }

        public Builder item(String title, String href) {
            items.add(new Item(title, href));
            return this;
        }

        public Builder item(String title, Consumer<Item> onClick) {
            items.add(new Item(title, onClick));
            return this;
        }

        public Builder items(String... titles) {
            for (String title : titles) {
                items.add(new Item(title));
            }
            return this;
        }

        public HBox build() {
            HBox breadcrumb = new HBox(4);
            breadcrumb.getStyleClass().add("breadcrumb");
            breadcrumb.setAlignment(Pos.CENTER_LEFT);
            breadcrumb.setStyle("-fx-padding: 8px 0;");

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                boolean isLast = i == items.size() - 1;

                // Item
                if (isLast) {
                    Label lastLabel = new Label(item.title);
                    lastLabel.getStyleClass().add("breadcrumb-item");
                    lastLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px;");
                    breadcrumb.getChildren().add(lastLabel);
                } else {
                    if (item.onClick != null) {
                        Hyperlink link = new Hyperlink(item.title);
                        link.getStyleClass().add("breadcrumb-link");
                        link.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-padding: 0 4px;");
                        link.setOnAction(e -> item.onClick.accept(item));
                        link.setOnMouseEntered(e -> link.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-padding: 0 4px; -fx-background-color: -color-bg-subtle; -fx-background-radius: 4px;"));
                        link.setOnMouseExited(e -> link.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-padding: 0 4px;"));
                        breadcrumb.getChildren().add(link);
                    } else if (item.href != null) {
                        Hyperlink link = new Hyperlink(item.title);
                        link.getStyleClass().add("breadcrumb-link");
                        link.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-padding: 0 4px;");
                        link.setOnMouseEntered(e -> link.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-padding: 0 4px; -fx-background-color: -color-bg-subtle; -fx-background-radius: 4px;"));
                        link.setOnMouseExited(e -> link.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-padding: 0 4px;"));
                        breadcrumb.getChildren().add(link);
                    } else {
                        Label label = new Label(item.title);
                        label.getStyleClass().add("breadcrumb-item");
                        label.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-padding: 0 4px;");
                        breadcrumb.getChildren().add(label);
                    }

                    // Separator
                    Label sepLabel = new Label(separator);
                    sepLabel.getStyleClass().add("breadcrumb-separator");
                    sepLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-padding: 0 4px;");
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
