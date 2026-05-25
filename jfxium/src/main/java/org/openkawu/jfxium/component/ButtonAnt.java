package org.openkawu.jfxium.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 按钮组件 - 对标 Ant Design Button
 *
 * 功能特性：
 * - 多种按钮类型：主按钮、默认按钮、成功/警告/危险、虚线、文字、链接
 * - 三种尺寸：小(Small)、中(Default)、大(Large)
 * - 形状变体：圆角(rounded)、方形(square)
 * - 加载状态：支持 loading 图标和禁用
 * - 图标支持：可在文字前/后添加图标
 * - 无障碍支持：支持 Tab 导航、Enter/Space 激活
 *
 * 使用示例：
 * <pre>{@code
 * // 基础用法
 * Button btn = ButtonAnt.create("点击我").build();
 *
 * // 主按钮 + 大尺寸 + 圆角
 * Button primaryBtn = ButtonAnt.create("提交")
 *     .type(ButtonAnt.Type.PRIMARY)
 *     .size(ButtonAnt.Size.LARGE)
 *     .rounded()
 *     .onClick(e -> System.out.println("点击了！"))
 *     .build();
 *
 * // 危险按钮（删除操作）
 * Button dangerBtn = ButtonAnt.create("删除")
 *     .type(ButtonAnt.Type.DANGER)
 *     .build();
 *
 * // 加载状态
 * Button loadingBtn = ButtonAnt.create("保存中...")
 *     .type(ButtonAnt.Type.PRIMARY)
 *     .loading(true)
 *     .build();
 * }</pre>
 */
public class ButtonAnt {

