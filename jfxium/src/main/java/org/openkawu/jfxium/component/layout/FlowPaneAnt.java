package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.openkawu.jfxium.core.css.Background;

/**
 * FlowPaneAnt - 继承式 FlowPane 容器（M19.36 引入）。
 *
 * <p>流式布局（自动换行）的双工厂模式——典型场景：标签云、自适应按钮组、
 * 不定数量子节点 wrap 排列。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * FlowPaneAnt tagCloud = FlowPaneAnt.create()
 *     .hgap(8).vgap(8)
 *     .children(tag1, tag2, tag3, tag4, ...);
 * }</pre>
 */
public class FlowPaneAnt extends FlowPane {

    public static FlowPaneAnt create() {
        return new FlowPaneAnt();
    }

    public static FlowPaneAnt create(Node... children) {
        return new FlowPaneAnt(children);
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public FlowPaneAnt() {
        super();
    }

    public FlowPaneAnt(Node... children) {
        super(children);
    }

    public FlowPaneAnt(Orientation orientation) {
        super(orientation);
    }

    public FlowPaneAnt(double hgap, double vgap) {
        super(hgap, vgap);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 子节点之间的水平间距。 */
    public FlowPaneAnt hgap(double hgap) {
        setHgap(hgap);
        return this;
    }

    /** 子节点之间的垂直间距。 */
    public FlowPaneAnt vgap(double vgap) {
        setVgap(vgap);
        return this;
    }

    /** 同时设置 hgap 和 vgap。 */
    public FlowPaneAnt gap(double gap) {
        setHgap(gap);
        setVgap(gap);
        return this;
    }

    public FlowPaneAnt orientation(Orientation orientation) {
        setOrientation(orientation);
        return this;
    }

    public FlowPaneAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    public FlowPaneAnt padding(double padding) {
        setPadding(new Insets(padding));
        return this;
    }

    public FlowPaneAnt padding(double top, double right, double bottom, double left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }

    public FlowPaneAnt children(Node... nodes) {
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

    public FlowPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public FlowPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public FlowPaneAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public FlowPaneAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public FlowPaneAnt build() {
        return this;
    }
}
