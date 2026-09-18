package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.function.Supplier;

/**
 * JFXium 间距组件 - 对标 Ant Design Space。
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 水平间距
 * Pane row = SpaceAnt.create()
 *     .size(16)
 *     .children(node1, node2, node3)
 *     .build();
 *
 * // 垂直间距
 * Pane col = SpaceAnt.createVertical()
 *     .size(8)
 *     .children(node1, node2)
 *     .build();
 *
 * // 带分隔线（自动用 Separator）
 * Pane withSplit = SpaceAnt.create()
 *     .size(8)
 *     .split(true)
 *     .children(a, b, c)
 *     .build();
 * }</pre>
 */
public class SpaceAnt {

    private static final Logger LOGGER = Logger.getLogger(SpaceAnt.class.getName());

    public enum Align {
        START, END, CENTER, BASELINE
    }

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder createVertical() {
        return new Builder().direction(Direction.VERTICAL);
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Node> children = new ArrayList<>();
        private Direction direction = Direction.HORIZONTAL;
        private double size = 8;
        private Align align = Align.CENTER;
        private boolean split = false;
        private Supplier<? extends Node> splitFactory;

        private Builder() {}

        public Builder direction(Direction direction) {
            this.direction = direction == null ? Direction.HORIZONTAL : direction;
            return this;
        }

        public Builder size(double size) {
            this.size = Double.isFinite(size) ? Math.max(0, size) : 0;
            return this;
        }

        public Builder align(Align align) {
            this.align = align == null ? Align.CENTER : align;
            return this;
        }

        /** 启用分隔模式。如未指定 {@link #split(Node)}，会自动用原生 {@link Separator}。*/
        public Builder split(boolean split) {
            this.split = split;
            if (!split) {
                this.splitFactory = null;
            }
            return this;
        }

        /**
         * 指定自定义分隔节点（覆盖默认的 Separator）。
         *
         * <p>每个分隔位置都需要独立 Node，避免同一 Node 被重复挂到多个 parent。
         * 这里会为常见轻量节点（如 Label / Text / Separator / SVGPath）自动复制实例；
         * 更复杂的自定义节点请改用 {@link #split(Supplier)} 显式提供新实例。</p>
         */
        public Builder split(Node splitNode) {
            this.split = true;
            this.splitFactory = repeatableFactory(splitNode);
            return this;
        }

        /** 指定自定义分隔节点工厂。每个分隔位置都会调用一次，避免重复复用同一个 Node。 */
        public Builder split(Supplier<? extends Node> splitFactory) {
            this.split = true;
            this.splitFactory = splitFactory;
            return this;
        }

        public Builder children(Node... nodes) {
            if (nodes != null) {
                for (Node node : nodes) {
                    if (node != null) {
                        this.children.add(node);
                    }
                }
            }
            return this;
        }

        public Pane build() {
            return direction == Direction.VERTICAL ? buildVBox() : buildHBox();
        }

        private HBox buildHBox() {
            HBox container = new HBox();
            container.getStyleClass().addAll(JfxStyles.SPACE, JfxStyles.SPACE_HORIZONTAL);
            container.setSpacing(size);
            container.setAlignment(toHBoxPos(align));
            applyChildrenWithOptionalSplit(container, Direction.HORIZONTAL);
            applyStyles(container);
            return container;
        }

        private VBox buildVBox() {
            VBox container = new VBox();
            container.getStyleClass().addAll(JfxStyles.SPACE, JfxStyles.SPACE_VERTICAL);
            container.setSpacing(size);
            container.setAlignment(toVBoxPos(align));
            applyChildrenWithOptionalSplit(container, Direction.VERTICAL);
            applyStyles(container);
            return container;
        }

        /**
         * 把 children 按顺序加入容器，启用 split 时在每两个相邻子节点之间插入分隔节点。
         * splitFactory 为 null 或返回 null 时使用原生 Separator（方向跟随容器方向），避免静默失效。
         */
        private void applyChildrenWithOptionalSplit(Pane container, Direction containerDir) {
            for (int i = 0; i < children.size(); i++) {
                container.getChildren().add(children.get(i));
                boolean isLast = i == children.size() - 1;
                if (split && !isLast) {
                    container.getChildren().add(makeSplit(containerDir));
                }
            }
        }

        private Node makeSplit(Direction containerDir) {
            if (splitFactory != null) {
                Node customSplit = splitFactory.get();
                if (customSplit != null) {
                    return customSplit;
                }
            }
            // 默认分隔：与容器方向垂直的 Separator（水平容器配垂直分隔线，反之亦然）
            Separator sep = new Separator(
                    containerDir == Direction.HORIZONTAL ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            sep.getStyleClass().add(JfxStyles.SPACE_SPLIT);
            return sep;
        }

        private Supplier<Node> repeatableFactory(Node template) {
            if (template == null) {
                return () -> null;
            }
            return () -> cloneSplitNode(template);
        }

        private Node cloneSplitNode(Node template) {
            if (template instanceof Label label) {
                if (label.getGraphic() != null) {
                    throw new IllegalArgumentException(
                            "SpaceAnt.split(Node) 不支持带 graphic 的 Label；" +
                                    "请改用 split(Supplier<? extends Node>)。");
                }
                Label copy = new Label(label.getText());
                copy.getStyleClass().setAll(label.getStyleClass());
                copy.setId(label.getId());
                copy.setStyle(label.getStyle());
                copy.setAlignment(label.getAlignment());
                copy.setContentDisplay(label.getContentDisplay());
                copy.setGraphicTextGap(label.getGraphicTextGap());
                copy.setTextAlignment(label.getTextAlignment());
                copy.setWrapText(label.isWrapText());
                copy.setMinSize(label.getMinWidth(), label.getMinHeight());
                copy.setPrefSize(label.getPrefWidth(), label.getPrefHeight());
                copy.setMaxSize(label.getMaxWidth(), label.getMaxHeight());
                copy.setOpacity(label.getOpacity());
                copy.setDisable(label.isDisable());
                copy.setVisible(label.isVisible());
                copy.setManaged(label.isManaged());
                return copy;
            }
            if (template instanceof Text text) {
                Text copy = new Text(text.getText());
                copy.getStyleClass().setAll(text.getStyleClass());
                copy.setId(text.getId());
                copy.setStyle(text.getStyle());
                copy.setTextAlignment(text.getTextAlignment());
                copy.setWrappingWidth(text.getWrappingWidth());
                copy.setStrikethrough(text.isStrikethrough());
                copy.setUnderline(text.isUnderline());
                copy.setOpacity(text.getOpacity());
                copy.setDisable(text.isDisable());
                copy.setVisible(text.isVisible());
                copy.setManaged(text.isManaged());
                return copy;
            }
            if (template instanceof Separator separator) {
                Separator copy = new Separator(separator.getOrientation());
                copy.getStyleClass().setAll(separator.getStyleClass());
                copy.setId(separator.getId());
                copy.setStyle(separator.getStyle());
                copy.setHalignment(separator.getHalignment());
                copy.setValignment(separator.getValignment());
                copy.setMinSize(separator.getMinWidth(), separator.getMinHeight());
                copy.setPrefSize(separator.getPrefWidth(), separator.getPrefHeight());
                copy.setMaxSize(separator.getMaxWidth(), separator.getMaxHeight());
                copy.setOpacity(separator.getOpacity());
                copy.setDisable(separator.isDisable());
                copy.setVisible(separator.isVisible());
                copy.setManaged(separator.isManaged());
                return copy;
            }
            if (template instanceof SVGPath path) {
                SVGPath copy = new SVGPath();
                copy.getStyleClass().setAll(path.getStyleClass());
                copy.setId(path.getId());
                copy.setStyle(path.getStyle());
                copy.setContent(path.getContent());
                copy.setOpacity(path.getOpacity());
                copy.setDisable(path.isDisable());
                copy.setVisible(path.isVisible());
                copy.setManaged(path.isManaged());
                return copy;
            }
            if (template.getClass() == Region.class) {
                Region region = (Region) template;
                Region copy = new Region();
                copy.getStyleClass().setAll(region.getStyleClass());
                copy.setId(region.getId());
                copy.setStyle(region.getStyle());
                copy.setMinSize(region.getMinWidth(), region.getMinHeight());
                copy.setPrefSize(region.getPrefWidth(), region.getPrefHeight());
                copy.setMaxSize(region.getMaxWidth(), region.getMaxHeight());
                copy.setOpacity(region.getOpacity());
                copy.setDisable(region.isDisable());
                copy.setVisible(region.isVisible());
                copy.setManaged(region.isManaged());
                return copy;
            }
            throw new IllegalArgumentException(
                    "SpaceAnt.split(Node) 仅支持可安全复制的轻量节点（Label/Text/Separator/SVGPath/Region）；" +
                            "复杂节点请改用 split(Supplier<? extends Node>)。");
        }

        // HBox 支持 BASELINE，VBox 不支持
        private static Pos toHBoxPos(Align a) {
            return switch (a) {
                case START -> Pos.TOP_LEFT;
                case END -> Pos.BOTTOM_LEFT;
                case CENTER -> Pos.CENTER_LEFT;
                case BASELINE -> Pos.BASELINE_LEFT;
            };
        }

        private static Pos toVBoxPos(Align a) {
            // VBox 没有 baseline 概念。BASELINE 在垂直容器里没有意义，
            // 这里降级为 START 并打印一次告警，让调用方知道行为不会按预期。
            return switch (a) {
                case START -> Pos.TOP_LEFT;
                case END -> Pos.TOP_RIGHT;
                case CENTER -> Pos.TOP_CENTER;
                case BASELINE -> {
                    LOGGER.warning("Align.BASELINE 在垂直方向无效，已降级为 START");
                    yield Pos.TOP_LEFT;
                }
            };
        }
    }
}
