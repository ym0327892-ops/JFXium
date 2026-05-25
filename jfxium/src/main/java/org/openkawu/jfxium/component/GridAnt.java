package org.openkawu.jfxium.component;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 24 列栅格系统组件 - 对标 Ant Design Grid (Row/Col)。
 *
 * <h2>设计说明</h2>
 * 原实现使用 {@code VBox+HBox+setPrefWidth(ratio*100)} 模拟栅格，
 * {@code ratio*100} 是像素值而非百分比，导致：
 * <ul>
 *   <li>列宽固定为像素，不随容器宽度变化（不是真栅格）</li>
 *   <li>{@code offset} 走同样错误的逻辑，偏移量是像素</li>
 *   <li>{@code GridPane} 已 import 但实际未使用</li>
 * </ul>
 *
 * <h2>本次重写约定</h2>
 * <ul>
 *   <li>每行使用一个 {@link GridPane}，配置 24 个 {@link ColumnConstraints}，
 *       每列 {@code percentWidth = 100/24 ≈ 4.17%}</li>
 *   <li>{@code Col.span} 通过 {@link GridPane#setColumnSpan} 跨列实现</li>
 *   <li>{@code Col.offset} 通过起始列号偏移实现，无需占位 Region</li>
 *   <li>{@code gutter / rowGutter / columnGutter} 通过 GridPane 的 hgap/vgap 设置</li>
 *   <li>外层用 {@link VBox} 容纳多行</li>
 * </ul>
 *
 * <h2>响应式断点</h2>
 * 当前版本不实现 xs/sm/md/lg/xl/xxl 断点（需要监听 Scene 宽度变化），
 * 留待后续版本。当前所有断点退化为基础 {@code span}。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * VBox grid = GridAnt.create()
 *     .gutter(16)
 *     .row(GridAnt.row()
 *         .col(12, leftCard)
 *         .col(12, rightCard))
 *     .row(GridAnt.row()
 *         .col(8, c1)
 *         .col(8, c2)
 *         .col(8, c3))
 *     .row(GridAnt.row()
 *         .col(6, 6, alignedCard))   // span=6, offset=6（从第 7 列开始）
 *     .build();
 * }</pre>
 */
public class GridAnt {

    /** 24 列总数，对齐 Ant Design Grid */
    private static final int TOTAL_COLUMNS = 24;
    private static final double COLUMN_PERCENT = 100.0 / TOTAL_COLUMNS;

    public static Builder create() {
        return new Builder();
    }

    /** 创建一个新的 Row 实例（语法糖）*/
    public static Row row() {
        return new Row();
    }

    public static class Col {
        private final int span;
        private final int offset;
        private final Node node;

        public Col(int span, Node node) {
            this(span, 0, node);
        }

        public Col(int span, int offset, Node node) {
            // 防御性约束：span 至少 1，offset 至少 0，且 span+offset 不超过 24
            this.span = clamp(span, 1, TOTAL_COLUMNS);
            this.offset = clamp(offset, 0, TOTAL_COLUMNS - 1);
            this.node = node;
        }

        public int getSpan() { return span; }
        public int getOffset() { return offset; }
        public Node getNode() { return node; }

        private static int clamp(int v, int min, int max) {
            return Math.max(min, Math.min(max, v));
        }
    }

    public static class Row {
        private final List<Col> cols = new ArrayList<>();
        private Pos alignment = Pos.CENTER_LEFT;
        private double height = -1;

        public Row col(int span, Node node) {
            cols.add(new Col(span, node));
            return this;
        }

        public Row col(int span, int offset, Node node) {
            cols.add(new Col(span, offset, node));
            return this;
        }

        public Row align(Pos alignment) {
            this.alignment = alignment;
            return this;
        }

        public Row height(double height) {
            this.height = height;
            return this;
        }

        List<Col> getCols() { return cols; }
        Pos getAlignment() { return alignment; }
        double getHeight() { return height; }
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Row> rows = new ArrayList<>();
        private double gutter = 0;
        private double rowGutter = -1;
        private double columnGutter = -1;

