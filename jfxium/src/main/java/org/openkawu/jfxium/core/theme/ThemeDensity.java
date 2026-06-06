package org.openkawu.jfxium.core.theme;

/**
 * 主题密度模式 —— 控制组件的间距和尺寸缩放，对齐 Ant Design 紧凑主题。
 */
public enum ThemeDensity {
    /**
     * 默认密度 —— 标准内边距和组件尺寸。
     * 等价于 Ant Design 默认算法（sizeStep=4, controlHeight=32）。
     */
    DEFAULT,

    /**
     * 紧凑密度 —— 减小内边距和组件尺寸。
     * 等价于 Ant Design 紧凑算法（sizeStep=2, controlHeight=28）。
     * 所有间距、内边距、高度约缩减 25%~30%。
     */
    COMPACT
}
