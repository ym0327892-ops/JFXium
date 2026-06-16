package org.openkawu.jfxium.component.layout;

import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 24 列栅格系统组件 - 对标 Ant Design Grid (Row/Col)。
 *
 * <h2>响应式断点（M19.21 新增）</h2>
 * 5 个标准断点（对齐 Ant Design / Bootstrap）：
 * <table>
 *   <caption>断点宽度</caption>
 *   <tr><th>断点</th><th>宽度（px）</th><th>适用</th></tr>
 *   <tr><td>xs</td><td>&lt; 576</td><td>极窄（手机竖屏）</td></tr>
 *   <tr><td>sm</td><td>≥ 576</td><td>窄（手机横屏）</td></tr>
 *   <tr><td>md</td><td>≥ 768</td><td>平板</td></tr>
 *   <tr><td>lg</td><td>≥ 992</td><td>桌面</td></tr>
 *   <tr><td>xl</td><td>≥ 1200</td><td>大桌面</td></tr>
 *   <tr><td>xxl</td><td>≥ 1600</td><td>超大桌面</td></tr>
 * </table>
 *
 * <p>Col 可以为不同断点指定不同的 span，未指定时回退到下一档（xxl→xl→lg→md→sm→xs→默认 span）。</p>
 *
 * <h2>使用示例</h2>
 *
 * <h3>基础（不响应式）</h3>
 * <pre>{@code
 * VBox grid = GridAnt.create()
 *     .gutter(16)
 *     .row(GridAnt.row()
 *         .col(12, leftCard)
 *         .col(12, rightCard))
 *     .build();
 * }</pre>
 *
 * <h3>响应式（窄屏 1 列、宽屏 4 列）</h3>
 * <pre>{@code
 * VBox grid = GridAnt.create()
 *     .gutter(16)
 *     .responsive()                // 启用断点监听
 *     .row(GridAnt.row()
 *         .col(GridAnt.col(card1).xs(24).sm(12).md(8).lg(6))
 *         .col(GridAnt.col(card2).xs(24).sm(12).md(8).lg(6))
 *         .col(GridAnt.col(card3).xs(24).sm(12).md(8).lg(6))
 *         .col(GridAnt.col(card4).xs(24).sm(12).md(8).lg(6)))
 *     .build();
 * }</pre>
 */
public class GridAnt {

    /** 24 列总数，对齐 Ant Design Grid */
    private static final int TOTAL_COLUMNS = 24;
    private static final double COLUMN_PERCENT = 100.0 / TOTAL_COLUMNS;

    private static int clampSpan(int span) {
        return Math.max(1, Math.min(TOTAL_COLUMNS, span));
    }

    private static int clampOffset(int offset) {
        return Math.max(0, Math.min(TOTAL_COLUMNS - 1, offset));
    }

    /** 响应式断点（M19.21）。 */
    public enum Breakpoint {
        XS(0),     // < 576
        SM(576),   // ≥ 576
        MD(768),   // ≥ 768
        LG(992),   // ≥ 992
        XL(1200),  // ≥ 1200
        XXL(1600); // ≥ 1600

        private final double minWidth;
        Breakpoint(double minWidth) { this.minWidth = minWidth; }
        public double getMinWidth() { return minWidth; }

