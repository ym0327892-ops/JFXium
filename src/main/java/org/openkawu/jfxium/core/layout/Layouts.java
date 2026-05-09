package org.openkawu.jfxium.core.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Layout DSL
 * 提供流式 API 创建常见布局
 *
 * Usage:
 * <pre>{@code
 * // 垂直布局
 * VBox vbox = Layouts.vbox()
 *     .spacing(16)
 *     .padding(20)
 *     .align(Pos.CENTER)
 *     .children(
 *         JFXButton.create("A").build(),
 *         JFXButton.create("B").build()
 *     )
 *     .build();
 *
 * // 水平布局
 * HBox hbox = Layouts.hbox()
 *     .spacing(12)
 *     .align(Pos.CENTER_LEFT)
 *     .children(
 *         JFXButton.create("Left").build(),
 *         Layouts.grow(),  // 弹性占位
 *         JFXButton.create("Right").build()
 *     )
 *     .build();
 *
 * // 网格布局
 * GridPane grid = Layouts.grid()
 *     .cols(3)
 *     .gap(8)
 *     .children(
 *         JFXButton.create("1").build(),
 *         JFXButton.create("2").build(),
 *         JFXButton.create("3").build()
 *     )
 *     .build();
 * }</pre>
 */
public class Layouts {

    /**
     * 创建垂直布局构建器
     */
    public static VBoxBuilder vbox() {
        return new VBoxBuilder();
    }

    /**
     * 创建水平布局构建器
     */
    public static HBoxBuilder hbox() {
        return new HBoxBuilder();
    }

    /**
     * 创建网格布局构建器
     */
    public static GridBuilder grid() {
        return new GridBuilder();
    }

    /**
     * 创建弹性占位区域（在 HBox/VBox 中自动填充剩余空间）
     */
    public static Region grow() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        VBox.setVgrow(region, Priority.ALWAYS);
        return region;
    }

    /**
     * 创建固定尺寸占位区域
     */
    public static Region spacer(double width, double height) {
        Region region = new Region();
        region.setPrefSize(width, height);
        region.setMinSize(width, height);
        region.setMaxSize(width, height);
        return region;
    }

    /**
     * VBox 布局构建器
     */
    public static class VBoxBuilder {
        private double spacing = 8;
        private Insets padding = Insets.EMPTY;
        private Pos alignment = Pos.TOP_LEFT;
        private final List<Node> children = new ArrayList<>();
        private String style = "";

        public VBoxBuilder spacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        public VBoxBuilder padding(double padding) {
            this.padding = new Insets(padding);
            return this;
        }

        public VBoxBuilder padding(double top, double right, double bottom, double left) {
            this.padding = new Insets(top, right, bottom, left);
            return this;
        }

        public VBoxBuilder align(Pos alignment) {
            this.alignment = alignment;
            return this;
        }

        public VBoxBuilder children(Node... nodes) {
            for (Node node : nodes) {
                if (node != null) {
                    this.children.add(node);
                }
            }
            return this;
        }

        public VBoxBuilder style(String style) {
            this.style = style;
            return this;
        }

        public VBox build() {
            VBox vbox = new VBox(spacing);
            vbox.setPadding(padding);
            vbox.setAlignment(alignment);
            vbox.getChildren().addAll(children);
            if (!style.isEmpty()) {
                vbox.setStyle(style);
            }
            return vbox;
        }
    }

    /**
     * HBox 布局构建器
     */
    public static class HBoxBuilder {
        private double spacing = 8;
        private Insets padding = Insets.EMPTY;
        private Pos alignment = Pos.CENTER_LEFT;
        private final List<Node> children = new ArrayList<>();
        private String style = "";

        public HBoxBuilder spacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        public HBoxBuilder padding(double padding) {
            this.padding = new Insets(padding);
            return this;
        }

        public HBoxBuilder padding(double top, double right, double bottom, double left) {
            this.padding = new Insets(top, right, bottom, left);
            return this;
        }

        public HBoxBuilder align(Pos alignment) {
            this.alignment = alignment;
            return this;
        }

        public HBoxBuilder children(Node... nodes) {
            for (Node node : nodes) {
                if (node != null) {
                    this.children.add(node);
                }
            }
            return this;
        }

        public HBoxBuilder style(String style) {
            this.style = style;
            return this;
        }

        public HBox build() {
            HBox hbox = new HBox(spacing);
            hbox.setPadding(padding);
            hbox.setAlignment(alignment);
            hbox.getChildren().addAll(children);
            if (!style.isEmpty()) {
                hbox.setStyle(style);
            }
            return hbox;
        }
    }

    /**
     * GridPane 布局构建器
     */
    public static class GridBuilder {
        private int cols = 2;
        private double hgap = 8;
        private double vgap = 8;
        private Insets padding = Insets.EMPTY;
        private final List<Node> children = new ArrayList<>();
        private String style = "";

        public GridBuilder cols(int cols) {
            this.cols = cols;
            return this;
        }

        public GridBuilder gap(double gap) {
            this.hgap = gap;
            this.vgap = gap;
            return this;
        }

        public GridBuilder gap(double hgap, double vgap) {
            this.hgap = hgap;
            this.vgap = vgap;
            return this;
        }

        public GridBuilder padding(double padding) {
            this.padding = new Insets(padding);
            return this;
        }

        public GridBuilder padding(double top, double right, double bottom, double left) {
            this.padding = new Insets(top, right, bottom, left);
            return this;
        }

        public GridBuilder children(Node... nodes) {
            for (Node node : nodes) {
                if (node != null) {
                    this.children.add(node);
                }
            }
            return this;
        }

        public GridBuilder style(String style) {
            this.style = style;
            return this;
        }

        public GridPane build() {
            GridPane grid = new GridPane();
            grid.setHgap(hgap);
            grid.setVgap(vgap);
            grid.setPadding(padding);

            int row = 0;
            int col = 0;
            for (Node child : children) {
                grid.add(child, col, row);
                col++;
                if (col >= cols) {
                    col = 0;
                    row++;
                }
            }

            if (!style.isEmpty()) {
                grid.setStyle(style);
            }
            return grid;
        }
    }
}