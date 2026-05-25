package org.openkawu.jfxium.component;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Dropdown - 对标 Ant Design Dropdown。
 *
 * 重构：所有 inline {@code setStyle} 改为挂 {@link CssClasses#POPUP_MENU} 系列 styleClass，
 * hover/disabled 由 LESS 伪类控制，不再用 setOnMouseEntered/Exited 注入字符串。
 */
public class DropdownAnt {

    public static class MenuItem {
        private final String key;
        private final String label;
        private final Node icon;
        private final boolean disabled;
        private final boolean divider;

        public MenuItem(String key, String label) {
            this(key, label, null, false, false);
        }

        public MenuItem(String key, String label, Node icon) {
            this(key, label, icon, false, false);
        }

        public MenuItem(String key, String label, boolean disabled) {
            this(key, label, null, disabled, false);
        }

        private MenuItem(String key, String label, Node icon, boolean disabled, boolean divider) {
            this.key = key;
            this.label = label;
            this.icon = icon;
            this.disabled = disabled;
            this.divider = divider;
        }

        public static MenuItem divider() {
            return new MenuItem("", "", null, false, true);
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

        public DropdownResult build() {
            if (trigger == null) {
                throw new IllegalStateException("Trigger node is required");
            }
            return new DropdownResult(this);
        }
    }

    public static class DropdownResult {
        private final Builder config;
        private final Node trigger;
        private final Popup popup;
        private final VBox menu;

        DropdownResult(Builder config) {
            this.config = config;
            this.trigger = config.trigger;
            this.popup = new Popup();
            this.menu = createMenu();
            setupTrigger();
        }

        private VBox createMenu() {
            VBox menuBox = new VBox(0);
            // 走通用 popup-menu 样式（背景/边框/阴影/padding/min-width 全在 LESS）
            menuBox.getStyleClass().add(CssClasses.POPUP_MENU);

            for (MenuItem item : config.items) {
                if (item.isDivider()) {
                    Region div = new Region();
                    div.getStyleClass().add(CssClasses.POPUP_MENU_DIVIDER);
                    menuBox.getChildren().add(div);
                    continue;
                }

                HBox menuItem = new HBox(8);
                menuItem.setAlignment(Pos.CENTER_LEFT);
                menuItem.getStyleClass().add(CssClasses.POPUP_MENU_ITEM);
                if (item.isDisabled()) {
                    menuItem.getStyleClass().add(CssClasses.POPUP_MENU_ITEM_DISABLED);
                }

                if (item.getIcon() != null) {
                    menuItem.getChildren().add(item.getIcon());
                }
                Label label = new Label(item.getLabel());
                menuItem.getChildren().add(label);

                if (!item.isDisabled()) {
                    // 点击回调；hover 视觉由 LESS .jfx-popup-menu-item:hover 控制
                    menuItem.setOnMouseClicked(e -> {
                        popup.hide();
                        if (config.onSelect != null) {
                            config.onSelect.accept(item.getKey());
                        }
                    });
                }
                menuBox.getChildren().add(menuItem);
            }
            return menuBox;
        }

        private void setupTrigger() {
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.getContent().add(menu);
            trigger.setOnMouseClicked(e -> {
                if (config.disabled) return;
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    showAtTrigger();
                }
            });
        }

        private void showAtTrigger() {
            Bounds bounds = trigger.localToScreen(trigger.getBoundsInLocal());
            double x = bounds.getMinX();
            double y = bounds.getMaxY() + 4;
            if (config.placement.contains("Right")) {
                x = bounds.getMaxX() - 160;
            }
            if (config.placement.contains("top")) {
                y = bounds.getMinY() - menu.getHeight() - 4;
            }
            popup.show(trigger, x, y);
        }

        public Node getTrigger() { return trigger; }

        public void show() { showAtTrigger(); }

        public void hide() { popup.hide(); }
    }

    public static Builder create() {
        return new Builder();
    }
}