        /** 根据宽度返回当前所属断点（取最大且 ≤ 当前宽度的）。 */
        public static Breakpoint of(double width) {
            Breakpoint result = XS;
            for (Breakpoint b : values()) {
                if (width >= b.minWidth) result = b;
            }
            return result;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    /** 创建一个新的 Row 实例（语法糖）。 */
    public static Row row() {
        return new Row();
    }

    /** 创建一个响应式 Col Builder（语法糖）。 */
    public static ColBuilder col(Node node) {
        return new ColBuilder(node);
    }

    /** 老 API：固定 span 的 Col。 */
    public static class Col {
        private final int span;
        private final int offset;
        private final Node node;
        // 响应式 span（M19.21）—— -1 表示未设置，回退到下一档
        private final int xs;
        private final int sm;
        private final int md;
        private final int lg;
        private final int xl;
        private final int xxl;
        // 响应式 offset（同上）
        private final int xsOffset;
        private final int smOffset;
        private final int mdOffset;
        private final int lgOffset;
        private final int xlOffset;
        private final int xxlOffset;

        public Col(int span, Node node) {
            this(span, 0, node);
        }

        public Col(int span, int offset, Node node) {
            this.span = clampSpan(span);
            this.offset = clampOffset(offset);
            this.node = node;
            this.xs = this.sm = this.md = this.lg = this.xl = this.xxl = -1;
            this.xsOffset = this.smOffset = this.mdOffset = this.lgOffset = this.xlOffset = this.xxlOffset = -1;
        }

        Col(int span, int offset, Node node,
            int xs, int sm, int md, int lg, int xl, int xxl,
            int xsOffset, int smOffset, int mdOffset, int lgOffset, int xlOffset, int xxlOffset) {
            this.span = clampSpan(span);
            this.offset = clampOffset(offset);
            this.node = node;
            this.xs = xs; this.sm = sm; this.md = md;
            this.lg = lg; this.xl = xl; this.xxl = xxl;
            this.xsOffset = xsOffset; this.smOffset = smOffset; this.mdOffset = mdOffset;
            this.lgOffset = lgOffset; this.xlOffset = xlOffset; this.xxlOffset = xxlOffset;
        }

        public int getSpan() { return span; }
        public int getOffset() { return offset; }
        public Node getNode() { return node; }

        /**
         * 按当前断点解析 effective span：从当前断点向下回退（xxl→xl→lg→md→sm→xs→默认 span）。
         */
        int effectiveSpan(Breakpoint bp) {
            return resolveBreakpointValue(bp,
                    new int[]{xs, sm, md, lg, xl, xxl},
                    span);
        }

        int effectiveOffset(Breakpoint bp) {
            return resolveBreakpointValue(bp,
                    new int[]{xsOffset, smOffset, mdOffset, lgOffset, xlOffset, xxlOffset},
                    offset);
        }

        /**
         * 断点回退查找：从当前 bp 向下，找第一个非 -1 的值；都没设则用 fallback。
         * 数组顺序：[xs, sm, md, lg, xl, xxl]
         */
        private static int resolveBreakpointValue(Breakpoint bp, int[] values, int fallback) {
            int idx = bp.ordinal();
            // 从 idx 向下找第一个有效值
            for (int i = idx; i >= 0; i--) {
                if (values[i] >= 0) return values[i];
            }
            return fallback;
        }

    }

    /** 响应式 Col 链式构造器（M19.21）。 */
    public static class ColBuilder {
        private final Node node;
        private int span = -1;
        private int offset = 0;
        private int xs = -1, sm = -1, md = -1, lg = -1, xl = -1, xxl = -1;
        private int xsOffset = -1, smOffset = -1, mdOffset = -1, lgOffset = -1, xlOffset = -1, xxlOffset = -1;

        ColBuilder(Node node) {
            this.node = node;
        }

        /** 默认 span（所有断点未设置时的回退值；不设则默认 24）。 */
        public ColBuilder span(int span) { this.span = clampSpan(span); return this; }
        public ColBuilder offset(int offset) { this.offset = clampOffset(offset); return this; }
        public ColBuilder xs(int span) { this.xs = clampSpan(span); return this; }
        public ColBuilder sm(int span) { this.sm = clampSpan(span); return this; }
        public ColBuilder md(int span) { this.md = clampSpan(span); return this; }
        public ColBuilder lg(int span) { this.lg = clampSpan(span); return this; }
        public ColBuilder xl(int span) { this.xl = clampSpan(span); return this; }
        public ColBuilder xxl(int span) { this.xxl = clampSpan(span); return this; }
        public ColBuilder xsOffset(int o) { this.xsOffset = clampOffset(o); return this; }
        public ColBuilder smOffset(int o) { this.smOffset = clampOffset(o); return this; }
        public ColBuilder mdOffset(int o) { this.mdOffset = clampOffset(o); return this; }
        public ColBuilder lgOffset(int o) { this.lgOffset = clampOffset(o); return this; }
        public ColBuilder xlOffset(int o) { this.xlOffset = clampOffset(o); return this; }
        public ColBuilder xxlOffset(int o) { this.xxlOffset = clampOffset(o); return this; }

        Col toCol() {
            int defaultSpan = span > 0 ? span : TOTAL_COLUMNS;
            return new Col(defaultSpan, offset, node,
                    xs, sm, md, lg, xl, xxl,
                    xsOffset, smOffset, mdOffset, lgOffset, xlOffset, xxlOffset);
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

        /** 响应式 Col（M19.21）。 */
        public Row col(ColBuilder cb) {
            if (cb != null) cols.add(cb.toCol());
            return this;
        }

        public Row align(Pos alignment) {
            if (alignment != null) {
                this.alignment = alignment;
            }
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
        private boolean responsive = false;

        private Builder() {}

        public Builder gutter(double gutter) {
            this.gutter = clampGap(gutter);
            return this;
        }

        public Builder rowGutter(double rowGutter) {
            this.rowGutter = clampGap(rowGutter);
            return this;
        }

        public Builder columnGutter(double columnGutter) {
            this.columnGutter = clampGap(columnGutter);
            return this;
        }

        public Builder row(Row row) {
            if (row != null) {
                this.rows.add(row);
            }
            return this;
        }

        /**
         * 启用响应式断点监听（M19.21）。
         * 启用后，Col.xs/sm/md/lg/xl/xxl 配置会随 Scene 宽度变化自动生效。
         * 不启用时所有断点退化为基础 span（保持老 API 行为）。
         */
        public Builder responsive() {
            this.responsive = true;
            return this;
        }

        public Builder responsive(boolean responsive) {
            this.responsive = responsive;
            return this;
        }

        public VBox build() {
            VBox container = new VBox();
            container.getStyleClass().add(JfxStyles.GRID);
            container.setSpacing(effectiveRowGutter());

            Breakpoint initialBp = initialBreakpoint();
            for (Row row : rows) {
                container.getChildren().add(buildRow(row, initialBp));
            }

            // 响应式：监听 Scene 宽度，跨断点时重建所有行
            if (responsive) {
                attachResponsiveListener(container);
            }

            applyStyles(container);
            return container;
        }

        /**
         * 监听 Scene 宽度变化，跨断点时重新构建所有 row。
         * 用 Scene 宽度而非 container 自身宽度的原因：响应式断点是「应用窗口尺寸」语义，
         * 跟容器在哪嵌套无关。
         */
        private void attachResponsiveListener(VBox container) {
            final Breakpoint[] currentBp = {initialBreakpoint()};

            ChangeListener<Number> widthListener = (obs, oldVal, newVal) -> {
                if (newVal == null) return;
                Breakpoint newBp = Breakpoint.of(newVal.doubleValue());
                if (newBp != currentBp[0]) {
                    currentBp[0] = newBp;
                    rebuildRows(container, newBp);
                }
            };

            // 容器入场景图后才能拿到 Scene；用 sceneProperty 监听
            container.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (oldScene != null) {
                    oldScene.widthProperty().removeListener(widthListener);
                }
                if (newScene != null) {
                    newScene.widthProperty().addListener(widthListener);
                    // 入场景图当下立即按当前宽度刷新一次
                    Breakpoint bp = Breakpoint.of(newScene.getWidth());
                    if (bp != currentBp[0]) {
                        currentBp[0] = bp;
                        rebuildRows(container, bp);
                    }
                }
            });
        }

        /** 跨断点时清空容器、用新断点重建所有行。 */
        private void rebuildRows(VBox container, Breakpoint bp) {
            for (Node child : new ArrayList<>(container.getChildren())) {
                if (child instanceof GridPane grid) {
                    grid.getChildren().clear();
                }
            }
            container.getChildren().clear();
            for (Row row : rows) {
                container.getChildren().add(buildRow(row, bp));
            }
        }

        /**
         * 构建一个逻辑 Row：使用 GridPane + 24 个百分比 ColumnConstraints。
         * 当 Col 累计超过 24 列时，自动换到下一条物理行继续放置。
         */
        private GridPane buildRow(Row row, Breakpoint bp) {
            GridPane grid = new GridPane();
            grid.getStyleClass().add(JfxStyles.GRID_ROW);
            grid.setHgap(effectiveColumnGutter());
            grid.setVgap(effectiveRowGutter());
            grid.setAlignment(row.getAlignment());

            for (int i = 0; i < TOTAL_COLUMNS; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(COLUMN_PERCENT);
                cc.setHgrow(Priority.SOMETIMES);
                grid.getColumnConstraints().add(cc);
            }

            int physicalRow = 0;
            int startColumn = 0;
            for (Col col : row.getCols()) {
                int effSpan = responsive ? col.effectiveSpan(bp) : col.getSpan();
                int effOffset = responsive ? col.effectiveOffset(bp) : col.getOffset();
                int targetColumn = startColumn + effOffset;

                if (targetColumn >= TOTAL_COLUMNS) {
                    physicalRow++;
                    targetColumn = effOffset;
                }

                if (targetColumn + effSpan > TOTAL_COLUMNS) {
                    physicalRow++;
                    targetColumn = effOffset;
                }

                ensureRowConstraint(grid, physicalRow, row);
                placeNode(grid, col.getNode(), physicalRow, targetColumn, effSpan, row);
                startColumn = targetColumn + effSpan;
            }

            if (grid.getRowConstraints().isEmpty()) {
                ensureRowConstraint(grid, 0, row);
            }
            if (row.getHeight() > 0) {
                int rowCount = grid.getRowConstraints().size();
                double totalHeight = row.getHeight() * rowCount + effectiveRowGutter() * Math.max(0, rowCount - 1);
                grid.setMinHeight(totalHeight);
                grid.setPrefHeight(totalHeight);
            }
            return grid;
        }

        private void ensureRowConstraint(GridPane grid, int rowIndex, Row row) {
            while (grid.getRowConstraints().size() <= rowIndex) {
                RowConstraints rc = new RowConstraints();
                rc.setVgrow(Priority.SOMETIMES);
                rc.setValignment(toVPos(row.getAlignment()));
                if (row.getHeight() > 0) {
                    rc.setMinHeight(row.getHeight());
                    rc.setPrefHeight(row.getHeight());
                }
                grid.getRowConstraints().add(rc);
            }
        }

        private void placeNode(GridPane grid, Node node, int rowIndex, int startCol, int span, Row row) {
            if (node == null || span <= 0) return;
            // 确保不会重复挂 styleClass（rebuild 场景下同一节点会被多次挂）
            if (!node.getStyleClass().contains(JfxStyles.GRID_COL)) {
                node.getStyleClass().add(JfxStyles.GRID_COL);
            }
            GridPane.setColumnIndex(node, startCol);
            GridPane.setRowIndex(node, rowIndex);
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

        private Breakpoint initialBreakpoint() {
            return responsive ? Breakpoint.XS : Breakpoint.XXL;
        }

        private static double clampGap(double gap) {
            return Double.isFinite(gap) ? Math.max(0, gap) : 0;
        }

        private static HPos toHPos(Pos pos) {
            return pos != null ? pos.getHpos() : HPos.LEFT;
        }

        private static VPos toVPos(Pos pos) {
            return pos != null ? pos.getVpos() : VPos.CENTER;
        }
    }
}
