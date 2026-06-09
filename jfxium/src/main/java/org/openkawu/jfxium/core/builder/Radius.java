package org.openkawu.jfxium.core.builder;

/**
 * 通用圆角枚举，用于组件 Builder 的 {@code borderRadius()} API。
 *
 * <p>映射到 LESS 变量：
 * <ul>
 *   <li>{@link #NONE} → 0px</li>
 *   <li>{@link #SM} → {@code @border-radius-sm} (4px)</li>
 *   <li>{@link #MD} → {@code @border-radius-md} (6px)，默认值</li>
 *   <li>{@link #LG} → {@code @border-radius-lg} (8px)</li>
 * </ul>
 */
public enum Radius {
    /** 无圆角（直角，0px）。 */
    NONE,
    /** 小圆角（4px）。 */
    SM,
    /** 默认圆角（6px）。 */
    MD,
    /** 大圆角（8px）。 */
    LG
}
