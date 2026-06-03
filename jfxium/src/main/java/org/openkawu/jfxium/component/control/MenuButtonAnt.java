package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;
import org.openkawu.jfxium.component.overlay.DropdownAnt;

/**
 * JFXium 菜单按钮组件（M19.6）— 包装 JavaFX {@link MenuButton}。
 *
 * <p><b>定位</b>：外观像普通按钮，点击后弹出下拉菜单。在一组动作中做出选择。
 * 与 {@link DropdownAnt}（trigger + Popup 弹层）和 {@link ButtonAnt}（一次性触发）严格区分。</p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>admin 列表页"批量操作 ▾"按钮（导出 / 删除 / 移动）</li>
 *   <li>编辑器"插入 ▾"按钮（图片 / 链接 / 表格）</li>
 *   <li>标题栏"用户菜单 ▾"</li>
 * </ul>
 *
 * <h2>API 用法</h2>
 * <pre>{@code
 * MenuButton actions = MenuButtonAnt.create("批量操作")
 *     .item("导出", e -> exportSelected())
 *     .item("删除", e -> deleteSelected())
 *     .separator()
 *     .item("移动到...", e -> moveSelected())
 *     .build();
 *
 * MenuButton withIcon = MenuButtonAnt.create("更多")
 *     .icon(IconAnt.path(IconAnt.Path.MORE, 16))
 *     .size(MenuButtonAnt.Size.SMALL)
 *     .item("设置", e -> openSettings())
 *     .item("帮助", e -> openHelp())
 *     .build();
 * }</pre>
 *
 * <h2>设计要点</h2>
 * <ul>
 *   <li>API 与 ButtonAnt 镜像（size / shape / icon / disabled）</li>
 *   <li>菜单项用 fluent {@code item / separator} 链式添加</li>
 *   <li>样式走 LESS {@code .menu-button} 系列（已有完整规则）</li>
 * </ul>
 */
public class MenuButtonAnt {

    /** 与 ButtonAnt 一致的尺寸枚举。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    /**
     * 箭头样式（M19.6.1）。
     * <ul>
     *   <li>{@link #CHEVRON} —— Ant Design 风格细 V 形（默认）</li>
     *   <li>{@link #TRIANGLE} —— 实心三角形（AtlantaFX 风格）</li>
     *   <li>{@link #NONE} —— 完全不显示箭头</li>
     * </ul>
     */
    public enum ArrowStyle {
        CHEVRON,
        TRIANGLE,
        NONE
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private Size size = Size.DEFAULT;
        private ArrowStyle arrowStyle = ArrowStyle.CHEVRON;
        private boolean disabled = false;
        private boolean rounded = false;
        private boolean square = false;
        private Node icon;
        private ContentDisplay contentDisplay = ContentDisplay.LEFT;
        private final List<MenuItem> items = new ArrayList<>();

        private Builder(String text) {
            this.text = text;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        /** 箭头样式（M19.6.1，默认 CHEVRON）。 */
        public Builder arrowStyle(ArrowStyle style) {
            this.arrowStyle = style;
            return this;
        }

        /** 不显示下拉箭头（语法糖，等价 {@code arrowStyle(ArrowStyle.NONE)}）。 */
        public Builder noArrow() {
            this.arrowStyle = ArrowStyle.NONE;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder rounded() {
            this.rounded = true;
            this.square = false;
            return this;
        }

        public Builder square() {
            this.square = true;
            this.rounded = false;
            return this;
        }

        public Builder icon(Node icon) {
            this.icon = icon;
            return this;
        }

        public Builder contentDisplay(ContentDisplay display) {
            this.contentDisplay = display;
            return this;
        }

        /** 添加菜单项。 */
        public Builder item(String label, EventHandler<ActionEvent> onClick) {
            MenuItem mi = new MenuItem(label);
            if (onClick != null) {
                mi.setOnAction(onClick);
            }
            items.add(mi);
            return this;
        }

        /** 添加带图标的菜单项。 */
        public Builder item(String label, Node icon, EventHandler<ActionEvent> onClick) {
            MenuItem mi = new MenuItem(label, icon);
            if (onClick != null) {
                mi.setOnAction(onClick);
            }
            items.add(mi);
            return this;
        }

        /** 添加禁用菜单项。 */
        public Builder itemDisabled(String label) {
            MenuItem mi = new MenuItem(label);
            mi.setDisable(true);
            items.add(mi);
            return this;
        }

        /** 添加分隔线。 */
        public Builder separator() {
            items.add(new SeparatorMenuItem());
            return this;
        }

        /** 直接添加原生 MenuItem（高级用法，自定义图标/快捷键等）。 */
        public Builder add(MenuItem item) {
            if (item != null) {
                items.add(item);
            }
            return this;
        }

        public MenuButton build() {
            MenuButton btn = new MenuButton(text);
            btn.getItems().addAll(items);
            btn.setDisable(disabled);

            // Size
            if (size == Size.SMALL) {
                btn.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                btn.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Shape
            if (rounded) {
                btn.getStyleClass().add(CssClasses.SHAPE_ROUNDED);
            } else if (square) {
                btn.getStyleClass().add(CssClasses.SHAPE_SQUARE);
            }

            // Arrow style（M19.6.1）
            switch (arrowStyle) {
                case TRIANGLE -> btn.getStyleClass().add("arrow-triangle");
                case NONE -> btn.getStyleClass().add("no-arrow");
                case CHEVRON -> { /* 默认，无需额外 styleClass */ }
            }

            // Icon
            if (icon != null) {
                btn.setGraphic(icon);
                btn.setContentDisplay(contentDisplay);
            }

            btn.setFocusTraversable(true);
            btn.getStyleClass().add("jfx-menu-button");
            applyStyles(btn);
            return btn;
        }
    }
}
