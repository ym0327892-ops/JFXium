package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 栅格系统组件 - 对标 Ant Design Grid
 *
 * 24 列栅格系统，支持响应式布局
 *
 * 使用示例：
 * <pre>{@code
 * // 基础栅格
 * GridPane grid = GridAnt.create()
 *     .gutter(16)
 *     .row(new GridAnt.Row()
 *         .col(12, node1)
 *         .col(12, node2))
 *     .row(new GridAnt.Row()
 *         .col(8, node3)
 *         .col(8, node4)
 *         .col(8, node5))
 *     .build();
 * }</pre>
 */
public class GridAnt {

    public static class Col {
        private final int span;
        private final int offset;
        private final Node node;

        public Col(int span, Node node) {
            this(span, 0, node);
        }

        public Col(int span, int offset, Node node) {
            this.span = span;
            this.offset = offset;
            this.node = node;
        }

        public int getSpan() { return span; }
        public int getOffset() { return offset; }
        public Node getNode() { return node; }
    }

    public static class Row {
        private List<Col> cols = new ArrayList<>();
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

        public List<Col> getCols() { return cols; }
        public Pos getAlignment() { return alignment; }
        public double getHeight() { return height; }
    }

    public static class Builder {
        private List<Row> rows = new ArrayList<>();
        private double gutter = 0;
        private double rowGutter = 0;
        private double columnGutter = 0;

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
            this.rows.add(row);
            return this;
        }

        public VBox build() {
            VBox container = new VBox();
            container.getStyleClass().add("grid");
            container.setStyle("-fx-background-color: transparent;");

            double vGap = gutter > 0 ? gutter : rowGutter;
            double hGap = gutter > 0 ? gutter : columnGutter;
            container.setSpacing(vGap);

            for (Row row : rows) {
                HBox rowBox = new HBox();
                rowBox.getStyleClass().add("grid-row");
                rowBox.setAlignment(row.getAlignment());
                rowBox.setSpacing(hGap);
                if (row.getHeight() > 0) {
                    rowBox.setPrefHeight(row.getHeight());
                    rowBox.setMinHeight(row.getHeight());
                }

                int totalSpan = 0;
                for (Col col : row.getCols()) {
                    totalSpan += col.getSpan() + col.getOffset();
                }

                // Normalize spans if total exceeds 24
                double totalWidth = 24.0;
                for (Col col : row.getCols()) {
                    double ratio = col.getSpan() / totalWidth;

                    VBox colBox = new VBox(col.getNode());
                    colBox.getStyleClass().add("grid-col");
                    colBox.setAlignment(row.getAlignment());

                    // Use percentage-based preferred width
                    colBox.setPrefWidth(ratio * 100);
                    HBox.setHgrow(colBox, Priority.SOMETIMES);

                    if (col.getOffset() > 0) {
                        double offsetRatio = col.getOffset() / totalWidth;
                        HBox offsetBox = new HBox();
                        offsetBox.setPrefWidth(offsetRatio * 100);
                        HBox.setHgrow(offsetBox, Priority.SOMETIMES);
                        rowBox.getChildren().add(offsetBox);
                    }

                    rowBox.getChildren().add(colBox);
                }

                container.getChildren().add(rowBox);
            }

            return container;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Row row() {
        return new Row();
    }
}