    public enum Type {
        DEFAULT,
        PRIMARY,
        ACCENT,
        SUCCESS,
        WARNING,
        DANGER,
        OUTLINED,
        DASHED,
        TEXT,
        LINK
    }

    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    /**
     * 运行时切换已构建按钮的 type（M19.26）。
     *
     * <p>注意：build() 之后 Builder 已不可达；想动态切色（比如「保存」按钮在「未修改/已修改」之间切换），
     * 用此静态方法即可。内部清掉旧 type 的 styleClass 再挂新 type 的 styleClass。</p>
     *
     * <pre>{@code
     * Button save = ButtonAnt.create("保存").type(Type.DEFAULT).build();
     *
     * // 用户改了表单 → 保存按钮变主题色提醒
     * editor.dirtyProperty().addListener((obs, ov, nv) ->
     *     ButtonAnt.changeType(save, nv ? Type.PRIMARY : Type.DEFAULT));
     * }</pre>
     *
     * @param button 已通过 {@link #create()} 构建的按钮
     * @param newType 目标类型；null 视为 {@link Type#DEFAULT}
     */
    public static void changeType(Button button, Type newType) {
        if (button == null) return;
        if (newType == null) newType = Type.DEFAULT;
        // 清掉所有 type 相关 styleClass（保留 size / shape / ghost / 用户自定义类）
        button.getStyleClass().removeAll(
                CssClasses.BUTTON_DEFAULT,
                CssClasses.BUTTON_PRIMARY,
                CssClasses.BUTTON_ACCENT,
                CssClasses.BUTTON_OUTLINED,
                CssClasses.BUTTON_DASHED,
                CssClasses.BUTTON_TEXT,
                CssClasses.BUTTON_LINK,
                "success", "warning", "danger"
        );
        // 重新挂上新 type 的 styleClass（与 Builder.build() 保持一致）
        switch (newType) {
            case PRIMARY, ACCENT -> button.getStyleClass().add(CssClasses.BUTTON_ACCENT);
            case SUCCESS -> button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "success");
            case WARNING -> button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "warning");
            case DANGER -> button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "danger");
            case OUTLINED -> button.getStyleClass().add(CssClasses.BUTTON_OUTLINED);
            case DASHED -> button.getStyleClass().add(CssClasses.BUTTON_DASHED);
            case TEXT -> button.getStyleClass().add(CssClasses.BUTTON_TEXT);
            case LINK -> button.getStyleClass().add(CssClasses.BUTTON_LINK);
            default -> button.getStyleClass().add(CssClasses.BUTTON_DEFAULT);
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private Type type = Type.DEFAULT;
        private Size size = Size.DEFAULT;
        private boolean rounded = false;
        private boolean square = false;
        private boolean disabled = false;
        private boolean loading = false;
        private boolean ghost = false;
        private boolean block = false;
        private Node icon;
        private Node loadingIcon;
        private ContentDisplay contentDisplay = ContentDisplay.LEFT;
        private EventHandler<ActionEvent> onClick;

        private Builder(String text) {
            this.text = text;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
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

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        /**
         * 幽灵按钮 - 背景透明，边框/文字反色
         * 对标 Ant Design ghost 属性
         */
        public Builder ghost(boolean ghost) {
            this.ghost = ghost;
            return this;
        }

        public Builder ghost() {
            return ghost(true);
        }

        /**
         * 块级按钮 - 宽度占满父容器
         * 对标 Ant Design block 属性
         */
        public Builder block(boolean block) {
            this.block = block;
            return this;
        }

        public Builder block() {
            return block(true);
        }

        public Builder icon(Node icon) {
            this.icon = icon;
            return this;
        }

        public Builder loadingIcon(Node loadingIcon) {
            this.loadingIcon = loadingIcon;
            return this;
        }

        public Builder contentDisplay(ContentDisplay display) {
            this.contentDisplay = display;
            return this;
        }

        public Builder onClick(EventHandler<ActionEvent> handler) {
            this.onClick = handler;
            return this;
        }

        public Button build() {
            Button button = new Button(text);

            // Button type
            switch (type) {
                case PRIMARY:
                case ACCENT:
                    button.getStyleClass().add(CssClasses.BUTTON_ACCENT);
                    break;
                case SUCCESS:
                    button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "success");
                    break;
                case WARNING:
                    button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "warning");
                    break;
                case DANGER:
                    button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "danger");
                    break;
                case OUTLINED:
                    button.getStyleClass().add(CssClasses.BUTTON_OUTLINED);
                    break;
                case DASHED:
                    button.getStyleClass().add(CssClasses.BUTTON_DASHED);
                    break;
                case TEXT:
                    button.getStyleClass().add(CssClasses.BUTTON_TEXT);
                    break;
                case LINK:
                    button.getStyleClass().add(CssClasses.BUTTON_LINK);
                    break;
                case DEFAULT:
                default:
                    button.getStyleClass().add(CssClasses.BUTTON_DEFAULT);
                    break;
            }

            // Button size
            if (size == Size.SMALL) {
                button.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                button.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Button shape
            if (rounded) {
                button.getStyleClass().add(CssClasses.SHAPE_ROUNDED);
            } else if (square) {
                button.getStyleClass().add(CssClasses.SHAPE_SQUARE);
            }

            // Ghost button - 背景透明，边框/文字使用主题色
            if (ghost) {
                button.getStyleClass().add("ghost");
                String ghostStyle = "-fx-background-color: transparent; -fx-border-width: 1px;";
                if (type == Type.PRIMARY || type == Type.ACCENT) {
                    ghostStyle += " -fx-border-color: -color-accent-emphasis; -fx-text-fill: -color-accent-emphasis;";
                } else if (type == Type.DANGER) {
                    ghostStyle += " -fx-border-color: -color-danger-emphasis; -fx-text-fill: -color-danger-emphasis;";
                } else if (type == Type.SUCCESS) {
                    ghostStyle += " -fx-border-color: -color-success-emphasis; -fx-text-fill: -color-success-emphasis;";
                } else if (type == Type.WARNING) {
                    ghostStyle += " -fx-border-color: -color-warning-emphasis; -fx-text-fill: -color-warning-emphasis;";
                } else {
                    ghostStyle += " -fx-border-color: -color-fg-default; -fx-text-fill: -color-fg-default;";
                }
                button.setStyle(button.getStyle() != null ? button.getStyle() + ghostStyle : ghostStyle);
            }

            // Block button - 宽度占满父容器
            if (block) {
                button.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(button, Priority.ALWAYS);
            }

            // 用户 style/styleClass：通过 AbstractStyleBuilder 的 applyStyles 应用，
            // 在内置 styleClass 和 ghost inline style 之后，便于用户覆盖
            applyStyles(button);

            // Icon handling
            if (loading && loadingIcon != null) {
                button.setGraphic(loadingIcon);
                button.setContentDisplay(contentDisplay);
                button.setDisable(true);
            } else if (icon != null) {
                button.setGraphic(icon);
                button.setContentDisplay(contentDisplay);
            }

            // Disabled state
            if (disabled) {
                button.setDisable(true);
            }

            // Accessibility
            button.setFocusTraversable(true);

            // Click handler
            if (onClick != null) {
                button.setOnAction(onClick);
            }

            // Note: Enter/Space key activation is handled by JavaFX Button natively

            return button;
        }
    }
}
