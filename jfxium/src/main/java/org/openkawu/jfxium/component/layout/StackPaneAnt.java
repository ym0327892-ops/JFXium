package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.css.Background;

/**
 * StackPaneAnt - 继承式 StackPane 容器（M19.36 引入）。
 *
 * <p>子节点叠层（z-axis）的双工厂模式——典型场景：徽标覆盖头像、loading 遮罩盖内容、
 * 浮层覆盖主面板。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * StackPaneAnt avatar = StackPaneAnt.create()
 *     .align(Pos.TOP_RIGHT)
 *     .children(avatarImage, badge);   // badge 浮在右上角
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class LoadingOverlay extends StackPaneAnt {
 *     public LoadingOverlay(Node content) {
 *         children(content, buildLoadingMask());
 *     }
 *     private Node buildLoadingMask() { ... }
 * }
 * }</pre>
 */
public class StackPaneAnt extends StackPane {

    public static StackPaneAnt create() {
        return new StackPaneAnt();
    }

    public static StackPaneAnt create(Node... children) {
        return new StackPaneAnt(children);
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public StackPaneAnt() {
        super();
    }

    public StackPaneAnt(Node... children) {
        super(children);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    public StackPaneAnt padding(double padding) {
        setPadding(new Insets(padding));
        return this;
    }

    public StackPaneAnt padding(double top, double right, double bottom, double left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }

    /** 设置子节点对齐方式（StackPane 默认 CENTER）。 */
    public StackPaneAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    public StackPaneAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    public StackPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public StackPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public StackPaneAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public StackPaneAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public StackPaneAnt build() {
        return this;
    }
}
