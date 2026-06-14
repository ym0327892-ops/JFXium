package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * ScrollPaneAnt - 继承式滚动容器（M19.36 升级为双工厂模式 + 改名自 ScrollContainerAnt）。
 *
 * <p>与 {@link LayoutCommon}{@code <SELF>} 一致 —— 既能当工厂用，也能被业务继承做"长列表页基类"。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * ScrollPaneAnt scroll = ScrollPaneAnt.create()
 *     .content(longContent)
 *     .fitToWidth(true)
 *     .padding(new Insets(24))
 *     .build();
 * }</pre>
 *
 * <h3>2. 直接 new + 链式</h3>
 * <pre>{@code
 * ScrollPaneAnt scroll = new ScrollPaneAnt(longContent)
 *     .fitToWidth(true)
 *     .padding(24);
 * }</pre>
 *
 * <h3>3. 业务继承（页面骨架基类）</h3>
 * <pre>{@code
 * public class FeedPage extends ScrollPaneAnt {
 *     public FeedPage(Node feed) {
 *         super(feed);
 *         fitToWidth(true);
 *         padding(16);
 *     }
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>视觉钩子、方向性边框线、尺寸、高频节点属性</b>统一继承自 {@link LayoutCommon}
 *       默认实现（节省 ~110 行重复模板代码，行为 100% 等价原 ScrollContainerAnt）</li>
 *   <li><b>双重身份</b>：是 ScrollPane 也是工厂——继承自 {@link ScrollPane}，可继续被业务继承</li>
 *   <li><b>流式 API 返回 this</b>：链式调用 + 子类继承时也保留链式</li>
 *   <li><b>viewport 增强</b>：内部自动包一层 {@link StackPane} 作为 content 容器，
 *       挂 {@code jfx-scroll-pane-viewport} class —— 满足红线 5「防容器吞 padding」，
 *       padding 下放到 viewport 而非 ScrollPane 自身（避免 padding 作用在视口边框）</li>
 *   <li><b>padding 下放</b>：3 个 {@code padding()} default 方法被本类覆盖，
 *       先存到 {@code pendingPadding} 字段，{@link #content(Node)} 时下放到 viewport
 *       （与 {@link #content(Node)} 调用的先后顺序无关）</li>
 * </ul>
 */
public class ScrollPaneAnt extends ScrollPane implements LayoutCommon<ScrollPaneAnt> {

    // ============================================================
    // 工厂入口
    // ============================================================

    /** 工厂入口（无 content，后续 {@link #content(Node)} 补上）。 */
    public static ScrollPaneAnt create() {
        return new ScrollPaneAnt();
    }

    /** 工厂入口（带 content）。 */
    public static ScrollPaneAnt create(Node content) {
        return new ScrollPaneAnt(content);
    }

    // ============================================================
    // 内部状态
    // ============================================================

    /** 当前 viewport 实例（{@link #content(Node)} 时懒创建）。 */
    private StackPane viewport;

    /** content() 之前调 padding() 时暂存，content() 时下放。 */
    private Insets pendingPadding;

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public ScrollPaneAnt() {
        super();
        getStyleClass().add(JfxStyles.SCROLL_PANE);
    }

    public ScrollPaneAnt(Node content) {
        super();
        getStyleClass().add(JfxStyles.SCROLL_PANE);
        content(content);
    }

    // ============================================================
    // 流式 API（ScrollPane 特有业务方法）
    // ============================================================

    /**
     * 设置滚动内容（自动包一层 {@link StackPane} 作为 viewport 容器）。
     *
     * <p>传 {@code null} 表示清空内容。</p>
     */
    public ScrollPaneAnt content(Node content) {
        if (content == null) {
            setContent(null);
            viewport = null;
            return this;
        }
        viewport = new StackPane(content);
        viewport.getStyleClass().add(JfxStyles.SCROLL_PANE_VIEWPORT);
        // padding 下放到 viewport（红线 5 防容器吞 padding）
        if (pendingPadding != null) {
            viewport.setPadding(pendingPadding);
        }
        setContent(viewport);
        return this;
    }

    /** 设置是否铺满视口宽度（{@link ScrollPane#setFitToWidth}）。 */
    public ScrollPaneAnt fitToWidth(boolean fit) {
        setFitToWidth(fit);
        return this;
    }

    /** 设置是否铺满视口高度（{@link ScrollPane#setFitToHeight}）。 */
    public ScrollPaneAnt fitToHeight(boolean fit) {
        setFitToHeight(fit);
        return this;
    }

    /** 设置是否可平移（{@link ScrollPane#setPannable}）。 */
    public ScrollPaneAnt pannable(boolean pannable) {
        setPannable(pannable);
        return this;
    }

    /** 设置水平滚动条策略。 */
    public ScrollPaneAnt hbarPolicy(ScrollBarPolicy policy) {
        if (policy != null) setHbarPolicy(policy);
        return this;
    }

    /** 设置垂直滚动条策略。 */
    public ScrollPaneAnt vbarPolicy(ScrollBarPolicy policy) {
        if (policy != null) setVbarPolicy(policy);
        return this;
    }

    // ============================================================
    // padding() 覆盖（不下到 ScrollPane 自身，下放到 viewport）
    // ============================================================

    /**
     * 设置统一 padding（四边相同）—— 下放到内部 viewport 而非 ScrollPane 自身。
     *
     * <p>覆盖 {@link LayoutCommon#padding(double)} default 实现，行为变更：
     * LayoutCommon 默认会 {@code r.setPadding(...)} 作用到 Region（即 ScrollPane 自身），
     * 但这会让 padding 渲染在视口边框上造成"作用在视口边框"的视觉错位。
     * 本类改把 padding 存到 {@code pendingPadding}，{@link #content(Node)} 时再下放到 viewport。</p>
     */
    @Override
    public ScrollPaneAnt padding(double padding) {
        pendingPadding = new Insets(padding);
        applyPendingPadding();
        return this;
    }

    /**
     * 设置 4 边各自 padding —— 下放到内部 viewport。详见 {@link #padding(double)}。
     */
    @Override
    public ScrollPaneAnt padding(double top, double right, double bottom, double left) {
        pendingPadding = new Insets(top, right, bottom, left);
        applyPendingPadding();
        return this;
    }

    /**
     * 设置 Insets 对象 —— 下放到内部 viewport。详见 {@link #padding(double)}。
     *
     * <p>传 {@code null} 等价于清空 pendingPadding（不主动清空 viewport 已有 padding，保持现状）。</p>
     */
    @Override
    public ScrollPaneAnt padding(Insets padding) {
        pendingPadding = padding;
        applyPendingPadding();
        return this;
    }

    /** 把 {@code pendingPadding} 应用到当前 viewport（如果有）。 */
    private void applyPendingPadding() {
        if (viewport != null && pendingPadding != null) {
            viewport.setPadding(pendingPadding);
        }
    }

    // ============================================================
    // Builder 终结
    // ============================================================

    /**
     * Builder 模式终结调用——返回自身。详见 {@link SplitPaneAnt#build()}。
     */
    public ScrollPaneAnt build() {
        return this;
    }
}
