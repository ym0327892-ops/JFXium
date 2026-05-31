package org.openkawu.jfxium.component.layout;

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

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public ScrollPaneAnt build() {
        return this;
    }
}