        private Builder() {}

        /** 行列统一间距。{@link #rowGutter}/{@link #columnGutter} 优先级更高。*/
        public Builder gutter(double gutter) {
            this.gutter = gutter;
            return this;
        }

        public Builder rowGutter(double rowGutter) {
            this.rowGutter = rowGutter;
            return this;
        }

        public Builder columnGutter(double columnGutter) {
            this.columnGutter = columnGutter;
            return this;
        }

        public Builder row(Row row) {
            if (row != null) {
                this.rows.add(row);
            }
            return this;
        }

        public VBox build() {
            VBox container = new VBox();
            container.getStyleClass().add(CssClasses.GRID);
            container.setSpacing(effectiveRowGutter());

            for (Row row : rows) {
                container.getChildren().add(buildRow(row));
            }
            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(container);
            return container;
        }

        /**
         * 构建单行：使用 GridPane + 24 个百分比 ColumnConstraints。
         * 这是与原实现的核心差异 —— 用 percentWidth 表达比例，列宽真正按容器宽度伸缩。
         */
        private GridPane buildRow(Row row) {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(CssClasses.GRID_ROW);
            grid.setHgap(effectiveColumnGutter());
            grid.setAlignment(row.getAlignment());
            if (row.getHeight() > 0) {
                grid.setMinHeight(row.getHeight());
                grid.setPrefHeight(row.getHeight());
            }

            // 配置 24 列，每列等宽百分比；hgap 由 GridPane 控制，不影响百分比计算
            for (int i = 0; i < TOTAL_COLUMNS; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(COLUMN_PERCENT);
                cc.setHgrow(Priority.SOMETIMES);
                grid.getColumnConstraints().add(cc);
            }

            // 单行配置：让行能伸展占据 GridPane 剩余高度（如有）
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.SOMETIMES);
            rc.setValignment(toVPos(row.getAlignment()));
            grid.getRowConstraints().add(rc);

            // 把 cols 排进网格：用 startColumn 累计偏移，依次摆放
            int startColumn = 0;
            for (Col col : row.getCols()) {
                int target = startColumn + col.getOffset();
                if (target + col.getSpan() > TOTAL_COLUMNS) {
                    // 超出 24 列时静默截断 span（避免抛异常打断渲染），并在控制台打印告警
                    int truncated = TOTAL_COLUMNS - target;
                    if (truncated <= 0) {
                        break; // 已无可用列
                    }
                    System.err.println("[GridAnt] col span+offset 超过 24 列，已截断到 span=" + truncated);
                    placeNode(grid, col.getNode(), target, truncated, col, row);
                    startColumn = TOTAL_COLUMNS;
                    break;
                }
                placeNode(grid, col.getNode(), target, col.getSpan(), col, row);
                startColumn = target + col.getSpan();
            }

            return grid;
        }

        /** 把节点放置到 GridPane 指定起始列、跨度，并应用 grid-col styleClass + 行对齐。*/
        private void placeNode(GridPane grid, Node node, int startCol, int span, Col col, Row row) {
            if (node == null) return;
            node.getStyleClass().add(CssClasses.GRID_COL);
            GridPane.setColumnIndex(node, startCol);
            GridPane.setRowIndex(node, 0);
            GridPane.setColumnSpan(node, span);
            GridPane.setHalignment(node, toHPos(row.getAlignment()));
            GridPane.setValignment(node, toVPos(row.getAlignment()));
            GridPane.setHgrow(node, Priority.ALWAYS);
            grid.getChildren().add(node);
        }

        private double effectiveRowGutter() {
            return rowGutter >= 0 ? rowGutter : gutter;
        }

        private double effectiveColumnGutter() {
            return columnGutter >= 0 ? columnGutter : gutter;
        }

        private static HPos toHPos(Pos pos) {
            return pos != null ? pos.getHpos() : HPos.LEFT;
        }

        private static VPos toVPos(Pos pos) {
            return pos != null ? pos.getVpos() : VPos.CENTER;
        }
    }
}
