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
 *
 * // 再编辑（响应式切换）
 * Button save = ButtonAnt.create("保存").build();
 * ButtonAnt.modify(save).type(Type.DANGER).shape(Shape.ROUNDED).ghost(true).apply();
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

    /**
     * 按钮形状（M19.29 引入枚举，让 ModifyBuilder 也能用一个值切形状）。
     *
     * <p>Builder 仍然提供 {@code rounded() / square()} 流式糖（向后兼容），
     * 内部都映射到此枚举。</p>
     */
    public enum Shape {
        /** 默认形状（无 shape 类，跟随主题默认圆角）。 */
        DEFAULT,
        /** 全圆角（pill 形状）。 */
        ROUNDED,
        /** 直角方形。 */
        SQUARE
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    /**
     * 再编辑已构建的 Button（M19.27 modify+apply 模式，M19.29 扩展 shape/ghost 暴露面）。
     *
     * <p>{@code create().build()} 之后 Builder 已不可达，但用户拿到的是原生 {@link Button}，
     * 之后想动态切类型 / 尺寸 / 形状 / ghost / 文字 等都通过 {@code modify(btn).xxx(...).apply()} 完成。
     * apply() 返回**原 Button**（不是新对象），便于链式继续用。</p>
     *
     * <pre>{@code
     * Button save = ButtonAnt.create("保存").type(Type.DEFAULT).build();
     *
     * // 表单 dirty → 切主题色提醒
     * editor.dirtyProperty().addListener((obs, ov, nv) ->
     *     ButtonAnt.modify(save).type(nv ? Type.PRIMARY : Type.DEFAULT).apply());
     *
     * // 异步加载 → 同时改文字 + 禁用
     * ButtonAnt.modify(save).text("保存中...").disabled(true).apply();
     *
     * // 切形状 + ghost 风格
     * ButtonAnt.modify(save).shape(Shape.ROUNDED).ghost(true).apply();
     * }</pre>
     *
     * @param button 已通过 {@link #create()} 构建的按钮，不能为 null
     * @return 用于链式修改的 ModifyBuilder
     */
    public static ModifyBuilder modify(Button button) {
        if (button == null) {
            throw new NullPointerException("button 不能为 null");
        }
        return new ModifyBuilder(button);
    }

    // ============================================================
    // 共享渲染原语（M19.29 抽取）
    // ============================================================
    // 设计契约：
    //   1. 每个方法做完整的「先清旧 + 按需挂新」，对全新 Button 而言 remove 是 no-op，无副作用
    //   2. 命名统一 applyXxxStyleClasses(Button, Xxx)，Builder.build() 与 ModifyBuilder.apply() 共用
    //   3. 内部不假设 button 的初始状态（可能是新建的、也可能挂着旧 styleClass）
    // ============================================================

    /**
     * 应用 type 对应的 styleClass。
     *
     * <p>调用前内部已先清掉所有可能残留的 type styleClass，调用方无需自己 remove。</p>
     */
    static void applyTypeStyleClasses(Button button, Type type) {
        removeTypeStyleClasses(button);
        switch (type) {
            case PRIMARY, ACCENT -> button.getStyleClass().add(CssClasses.BUTTON_ACCENT);
            case SUCCESS -> button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "success");
            case WARNING -> button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "warning");
            case DANGER -> button.getStyleClass().addAll(CssClasses.BUTTON_DEFAULT, "danger");
            case OUTLINED -> button.getStyleClass().add(CssClasses.BUTTON_OUTLINED);
            case DASHED -> button.getStyleClass().add(CssClasses.BUTTON_DASHED);
            case TEXT -> button.getStyleClass().add(CssClasses.BUTTON_TEXT);
            case LINK -> button.getStyleClass().add(CssClasses.BUTTON_LINK);
            case DEFAULT -> button.getStyleClass().add(CssClasses.BUTTON_DEFAULT);
            default -> button.getStyleClass().add(CssClasses.BUTTON_DEFAULT);
        }
    }

    /** 移除所有 type 相关 styleClass（保留 size / shape / ghost / 用户自定义类）。 */
    static void removeTypeStyleClasses(Button button) {
        button.getStyleClass().removeAll(
                CssClasses.BUTTON_DEFAULT,
                CssClasses.BUTTON_ACCENT,
                CssClasses.BUTTON_OUTLINED,
                CssClasses.BUTTON_DASHED,
                CssClasses.BUTTON_TEXT,
                CssClasses.BUTTON_LINK,
                "success", "warning", "danger"
        );
    }

    /** 应用 size styleClass：先清掉 small/large，再按需挂上（DEFAULT 仅清不挂）。 */
    static void applySizeStyleClasses(Button button, Size size) {
        button.getStyleClass().removeAll(CssClasses.SIZE_SMALL, CssClasses.SIZE_LARGE);
        if (size == Size.SMALL) {
            button.getStyleClass().add(CssClasses.SIZE_SMALL);
        } else if (size == Size.LARGE) {
            button.getStyleClass().add(CssClasses.SIZE_LARGE);
        }
        // Size.DEFAULT：仅清掉，不补任何 styleClass
    }

    /** 应用 shape styleClass：先清掉 rounded/square，再按需挂上（DEFAULT 仅清不挂）。 */
    static void applyShapeStyleClasses(Button button, Shape shape) {
        button.getStyleClass().removeAll(CssClasses.SHAPE_ROUNDED, CssClasses.SHAPE_SQUARE);
        if (shape == Shape.ROUNDED) {
            button.getStyleClass().add(CssClasses.SHAPE_ROUNDED);
        } else if (shape == Shape.SQUARE) {
            button.getStyleClass().add(CssClasses.SHAPE_SQUARE);
        }
        // Shape.DEFAULT：仅清掉，不补任何 styleClass
    }

    /** 应用 ghost styleClass：true 挂上、false 移除，幂等。 */
    static void applyGhostStyleClass(Button button, boolean ghost) {
        if (ghost) {
            if (!button.getStyleClass().contains(CssClasses.BUTTON_GHOST)) {
                button.getStyleClass().add(CssClasses.BUTTON_GHOST);
            }
        } else {
            button.getStyleClass().remove(CssClasses.BUTTON_GHOST);
        }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private Type type = Type.DEFAULT;
        private Size size = Size.DEFAULT;
        private Shape shape = Shape.DEFAULT;
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

        /** 设置形状（M19.29 引入，与 {@link #rounded()} / {@link #square()} 三选一）。 */
        public Builder shape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Builder rounded() {
            this.shape = Shape.ROUNDED;
            return this;
        }

        public Builder square() {
            this.shape = Shape.SQUARE;
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

            // styleClass 渲染：100% 走共享原语，与 ModifyBuilder.apply() 完全同步
            applyTypeStyleClasses(button, type);
            applySizeStyleClasses(button, size);
            applyShapeStyleClasses(button, shape);
            applyGhostStyleClass(button, ghost);

            // Block button - 宽度占满父容器（layout hint，非 styleClass）
            if (block) {
                button.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(button, Priority.ALWAYS);
            }

            // 用户 style/styleClass：通过 AbstractStyleBuilder 的 applyStyles 应用，
            // 在内置 styleClass 之后，便于用户覆盖
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

    /**
     * 已构建 Button 的再编辑入口（M19.27 引入，M19.29 扩展 shape/ghost 暴露面）。
     *
     * <p>设计原则：
     * <ul>
     *   <li>暴露「可逆 / 可重设」的属性：type / size / shape / ghost / text / disabled / loading；
     *       不暴露 onClick / icon / block / loadingIcon 等"一次性配置"——那些用原生 setter 即可
     *       （{@code btn.setOnAction(...)}），不需要框架包装。</li>
     *   <li>{@link #apply()} 返回**原 Button 实例**（不是新对象），避免误用陷阱。</li>
     *   <li>未调用的属性保持原值（不会清掉用户的 size/shape/ghost）—— sentinel 模式。</li>
     *   <li>视觉渲染 100% 走与 {@link Builder#build()} 共享的 {@code applyXxxStyleClasses(...)} 方法，
     *       保证两条路径输出完全一致。</li>
     * </ul>
     */
    public static class ModifyBuilder {
        private final Button button;
        // sentinel 模式：null / xxxSet=false 表示"未调用 setter，不动该属性"
        private Type type;
        private boolean typeSet = false;
        private Size size;
        private boolean sizeSet = false;
        private Shape shape;
        private boolean shapeSet = false;
        private String text;
        private boolean textSet = false;
        // boolean 用包装类做 sentinel：null = 未调用
        private Boolean disabled;
        private Boolean loading;
        private Boolean ghost;

        ModifyBuilder(Button button) {
            this.button = button;
        }

        public ModifyBuilder type(Type type) {
            this.type = type;
            this.typeSet = true;
            return this;
        }

        public ModifyBuilder size(Size size) {
            this.size = size;
            this.sizeSet = true;
            return this;
        }

        public ModifyBuilder shape(Shape shape) {
            this.shape = shape;
            this.shapeSet = true;
            return this;
        }

        public ModifyBuilder text(String text) {
            this.text = text;
            this.textSet = true;
            return this;
        }

        public ModifyBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public ModifyBuilder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        public ModifyBuilder ghost(boolean ghost) {
            this.ghost = ghost;
            return this;
        }

        /**
         * 应用所有修改到原 Button 上。
         *
         * @return 原 Button 实例（未克隆，便于继续链式使用）
         */
        public Button apply() {
            // styleClass 类：仅在对应 setter 被调用过时，才走共享原语刷新
            if (typeSet) {
                applyTypeStyleClasses(button, type != null ? type : Type.DEFAULT);
            }
            if (sizeSet) {
                applySizeStyleClasses(button, size != null ? size : Size.DEFAULT);
            }
            if (shapeSet) {
                applyShapeStyleClasses(button, shape != null ? shape : Shape.DEFAULT);
            }
            if (ghost != null) {
                applyGhostStyleClass(button, ghost);
            }
            // 非 styleClass 属性：直接走 JavaFX 原生 setter
            if (textSet) {
                button.setText(text);
            }
            if (disabled != null) {
                button.setDisable(disabled);
            }
            // loading 简化版：只切 disabled，不动 graphic（用户的 loadingIcon 已在 build 时绑过）
            // 框架不暴露动态切 graphic 的 API，避免和 onClick / icon 缠在一起
            if (loading != null) {
                button.setDisable(loading);
            }
            return button;
        }
    }
}
