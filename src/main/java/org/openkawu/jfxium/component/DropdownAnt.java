package org.openkawu.jfxium.component;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * DropdownAnt Component
 * Inspired by Ant Design Dropdown
 * Used to display a dropdown menu when triggered.
 */
public class DropdownAnt {

    public static class MenuItem {
        private final String key;
        private final String label;
        private final Node icon;
        private final boolean disabled;
        private final boolean divider;

        public MenuItem(String key, String label) {
            this.key = key;
            this.label = label;
            this.icon = null;
            this.disabled = false;
            this.divider = false;
        }

        public MenuItem(String key, String label, Node icon) {
            this.key = key;
            this.label = label;
            this.icon = icon;
            this.disabled = false;
            this.divider = false;
        }

        public MenuItem(String key, String label, boolean disabled) {
            this.key = key;
            this.label = label;
            this.icon = null;
            this.disabled = disabled;
            this.divider = false;
        }

        private MenuItem(boolean divider) {
            this.key = "";
            this.label = "";
            this.icon = null;
            this.disabled = false;
            this.divider = divider;
        }

        public static MenuItem divider() {
            return new MenuItem(true);
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public Node getIcon() { return icon; }
        public boolean isDisabled() { return disabled; }
        public boolean isDivider() { return divider; }
    }

    public static class Builder {
        private Node trigger;
        private List<MenuItem> items = new ArrayList<>();
        private Consumer<String> onSelect = null;
        private boolean disabled = false;
        private String placement = "bottomLeft";

        public Builder trigger(Node trigger) {
            this.trigger = trigger;
            return this;
        }

        public Builder item(String key, String label) {
            this.items.add(new MenuItem(key, label));
            return this;
        }

        public Builder item(String key, String label, Node icon) {
            this.items.add(new MenuItem(key, label, icon));
            return this;
        }

        public Builder item(String key, String label, boolean disabled) {
            this.items.add(new MenuItem(key, label, disabled));
            return this;
        }

        public Builder divider() {
            this.items.add(MenuItem.divider());
            return this;
        }

        public Builder items(List<MenuItem> items) {
            this.items = items;
            return this;
        }

        public Builder onSelect(Consumer<String> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder placement(String placement) {
            this.placement = placement;
            return this;
        }

        public Node build() {
            if (trigger == null) {
                throw new IllegalStateException("Trigger node is required");
            }

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            VBox menu = new VBox(0);
            menu.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 8px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);" +
                "-fx-min-width: 160px;"
            );
            menu.setPadding(new Insets(4, 0, 4, 0));

            for (MenuItem item : items) {
                if (item.isDivider()) {
                    javafx.scene.layout.Region div = new javafx.scene.layout.Region();
                    div.setStyle("-fx-background-color: -color-border-default; -fx-min-height: 1px; -fx-pref-height: 1px;");
                    div.setPadding(new Insets(4, 8, 4, 8));
                    menu.getChildren().add(div);
                } else {
                    HBox menuItem = new HBox(8);
                    menuItem.setAlignment(Pos.CENTER_LEFT);
                    menuItem.setPadding(new Insets(8, 12, 8, 12));
                    menuItem.setStyle(
                        "-fx-cursor: " + (item.isDisabled() ? "default" : "hand") + ";" +
                        "-fx-background-color: transparent;"
                    );

                    if (item.getIcon() != null) {
                        menuItem.getChildren().add(item.getIcon());
                    }

                    Label label = new Label(item.getLabel());
                    label.setStyle(
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + (item.isDisabled() ? "-color-fg-subtle" : "-color-fg-default") + ";"
                    );
                    menuItem.getChildren().add(label);

                    if (!item.isDisabled()) {
                        menuItem.setOnMouseEntered(e -> {
                            menuItem.setStyle("-fx-cursor: hand; -fx-background-color: -color-bg-subtle;");
                        });
                        menuItem.setOnMouseExited(e -> {
                            menuItem.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");
                        });
                        menuItem.setOnMouseClicked(e -> {
                            popup.hide();
                            if (onSelect != null) {
                                onSelect.accept(item.getKey());
                            }
                        });
                    }

                    menu.getChildren().add(menuItem);
                }
            }

            popup.getContent().add(menu);

            trigger.setOnMouseClicked(e -> {
                if (!disabled) {
                    if (popup.isShowing()) {
                        popup.hide();
                    } else {
                        Bounds bounds = trigger.localToScreen(trigger.getBoundsInLocal());
                        double x = bounds.getMinX();
                        double y = bounds.getMaxY() + 4;

                        if (placement.contains("Right")) {
                            x = bounds.getMaxX() - 160;
                        }
                        if (placement.contains("top")) {
                            y = bounds.getMinY() - menu.getHeight() - 4;
                        }

                        popup.show(trigger, x, y);
                    }
                }
            });

            return trigger;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
