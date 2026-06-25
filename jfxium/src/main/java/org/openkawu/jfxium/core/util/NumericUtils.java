package org.openkawu.jfxium.core.util;

/**
 * 数值处理辅助工具。
 * 提供所有组件通用的数值钳制 / 取整 / 有限性判断，集中替代散落在各 Builder 私有方法中的
 * 重复实现（{@code clamp / normalizeRange / isFinitePositive / toIntString} 等）。
 *
 * <h2>设计动机</h2>
 * <p>原 {@code SliderAnt.clamp / normalizeRange}、{@code ProgressAnt.clamp}、
 * {@code ResizablePanelAnt.clamp}、{@code WatermarkAnt} 中 6+ 处
 * {@code Double.isFinite(value) && value > 0 ? value : default} 都是同一段逻辑的
 * 不同副本。本类收口后各组件直接调用，修改默认值或容错策略也只需改一处。</p>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 限幅到 [min, max]，NaN/Infinity 回退到 min
 * double v = NumericUtils.clamp(rawValue, 0, 100, 0);
 *
 * // 安全的整数显示（避免 1.0 出现小数）
 * String s = NumericUtils.toIntString(42.0);   // "42"
 *
 * // 有限性 + 正值判断（默认值兜底）
 * double s = NumericUtils.isFinitePositive(size) ? size : 12;
 * }</pre>
 */
public final class NumericUtils {

    private NumericUtils() {
        // 工具类禁止实例化
    }

    /**
     * 把 {@code rawValue} 限幅到 {@code [min, max]} 区间。
     *
     * <p>NaN / Infinity 一律回退到 {@code fallback}（不抛异常、不返回 min/max，避免调用方
     * 误以为"合理值"而继续走主流程）。{@code min > max} 时返回 fallback 并自动交换，
     * 调用方不必预先校验范围。</p>
     *
     * @param rawValue 原始输入（可能为 NaN / Infinity / 越界）
     * @param min      下界
     * @param max      上界
     * @param fallback 非有限输入的兜底值
     * @return 限幅后的值
     */
    public static double clamp(double rawValue, double min, double max, double fallback) {
        if (!Double.isFinite(rawValue)) {
            return fallback;
        }
        // 区间反向时交换
        double lo = min;
        double hi = max;
        if (lo > hi) {
            double tmp = lo;
            lo = hi;
            hi = tmp;
        }
        if (rawValue < lo) return lo;
        if (rawValue > hi) return hi;
        return rawValue;
    }

    /**
     * 把 {@code rawValue} 限幅到 {@code [0, max]} 区间，非有限输入回退到 0。
     *
     * <p>是 {@link #clamp(double, double, double, double)} 的下界=0 快捷重载，
     * 用于 width / height / spacing 等语义上不可能为负的尺寸类参数。</p>
     */
    public static double clampNonNegative(double rawValue, double max, double fallback) {
        return clamp(rawValue, 0, max, fallback);
    }

    /**
     * 判断 {@code value} 是否为有限的正数（{@code > 0}）。
     *
     * <p>替代散落的 {@code Double.isFinite(value) && value > 0} 模式。配合三元
     * 表达式使用：{@code isFinitePositive(v) ? v : defaultValue}。</p>
     */
    public static boolean isFinitePositive(double value) {
        return Double.isFinite(value) && value > 0;
    }

    /**
     * 判断 {@code value} 是否为有限的非负数（{@code >= 0}）。
     */
    public static boolean isFiniteNonNegative(double value) {
        return Double.isFinite(value) && value >= 0;
    }

    /**
     * 把 {@code value} 安全地格式化为整数字符串。
     *
     * <p>针对 {@code SliderAnt} 等组件中 5+ 次出现的 {@code String.valueOf((int) val)} 模式
     * 抽离。优势：(1) 单点维护取整规则；(2) 后续要改成 {@code Math.round} 或本地化
     * 数字格式（{@code Locale-sensitive}）只改一处。</p>
     *
     * @param value 原始值（NaN / Infinity 时返回 "0"）
     * @return 整数字符串
     */
    public static String toIntString(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }
        return String.valueOf((int) value);
    }

    /**
     * 保证 {@code count} 不小于 1。
     *
     * <p>用于"页码 / 步数 / 行数"等至少为 1 的语义参数。{@code count <= 0} 或非有限
     * 时回退到 1。</p>
     */
    public static int ensureAtLeastOne(int count) {
        return count >= 1 ? count : 1;
    }

    /**
     * 保证 {@code size} 不小于 0（非负尺寸）。
     *
     * <p>NaN / Infinity / 负数一律回退到 0。</p>
     */
    public static double ensureNonNegative(double size) {
        return Double.isFinite(size) && size > 0 ? size : 0;
    }
}
