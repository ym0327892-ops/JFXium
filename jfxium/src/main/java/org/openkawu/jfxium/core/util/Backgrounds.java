package org.openkawu.jfxium.core.util;

import javafx.scene.Node;
import org.openkawu.jfxium.core.css.Background;

/**
 * Background 工具类（M19.35）—— 给任意 {@link Node} 挂 / 切换 background styleClass。
 *
 * <h2>背景</h2>
 * <p>JFXium 的 {@link Background} 枚举对应 5 个 styleClass（{@code .jfx-bg-default} 等），
 * LESS 中已经为这些 styleClass 绑定了主题变量。本工具类是「方便业务侧给原生 JavaFX Node
 * （VBox / HBox / StackPane / GridPane / ScrollPane 等）也用上 Background 体系」的入口。</p>
 *
 * <h2>三种用法对照</h2>
 * <pre>{@code
 * // 方式 1：原生写法（最朴素）
 * VBox panel = new VBox();
 * panel.getStyleClass().add(Background.SUBTLE.styleClass());
 *
 * // 方式 2：本工具类（推荐——更顺手）
 * VBox panel = Backgrounds.apply(new VBox(), Background.SUBTLE);
 *
 * // 方式 3：动态切换（响应式场景）
 * Backgrounds.replace(panel, Background.DEFAULT);
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>幂等</b>：{@link #apply} 重复调用不会重复挂 styleClass</li>
 *   <li><b>泛型友好</b>：返回原 Node 类型（{@code <N extends Node>}），便于链式调用</li>
 *   <li><b>null 安全</b>：node 或 bg 为 null 时返回原节点不抛异常</li>
 *   <li><b>{@link #replace} 互斥语义</b>：先清掉所有 jfx-bg-* 类再挂新的，避免多 background 叠加</li>
 * </ul>
 */
public final class Backgrounds {

    /** 所有 Background 对应的 styleClass 集合，{@link #replace} 用它做先清后挂。 */
    private static final String[] ALL_BG_CLASSES = {
            Background.DEFAULT.styleClass(),
            Background.SUBTLE.styleClass(),
            Background.LAYOUT.styleClass(),
            Background.INSET.styleClass(),
            Background.TRANSPARENT.styleClass(),
    };

    private Backgrounds() {
        // 工具类禁实例化
    }

    /**
     * 给 node 挂上 background 对应的 styleClass。
     *
     * <p>幂等——如果 node 已经包含该 styleClass，本方法是 no-op。
     * 不会清掉已有的其他 jfx-bg-* 类，需要互斥语义请用 {@link #replace}。</p>
     *
     * @param node 目标节点；null 时直接返回 null
     * @param bg   背景层级；null 时不做任何修改
     * @return 同一个 node 实例，便于链式 {@code Backgrounds.apply(new VBox(), SUBTLE)}
     */
    public static <N extends Node> N apply(N node, Background bg) {
        if (node == null || bg == null) return node;
        String cls = bg.styleClass();
        if (!node.getStyleClass().contains(cls)) {
            node.getStyleClass().add(cls);
        }
        return node;
    }

    /**
     * 移除指定 background styleClass。
     *
     * <p>典型场景：业务想"还原"到主题默认背景时调用。</p>
     */
    public static void remove(Node node, Background bg) {
        if (node == null || bg == null) return;
        node.getStyleClass().remove(bg.styleClass());
    }

    /**
     * 切换 background：先清掉所有 jfx-bg-* 类，再挂上指定的。
     *
     * <p>这是「互斥替换」语义——确保 node 同一时刻只挂一个 jfx-bg-* 类。
     * 适合响应式场景（如根据状态动态切换背景层级）。
     * bg 为 null 时只清不挂（等同于"还原默认"）。</p>
     *
     * @param node 目标节点；null 时直接返回 null
     * @param bg   新的背景层级；null 时仅清掉所有 jfx-bg-* 类
     * @return 同一个 node 实例
     */
    public static <N extends Node> N replace(N node, Background bg) {
        if (node == null) return null;
        // 先清掉所有 jfx-bg-* 类（避免叠加）
        node.getStyleClass().removeAll(ALL_BG_CLASSES);
        if (bg != null) {
            node.getStyleClass().add(bg.styleClass());
        }
        return node;
    }
}
