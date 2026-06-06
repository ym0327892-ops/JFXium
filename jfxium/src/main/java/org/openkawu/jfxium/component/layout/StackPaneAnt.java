package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

    /** 设置指定子节点在 StackPane 内的对齐方式（覆盖容器级 align）。 */
    public StackPaneAnt childAlign(Node child, Pos alignment) {
        StackPane.setAlignment(child, alignment);
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public StackPaneAnt margin(Node child, Insets margin) {
        StackPane.setMargin(child, margin);
        return this;
    }

    public StackPaneAnt maxW(double width) {
        setMaxWidth(width);
        return this;
    }

    public StackPaneAnt maxH(double height) {
        setMaxHeight(height);
        return this;
    }

    /** 同时设置 maxWidth 和 maxHeight（常用于 USE_PREF_SIZE 收缩，见 SKILL §20.1）。 */
    public StackPaneAnt maxSize(double width, double height) {
        setMaxWidth(width);
        setMaxHeight(height);
        return this;
    }

    public StackPaneAnt minW(double width) {
        setMinWidth(width);
        return this;
    }

    public StackPaneAnt minH(double height) {
        setMinHeight(height);
        return this;
    }

    public StackPaneAnt prefW(double width) {
        setPrefWidth(width);
        return this;
    }

    public StackPaneAnt prefH(double height) {
        setPrefHeight(height);
        return this;
    }

    /** 同时设置首选宽高。 */
    public StackPaneAnt prefSize(double w, double h) { setPrefSize(w, h); return this; }
    /** 同时设置最小宽高。 */
    public StackPaneAnt minSize(double w, double h) { setMinSize(w, h); return this; }

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

    // ============================================================
    // 方向性边框线（分割线）
    // ============================================================

    /** 顶部分割线。 */
    public StackPaneAnt borderTop() { styleClass("border-top"); return this; }
    /** 顶部分割线（开关）。 */
    public StackPaneAnt borderTop(boolean on) { if (on) return borderTop(); return this; }
    /** 底部分割线。 */
    public StackPaneAnt borderBottom() { styleClass("border-bottom"); return this; }
    /** 底部分割线（开关）。 */
    public StackPaneAnt borderBottom(boolean on) { if (on) return borderBottom(); return this; }
    /** 左侧分割线。 */
    public StackPaneAnt borderLeft() { styleClass("border-left"); return this; }
    /** 左侧分割线（开关）。 */
    public StackPaneAnt borderLeft(boolean on) { if (on) return borderLeft(); return this; }
    /** 右侧分割线。 */
    public StackPaneAnt borderRight() { styleClass("border-right"); return this; }
    /** 右侧分割线（开关）。 */
    public StackPaneAnt borderRight(boolean on) { if (on) return borderRight(); return this; }

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 设置可见性。 */
    public StackPaneAnt visible(boolean v) { setVisible(v); return this; }
    /** 设置禁用状态。 */
    public StackPaneAnt disable(boolean d) { setDisable(d); return this; }
    /** 设置是否受布局管理。 */
    public StackPaneAnt managed(boolean m) { setManaged(m); return this; }
    /** 设置透明度（0.0 ~ 1.0）。 */
    public StackPaneAnt opacity(double o) { setOpacity(o); return this; }
    /** 设置鼠标光标。 */
    public StackPaneAnt cursor(Cursor c) { setCursor(c); return this; }
    /** 设置节点 ID。 */
    public StackPaneAnt id(String id) { setId(id); return this; }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public StackPaneAnt build() {
        return this;
    }
}
