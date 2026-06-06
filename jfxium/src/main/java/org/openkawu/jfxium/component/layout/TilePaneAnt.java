package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium 平铺布局组件 - 对标 Ant Design 的平铺/缩略图网格。
 *
 * <p><b>定位</b>：平铺排列容器，继承自 JavaFX {@link TilePane}。
 * 子节点按固定行列数平铺排列，常用于缩略图网格、图标面板、图片墙等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>行列数</b>：prefColumns(n) / prefRows(n) 设置首选行列数</li>
 *   <li><b>方向</b>：orientation(HORIZONTAL/VERTICAL) 排列方向</li>
 *   <li><b>间距</b>：hgap(vgap) / vgap(vgap) 行列间距</li>
 *   <li><b>对齐</b>：alignment(Pos) 整体对齐方式</li>
 *   <li><b>子节点</b>：children(Node...) 批量添加子节点</li>
 *   <li><b>视觉</b>：走 {@link CssClasses#TILE_PANE} LESS 样式</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 缩略图网格（4列）
 * TilePaneAnt grid = TilePaneAnt.create()
 *     .prefColumns(4)
 *     .hgap(10).vgap(10)
 *     .alignment(Pos.CENTER)
 *     .children(thumb1, thumb2, thumb3, thumb4)
 *     .build();
 *
 * // 垂直平铺（图标面板）
 * TilePaneAnt icons = TilePaneAnt.create()
 *     .orientation(Orientation.VERTICAL)
 *     .prefRows(3)
 *     .hgap(8).vgap(8)
 *     .children(icon1, icon2, icon3)
 *     .build();
 * }</pre>
 */
public class TilePaneAnt extends TilePane {

    // ============================================================
    // 工厂入口
    // ============================================================

    public static TilePaneAnt create() {
        return new TilePaneAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public TilePaneAnt() {
        super();
        getStyleClass().add(CssClasses.TILE_PANE);
    }

    // ============================================================
    // 流式配置
    // ============================================================

    public TilePaneAnt prefColumns(int columns) {
        setPrefColumns(columns);
        return this;
    }

    public TilePaneAnt prefRows(int rows) {
        setPrefRows(rows);
        return this;
    }

    public TilePaneAnt orientation(Orientation orientation) {
        setOrientation(orientation);
        return this;
    }

    public TilePaneAnt hgap(double gap) {
        setHgap(gap);
        return this;
    }

    public TilePaneAnt vgap(double gap) {
        setVgap(gap);
        return this;
    }

    public TilePaneAnt gap(double gap) {
        setHgap(gap);
        setVgap(gap);
        return this;
    }

    public TilePaneAnt alignment(javafx.geometry.Pos pos) {
        setAlignment(pos);
        return this;
    }

    public TilePaneAnt children(Node... nodes) {
        if (nodes != null) {
            getChildren().addAll(nodes);
        }
        return this;
    }

    public TilePaneAnt add(Node node) {
        if (node != null) {
            getChildren().add(node);
        }
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    /** 追加一个 styleClass（幂等——重复调不会重复挂）。 */
    public TilePaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    /** 批量挂多个 styleClass（变长重载）。 */
    public TilePaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    // ============================================================
    // 方向性边框线（分割线）
    // ============================================================

    /** 顶部分割线。 */
    public TilePaneAnt borderTop() { styleClass("border-top"); return this; }
    /** 顶部分割线（开关）。 */
    public TilePaneAnt borderTop(boolean on) { if (on) return borderTop(); return this; }
    /** 底部分割线。 */
    public TilePaneAnt borderBottom() { styleClass("border-bottom"); return this; }
    /** 底部分割线（开关）。 */
    public TilePaneAnt borderBottom(boolean on) { if (on) return borderBottom(); return this; }
    /** 左侧分割线。 */
    public TilePaneAnt borderLeft() { styleClass("border-left"); return this; }
    /** 左侧分割线（开关）。 */
    public TilePaneAnt borderLeft(boolean on) { if (on) return borderLeft(); return this; }
    /** 右侧分割线。 */
    public TilePaneAnt borderRight() { styleClass("border-right"); return this; }
    /** 右侧分割线（开关）。 */
    public TilePaneAnt borderRight(boolean on) { if (on) return borderRight(); return this; }

    // ============================================================
    // 高频节点属性
    // ============================================================

    /** 设置可见性。 */
    public TilePaneAnt visible(boolean v) { setVisible(v); return this; }
    /** 设置禁用状态。 */
    public TilePaneAnt disable(boolean d) { setDisable(d); return this; }
    /** 设置是否受布局管理。 */
    public TilePaneAnt managed(boolean m) { setManaged(m); return this; }
    /** 设置透明度（0.0 ~ 1.0）。 */
    public TilePaneAnt opacity(double o) { setOpacity(o); return this; }
    /** 设置鼠标光标。 */
    public TilePaneAnt cursor(Cursor c) { setCursor(c); return this; }
    /** 设置节点 ID。 */
    public TilePaneAnt id(String id) { setId(id); return this; }

    /** 同时设置首选宽高。 */
    public TilePaneAnt prefSize(double w, double h) { setPrefSize(w, h); return this; }
    /** 同时设置最大宽高。 */
    public TilePaneAnt maxSize(double w, double h) { setMaxSize(w, h); return this; }
    /** 同时设置最小宽高。 */
    public TilePaneAnt minSize(double w, double h) { setMinSize(w, h); return this; }

    // ============================================================
    // 构建
    // ============================================================

    public TilePaneAnt build() {
        return this;
    }
}
