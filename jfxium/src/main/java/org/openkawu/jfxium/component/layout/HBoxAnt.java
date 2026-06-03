package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.core.css.Background;

/**
 * HBoxAnt - 继承式 HBox 容器（M19.36 引入）。
 *
 * <p>水平布局的双工厂模式——既能当工厂用，也能被业务继承。详细设计参见 {@link VBoxAnt}。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * HBoxAnt toolbar = HBoxAnt.create()
 *     .spacing(8)
 *     .align(Pos.CENTER_LEFT)
 *     .background(Background.SUBTLE)
 *     .children(searchField, filterCombo, addBtn);
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class ToolBar extends HBoxAnt {
 *     public ToolBar() {
 *         spacing(8);
 *         padding(8, 16, 8, 16);
 *         align(Pos.CENTER_LEFT);
 *         children(searchField, filterCombo, addBtn);
 *     }
 * }
 * }</pre>
 */
public class HBoxAnt extends HBox {

    /** 工厂入口。 */
    public static HBoxAnt create() {
        return new HBoxAnt();
    }

    /** 工厂入口（带初始子节点）。 */
    public static HBoxAnt create(Node... children) {
        return new HBoxAnt(children);
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public HBoxAnt() {
        super();
    }

    public HBoxAnt(Node... children) {
        super(children);
    }

    public HBoxAnt(double spacing) {
        super(spacing);
    }

    public HBoxAnt(double spacing, Node... children) {
        super(spacing, children);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    /** 设置子节点之间的水平间距。 */
    public HBoxAnt spacing(double spacing) {
        setSpacing(spacing);
        return this;
    }

    public HBoxAnt padding(double padding) {
        setPadding(new Insets(padding));
        return this;
    }

    public HBoxAnt padding(double top, double right, double bottom, double left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }

    /** 设置子节点对齐方式（默认 {@code CENTER_LEFT}，与 HBoxBuilder 一致）。 */
    public HBoxAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    public HBoxAnt children(Node... nodes) {
        if (nodes != null) {
            for (Node n : nodes) {
                if (n != null) getChildren().add(n);
            }
        }
        return this;
    }

    /** HBox 是否让子节点垂直撑满（默认 true）。 */
    public HBoxAnt fillHeight(boolean fill) {
        setFillHeight(fill);
        return this;
    }

    /** 给指定子节点设置水平拉伸优先级。 */
    public HBoxAnt hgrow(Node child, Priority priority) {
        HBox.setHgrow(child, priority);
        return this;
    }

    /** 给指定子节点设置外边距。 */
    public HBoxAnt margin(Node child, Insets margin) {
        HBox.setMargin(child, margin);
        return this;
    }

    public HBoxAnt maxW(double width) {
        setMaxWidth(width);
        return this;
    }

    public HBoxAnt maxH(double height) {
        setMaxHeight(height);
        return this;
    }

    public HBoxAnt minW(double width) {
        setMinWidth(width);
        return this;
    }

    public HBoxAnt minH(double height) {
        setMinHeight(height);
        return this;
    }

    public HBoxAnt prefW(double width) {
        setPrefWidth(width);
        return this;
    }

    public HBoxAnt prefH(double height) {
        setPrefHeight(height);
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    public HBoxAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public HBoxAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public HBoxAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public HBoxAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /**
     * Builder 模式终结调用——返回自身。详见 {@link VBoxAnt#build()}。
     */
    public HBoxAnt build() {
        return this;
    }
}
