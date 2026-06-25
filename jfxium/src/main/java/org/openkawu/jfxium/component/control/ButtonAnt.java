package org.openkawu.jfxium.component.control;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.command.Command;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.util.TextUtils;
import org.openkawu.jfxium.core.token.Size;

/**
 * JFXium 按钮组件 - 对标 Ant Design Button（继承式，M19.50 重构）。
 *
 * <p><b>定位</b>：项目最基础的交互控件，继承自 {@link Button}，
 * 跟 {@link InputAnt} / {@link CheckBoxAnt} / {@link org.openkawu.jfxium.component.layout.VBoxAnt VBoxAnt}
 * 同款「双工厂模式」。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>多种按钮类型：主按钮、默认按钮、成功/警告/危险、虚线、文字、链接</li>
 *   <li>四种尺寸：大(Large)、中(Middle)、小(Small)、超小(XS)</li>
 *   <li>形状变体：圆角(rounded)、方形(square)</li>
 *   <li>加载状态 / 幽灵按钮 / 块级按钮</li>
 *   <li>图标支持 / Command 绑定</li>
 * </ul>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂链式（build 可选）</h3>
 * <pre>{@code
 * Button btn = ButtonAnt.create("点击我").build();
 *
 * Button primaryBtn = ButtonAnt.create("提交")
 *     .type(Type.PRIMARY)
 *     .size(Size.LARGE)
 *     .rounded()
 *     .onClick(e -> System.out.println("点击了！"))
 *     .build();
 *
 * // build 后再改（继承式核心优势，取代原 modify()）
 * ButtonAnt save = ButtonAnt.create("保存");
 * save.type(Type.DANGER).shape(Shape.ROUNDED).ghost(true);
 * }</pre>
 *
 * <h3>2. 业务继承</h3>
 * <pre>{@code
 * public class PrimarySubmit extends ButtonAnt {
 *     public PrimarySubmit() {
 *         text("提交");
 *         type(Type.PRIMARY);
 *         size(Size.LARGE);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link Button} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code type()} / {@code size()} / {@code shape()} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class ButtonAnt extends Button
        implements LayoutCommon<ButtonAnt>, DisabledSupport<ButtonAnt> {

    // ============================================================
    // 枚举
    // ============================================================

    public enum Type {
        DEFAULT, PRIMARY, ACCENT, SUCCESS, WARNING, DANGER,
        OUTLINED, DASHED, TEXT, LINK
    }

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size，
    // 删除此处重复定义。调用方需 import 顶级 Size。

    public enum Shape {
        DEFAULT, ROUNDED, SQUARE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（无文本）。 */
    public static ButtonAnt create() {
        return new ButtonAnt();
    }

    /** 工厂入口（带文本）。 */
    public static ButtonAnt create(String text) {
        return new ButtonAnt(text);
    }

    /** 工厂入口（带文本 + 图标）。 */
    public static ButtonAnt create(String text, Node graphic) {
        return new ButtonAnt(text, graphic);
    }

    /**
     * 链接按钮：文字链接 + 指定尺寸。
     *
     * <p>适合工程页头、卡片操作、列表操作这类高频“小动作”。</p>
     */
    public static ButtonAnt link(String text, Size size) {
        return create(text).type(Type.LINK).size(size != null ? size : Size.DEFAULT);
    }

    /** 链接按钮（带图标）。 */
    public static ButtonAnt link(String text, Node graphic, Size size) {
        return create(text, graphic).type(Type.LINK).size(size != null ? size : Size.DEFAULT);
    }

    /** 紧凑链接按钮：文字链接 + XS 尺寸。 */
    public static ButtonAnt compactLink(String text) {
        return link(text, Size.XS);
    }

    /** 紧凑链接按钮（带图标）。 */
    public static ButtonAnt compactLink(String text, Node graphic) {
        return link(text, graphic, Size.XS);
    }

    /**
     * 图标按钮：图形独占 + XS 尺寸 + 方形。
     *
     * <p>适合页面右上角设置、工具条按钮等不需要文字的入口。</p>
     */
    public static ButtonAnt iconOnly(Node graphic) {
        return create()
                .icon(graphic)
                .contentDisplay(ContentDisplay.GRAPHIC_ONLY)
                .type(Type.TEXT)
                .size(Size.XS)
                .square();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ButtonAnt() {
        super();
        init();
    }

    public ButtonAnt(String text) {
        super(TextUtils.safeText(text));
        init();
    }

    public ButtonAnt(String text, Node graphic) {
        super(TextUtils.safeText(text), graphic);
        init();
    }

    private void init() {
        setFocusTraversable(true);
        applyTypeStyleClasses(this, Type.DEFAULT);
    }

    // ============================================================
    // 流式 API —— 类型 / 尺寸 / 形状
    // ============================================================

    /**
     * 设置按钮类型。幂等——先清旧 type styleClass，再按需挂新。
     */
    public ButtonAnt type(Type type) {
        applyTypeStyleClasses(this, type);
        return this;
    }

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT / MIDDLE 视为同一档位（均映射 SIZE_MIDDLE）。
     * 委托 {@link ApplySizeUtil#apply(javafx.scene.Node, Size, boolean)} 实现。
     */
    public ButtonAnt size(Size size) {
        return org.openkawu.jfxium.core.util.ApplySizeUtil.apply(this, size, true);
    }

    /**
     * 设置形状。幂等——先清旧 shape styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public ButtonAnt shape(Shape shape) {
        getStyleClass().removeAll(JfxStyles.SHAPE_ROUNDED, JfxStyles.SHAPE_SQUARE);
        if (shape == Shape.ROUNDED) {
            getStyleClass().add(JfxStyles.SHAPE_ROUNDED);
        } else if (shape == Shape.SQUARE) {
            getStyleClass().add(JfxStyles.SHAPE_SQUARE);
        }
        return this;
    }

    /** 圆角（pill 形状），等价于 {@code shape(Shape.ROUNDED)}。 */
    public ButtonAnt rounded() {
        return shape(Shape.ROUNDED);
    }

    /** 直角方形，等价于 {@code shape(Shape.SQUARE)}。 */
    public ButtonAnt square() {
        return shape(Shape.SQUARE);
    }

    // ============================================================
    // 流式 API —— 状态
    // ============================================================

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    /**
     * 设置加载状态：加载时禁用按钮。
     * 若之前通过 {@link #loadingIcon(Node)} 设过 loading icon，同时切换 graphic。
     */
    public ButtonAnt loading(boolean loading) {
        setDisable(loading);
        return this;
    }

    /** 幽灵按钮 - 背景透明，边框/文字反色。 */
    public ButtonAnt ghost(boolean ghost) {
        if (ghost) {
            if (!getStyleClass().contains(JfxStyles.BUTTON_GHOST)) {
                getStyleClass().add(JfxStyles.BUTTON_GHOST);
            }
        } else {
            getStyleClass().remove(JfxStyles.BUTTON_GHOST);
        }
        return this;
    }

    /** 幽灵按钮（无参便捷版）。 */
    public ButtonAnt ghost() {
        return ghost(true);
    }

    /** 块级按钮 - 宽度占满父容器。 */
    public ButtonAnt block(boolean block) {
        if (block) {
            setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(this, Priority.ALWAYS);
        } else {
            setMaxWidth(Region.USE_COMPUTED_SIZE);
            HBox.setHgrow(this, Priority.NEVER);
        }
        return this;
    }

    /** 块级按钮（无参便捷版）。 */
    public ButtonAnt block() {
        return block(true);
    }

    // ============================================================
    // 流式 API —— 图标 / 事件 / 命令
    // ============================================================

    /** 设置文本（链式包装 setText）。 */
    public ButtonAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /** 设置图标。 */
    public ButtonAnt icon(Node icon) {
        setGraphic(icon);
        return this;
    }

    /** 设置图标位置。 */
    public ButtonAnt contentDisplay(ContentDisplay display) {
        setContentDisplay(display);
        return this;
    }

    /** 设置点击事件。 */
    public ButtonAnt onClick(EventHandler<ActionEvent> handler) {
        setOnAction(handler);
        return this;
    }

    /**
     * 绑定命令：按钮点击时执行 command.execute()，按钮 disable 跟随 command.canExecuteProperty()。
     * <p>设置 command 后，onClick 会被覆盖（command 优先）。</p>
     */
    public ButtonAnt command(Command command) {
        if (command != null) {
            disableProperty().bind(command.canExecuteProperty().not());
            setOnAction(e -> command.execute());
        }
        return this;
    }

    /** Builder 模式终结调用——返回自身（向后兼容）。 */
    public ButtonAnt build() {
        return this;
    }

    // ============================================================
    // 包级工具方法（供 TableAnt 等内部组件使用）
    // ============================================================

    /**
     * 对任意 Button 应用 type styleClass（包级可见，供 TableAnt 等内部使用）。
     */
    static void applyTypeStyleClasses(Button button, Type type) {
        button.getStyleClass().removeAll(
                JfxStyles.BUTTON_DEFAULT, JfxStyles.BUTTON_ACCENT,
                JfxStyles.BUTTON_OUTLINED, JfxStyles.BUTTON_DASHED,
                JfxStyles.BUTTON_TEXT, JfxStyles.BUTTON_LINK,
                JfxStyles.BUTTON_SUCCESS, JfxStyles.BUTTON_WARNING, JfxStyles.BUTTON_DANGER
        );
        Type t = type != null ? type : Type.DEFAULT;
        switch (t) {
            case PRIMARY, ACCENT -> button.getStyleClass().add(JfxStyles.BUTTON_ACCENT);
            case SUCCESS -> button.getStyleClass().addAll(JfxStyles.BUTTON_DEFAULT, JfxStyles.BUTTON_SUCCESS);
            case WARNING -> button.getStyleClass().addAll(JfxStyles.BUTTON_DEFAULT, JfxStyles.BUTTON_WARNING);
            case DANGER -> button.getStyleClass().addAll(JfxStyles.BUTTON_DEFAULT, JfxStyles.BUTTON_DANGER);
            case OUTLINED -> button.getStyleClass().add(JfxStyles.BUTTON_OUTLINED);
            case DASHED -> button.getStyleClass().add(JfxStyles.BUTTON_DASHED);
            case TEXT -> button.getStyleClass().add(JfxStyles.BUTTON_TEXT);
            case LINK -> button.getStyleClass().add(JfxStyles.BUTTON_LINK);
            case DEFAULT -> button.getStyleClass().add(JfxStyles.BUTTON_DEFAULT);
            default -> button.getStyleClass().add(JfxStyles.BUTTON_DEFAULT);
        }
    }
}
