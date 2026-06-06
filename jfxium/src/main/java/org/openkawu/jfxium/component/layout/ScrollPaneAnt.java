package org.openkawu.jfxium.component.layout;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import org.openkawu.jfxium.core.css.Background;

/**
 * ScrollPaneAnt - 继承式 ScrollPane 容器（M19.36 引入）。
 *
 * <p>JavaFX 原生 ScrollPane 的双工厂模式——简单滚动场景首选。
 * 详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>跟 ScrollContainerAnt 的区别</h2>
 * <ul>
 *   <li><b>ScrollPaneAnt</b>（本类）—— 裸 ScrollPane 的双工厂，可继承，
 *       内容直接放，无 viewport 包装</li>
 *   <li><b>ScrollContainerAnt</b> —— 老版 Builder 模式，会自动包一个 StackPane viewport
 *       便于 padding 控制；适合需要"内容内边距"的场景</li>
 * </ul>
 *
 * <p>简单滚动场景用 ScrollPaneAnt；需要 viewport 内边距用 ScrollContainerAnt。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * ScrollPaneAnt scroll = ScrollPaneAnt.create()
 *     .content(longContent)
 *     .fitToWidth(true)
 *     .background(Background.LAYOUT);
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class PageScroll extends ScrollPaneAnt {
 *     public PageScroll(Node body) {
 *         content(body);
 *         fitToWidth(true);
 *         hbarPolicy(ScrollBarPolicy.NEVER);
 *     }
 * }
 * }</pre>
 */
public class ScrollPaneAnt extends ScrollPane {

    public static ScrollPaneAnt create() {
        return new ScrollPaneAnt();
    }

    public static ScrollPaneAnt create(Node content) {
        return new ScrollPaneAnt(content);
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public ScrollPaneAnt() {
        super();
    }

    public ScrollPaneAnt(Node content) {
        super(content);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    public ScrollPaneAnt content(Node content) {
        setContent(content);
        return this;
    }

    public ScrollPaneAnt fitToWidth(boolean fit) {
        setFitToWidth(fit);
        return this;
    }

    public ScrollPaneAnt fitToHeight(boolean fit) {
        setFitToHeight(fit);
        return this;
    }

    public ScrollPaneAnt pannable(boolean pannable) {
        setPannable(pannable);
        return this;
    }

    public ScrollPaneAnt hbarPolicy(ScrollBarPolicy policy) {
        setHbarPolicy(policy);
        return this;
    }

    public ScrollPaneAnt vbarPolicy(ScrollBarPolicy policy) {
        setVbarPolicy(policy);
        return this;
    }

    /** 设置视口首选宽度。 */
    public ScrollPaneAnt prefViewportWidth(double width) {
        setPrefViewportWidth(width);
        return this;
    }

    /** 设置视口首选高度。 */
    public ScrollPaneAnt prefViewportHeight(double height) {
        setPrefViewportHeight(height);
        return this;
    }

    /** 设置视口最小宽度。 */
    public ScrollPaneAnt minViewportWidth(double width) {
        setMinViewportWidth(width);
        return this;
    }

    /** 设置视口最小高度。 */
    public ScrollPaneAnt minViewportHeight(double height) {
        setMinViewportHeight(height);
        return this;
    }

    public ScrollPaneAnt maxW(double width) {
        setMaxWidth(width);
        return this;
    }

    public ScrollPaneAnt maxH(double height) {
        setMaxHeight(height);
        return this;
    }

    public ScrollPaneAnt minW(double width) {
        setMinWidth(width);
        return this;
    }

    public ScrollPaneAnt minH(double height) {
        setMinHeight(height);
        return this;
    }

    public ScrollPaneAnt prefW(double width) {
        setPrefWidth(width);
        return this;
    }

    public ScrollPaneAnt prefH(double height) {
        setPrefHeight(height);
        return this;
    }

    /** 同时设置首选宽高。 */
    public ScrollPaneAnt prefSize(double w, double h) { setPrefSize(w, h); return this; }
    /** 同时设置最大宽高。 */
    public ScrollPaneAnt maxSize(double w, double h) { setMaxSize(w, h); return this; }
    /** 同时设置最小宽高。 */
    public ScrollPaneAnt minSize(double w, double h) { setMinSize(w, h); return this; }

    // ============================================================
    // 视觉钩子
    // ============================================================

    public ScrollPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public ScrollPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public ScrollPaneAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public ScrollPaneAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    // ============================================================
    // 方向性边框线（分割线）
    // ============================================================

    /** 顶部分割线。 */
    public ScrollPaneAnt borderTop() { styleClass("border-top"); return this; }
    /** 顶部分割线（开关）。 */
    public ScrollPaneAnt borderTop(boolean on) { if (on) return borderTop(); return this; }
    /** 底部分割线。 */
    public ScrollPaneAnt borderBottom() { styleClass("border-bottom"); return this; }
    /** 底部分割线（开关）。 */
    public ScrollPaneAnt borderBottom(boolean on) { if (on) return borderBottom(); return this; }
    /** 左侧分割线。 */
    public ScrollPaneAnt borderLeft() { styleClass("border-left"); return this; }
    /** 左侧分割线（开关）。 */
    public ScrollPaneAnt borderLeft(boolean on) { if (on) return borderLeft(); return this; }
    /** 右侧分割线。 */
    public ScrollPaneAnt borderRight() { styleClass("border-right"); return this; }
    /** 右侧分割线（开关）。 */
    public ScrollPaneAnt borderRight(boolean on) { if (on) return borderRight(); return this; }

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 设置可见性。 */
    public ScrollPaneAnt visible(boolean v) { setVisible(v); return this; }
    /** 设置禁用状态。 */
    public ScrollPaneAnt disable(boolean d) { setDisable(d); return this; }
    /** 设置是否受布局管理。 */
    public ScrollPaneAnt managed(boolean m) { setManaged(m); return this; }
    /** 设置透明度（0.0 ~ 1.0）。 */
    public ScrollPaneAnt opacity(double o) { setOpacity(o); return this; }
    /** 设置鼠标光标。 */
    public ScrollPaneAnt cursor(Cursor c) { setCursor(c); return this; }
    /** 设置节点 ID。 */
    public ScrollPaneAnt id(String id) { setId(id); return this; }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public ScrollPaneAnt build() {
        return this;
    }
}
