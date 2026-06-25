package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * JFXium 工具提示组件 - 对标 Ant Design Tooltip（继承式 + 双工厂模式，M19.x 重构）。
 *
 * <p><b>定位</b>：悬浮提示气泡，继承自 {@link Tooltip}，
 * 鼠标悬浮在目标节点上时显示提示文本。</p>
 *
 * <h2>特殊设计：未实现 LayoutCommon</h2>
 * <p>Tooltip 是 {@link Tooltip}（extends {@code PopupControl} extends {@code Control}），
 * 不是 {@link Node}，因此 <b>不能</b> 直接 {@code implements LayoutCommon}——
 * LayoutCommon 的 default 方法会对 {@code this} 做 {@code (Node) this} 强转，
 * 而 Tooltip 不是 Node，会抛 ClassCastException。</p>
 *
 * <p>本组件采用「继承 Tooltip + 独立 style/styleClass 桥接」的模式：</p>
 * <ul>
 *   <li>继承 Tooltip 本身，可直接 {@code Tooltip.install(node, tooltipAnt)} 使用</li>
 *   <li>提供 {@link #styleClass(String)} / {@link #style(String)} 链式 API（受控入口）</li>
 *   <li>保留 {@link #install(Node)} 一键安装便捷方法</li>
 * </ul>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li>设置提示文本</li>
 *   <li>控制显示延迟（{@code delay}，默认 200ms）</li>
 *   <li>控制显示时长（{@code duration}，默认 10s）</li>
 *   <li>控制消失延迟（{@code hideDelay}，默认 200ms）</li>
 *   <li>{@code install(node)} 一键安装到目标节点</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>图标按钮说明（图标无文本时提示功能）</li>
 *   <li>表格列标题解释（悬停显示列含义）</li>
 *   <li>禁用按钮原因说明（为什么不能点击）</li>
 * </ul>
 *
 * <h2>用法 1：工厂链式 + install 一键绑定</h2>
 * <pre>{@code
 * Button btn = ButtonAnt.create("悬停我").build();
 * TooltipAnt.create("这是提示文本")
 *     .delay(Duration.millis(300))
 *     .install(btn);
 * }</pre>
 *
 * <h2>用法 2：先 build 再手动安装</h2>
 * <pre>{@code
 * TooltipAnt tip = TooltipAnt.create("快捷键：Ctrl+S").build();
 * Tooltip.install(saveButton, tip);
 * }</pre>
 *
 * <h2>用法 3：业务继承</h2>
 * <pre>{@code
 * public class CtrlSTooltip extends TooltipAnt {
 *     public CtrlSTooltip() {
 *         text("快捷键：Ctrl+S");
 *         delay(Duration.millis(300));
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>双重身份</b>：是 {@link Tooltip} 也是工厂——可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时仍保留链式</li>
 *   <li><b>幂等性</b>：{@code delay/duration/hideDelay} 重复调用以最后一次为准</li>
 *   <li><b>向后兼容</b>：{@code build()} 返回自身 + {@code install(Node)} 静态方法保留</li>
 * </ul>
 */
public class TooltipAnt extends Tooltip {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（带文本）。 */
    public static TooltipAnt create(String text) {
        return new TooltipAnt(TextUtils.safeText(text));
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public TooltipAnt() {
        super();
        init();
    }

    public TooltipAnt(String text) {
        super(TextUtils.safeText(text));
        init();
    }

    private void init() {
        getStyleClass().add(JfxStyles.TOOLTIP);
    }

    // ============================================================
    // 流式 API —— 文本 / 样式
    // ============================================================

    /** 设置提示文本（链式包装 setText）。 */
    public TooltipAnt text(String text) {
        setText(TextUtils.safeText(text));
        return this;
    }

    /**
     * 追加一个 styleClass（幂等——重复调不会重复挂）。
     * Tooltip 是 Styleable 但不是 Node，因此需要独立暴露此 API。
     */
    public TooltipAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /**
     * inline style（应急用，优先用 styleClass + LESS）。
     */
    public TooltipAnt style(String style) {
        if (style != null) {
            setStyle(style);
        }
        return this;
    }

    // ============================================================
    // 流式 API —— 时序控制
    // ============================================================

    /**
     * 显示延迟——鼠标悬浮多久后才弹出。默认 200ms。
     */
    public TooltipAnt delay(Duration delay) {
        if (delay != null) {
            setShowDelay(delay);
        }
        return this;
    }

    /**
     * 显示时长——弹出后多久自动消失（自动消失前若鼠标移开会提前消失）。
     * 默认 10s。
     */
    public TooltipAnt duration(Duration duration) {
        if (duration != null) {
            setShowDuration(duration);
        }
        return this;
    }

    /**
     * 隐藏延迟——鼠标移开后多久消失。默认 200ms。
     */
    public TooltipAnt hideDelay(Duration delay) {
        if (delay != null) {
            setHideDelay(delay);
        }
        return this;
    }

    // ============================================================
    // 构建 / 安装
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身（向后兼容）。
     *
     * <p>TooltipAnt 既是工厂也是 Tooltip：{@code build()} 跟直接拿 {@code this} 等价，
     * 提供本方法是为了让 API 跟旧版 Builder 的 {@code .build()} 完全对齐。</p>
     */
    public TooltipAnt build() {
        return this;
    }

    /**
     * 一键安装到目标节点（静态便捷方法）。
     *
     * <p>等价于 {@code Tooltip.install(node, this.build())}。</p>
     */
    public void install(Node node) {
        if (node != null) {
            Tooltip.install(node, this);
        }
    }
}
