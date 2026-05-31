package org.openkawu.jfxium.component.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import org.openkawu.jfxium.core.css.Background;

/**
 * GridPaneAnt - 继承式 GridPane 容器（M19.36 引入）。
 *
 * <p>JavaFX 原生二维网格的双工厂模式。**注意跟 {@link GridAnt} 区分**：</p>
 *
 * <ul>
 *   <li>{@link GridPaneAnt}（本类）—— 原生 JavaFX GridPane 的轻量包装，
 *       行/列约束完全由调用方控制（用 row/col index 直接 add 节点）</li>
 *   <li>{@link GridAnt} —— Ant Design 风格的 24 栅格响应式系统，
 *       带 row()/col() 抽象 + xs/sm/md/lg 断点</li>
 * </ul>
 *
 * <p>简单网格用 GridPaneAnt；响应式 24 栅格用 GridAnt。</p>
 *
 * <h2>用法</h2>
 *
 * <h3>1. 工厂用法</h3>
 * <pre>{@code
 * GridPaneAnt grid = GridPaneAnt.create()
 *     .hgap(12).vgap(12)
 *     .cell(new Label("用户名"), 0, 0)
 *     .cell(usernameField, 1, 0)
 *     .cell(new Label("邮箱"), 0, 1)
 *     .cell(emailField, 1, 1);
 * }</pre>
 *
 * <h3>2. 业务继承用法</h3>
 * <pre>{@code
 * public class FormGrid extends GridPaneAnt {
 *     public FormGrid() {
 *         hgap(12).vgap(12);
 *         cell(new Label("用户名"), 0, 0).cell(usernameField, 1, 0);
 *         cell(new Label("邮箱"), 0, 1).cell(emailField, 1, 1);
 *     }
 * }
 * }</pre>
 */
public class GridPaneAnt extends GridPane {

    public static GridPaneAnt create() {
        return new GridPaneAnt();
    }

    // ============================================================
    // 构造函数
    // ============================================================

    public GridPaneAnt() {
        super();
    }

    // ============================================================
    // 流式 API
    // ============================================================

    public GridPaneAnt hgap(double hgap) {
        setHgap(hgap);
        return this;
    }

    public GridPaneAnt vgap(double vgap) {
        setVgap(vgap);
        return this;
    }

    /** 同时设置 hgap 和 vgap。 */
    public GridPaneAnt gap(double gap) {
        setHgap(gap);
        setVgap(gap);
        return this;
    }

    public GridPaneAnt align(Pos alignment) {
        setAlignment(alignment);
        return this;
    }

    public GridPaneAnt padding(double padding) {
        setPadding(new Insets(padding));
        return this;
    }

    public GridPaneAnt padding(double top, double right, double bottom, double left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }

    /** 在指定行列添加节点（链式版，等价于 {@link GridPane#add(Node, int, int)}）。 */
    public GridPaneAnt cell(Node node, int columnIndex, int rowIndex) {
        super.add(node, columnIndex, rowIndex);
        return this;
    }

    /** 添加跨多行多列的节点（链式版，等价于 {@link GridPane#add(Node, int, int, int, int)}）。 */
    public GridPaneAnt cell(Node node, int columnIndex, int rowIndex,
                            int columnSpan, int rowSpan) {
        super.add(node, columnIndex, rowIndex, columnSpan, rowSpan);
        return this;
    }

    /** 添加一行子节点（链式版，按列索引 0..N 排列）。 */
    public GridPaneAnt row(int rowIndex, Node... children) {
        super.addRow(rowIndex, children);
        return this;
    }

    /** 添加一列子节点（链式版，按行索引 0..N 排列）。 */
    public GridPaneAnt column(int columnIndex, Node... children) {
        super.addColumn(columnIndex, children);
        return this;
    }

    // ============================================================
    // 视觉钩子
    // ============================================================

    public GridPaneAnt styleClass(String cls) {
        if (cls != null && !cls.isEmpty() && !getStyleClass().contains(cls)) {
            getStyleClass().add(cls);
        }
        return this;
    }

    public GridPaneAnt styleClass(String... classes) {
        if (classes != null) {
            for (String c : classes) styleClass(c);
        }
        return this;
    }

    public GridPaneAnt background(Background bg) {
        if (bg != null) styleClass(bg.styleClass());
        return this;
    }

    public GridPaneAnt style(String style) {
        if (style != null) setStyle(style);
        return this;
    }

    /** Builder 模式终结调用，返回自身。详见 {@link VBoxAnt#build()}。 */
    public GridPaneAnt build() {
        return this;
    }
}
