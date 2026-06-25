package org.openkawu.jfxium.core.token;

/**
 * 控件尺寸通用枚举（P1-S1 抽取）。
 *
 * <p>此前在 13 个控件（ButtonAnt / ToggleButtonAnt / ComboBoxAnt / CheckBoxAnt /
 * ChoiceBoxAnt / SplitMenuButtonAnt / InputAnt / DatePickerAnt / RadioButtonAnt /
 * MenuButtonAnt / SplitButtonAnt / ColorPickerAnt / TableAnt）中重复定义了 13 份
 * {@code public enum Size}，既冗余又彼此不一致：
 * <ul>
 *   <li>11 处 {@code DEFAULT, SMALL, LARGE}（标准三态）</li>
 *   <li>TableAnt 一家 {@code SMALL, MIDDLE, LARGE}（无 DEFAULT）</li>
 *   <li>ButtonAnt 一家 {@code DEFAULT, MIDDLE, SMALL, XS, LARGE}（五态，含 XS）</li>
 * </ul>
 *
 * <p>统一为本枚举（覆盖五态），各组件按需暴露子集。
 * TableAnt 的 {@code MIDDLE} 与本枚举 {@code MIDDLE} 同义；{@code DEFAULT} 语义
 * （CSS 不挂任何 size-* 类）由调用方约定，使用方保留向后兼容的 {@code DEFAULT} 常量。</p>
 *
 * <h3>枚举值说明</h3>
 * <ul>
 *   <li>{@link #DEFAULT} —— 不挂 size-* styleClass，由 LESS 默认尺寸控制（默认行为）</li>
 *   <li>{@link #XS} —— 极小（仅 ButtonAnt 等需要 5 态的控件使用）</li>
 *   <li>{@link #SMALL} —— 紧凑（小号 / 列表密集场景）</li>
 *   <li>{@link #MIDDLE} —— 中号（TableAnt 显式使用，等价 DEFAULT 在 Table 上）</li>
 *   <li>{@link #LARGE} —— 大号（宽松 / 触控场景）</li>
 * </ul>
 *
 * <p><b>迁移策略</b>:各组件内部 {@code Size} 枚举全部删除，公开 API
 * （如 {@code ButtonAnt.Size.SMALL}）改为引用本枚举。已有调用方源码可能需更新
 * （{@code ButtonAnt.Size.SMALL} → {@code Size.SMALL} 加 import）。</p>
 */
public enum Size {
    DEFAULT,
    XS,
    SMALL,
    MIDDLE,
    LARGE
}