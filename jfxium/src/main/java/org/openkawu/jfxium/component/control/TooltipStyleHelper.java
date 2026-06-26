package org.openkawu.jfxium.component.control;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * Tooltip 样式 / 安装辅助工具 —— 集中收口「快捷 tooltip 安装 + 状态色变体」。
 *
 * <p><b>设计动机</b>：业务方过去需要 Tooltip 时,要么手写
 * {@code new javafx.scene.control.Tooltip(text) + Tooltip.install(node, t)} 绕过整个 JFXium 主题体系
 * （见 {@code FloatButtonAnt} 第 102-105 行）,要么走 {@link TooltipAnt} 但缺少「一键 install + 状态色」组合入口。
 * 本类把这两种场景合并成一个工具方法,统一挂 {@link JfxStyles#TOOLTIP} 根类 + 状态修饰类,
 * 杜绝「裸 JavaFX Tooltip」绕过主题系统。</p>
 *
 * <h2>提供能力</h2>
 * <ul>
 *   <li><b>状态色变体</b>：{@link Type#SUCCESS} / {@link Type#WARNING} / {@link Type#ERROR} / {@link Type#INFO},
 *       对应 LESS {@code components/_tooltip.less} 中的 4 个变体（与 {@code _alert-enhance.less} 颜色语义对齐）</li>
 *   <li><b>时序常量</b>：{@link #SHOW_DELAY_DEFAULT} / {@link #HIDE_DELAY_DEFAULT} / {@link #SHOW_DURATION_DEFAULT},
 *       集中收纳散落的 {@code Duration.millis(200)} / {@code Duration.seconds(10)} 魔法值</li>
 *   <li><b>一键 install 工厂方法</b>：重载 4 种,覆盖「默认 / 带类型 / 带类型 + 显示时长 / 全量控制」</li>
 * </ul>
 *
 * <h2>用法示例</h2>
 * <pre>{@code
 * // 旧写法：裸 JavaFX Tooltip,绕过 JFXium 主题
 * Tooltip t = new Tooltip("点击保存");
 * Tooltip.install(saveBtn, t);
 *
 * // 新写法 1：默认 tooltip
 * TooltipStyleHelper.install(saveBtn, "点击保存");
 *
 * // 新写法 2：成功/警告/错误/信息 状态色
 * TooltipStyleHelper.install(saveBtn, "保存成功", TooltipStyleHelper.Type.SUCCESS);
 * TooltipStyleHelper.install(deleteBtn, "危险操作", TooltipStyleHelper.Type.ERROR);
 *
 * // 新写法 3：自定义显示时长
 * TooltipStyleHelper.install(hintBtn, "复制成功", Type.SUCCESS, Duration.seconds(2));
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>不破坏 {@link TooltipAnt} 现有 API</b>：本类是叠加在 TooltipAnt 之上的便捷入口,
 *       内部通过 {@link TooltipAnt#create(String)} 复用现有链式 API,不重写 Tooltip 渲染逻辑</li>
 *   <li><b>所有颜色走 styleClass + LESS</b>：永远不调用 {@code setStyle(...)} 写颜色,
 *       状态色变体通过挂 {@link JfxStyles#TOOLTIP_SUCCESS} 等修饰类生效（红线 #1 防御）</li>
 *   <li><b>幂等防御</b>：{@code node == null} / {@code text == null} / {@code type == null} 全部静默跳过,
 *       不会抛 NPE；{@code delay/duration/hideDelay == null} 时不调用 setter,保留 TooltipAnt 默认值</li>
 *   <li><b>工具类不可实例化</b>：私有构造方法禁止 {@code new}</li>
 * </ul>
 *
 * <h2>为什么不内置 {@code Placement}</h2>
 * <p>JavaFX {@link Tooltip} 没有公开的 {@code setPlacement} API（placement 只能通过 CSS 调整
 * {@code -fx-translate-x/y} 实现）,提供一个「无法生效」的 API 会让用户困惑。
 * 如需位置控制,请直接用 {@link TooltipAnt#create(String)} + 后续 CSS 注入。本类暂不引入 Placement 枚举。</p>
 */
public final class TooltipStyleHelper {

    // ============================================================
    // 类型 / 状态色变体
    // ============================================================

    /**
     * Tooltip 状态色枚举 —— 对标 Ant Design Tooltip 的 success / warning / error / info。
     *
     * <p>通过 {@link #modifier(Type)} 映射到 {@link JfxStyles} 修饰类常量,
     * 挂到 {@link TooltipAnt} 上即可命中 {@code components/_tooltip.less} 中的对应规则。</p>
     */
    public enum Type {
        /** 默认（中性灰底 + 默认文字色）。不挂任何修饰类。 */
        DEFAULT,
        /** 成功（绿色语义色）。 */
        SUCCESS,
        /** 警告（黄色语义色）。 */
        WARNING,
        /** 错误 / 危险（红色语义色）。 */
        ERROR,
        /** 信息（蓝色语义色）。 */
        INFO
    }

    // ============================================================
    // 时序常量（收纳散落的 Duration.millis 魔法值）
    // ============================================================

    /** 默认显示延迟 —— 鼠标悬浮多久后弹出 tooltip。{@code 200ms},与 TooltipAnt 默认值一致。 */
    public static final Duration SHOW_DELAY_DEFAULT = Duration.millis(200);

    /** 默认隐藏延迟 —— 鼠标移开后多久消失。{@code 200ms},与 TooltipAnt 默认值一致。 */
    public static final Duration HIDE_DELAY_DEFAULT = Duration.millis(200);

    /** 默认显示时长 —— 弹出后多久自动消失。{@code 10s},与 TooltipAnt 默认值一致。 */
    public static final Duration SHOW_DURATION_DEFAULT = Duration.seconds(10);

    // ============================================================
    // 工具类主体
    // ============================================================

    private TooltipStyleHelper() {
        // 工具类禁止实例化
    }

    /**
     * 把 {@link Type} 映射为 {@link JfxStyles} 修饰类常量。
     *
     * <p>返回 {@code null} 表示「不挂任何状态修饰类」（仅保留 {@link JfxStyles#TOOLTIP} 根类的默认外观）。
     * 调用方需要 {@code null}-guard。</p>
     *
     * @param type 状态色类型（{@code null} 等价 {@link Type#DEFAULT} → 返回 {@code null}）
     * @return 对应的 JfxStyles 常量,或 {@code null}（DEFAULT / null）
     */
    public static String modifier(Type type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case SUCCESS -> JfxStyles.TOOLTIP_SUCCESS;
            case WARNING -> JfxStyles.TOOLTIP_WARNING;
            case ERROR -> JfxStyles.TOOLTIP_ERROR;
            case INFO -> JfxStyles.TOOLTIP_INFO;
            case DEFAULT -> null;
        };
    }

    // ============================================================
    // 一键 install —— 重载 4 种
    // ============================================================

    /**
     * 一键安装默认 tooltip 到目标节点。
     *
     * <p>等价于：</p>
     * <pre>{@code
     * TooltipAnt tip = TooltipAnt.create(text).build();
     * Tooltip.install(node, tip);
     * }</pre>
     *
     * @param node 目标节点（{@code null} 静默跳过）
     * @param text 提示文本（{@code null} 走 {@link TextUtils#safeText(String)} 降级为 {@code ""}）
     */
    public static void install(Node node, String text) {
        install(node, text, Type.DEFAULT);
    }

    /**
     * 一键安装带状态色变体的 tooltip 到目标节点。
     *
     * <p>{@link Type#DEFAULT} 等价于 {@link #install(Node, String)}。</p>
     *
     * @param node 目标节点（{@code null} 静默跳过）
     * @param text 提示文本（{@code null} 走 {@link TextUtils#safeText(String)} 降级为 {@code ""}）
     * @param type 状态色变体（{@code null} 等价 {@link Type#DEFAULT}）
     */
    public static void install(Node node, String text, Type type) {
        install(node, text, type, null, null, null);
    }

    /**
     * 一键安装带状态色 + 自定义显示时长的 tooltip 到目标节点。
     *
     * <p>典型场景：copy success / save success 这类「短时反馈」提示,希望
     * {@code showDuration = 2s} 而非默认 10s。{@code delay} / {@code hideDelay} 仍使用
     * {@link #SHOW_DELAY_DEFAULT} / {@link #HIDE_DELAY_DEFAULT}。</p>
     *
     * @param node         目标节点（{@code null} 静默跳过）
     * @param text         提示文本（{@code null} 降级为 {@code ""}）
     * @param type         状态色变体（{@code null} 等价 {@link Type#DEFAULT}）
     * @param showDuration 显示时长（{@code null} 走 TooltipAnt 默认 10s）
     */
    public static void install(Node node, String text, Type type, Duration showDuration) {
        install(node, text, type, null, showDuration, null);
    }

    /**
     * 一键安装带状态色 + 全量时序控制的 tooltip 到目标节点。
     *
     * <p>任何 {@code Duration} 参数为 {@code null} 时保留 TooltipAnt 默认值,不调用 setter。
     * 适用于需要精细控制「显示延迟 / 显示时长 / 隐藏延迟」三段时序的场景。</p>
     *
     * @param node         目标节点（{@code null} 静默跳过）
     * @param text         提示文本（{@code null} 降级为 {@code ""}）
     * @param type         状态色变体（{@code null} 等价 {@link Type#DEFAULT}）
     * @param showDelay    显示延迟（{@code null} 保留默认 200ms）
     * @param showDuration 显示时长（{@code null} 保留默认 10s）
     * @param hideDelay    隐藏延迟（{@code null} 保留默认 200ms）
     */
    public static void install(Node node, String text, Type type,
                               Duration showDelay, Duration showDuration, Duration hideDelay) {
        if (node == null) {
            return;
        }
        TooltipAnt tip = TooltipAnt.create(TextUtils.safeText(text));
        String mod = modifier(type);
        if (mod != null) {
            tip.styleClass(mod);
        }
        if (showDelay != null) {
            tip.delay(showDelay);
        }
        if (showDuration != null) {
            tip.duration(showDuration);
        }
        if (hideDelay != null) {
            tip.hideDelay(hideDelay);
        }
        tip.install(node);
    }
}