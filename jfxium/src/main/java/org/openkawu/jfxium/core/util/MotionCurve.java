package org.openkawu.jfxium.core.util;

import javafx.animation.Interpolator;

/**
 * 动效缓动曲线常量集中地 —— 取代散落在各组件里的
 * {@code Interpolator.EASE_OUT / EASE_IN / EASE_BOTH} 魔法值。
 *
 * <p>{@link AnimationDuration} 负责「多久」，本类负责「怎么走」。二者合起来才是一条完整动效；
 * 此前时长已收口而曲线没有，于是节奏由每个组件作者随手挑内置常量决定，全局不齐。</p>
 *
 * <h2>为什么不用 JavaFX 内置的 {@code EASE_OUT / EASE_IN}</h2>
 * <p>内置档位近似线性，几乎感觉不到缓动；实测对照（输入进度 → 输出进度）：</p>
 * <pre>
 *   x:        0.1     0.2     0.3     0.5     0.7     0.9
 *   EASE_OUT  0.111   0.222   0.333   0.556   0.778   0.972
 *   本类 ENTER 0.446   0.626   0.744   0.890   0.965   0.997
 * </pre>
 * <p>同样的 200ms，本类曲线前 10% 时间就走完 45% 行程（Fluent 的「快起缓收」），
 * 观感明显更利落；{@code EASE_OUT} 则像匀速。</p>
 *
 * <h2>曲线来源与保真度</h2>
 * <p>取 <a href="https://github.com/microsoft/fluentui/blob/master/packages/tokens/src/global/curves.ts">
 * Fluent 2 官方 motion 曲线</a>——与阴影 elevation 阶梯、焦点环双色一样，
 * 属「按语义收口」而非「换一套皮」：本项目的主题是 token 驱动的，
 * 曲线进 token 后所有主题一致受益。</p>
 * <p>{@link Interpolator#SPLINE SPLINE} 是标准三次贝塞尔：以 CSS
 * {@code cubic-bezier(x1,y1,x2,y2)} 的四个控制点构造即可，实测逐点偏差 0.00000、
 * 端点严格 0/1，故可直接照搬官方数值，无需自实现曲线。</p>
 *
 * <h2>选档规则（对齐 Fluent「入场减速、退场加速」）</h2>
 * <table border="1">
 *   <tr><th>常量</th><th>Fluent 曲线</th><th>控制点</th><th>典型场景</th></tr>
 *   <tr><td>{@link #ENTER}</td><td>curveDecelerateMid</td><td>(0,0,0,1)</td>
 *       <td>出现在用户面前：淡入 / 滑入 / 放大进入（Modal、Drawer、Message、Notification、Carousel）</td></tr>
 *   <tr><td>{@link #EXIT}</td><td>curveAccelerateMid</td><td>(1,0,1,1)</td>
 *       <td>从用户面前离开：淡出 / 滑出 / 缩小退出</td></tr>
 *   <tr><td>{@link #STANDARD}</td><td>curveEasyEase</td><td>(0.33,0,0.67,1)</td>
 *       <td>原地或位置变化的位移（滚动到顶、面板位移），两端对称</td></tr>
 * </table>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 旧写法：语义不明的内置档
 * fade.setInterpolator(Interpolator.EASE_OUT);
 *
 * // 新写法：语义档位
 * fade.setInterpolator(MotionCurve.ENTER);
 * }</pre>
 */
public final class MotionCurve {

    /** 入场（Fluent curveDecelerateMid）：快起缓收 —— 淡入 / 滑入 / 放大进入。 */
    public static final Interpolator ENTER = Interpolator.SPLINE(0.0, 0.0, 0.0, 1.0);

    /** 退场（Fluent curveAccelerateMid）：缓起快收 —— 淡出 / 滑出 / 缩小退出。 */
    public static final Interpolator EXIT = Interpolator.SPLINE(1.0, 0.0, 1.0, 1.0);

    /** 标准（Fluent curveEasyEase）：两端对称 —— 原地或位置变化的位移（如滚动到顶）。 */
    public static final Interpolator STANDARD = Interpolator.SPLINE(0.33, 0.0, 0.67, 1.0);

    private MotionCurve() {
        // 工具类禁止实例化
    }
}
