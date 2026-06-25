package org.openkawu.jfxium.core.util;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

import java.util.Arrays;
import java.util.List;

/**
 * Size 枚举应用工具（P1-S2 抽取）—— 把 {@link Size} 映射到通用 size 修饰类并挂到 {@link Node}。
 *
 * <h2>背景</h2>
 * <p>此前在 8 个 Pattern A 控件（{@code DatePickerAnt / ChoiceBoxAnt / InputAnt /
 * CheckBoxAnt / SplitMenuButtonAnt / ComboBoxAnt / ColorPickerAnt / RadioButtonAnt}）
 * 以及 {@code InputAnt.PasswordBuilder} 里重复定义了如下 100% 相同的 size 切换逻辑：</p>
 *
 * <pre>{@code
 * getStyleClass().removeAll(JfxStyles.SIZE_SMALL, JfxStyles.SIZE_LARGE);
 * if (size == Size.SMALL) {
 *     getStyleClass().add(JfxStyles.SIZE_SMALL);
 * } else if (size == Size.LARGE) {
 *     getStyleClass().add(JfxStyles.SIZE_LARGE);
 * }
 * }</pre>
 *
 * <p>本工具类把这个模式下沉为 {@link #apply(Node, Size)}，业务侧 size() 方法变成
 * 一行委托：{@code return ApplySizeUtil.apply(this, size);}。同时提供：</p>
 * <ul>
 *   <li>{@link #normalize(Size)} —— null 兜底 DEFAULT，消除 16+ 处 `size != null ? size : Size.DEFAULT` 重复</li>
 *   <li>{@link #apply(ObservableList, Size)} —— 挂在内部 styleClass list 上（用于组合控件）</li>
 *   <li>{@link #progressBarHeight(Size)} —— 进度条专用高度（小 4 / 中 8 / 大 12）</li>
 * </ul>
 *
 * <h2>ButtonAnt 五态支持</h2>
 * <p>{@link org.openkawu.jfxium.component.control.ButtonAnt#size(Size)} 的 DEFAULT/MIDDLE
 * 都显式挂 {@link JfxStyles#SIZE_MIDDLE}，与其他控件 "DEFAULT 仅清不挂" 不同。
 * 使用 {@link #apply(Node, Size, boolean) apply(node, size, true)} 委托即可。</p>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>幂等</b>：{@link #apply(Node, Size)} 重复调用不会重复挂 styleClass</li>
 *   <li><b>互斥</b>：每次先清掉 {@code size-xs/small/middle/large}，再挂当前 size（DEFAULT/XS 不挂）</li>
 *   <li><b>null 安全</b>：node 为 null 直接返回 null；size 为 null 等价 Size.DEFAULT</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // Pattern A 控件 size(Size) 方法直接委托：
 * public ComboBoxAnt<T> size(Size size) {
 *     return ApplySizeUtil.apply(this, size);
 * }
 *
 * // 组合控件内部挂到 styleClass list：
 * ApplySizeUtil.apply(pwdField.getStyleClass(), size);
 *
 * // 进度条高度：
 * progressBar.setPrefHeight(ApplySizeUtil.progressBarHeight(size));
 * }</pre>
 *
 * @see Size
 * @see JfxStyles#SIZE_XS
 * @see JfxStyles#SIZE_SMALL
 * @see JfxStyles#SIZE_MIDDLE
 * @see JfxStyles#SIZE_LARGE
 */
public final class ApplySizeUtil {

    /**
     * 所有通用 size 修饰类集合（互斥清理用）。
     */
    private static final List<String> ALL_SIZE_CLASSES = Arrays.asList(
            JfxStyles.SIZE_XS,
            JfxStyles.SIZE_SMALL,
            JfxStyles.SIZE_MIDDLE,
            JfxStyles.SIZE_LARGE
    );

    /**
     * 所有通用 shape 修饰类集合（互斥清理用）。
     */
    private static final List<String> ALL_SHAPE_CLASSES = Arrays.asList(
            JfxStyles.SHAPE_ROUNDED,
            JfxStyles.SHAPE_SQUARE
    );

    private ApplySizeUtil() {
        // 工具类不允许实例化
    }

    /**
     * null 兜底：size == null 返回 {@link Size#DEFAULT}，否则原样返回。
     *
     * <p>消除 16+ 处 `size != null ? size : Size.DEFAULT` 重复模板。</p>
     */
    public static Size normalize(Size size) {
        return size != null ? size : Size.DEFAULT;
    }

    /**
     * Size → 通用 size 修饰类字符串。{@link Size#DEFAULT} / {@link Size#XS} 返回 null（不挂任何 size 类）。
     */
    public static String classFor(Size size) {
        return classFor(size, false);
    }

