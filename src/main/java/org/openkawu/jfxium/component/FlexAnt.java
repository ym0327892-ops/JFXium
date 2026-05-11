package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 弹性布局组件 - 对标 Ant Design Flex
 *
 * 基于 CSS Flexbox 的弹性布局容器
 *
 * 使用示例：
 * <pre>{@code
 * // 水平排列
 * HBox flex = FlexAnt.create()
 *     .gap(16)
 *     .justify(FlexAnt.Justify.CENTER)
 *     .align(FlexAnt.Align.CENTER)
 *     .children(node1, node2, node3)
 *     .build();
 *
 * // 垂直排列
 * VBox flexVertical = FlexAnt.createVertical()
 *     .gap(8)
 *     .children(node1, node2)
 *     .build();
 * }</pre>
 */
public class FlexAnt {

    public enum Justify {
        START, END, CENTER, BETWEEN, AROUND, EVENLY
    }

    public enum Align {
        START, END, CENTER, STRETCH, BASELINE
    }

    public enum Wrap {
        NOWRAP, WRAP, WRAP_REVERSE
    }

    public enum Direction {
        ROW, ROW_REVERSE, COLUMN, COLUMN_REVERSE
    }

    public static class Builder {
        private List<Node> children = new ArrayList<>();
        private Direction direction = Direction.ROW;
        private Justify justify = Justify.START;
        private Align align = Align.CENTER;
        private Wrap wrap = Wrap.NOWRAP;
        private double gap = 0;
        private double rowGap = 0;
        private double columnGap = 0;

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder justify(Justify justify) {
            this.justify = justify;
            return this;
        }

        public Builder align(Align align) {
            this.align = align;
            return this;
        }

        public Builder wrap(Wrap wrap) {
            this.wrap = wrap;
            return this;
        }

        public Builder gap(double gap) {
            this.gap = gap;
            return this;
        }

        public Builder rowGap(double rowGap) {
            this.rowGap = rowGap;
            return this;
        }

        public Builder columnGap(double columnGap) {
            this.columnGap = columnGap;
            return this;
        }

        public Builder children(Node... nodes) {
            for (Node node : nodes) {
                this.children.add(node);
            }
            return this;
        }

        public Builder child(Node node, boolean grow) {
            this.children.add(node);
            if (grow && node != null) {
                HBox.setHgrow(node, Priority.ALWAYS);
                VBox.setVgrow(node, Priority.ALWAYS);
            }
            return this;
        }

        public HBox build() {
            HBox container = new HBox();
            container.getStyleClass().add("flex");

            StringBuilder style = new StringBuilder();
            style.append("-fx-background-color: transparent;");

            // Direction
            switch (direction) {
                case ROW -> style.append("-fx-orientation: horizontal;");
                case COLUMN -> {
                    VBox vbox = new VBox();
                    vbox.getStyleClass().add("flex");
                    StringBuilder vStyle = new StringBuilder();
                    vStyle.append("-fx-background-color: transparent;");
                    vStyle.append("-fx-spacing: ").append(gap > 0 ? gap : (rowGap > 0 ? rowGap : 0)).append("px;");
                    vbox.setStyle(vStyle.toString());

                    // Alignment
                    switch (align) {
                        case START -> vbox.setAlignment(Pos.TOP_LEFT);
                        case END -> vbox.setAlignment(Pos.BOTTOM_LEFT);
                        case CENTER -> vbox.setAlignment(Pos.CENTER_LEFT);
                        case STRETCH -> vbox.setAlignment(Pos.CENTER_LEFT);
                        case BASELINE -> vbox.setAlignment(Pos.BASELINE_LEFT);
                    }

                    for (Node child : children) {
                        vbox.getChildren().add(child);
                    }
                    return new HBox(vbox); // Wrap in HBox for return type consistency
                }
                case ROW_REVERSE -> {
                    // Reverse children order
                    List<Node> reversed = new ArrayList<>();
                    for (int i = children.size() - 1; i >= 0; i--) {
                        reversed.add(children.get(i));
                    }
                    children = reversed;
                }
                case COLUMN_REVERSE -> {
                    VBox vbox = new VBox();
                    vbox.getStyleClass().add("flex");
                    StringBuilder vStyle = new StringBuilder();
                    vStyle.append("-fx-background-color: transparent;");
                    vStyle.append("-fx-spacing: ").append(gap > 0 ? gap : (rowGap > 0 ? rowGap : 0)).append("px;");
                    vbox.setStyle(vStyle.toString());

                    List<Node> reversed = new ArrayList<>();
                    for (int i = children.size() - 1; i >= 0; i--) {
                        reversed.add(children.get(i));
                    }
                    for (Node child : reversed) {
                        vbox.getChildren().add(child);
                    }
                    return new HBox(vbox);
                }
            }

            // Horizontal gap
            double hGap = gap > 0 ? gap : columnGap;
            container.setSpacing(hGap);

            // Justify content
            switch (justify) {
                case START -> container.setAlignment(Pos.CENTER_LEFT);
                case END -> container.setAlignment(Pos.CENTER_RIGHT);
                case CENTER -> container.setAlignment(Pos.CENTER);
                case BETWEEN, AROUND, EVENLY -> container.setAlignment(Pos.CENTER);
            }

            // For BETWEEN/AROUND/EVENLY, we need special handling
            if (justify == Justify.BETWEEN || justify == Justify.AROUND || justify == Justify.EVENLY) {
                if (children.size() > 1) {
                    container.getChildren().clear();
                    for (int i = 0; i < children.size(); i++) {
                        Node child = children.get(i);
                        HBox wrapper = new HBox(child);
                        if (i == 0 && justify == Justify.BETWEEN) {
                            wrapper.setAlignment(Pos.CENTER_LEFT);
                        } else if (i == children.size() - 1 && justify == Justify.BETWEEN) {
                            wrapper.setAlignment(Pos.CENTER_RIGHT);
                        } else {
                            wrapper.setAlignment(Pos.CENTER);
                        }
                        HBox.setHgrow(wrapper, Priority.ALWAYS);
                        container.getChildren().add(wrapper);
                    }
                } else {
                    container.getChildren().addAll(children);
                }
            } else {
                container.getChildren().addAll(children);
            }

            container.setStyle(style.toString());
            return container;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder createVertical() {
        return new Builder().direction(Direction.COLUMN);
    }
}
