package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * SplitPaneAnt - 继承式 SplitPane 容器（M19.36 升级为双工厂模式）。
 *
 * <p>可拖拽分隔的多窗格的双工厂模式——既能当工厂用，也能被业务继承做"分屏页基类"。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * SplitPaneAnt split = SplitPaneAnt.create()
 *     .direction(SplitPaneAnt.Direction.HORIZONTAL)
 *     .items(leftPanel, rightPanel)
 *     .dividerPositions(0.3);
 * }</pre>
 *
 * <h3>2. 业务继承用法（IDE 风格分屏页）</h3>
 * <pre>{@code
 * public class IdePage extends SplitPaneAnt {
 *     public IdePage() {
 *         direction(Direction.HORIZONTAL);
 *         items(buildSidebar(), buildEditorArea());
 *         dividerPositions(0.25);
 *     }
 * }
 * }</pre>
 */
public class SplitPaneAnt extends SplitPane {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    public static SplitPaneAnt create() {
        return new SplitPaneAnt();
    }

    // ============================================================
    // 构造函数（公开，便于业务 extends）
    // ============================================================

    public SplitPaneAnt() {
        super();
        getStyleClass().add(JfxStyles.SPLIT_PANE);
    }

    public SplitPaneAnt(Node... items) {
        super(items);
        getStyleClass().add(JfxStyles.SPLIT_PANE);
    }

    // ============================================================
    // 流式 API
    // ============================================================

    public SplitPaneAnt direction(Direction direction) {
        setOrientation(direction == Direction.VERTICAL
                ? Orientation.VERTICAL : Orientation.HORIZONTAL);
        return this;
    }

    /** 添加单个窗格。 */
    public SplitPaneAnt item(Node item) {
        if (item != null) getItems().add(item);
        return this;
    }

    /** 批量添加窗格。 */
    public SplitPaneAnt items(Node... items) {
        if (items != null) {
            for (Node n : items) {
                if (n != null) getItems().add(n);
            }
        }
        return this;
    }

    /** 设置分隔条位置（0.0 ~ 1.0 比例，可设多个；items 数 - 1 个分隔条）。 */
    public SplitPaneAnt dividerPositions(double... positions) {
        if (positions != null && positions.length > 0) {
            setDividerPositions(positions);
        }
        return this;
    }

    /** 设置某个子节点是否随父容器调整大小。 */
    public SplitPaneAnt resizableWithParent(Node node, boolean resizable) {
        if (node != null) {
            SplitPane.setResizableWithParent(node, resizable);
        }
        return this;
    }

    public SplitPaneAnt padding(double padding) {
        setPadding(new javafx.geometry.Insets(padding));
        return this;
    }

    public SplitPaneAnt padding(double top, double right, double bottom, double left) {
        setPadding(new javafx.geometry.Insets(top, right, bottom, left));
        return this;
    }

    public SplitPaneAnt maxW(double width) {
        setMaxWidth(width);
        return this;
    }

    public SplitPaneAnt maxH(double height) {
        setMaxHeight(height);
        return this;
    }

    public SplitPaneAnt minW(double width) {
        setMinWidth(width);
        return this;
    }

    public SplitPaneAnt minH(double height) {
        setMinHeight(height);
        return this;
    }

    public SplitPaneAnt prefW(double width) {
        setPrefWidth(width);
        return this;
    }

    public SplitPaneAnt prefH(double height) {
        setPrefHeight(height);
        return this;
    }

    /** 同时设置首选宽高。 */
    public SplitPaneAnt prefSize(double w, double h) { setPrefSize(w, h); return this; }
    /** 同时设置最大宽高。 */
    public SplitPaneAnt maxSize(double w, double h) { setMaxSize(w, h); return this; }
    /** 同时设置最小宽高。 */
    public SplitPaneAnt minSize(double w, double h) { setMinSize(w, h); return this; }

    // ============================================================
    // 视觉钩子
    // ============================================================

    public SplitPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public SplitPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public SplitPaneAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public SplitPaneAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    // ============================================================
    // 方向性边框线（分割线）
    // ============================================================

    /** 顶部分割线。 */
    public SplitPaneAnt borderTop() { styleClass("border-top"); return this; }
    /** 顶部分割线（开关）。 */
    public SplitPaneAnt borderTop(boolean on) { if (on) return borderTop(); return this; }
    /** 底部分割线。 */
    public SplitPaneAnt borderBottom() { styleClass("border-bottom"); return this; }
    /** 底部分割线（开关）。 */
    public SplitPaneAnt borderBottom(boolean on) { if (on) return borderBottom(); return this; }
    /** 左侧分割线。 */
    public SplitPaneAnt borderLeft() { styleClass("border-left"); return this; }
    /** 左侧分割线（开关）。 */
    public SplitPaneAnt borderLeft(boolean on) { if (on) return borderLeft(); return this; }
    /** 右侧分割线。 */
    public SplitPaneAnt borderRight() { styleClass("border-right"); return this; }
    /** 右侧分割线（开关）。 */
    public SplitPaneAnt borderRight(boolean on) { if (on) return borderRight(); return this; }

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 设置可见性。 */
    public SplitPaneAnt visible(boolean v) { setVisible(v); return this; }
    /** 设置禁用状态。 */
    public SplitPaneAnt disable(boolean d) { setDisable(d); return this; }
    /** 设置是否受布局管理。 */
    public SplitPaneAnt managed(boolean m) { setManaged(m); return this; }
    /** 设置透明度（0.0 ~ 1.0）。 */
    public SplitPaneAnt opacity(double o) { setOpacity(o); return this; }
    /** 设置鼠标光标。 */
    public SplitPaneAnt cursor(Cursor c) { setCursor(c); return this; }
    /** 设置节点 ID。 */
    public SplitPaneAnt id(String id) { setId(id); return this; }

    /** Builder 模式终结调用，返回自身。详见 {@code VBoxAnt#build()}。 */
    public SplitPaneAnt build() {
        return this;
    }
}
