package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

    /** 间距（等同 gap，与 HBoxAnt/VBoxAnt 命名统一）。 */
    public FlowPaneAnt spacing(double spacing) {
        setHgap(spacing);
        setVgap(spacing);
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

    /** 触发换行的首选宽度（水平方向时）或高度（垂直方向时）。 */
    public FlowPaneAnt prefWrapLength(double length) {
        setPrefWrapLength(length);
        return this;
    }

    /** 行内节点的垂直对齐方式（水平方向时生效）。 */
    public FlowPaneAnt rowValignment(javafx.geometry.VPos vpos) {
        setRowValignment(vpos);
        return this;
    }

    /** 列内节点的水平对齐方式（垂直方向时生效）。 */
    public FlowPaneAnt columnHalignment(javafx.geometry.HPos hpos) {
        setColumnHalignment(hpos);
        return this;
    }

    public FlowPaneAnt maxW(double width) {
        setMaxWidth(width);
        return this;
    }

    public FlowPaneAnt maxH(double height) {
        setMaxHeight(height);
        return this;
    }

    public FlowPaneAnt minW(double width) {
        setMinWidth(width);
        return this;
    }

    public FlowPaneAnt minH(double height) {
        setMinHeight(height);
        return this;
    }

    public FlowPaneAnt prefW(double width) {
        setPrefWidth(width);
        return this;
    }

    public FlowPaneAnt prefH(double height) {
        setPrefHeight(height);
        return this;
    }

    /** 同时设置首选宽高。 */
    public FlowPaneAnt prefSize(double w, double h) { setPrefSize(w, h); return this; }
    /** 同时设置最大宽高。 */
    public FlowPaneAnt maxSize(double w, double h) { setMaxSize(w, h); return this; }
    /** 同时设置最小宽高。 */
    public FlowPaneAnt minSize(double w, double h) { setMinSize(w, h); return this; }

    /** 给指定子节点设置外边距。 */
    public FlowPaneAnt margin(Node child, Insets margin) {
        FlowPane.setMargin(child, margin);
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

    // ============================================================
    // 方向性边框线（分割线）
    // ============================================================

    /** 顶部分割线。 */
    public FlowPaneAnt borderTop() { styleClass("border-top"); return this; }
    /** 顶部分割线（开关）。 */
    public FlowPaneAnt borderTop(boolean on) { if (on) return borderTop(); return this; }
    /** 底部分割线。 */
    public FlowPaneAnt borderBottom() { styleClass("border-bottom"); return this; }
    /** 底部分割线（开关）。 */
    public FlowPaneAnt borderBottom(boolean on) { if (on) return borderBottom(); return this; }
    /** 左侧分割线。 */
    public FlowPaneAnt borderLeft() { styleClass("border-left"); return this; }
    /** 左侧分割线（开关）。 */
    public FlowPaneAnt borderLeft(boolean on) { if (on) return borderLeft(); return this; }
    /** 右侧分割线。 */
    public FlowPaneAnt borderRight() { styleClass("border-right"); return this; }
    /** 右侧分割线（开关）。 */
    public FlowPaneAnt borderRight(boolean on) { if (on) return borderRight(); return this; }

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 设置可见性。 */
    public FlowPaneAnt visible(boolean v) { setVisible(v); return this; }
    /** 设置禁用状态。 */
    public FlowPaneAnt disable(boolean d) { setDisable(d); return this; }
    /** 设置是否受布局管理。 */
    public FlowPaneAnt managed(boolean m) { setManaged(m); return this; }
    /** 设置透明度（0.0 ~ 1.0）。 */
    public FlowPaneAnt opacity(double o) { setOpacity(o); return this; }
    /** 设置鼠标光标。 */
    public FlowPaneAnt cursor(Cursor c) { setCursor(c); return this; }
    /** 设置节点 ID。 */
    public FlowPaneAnt id(String id) { setId(id); return this; }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public FlowPaneAnt build() {
        return this;
    }
}
