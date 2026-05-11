package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Anchor Component
 * Inspired by Ant Design Anchor
 * Used for page navigation with anchor links.
 */
public class AnchorAnt {

    public enum Direction {
        VERTICAL, HORIZONTAL
    }

    public static class AnchorItem {
        private final String key;
        private final String title;
        private final String href;
        private final List<AnchorItem> children;

        public AnchorItem(String key, String title, String href) {
            this.key = key;
            this.title = title;
            this.href = href;
            this.children = new ArrayList<>();
        }

        public AnchorItem(String key, String title, String href, List<AnchorItem> children) {
            this.key = key;
            this.title = title;
            this.href = href;
            this.children = children != null ? children : new ArrayList<>();
        }

        public String getKey() { return key; }
        public String getTitle() { return title; }
        public String getHref() { return href; }
        public List<AnchorItem> getChildren() { return children; }
    }

    public static class Builder {
        private List<AnchorItem> items = new ArrayList<>();
        private Direction direction = Direction.VERTICAL;
        private int offsetTop = 0;
        private String activeKey = null;
        private java.util.function.Consumer<String> onChange = null;
        private boolean affix = false;
        private Color inkColor = null;

        public Builder item(String key, String title, String href) {
            this.items.add(new AnchorItem(key, title, href));
            return this;
        }

        public Builder item(String key, String title, String href, List<AnchorItem> children) {
            this.items.add(new AnchorItem(key, title, href, children));
            return this;
        }

        public Builder items(List<AnchorItem> items) {
            this.items = items;
            return this;
        }

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder offsetTop(int offsetTop) {
            this.offsetTop = offsetTop;
            return this;
        }

        public Builder activeKey(String activeKey) {
            this.activeKey = activeKey;
            return this;
        }

        public Builder onChange(java.util.function.Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder affix(boolean affix) {
            this.affix = affix;
            return this;
        }

        public Builder inkColor(Color color) {
            this.inkColor = color;
            return this;
        }

        public VBox build() {
            VBox anchor = new VBox(4);
            anchor.getStyleClass().add("anchor");
            anchor.setPadding(new Insets(8, 12, 8, 0));

            if (direction == Direction.HORIZONTAL) {
                anchor.getStyleClass().add("anchor-horizontal");
            } else {
                anchor.getStyleClass().add("anchor-vertical");
            }

            String inkColorStr = inkColor != null ? toHex(inkColor) : "-color-accent-emphasis";

            for (AnchorItem item : items) {
                javafx.scene.Node itemNode = createItemNode(item, inkColorStr);
                anchor.getChildren().add(itemNode);

                if (!item.getChildren().isEmpty()) {
                    VBox subBox = new VBox(2);
                    subBox.setPadding(new Insets(4, 0, 4, 16));
                    for (AnchorItem child : item.getChildren()) {
                        subBox.getChildren().add(createItemNode(child, inkColorStr));
                    }
                    anchor.getChildren().add(subBox);
                }
            }

            return anchor;
        }

        private javafx.scene.Node createItemNode(AnchorItem item, String inkColorStr) {
            boolean isActive = activeKey != null && activeKey.equals(item.getKey());

            Label label = new Label(item.getTitle());
            label.getStyleClass().add("anchor-link");
            label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4px 8px;" +
                (isActive
                    ? "-fx-text-fill: " + inkColorStr + "; -fx-font-weight: 600; -fx-border-color: transparent transparent transparent " + inkColorStr + "; -fx-border-width: 0 0 0 2px;"
                    : "-fx-text-fill: -color-fg-muted;")
            );

            label.setOnMouseClicked(e -> {
                if (onChange != null) {
                    onChange.accept(item.getKey());
                }
            });

            label.setOnMouseEntered(e -> {
                if (!isActive) {
                    label.setStyle(label.getStyle().replace("-fx-text-fill: -color-fg-muted;", "-fx-text-fill: -color-fg-default;"));
                }
            });

            label.setOnMouseExited(e -> {
                if (!isActive) {
                    label.setStyle(label.getStyle().replace("-fx-text-fill: -color-fg-default;", "-fx-text-fill: -color-fg-muted;"));
                }
            });

            return label;
        }

        private String toHex(Color color) {
            return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
