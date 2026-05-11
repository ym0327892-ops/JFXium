package org.openkawu.jfxium.component;

import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium 高级侧边菜单组件
 * Inspired by Ant Design Menu
 *
 * 特性：
 * - 支持折叠/展开子菜单
 * - 左侧图标 + 文字 + 右侧箭头
 * - 子菜单嵌套
 * - 选中高亮
 * - 点击回调
 *
 * 使用示例：
 * <pre>{@code
 * VBox menu = MenuAnt.create()
 *     .item("首页", IconAnt.symbol(IconAnt.Symbol.HOME), () -> showHome())
 *     .subMenu("通用")
 *         .item("Button", () -> showButton())
 *         .item("Input", () -> showInput())
 *         .endSubMenu()
 *     .subMenu("布局")
 *         .item("Card", () -> showCard())
 *         .item("Form", () -> showForm())
 *         .subMenu("子菜单")
 *             .item("子项1", () -> {})
 *             .item("子项2", () -> {})
 *             .endSubMenu()
 *         .endSubMenu()
 *     .build();
 * }</pre>
 */
public class MenuAnt {

    private static final String ARROW_RIGHT = "M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z";
    private static final String ARROW_DOWN = "M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z";

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private final List<MenuItem> items = new ArrayList<>();
        private SubMenuBuilder currentSubMenu = null;

        private Builder() {}

        public Builder item(String text, Runnable onClick) {
            return item(text, null, onClick);
        }

        public Builder item(String text, Node icon, Runnable onClick) {
            if (currentSubMenu != null) {
                currentSubMenu.item(text, icon, onClick);
            } else {
                items.add(new MenuItem(text, icon, onClick, 0));
            }
            return this;
        }

        public SubMenuBuilder subMenu(String text) {
            return subMenu(text, null);
        }

        public SubMenuBuilder subMenu(String text, Node icon) {
            SubMenuBuilder sub = new SubMenuBuilder(text, icon, 0, this);
            if (currentSubMenu != null) {
                currentSubMenu.addSubMenu(sub);
            } else {
                items.add(sub);
            }
            currentSubMenu = sub;
            return sub;
        }

        /**
         * 分组标题 - 对标 Ant Design Menu.ItemGroup
         */
        public Builder group(String title) {
            if (currentSubMenu != null) {
                currentSubMenu.addGroup(title);
            } else {
                items.add(new MenuGroup(title, 0));
            }
            return this;
        }

        /**
         * 分割线 - 对标 Ant Design Menu.Divider
         */
        public Builder divider() {
            if (currentSubMenu != null) {
                currentSubMenu.addDivider();
            } else {
                items.add(new MenuDivider(0));
            }
            return this;
        }

        void endSubMenu(SubMenuBuilder subMenu) {
            if (currentSubMenu == subMenu) {
                currentSubMenu = subMenu.parent;
            }
        }

        public VBox build() {
            VBox menu = new VBox(0);
            menu.getStyleClass().add("menu");
            menu.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: transparent -color-border-default transparent transparent;" +
                "-fx-border-width: 0 1px 0 0;"
            );

            for (MenuItem item : items) {
                menu.getChildren().add(item.build());
            }

