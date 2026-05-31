package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.openkawu.jfxium.core.css.Background;

/**
 * BorderPaneAnt - 继承式 BorderPane 容器（M19.36 引入）。
 *
 * <p>五区位（top/right/bottom/left/center）骨架的双工厂模式——既能当工厂用，
 * 也能被业务继承做"页面骨架基类"。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * BorderPaneAnt shell = BorderPaneAnt.create()
 *     .top(headerBar)
 *     .left(sideMenu)
 *     .center(mainContent)
 *     .background(Background.LAYOUT);
 * }</pre>
 *
 * <h3>2. 业务继承用法（admin 主框架基类）</h3>
 * <pre>{@code
 * public class AppMainShell extends BorderPaneAnt {
 *     public AppMainShell(String currentUser) {
 *         top(buildHeader(currentUser));
 *         left(buildSideMenu());
 *         center(buildContent());
 *         background(Background.LAYOUT);
 *     }
 * }
 * }</pre>
 */
public class BorderPaneAnt extends BorderPane {

    public static BorderPaneAnt create() {
        return new BorderPaneAnt();
    }

    public static BorderPaneAnt create(Node center) {
        BorderPaneAnt p = new BorderPaneAnt();
        p.setCenter(center);
        return p;
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public BorderPaneAnt() {
        super();
    }

    public BorderPaneAnt(Node center) {
        super(center);
    }

    public BorderPaneAnt(Node center, Node top, Node right, Node bottom, Node left) {
        super(center, top, right, bottom, left);
    }

    // ============================================================
    // 流式 API（5 区位）
    // ============================================================

    public BorderPaneAnt top(Node node) {
        setTop(node);
        return this;
    }

    public BorderPaneAnt right(Node node) {
        setRight(node);
        return this;
    }

    public BorderPaneAnt bottom(Node node) {
        setBottom(node);
        return this;
    }

    public BorderPaneAnt left(Node node) {
        setLeft(node);
        return this;
    }

    public BorderPaneAnt center(Node node) {
        setCenter(node);
        return this;
    }

    // ============================================================
    // 装饰
    // ============================================================

    public BorderPaneAnt padding(double padding) {
        setPadding(new Insets(padding));
        return this;
    }

    public BorderPaneAnt padding(double top, double right, double bottom, double left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    public BorderPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public BorderPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public BorderPaneAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public BorderPaneAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public BorderPaneAnt build() {
        return this;
    }
}
