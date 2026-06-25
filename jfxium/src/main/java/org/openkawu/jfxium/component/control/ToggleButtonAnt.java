package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.component.layout.LayoutCommon;
import org.openkawu.jfxium.core.builder.DisabledSupport;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.ApplySizeUtil;
import org.openkawu.jfxium.core.util.Bindings;
import org.openkawu.jfxium.core.util.TextUtils;

import java.util.function.Consumer;

/**
 * JFXium 切换按钮组件 - 对标 Ant Design Switch/Tag.CheckableTag（继承式 + 双工厂模式，M19.x 重构）。
 *
 * <p><b>定位</b>：具有「按下 / 弹起」两态切换的按钮（独立或加入 {@link ToggleGroup} 形成互斥组）。
 * 与 {@link ButtonAnt}（一次性触发）和 {@link org.openkawu.jfxium.component.composite.SwitchAnt}（开关语义）严格区分。</p>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>编辑器工具条："加粗 / 斜体 / 下划线" 独立切换</li>
 *   <li>视图切换：列表 / 网格视图（同一 ToggleGroup 互斥）</li>
 *   <li>过滤开关：勾上「仅显示活跃用户」</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式（build 可选）</h2>
 * <pre>{@code
 * ToggleButton bold = ToggleButtonAnt.create("加粗")
 *     .selected(true)
 *     .onChange(sel -> applyBold(sel))
 *     .build();
 *
 * // build 之后再改（继承式核心优势，取代原 modify()）
 * bold.disabled(true);
 * }</pre>
 *
 * <h2>用法 2：业务继承</h2>
 * <pre>{@code
 * public class BoldToggle extends ToggleButtonAnt {
 *     public BoldToggle() {
 *         text("加粗");
 *         selected(true);
 *         size(Size.SMALL);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link ToggleButton} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code size()} / {@code shape()} 重复调用不会重复挂 styleClass</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身，旧代码 {@code .build()} 写法无需改动</li>
 * </ul>
 */
