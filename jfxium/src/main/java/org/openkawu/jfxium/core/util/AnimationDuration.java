package org.openkawu.jfxium.core.util;

import javafx.util.Duration;

/**
 * 动画时长常量集中地 —— 取代散落在各组件里的 {@code Duration.millis(200/250/300/...)} 魔法值。
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li><b>语义档位</b>：{@link #FAST}/{@link #NORMAL}/{@link #SLOW}/{@link #SLIDE}/{@link #BACK_TOP}/{@link #COPY_TOAST}
 *       按用途命名而非纯数字 —— 改全局节奏只需改一处。</li>
 *   <li><b>不可变</b>：常量是 {@code public static final}，调用方拿到的是固定 {@link Duration}。</li>
 *   <li><b>复用</b>：所有需要动画时长的组件统一引用本类，避免组件之间节奏不一致。</li>
 * </ul>
 *
 * <h2>档位划分</h2>
 * <table border="1">
 *   <tr><th>常量</th><th>时长</th><th>典型场景</th></tr>
 *   <tr><td>{@link #ULTRA_FAST}</td><td>150ms</td><td>Modal/Popover 紧急关闭（比 {@link #FAST} 更紧凑，避免关闭卡顿感）</td></tr>
 *   <tr><td>{@link #FAST}</td><td>200ms</td><td>Switch 滑动 / Menu arrow 旋转 / Alert fade-out / BackTop 渐显 / Drawer 紧急关闭</td></tr>
 *   <tr><td>{@link #NORMAL}</td><td>250ms</td><td>Drawer 抽屉动画（带淡入）</td></tr>
 *   <tr><td>{@link #SLOW}</td><td>300ms</td><td>Notification 渐入 / Tabs 切换延迟 / Carousel 淡入淡出</td></tr>
 *   <tr><td>{@link #SLIDE}</td><td>400ms</td><td>Carousel 滑入滑出（带位移，比 fade 更慢）</td></tr>
 *   <tr><td>{@link #BACK_TOP}</td><td>450ms</td><td>BackTop 滚动到顶部动画</td></tr>
 *   <tr><td>{@link #COPY_TOAST}</td><td>1200ms</td><td>CodeBlock 复制成功提示显示时长</td></tr>
 * </table>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 旧写法：散落的魔法值
 * FadeTransition fade = new FadeTransition(Duration.millis(200), node);
 *
 * // 新写法：语义档位
 * FadeTransition fade = new FadeTransition(AnimationDuration.FAST, node);
 * }</pre>
 */
public final class AnimationDuration {

    /** 最快档（150ms）—— Modal/Popover 紧急关闭（避免关闭卡顿感，比 {@link #FAST} 更紧凑）。 */
    public static final Duration ULTRA_FAST = Duration.millis(150);

    /** 快档（200ms）—— Switch 滑动 / Menu arrow 旋转 / Alert fade-out / Drawer 紧急关闭 / Collapse 展开收起。 */
    public static final Duration FAST = Duration.millis(200);

    /** 标准档（250ms）—— Drawer 抽屉动画。 */
    public static final Duration NORMAL = Duration.millis(250);

    /** 较慢档（300ms）—— Notification 渐入 / Tabs 切换延迟 / Carousel 淡入淡出。 */
    public static final Duration SLOW = Duration.millis(300);

    /** 滑动档（400ms）—— Carousel 滑入滑出。 */
    public static final Duration SLIDE = Duration.millis(400);

    /** BackTop 专用档（450ms）—— 回到顶部动画（BackTop 暴露 {@code duration} 字段供外部覆写）。 */
    public static final Duration BACK_TOP = Duration.millis(450);

    /** 复制提示档（1200ms）—— CodeBlock 复制成功 toast 显示时长。 */
    public static final Duration COPY_TOAST = Duration.millis(1200);

    private AnimationDuration() {
        // 工具类禁止实例化
    }
}