            return menu;
        }
    }

    public static class SubMenuBuilder extends MenuItem {
        private final List<MenuItem> children = new ArrayList<>();
        private final Builder rootBuilder;
        private final SubMenuBuilder parent;
        private boolean expanded = false;

        SubMenuBuilder(String text, Node icon, int level, Builder rootBuilder) {
            this(text, icon, level, rootBuilder, null);
        }

        SubMenuBuilder(String text, Node icon, int level, Builder rootBuilder, SubMenuBuilder parent) {
            super(text, icon, null, level);
            this.rootBuilder = rootBuilder;
            this.parent = parent;
        }

        public SubMenuBuilder item(String text, Runnable onClick) {
            return item(text, null, onClick);
        }

        public SubMenuBuilder item(String text, Node icon, Runnable onClick) {
            children.add(new MenuItem(text, icon, onClick, level + 1));
            return this;
        }

        public SubMenuBuilder subMenu(String text) {
            return subMenu(text, null);
        }

        public SubMenuBuilder subMenu(String text, Node icon) {
            SubMenuBuilder sub = new SubMenuBuilder(text, icon, level + 1, rootBuilder, this);
            children.add(sub);
            rootBuilder.currentSubMenu = sub;
            return sub;
        }

        void addSubMenu(SubMenuBuilder subMenu) {
            children.add(subMenu);
        }

        void addGroup(String title) {
            children.add(new MenuGroup(title, level + 1));
        }

        void addDivider() {
            children.add(new MenuDivider(level + 1));
        }

        public Builder endSubMenu() {
            rootBuilder.endSubMenu(this);
            return rootBuilder;
        }

        @Override
        VBox build() {
            VBox container = new VBox(0);

            // Header row
            HBox header = createHeaderRow();
            container.getChildren().add(header);

            // Children container
            VBox childrenContainer = new VBox(0);
            childrenContainer.setStyle("-fx-background-color: -color-bg-subtle;");
            for (MenuItem child : children) {
                childrenContainer.getChildren().add(child.build());
            }
            childrenContainer.setVisible(false);
            childrenContainer.setManaged(false);
            container.getChildren().add(childrenContainer);

            // Arrow icon
            SVGPath arrow = new SVGPath();
            arrow.setContent(ARROW_RIGHT);
            arrow.setStyle("-fx-fill: -color-fg-muted;");
            arrow.setScaleX(0.8);
            arrow.setScaleY(0.8);

            // Add arrow to header (right-aligned)
            HBox arrowContainer = new HBox(arrow);
            arrowContainer.setAlignment(Pos.CENTER_RIGHT);
            HBox.setHgrow(arrowContainer, Priority.NEVER);
            header.getChildren().add(arrowContainer);

            // Click to toggle
            header.setOnMouseClicked(e -> {
                expanded = !expanded;
                childrenContainer.setVisible(expanded);
                childrenContainer.setManaged(expanded);

                RotateTransition rotate = new RotateTransition(Duration.millis(200), arrow);
                rotate.setToAngle(expanded ? 90 : 0);
                rotate.play();
            });

            return container;
        }

        private HBox createHeaderRow() {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16 + level * 16));
            row.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: transparent;"
            );

            // Hover effect
            row.setOnMouseEntered(e -> row.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: -color-bg-subtle;"
            ));
            row.setOnMouseExited(e -> row.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: transparent;"
            ));

            // Icon
            if (icon != null) {
                row.getChildren().add(icon);
            }

            // Text
            Label label = new Label(text);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default;");
            HBox.setHgrow(label, Priority.ALWAYS);
            row.getChildren().add(label);

            return row;
        }
    }

    static class MenuItem {
        protected final String text;
        protected final Node icon;
        protected final Runnable onClick;
        protected final int level;

        MenuItem(String text, Node icon, Runnable onClick, int level) {
            this.text = text;
            this.icon = icon;
            this.onClick = onClick;
            this.level = level;
        }

        VBox build() {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 16, 10, 16 + level * 16));
            row.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: transparent;"
            );

            // Hover effect
            row.setOnMouseEntered(e -> row.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: -color-accent-subtle;"
            ));
            row.setOnMouseExited(e -> row.setStyle(
                "-fx-cursor: hand;" +
                "-fx-background-color: transparent;"
            ));

            // Click
            if (onClick != null) {
                row.setOnMouseClicked(e -> onClick.run());
            }

            // Icon
            if (icon != null) {
                row.getChildren().add(icon);
            }

            // Text
            Label label = new Label(text);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default;");
            row.getChildren().add(label);

            VBox container = new VBox(0);
            container.getChildren().add(row);
            return container;
        }
    }

    /**
     * 分组标题 - 对标 Ant Design Menu.ItemGroup
     */
    static class MenuGroup extends MenuItem {
        MenuGroup(String title, int level) {
            super(title, null, null, level);
        }

        @Override
        VBox build() {
            Label label = new Label(text);
            label.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: -color-fg-muted;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 16px 16px 8px " + (16 + level * 16) + "px;"
            );
            VBox container = new VBox(0);
            container.getChildren().add(label);
            return container;
        }
    }

    /**
     * 分割线 - 对标 Ant Design Menu.Divider
     */
    static class MenuDivider extends MenuItem {
        MenuDivider(int level) {
            super("", null, null, level);
        }

        @Override
        VBox build() {
            javafx.scene.layout.Region line = new javafx.scene.layout.Region();
            line.setStyle(
                "-fx-background-color: -color-border-muted;" +
                "-fx-min-height: 1px;" +
                "-fx-pref-height: 1px;" +
                "-fx-max-height: 1px;"
            );
            line.setPadding(new Insets(8, 16, 8, 16 + level * 16));
            VBox container = new VBox(0);
            container.getChildren().add(line);
            return container;
        }
    }
}