public class ToggleButtonAnt extends ToggleButton
        implements LayoutCommon<ToggleButtonAnt>, DisabledSupport<ToggleButtonAnt> {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    /**
     * 形状枚举。
     * <ul>
     *   <li>{@link #DEFAULT}：默认（继承 .button 既有圆角，Ant Design 默认）</li>
     *   <li>{@link #ROUNDED}：pill 圆角（适合标签式 toggle）</li>
     *   <li>{@link #SQUARE}：直角方形（适合工具条）</li>
     * </ul>
     */
    public enum Shape {
        DEFAULT, ROUNDED, SQUARE
    }

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（无文本）。 */
    public static ToggleButtonAnt create() {
        return new ToggleButtonAnt();
    }

    /** 工厂入口（带文本）。 */
    public static ToggleButtonAnt create(String text) {
        return new ToggleButtonAnt(TextUtils.safeText(text));
    }

    /**
     * 创建一个「必选」互斥组（M19.42 #9）—— 至少有一个按钮保持选中，用户无法把当前选中项点成「全不选」。
     *
     * <p>JavaFX 原生 {@link ToggleGroup} 允许点击已选中项使其取消，导致「全不选」状态。
     * admin 的视图切换器（列表/卡片/表格）等场景要求「永远选中一个」，本方法挂一个监听器：
     * 当用户试图取消最后一个选中项时，自动把它选回去。</p>
     *
     * <pre>{@code
     * ToggleGroup viewGroup = ToggleButtonAnt.mandatoryGroup();
     * ToggleButtonAnt.create("列表").toggleGroup(viewGroup).selected(true).build();
     * ToggleButtonAnt.create("卡片").toggleGroup(viewGroup).build();
     * // 点击当前选中项不会取消，必须切到另一个
     * }</pre>
     */
    public static ToggleGroup mandatoryGroup() {
        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            // newToggle == null 说明用户把唯一选中项点掉了 → 选回旧的，保证永远有一个选中
            if (newToggle == null && oldToggle != null) {
                javafx.application.Platform.runLater(() -> group.selectToggle(oldToggle));
            }
        });
        return group;
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ToggleButtonAnt() {
        super();
        init();
    }

    public ToggleButtonAnt(String text) {
        super(TextUtils.safeText(text));
        init();
    }

    private void init() {
        setFocusTraversable(true);
        getStyleClass().add(JfxStyles.JFX_TOGGLE_BUTTON);
    }

    // ============================================================
    // 流式 API —— 文本 / 图标
    // ============================================================

    /** 设置文本（链式包装 setText）。 */
    public ToggleButtonAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /** 设置图标。 */
    public ToggleButtonAnt icon(Node icon) {
        setGraphic(icon);
        return this;
    }

    /** 设置图标位置。 */
    public ToggleButtonAnt contentDisplay(ContentDisplay display) {
        if (display != null) {
            setContentDisplay(display);
        }
        return this;
    }

    // ============================================================
    // 流式 API —— 尺寸 / 形状（幂等，先清后挂）
    // ============================================================

    /**
     * 设置尺寸。幂等——先清旧 size styleClass，再按需挂新。
     * DEFAULT / MIDDLE 视为同一档位。
     */
    public ToggleButtonAnt size(Size size) {
        return ApplySizeUtil.apply(this, size);
    }

    /**
     * 设置形状。幂等——先清旧 shape styleClass，再按需挂新。
     * DEFAULT 仅清不挂。
     */
    public ToggleButtonAnt shape(Shape shape) {
        getStyleClass().removeAll(JfxStyles.SHAPE_ROUNDED, JfxStyles.SHAPE_SQUARE);
        if (shape == Shape.ROUNDED) {
            getStyleClass().add(JfxStyles.SHAPE_ROUNDED);
        } else if (shape == Shape.SQUARE) {
            getStyleClass().add(JfxStyles.SHAPE_SQUARE);
        }
        return this;
    }

    /** pill 圆角，等价于 {@code shape(Shape.ROUNDED)}。 */
    public ToggleButtonAnt rounded() {
        return shape(Shape.ROUNDED);
    }

    /** 直角方形，等价于 {@code shape(Shape.SQUARE)}。 */
    public ToggleButtonAnt square() {
        return shape(Shape.SQUARE);
    }

    // ============================================================
    // 流式 API —— 状态（selected / disabled）
    // ============================================================

    /** 设置选中状态。 */
    public ToggleButtonAnt selected(boolean selected) {
        setSelected(selected);
        return this;
    }

    // disabled(boolean) / disabled() 由 DisabledSupport 接口默认提供（P2-S7 抽取 + P1 升级为 default 方法）

    // ============================================================
    // 流式 API —— 互斥组 / 事件 / 双向绑定
    // ============================================================

    /** 加入互斥 ToggleGroup（同组内只能选中一个）。 */
    public ToggleButtonAnt toggleGroup(ToggleGroup group) {
        if (group != null) {
            setToggleGroup(group);
        }
        return this;
    }

    /** 设置点击事件（与 onChange 不同：onAction 总是触发，onChange 仅在 selected 变化时触发）。 */
    public ToggleButtonAnt onAction(EventHandler<ActionEvent> handler) {
        setOnAction(handler);
        return this;
    }

    /** 选中状态变化回调（推荐使用，sel=true 表示被选中，false 表示取消）。 */
    public ToggleButtonAnt onChange(Consumer<Boolean> handler) {
        if (handler != null) {
            Bindings.onChange(selectedProperty(), handler);
        }
        return this;
    }

    /** 双向绑定：控件值 ↔ Property 值实时同步。 */
    public ToggleButtonAnt bindValue(BooleanProperty property) {
        if (property != null) {
            Bindings.bindBidirectional(selectedProperty(), property);
        }
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身（向后兼容）。
     *
     * <p>ToggleButtonAnt 既是工厂也是节点：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public ToggleButtonAnt build() {
        return this;
    }
}