    /**
     * Size → 通用 size 修饰类字符串（带 DEFAULT 映射控制）。
     *
     * @param size            目标尺寸
     * @param defaultAsMiddle true 时 DEFAULT → {@link JfxStyles#SIZE_MIDDLE}（ButtonAnt 五态模式）
     * @return 修饰类字符串；DEFAULT（defaultAsMiddle=false）/XS 时返回 null
     */
    private static String classFor(Size size, boolean defaultAsMiddle) {
        return switch (normalize(size)) {
            case XS -> JfxStyles.SIZE_XS;
            case SMALL -> JfxStyles.SIZE_SMALL;
            case MIDDLE -> JfxStyles.SIZE_MIDDLE;
            case LARGE -> JfxStyles.SIZE_LARGE;
            default -> defaultAsMiddle ? JfxStyles.SIZE_MIDDLE : null;
        };
    }

    /**
     * 把 Size 挂到 {@link Node} 的 styleClass。先清掉所有通用 size 类，再挂当前 size 对应的类。
     *
     * <p>DEFAULT/XS 不挂任何类（相当于"恢复默认尺寸"，由 LESS 默认 styleClass 控制）。</p>
     *
     * @param node 目标 Node；null 时直接返回 null（不抛异常）
     * @param size 目标尺寸；null 等价 {@link Size#DEFAULT}
     * @param <N>  Node 子类型泛型，便于链式调用
     * @return 原 node（便于链式）
     */
    public static <N extends Node> N apply(N node, Size size) {
        return apply(node, size, false);
    }

    /**
     * 把 Size 挂到 {@link Node} 的 styleClass（带 DEFAULT 映射控制）。
     *
     * <p>{@code defaultAsMiddle=true} 时 DEFAULT → {@link JfxStyles#SIZE_MIDDLE}，适用于
     * {@link org.openkawu.jfxium.component.control.ButtonAnt} 五态模式（DEFAULT/MIDDLE 同义）。</p>
     *
     * @param node            目标 Node；null 时直接返回 null
     * @param size            目标尺寸；null 等价 {@link Size#DEFAULT}
     * @param defaultAsMiddle true 时 DEFAULT 映射到 SIZE_MIDDLE
     * @param <N>             Node 子类型泛型
     * @return 原 node（便于链式）
     */
    public static <N extends Node> N apply(N node, Size size, boolean defaultAsMiddle) {
        if (node == null) {
            return null;
        }
        apply(node.getStyleClass(), size, defaultAsMiddle);
        return node;
    }

    /**
     * 把 Size 挂到任意 {@link ObservableList}（典型场景：组合控件内部 styleClass list）。
     *
     * @param target 目标 list；null 时静默返回
     * @param size   目标尺寸；null 等价 {@link Size#DEFAULT}
     */
    public static void apply(ObservableList<String> target, Size size) {
        apply(target, size, false);
    }

    /**
     * 把 Size 挂到任意 {@link ObservableList}（带 DEFAULT 映射控制）。
     */
    private static void apply(ObservableList<String> target, Size size, boolean defaultAsMiddle) {
        if (target == null) {
            return;
        }
        target.removeAll(ALL_SIZE_CLASSES);
        String cls = classFor(size, defaultAsMiddle);
        if (cls != null) {
            target.add(cls);
        }
    }

    /**
     * 进度条专用高度（小 4 / 中 8 / 大 12）。DEFAULT / MIDDLE / XS 用中等 8。
     *
     * <p>该数值与 {@code variables-base.less} 中 {@code @progress-height} 系族一致：
     * SMALL = 4（@progress-height-sm），MIDDLE = 8（@progress-height-md），LARGE = 12（@progress-height-lg）。
     * 集中维护避免各组件硬编码漂移。</p>
     */
    public static double progressBarHeight(Size size) {
        return switch (normalize(size)) {
            case SMALL -> 4.0;
            case LARGE -> 12.0;
            default -> 8.0;
        };
    }

    // ============================================================
    // Shape 互斥工具
    // ============================================================

    /**
     * 应用 ROUNDED 形状（先清 SQUARE，再幂等挂 ROUNDED）。
     * <p>消除 {@link org.openkawu.jfxium.component.control.MenuButtonAnt#rounded()}
     * 与 {@link org.openkawu.jfxium.component.control.SplitButtonAnt#rounded()} 重复的
     * inline styleClass 操作。</p>
     */
    public static <N extends Node> N applyShapeRounded(N node) {
        if (node != null) {
            node.getStyleClass().removeAll(JfxStyles.SHAPE_SQUARE);
            if (!node.getStyleClass().contains(JfxStyles.SHAPE_ROUNDED)) {
                node.getStyleClass().add(JfxStyles.SHAPE_ROUNDED);
            }
        }
        return node;
    }

    /**
     * 应用 SQUARE 形状（先清 ROUNDED，再幂等挂 SQUARE）。
     */
    public static <N extends Node> N applyShapeSquare(N node) {
        if (node != null) {
            node.getStyleClass().removeAll(JfxStyles.SHAPE_ROUNDED);
            if (!node.getStyleClass().contains(JfxStyles.SHAPE_SQUARE)) {
                node.getStyleClass().add(JfxStyles.SHAPE_SQUARE);
            }
        }
        return node;
    }
}