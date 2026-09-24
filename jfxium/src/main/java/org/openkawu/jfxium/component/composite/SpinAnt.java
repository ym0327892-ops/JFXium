package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.token.Size;

/**
 * JFXium 加载中组件 - 对标 Ant Design Spin。
 *
 * <p><b>定位</b>：加载状态指示器，支持三种动画样式（spinner/dots/bars），
 * 可嵌入内容区或全屏覆盖。常用于数据加载中、提交等待、异步操作等场景。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>尺寸</b>：SMALL / DEFAULT / LARGE</li>
 *   <li><b>动画样式</b>：SPINNER（旋转圆环）/ DOTS（圆点）/ BARS（矩形条）</li>
 *   <li><b>提示文本</b>：tip("加载中...")</li>
 *   <li><b>嵌入模式</b>：content(Node) 把加载器盖在内容上</li>
 *   <li><b>全屏模式</b>：fullscreen(true) 覆盖整个场景</li>
 *   <li><b>遮罩透明度</b>：overlayOpacity(OverlayOpacity) 控制 fullscreen/overlay 背景透明度</li>
 *   <li><b>主题色</b>：所有颜色通过 LESS 变量控制，支持主题切换</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 简单加载器
 * StackPane spin = SpinAnt.create()
 *     .size(Size.LARGE)
 *     .tip("加载中...")
 *     .build();
 *
 * // 嵌入内容加载
 * StackPane loading = SpinAnt.create()
 *     .content(dataTable)
 *     .tip("正在加载数据...")
 *     .build();
 * }</pre>
 */
public class SpinAnt {

    // P1-S1 抽取：Size 枚举迁到 org.openkawu.jfxium.core.token.Size。

    public enum Indicator {
        SPINNER, DOTS, BARS
    }

    /**
     * 遮罩背景透明度档位，用于 fullscreen 和 overlay 模式。
     * <ul>
     *   <li>{@link #NONE} — 完全透明，无遮罩背景</li>
     *   <li>{@link #LIGHT} — 轻度半透明（~30%），内容若隐若现</li>
     *   <li>{@link #NORMAL} — 中度半透明（~65%），默认遮罩效果</li>
     *   <li>{@link #STRONG} — 强半透明（~85%），几乎遮挡底层内容</li>
     * </ul>
     */
    public enum OverlayOpacity {
        NONE, LIGHT, NORMAL, STRONG
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String tip = null;
        private Size size = Size.DEFAULT;
        private Indicator indicator = Indicator.SPINNER;
        private boolean fullscreen = false;
        private OverlayOpacity overlayOpacity = null;
        private String delay = null;

        public Builder tip(String tip) {
            this.tip = tip;
            return this;
        }

        public Builder size(Size size) {
            this.size = size != null ? size : Size.DEFAULT;
            return this;
        }

        public Builder indicator(Indicator indicator) {
            this.indicator = indicator != null ? indicator : Indicator.SPINNER;
            return this;
        }

        public Builder fullscreen(boolean fullscreen) {
            this.fullscreen = fullscreen;
            return this;
        }

        public Builder fullscreen() {
            return fullscreen(true);
        }

        /**
         * 设置遮罩背景透明度（仅 fullscreen 模式生效）。
         * <p>用法：{@code SpinAnt.create().fullscreen().overlayOpacity(OverlayOpacity.NONE).build()}</p>
         *
         * @param overlayOpacity 透明度档位，null 视为 {@link OverlayOpacity#NORMAL}（默认）
         * @return this
         */
        public Builder overlayOpacity(OverlayOpacity overlayOpacity) {
            this.overlayOpacity = overlayOpacity;
            return this;
        }

        public VBox build() {
            VBox spin = new VBox();
            spin.setAlignment(Pos.CENTER);
            spin.getStyleClass().add(JfxStyles.SPIN);

            if (fullscreen) {
                spin.getStyleClass().add(JfxStyles.SPIN_FULLSCREEN);
                if (overlayOpacity != null) {
                    switch (overlayOpacity) {
                        case NONE -> spin.getStyleClass().add(JfxStyles.SPIN_BG_NONE);
                        case LIGHT -> spin.getStyleClass().add(JfxStyles.SPIN_BG_LIGHT);
                        case STRONG -> spin.getStyleClass().add(JfxStyles.SPIN_BG_STRONG);
                        // NORMAL 不加修饰类，走 .jfx-spin-fullscreen 默认值
                    }
                }
            }

            double scale = size == Size.SMALL ? 0.6 : size == Size.LARGE ? 1.4 : 1.0;
            spin.getChildren().add(createIndicator(indicator, scale));

            if (tip != null && !tip.isEmpty()) {
                Label tipLabel = new Label(tip);
                tipLabel.getStyleClass().add(JfxStyles.SPIN_TIP);
                spin.getChildren().add(tipLabel);
            }

            applyStyles(spin);
            return spin;
        }

        private Node createIndicator(Indicator type, double scale) {
            return switch (type) {
                case DOTS -> createDotsIndicator(scale);
                case BARS -> createBarsIndicator(scale);
                case SPINNER -> createSpinnerIndicator(scale);
            };
        }

        private Node createSpinnerIndicator(double scale) {
            StackPane container = new StackPane();
            container.setPrefSize(32 * scale, 32 * scale);

            // 使用 Region 替代 Arc，通过 CSS 控制颜色
            Region arc = new Region();
            arc.setPrefSize(28 * scale, 28 * scale);
            arc.setMaxSize(28 * scale, 28 * scale);
            arc.setMinSize(28 * scale, 28 * scale);
            arc.getStyleClass().add(JfxStyles.SPIN_INDICATOR_SPINNER);

            Rotate rotate = new Rotate(0, 14 * scale, 14 * scale);
            arc.getTransforms().add(rotate);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(rotate.angleProperty(), 360))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            container.getChildren().add(arc);
            return container;
        }

        private Node createDotsIndicator(double scale) {
            HBox dots = new HBox(6 * scale);
            dots.setAlignment(Pos.CENTER);

            double dotSize = 8 * scale;
            for (int i = 0; i < 3; i++) {
                // 使用 Region 替代 Circle，通过 CSS 控制颜色
                Region dot = new Region();
                dot.setPrefSize(dotSize, dotSize);
                dot.setMaxSize(dotSize, dotSize);
                dot.setMinSize(dotSize, dotSize);
                dot.getStyleClass().add(JfxStyles.SPIN_INDICATOR_DOT);
                dot.setOpacity(0.3);

                Timeline timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.setAutoReverse(true);

                KeyFrame start = new KeyFrame(Duration.millis(i * 160), new KeyValue(dot.opacityProperty(), 0.3));
                KeyFrame mid = new KeyFrame(Duration.millis(i * 160 + 400), new KeyValue(dot.opacityProperty(), 1.0));
                KeyFrame end = new KeyFrame(Duration.millis(i * 160 + 800), new KeyValue(dot.opacityProperty(), 0.3));
                timeline.getKeyFrames().addAll(start, mid, end);
                timeline.play();

                dots.getChildren().add(dot);
            }

            return dots;
        }

        private Node createBarsIndicator(double scale) {
            HBox bars = new HBox(3 * scale);
            bars.setAlignment(Pos.BOTTOM_CENTER);

            double barWidth = 4 * scale;
            double baseHeight = 16 * scale;
            double maxHeight = 28 * scale;
            // 容器高度固定为 maxHeight，避免 bar 高低变化撑开 HBox 导致 tip 文字上下抖动
            bars.setMinHeight(maxHeight);
            bars.setPrefHeight(maxHeight);
            bars.setMaxHeight(maxHeight);
            for (int i = 0; i < 5; i++) {
                // 使用 Region 替代 Rectangle，通过 CSS 控制颜色
                // min/max 不能钳死为同一值，否则 prefHeight 动画不生效（BUG: BARS 无动态效果）
                Region bar = new Region();
                bar.setMinSize(barWidth, 0);
                bar.setPrefSize(barWidth, baseHeight);
                bar.setMaxSize(barWidth, Region.USE_PREF_SIZE);
                bar.getStyleClass().add(JfxStyles.SPIN_INDICATOR_BAR);
                bar.setOpacity(0.3);

                Timeline timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.setAutoReverse(true);

                KeyFrame start = new KeyFrame(Duration.millis(i * 100), new KeyValue(bar.prefHeightProperty(), baseHeight));
                KeyFrame mid = new KeyFrame(Duration.millis(i * 100 + 300), new KeyValue(bar.prefHeightProperty(), maxHeight));
                KeyFrame end = new KeyFrame(Duration.millis(i * 100 + 600), new KeyValue(bar.prefHeightProperty(), baseHeight));
                timeline.getKeyFrames().addAll(start, mid, end);
                timeline.play();

                bars.getChildren().add(bar);
            }

            return bars;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    // ---- Overlay 挂载 ---- //

    /**
     * 挂载加载遮罩到目标节点。把 target 包进 StackPane，上层叠加载层。
     * 传 scene.getRoot() 即覆盖全窗口。
     */
    public static Overlay overlay(Node target) {
        return new Overlay(target);
    }

    public static class Overlay {
        private final Node target;
        private final StackPane wrapper;
        private final StackPane overlay;

        private Overlay(Node target) {
            this.target = target;
            Parent parent = target.getParent();
            Scene scene = target.getScene();

            // scene.getRoot() 没有父节点，用 scene.setRoot(wrapper) 顶替，同样覆盖全窗口。
            if (parent == null && scene != null && scene.getRoot() == target) {
                this.overlay = new StackPane();
                overlay.getStyleClass().add(JfxStyles.SPIN_OVERLAY);
                overlay.setVisible(false);
                this.wrapper = new StackPane(target, overlay);
                scene.setRoot(wrapper);
                return;
            }

            if (parent == null) {
                throw new IllegalArgumentException(
                        "SpinAnt.overlay() requires a node already in a scene graph");
            }
            if (!(parent instanceof Pane pane)) {
                throw new IllegalArgumentException(
                        "SpinAnt.overlay() target parent must be a Pane subclass");
            }

            int index = pane.getChildren().indexOf(target);
            pane.getChildren().remove(index);

            this.overlay = new StackPane();
            overlay.getStyleClass().add(JfxStyles.SPIN_OVERLAY);
            overlay.setVisible(false);

            this.wrapper = new StackPane(target, overlay);
            // 关键：把父容器记在 target 上的布局约束迁移到 wrapper，
            // 否则 wrapper 顶替 target 后父容器读到的是无约束节点，
            // vgrow/hgrow/margin/Grid 定位等全部失效（布局静默塌缩且不自愈）。
            transferConstraints(pane, target, wrapper);
            pane.getChildren().add(index, wrapper);
        }

        /**
         * 把父容器记在 target 上的布局约束迁移到 wrapper。
         *
         * <p>vgrow/hgrow/margin、GridPane 行列定位、AnchorPane 锚点等都是节点属性，
         * wrapper 顶替 target 后必须复制，否则父容器按无约束节点布局。</p>
         */
        private static void transferConstraints(Parent parent, Node target, Node wrapper) {
            if (parent instanceof VBox) {
                VBox.setVgrow(wrapper, VBox.getVgrow(target));
                VBox.setMargin(wrapper, VBox.getMargin(target));
            } else if (parent instanceof HBox) {
                HBox.setHgrow(wrapper, HBox.getHgrow(target));
                HBox.setMargin(wrapper, HBox.getMargin(target));
            } else if (parent instanceof GridPane) {
                Integer row = GridPane.getRowIndex(target);
                Integer col = GridPane.getColumnIndex(target);
                if (row != null) GridPane.setRowIndex(wrapper, row);
                if (col != null) GridPane.setColumnIndex(wrapper, col);
                Integer rowSpan = GridPane.getRowSpan(target);
                Integer colSpan = GridPane.getColumnSpan(target);
                if (rowSpan != null) GridPane.setRowSpan(wrapper, rowSpan);
                if (colSpan != null) GridPane.setColumnSpan(wrapper, colSpan);
                HPos halign = GridPane.getHalignment(target);
                VPos valign = GridPane.getValignment(target);
                if (halign != null) GridPane.setHalignment(wrapper, halign);
                if (valign != null) GridPane.setValignment(wrapper, valign);
                GridPane.setHgrow(wrapper, GridPane.getHgrow(target));
                GridPane.setVgrow(wrapper, GridPane.getVgrow(target));
                Boolean fillW = GridPane.isFillWidth(target);
                Boolean fillH = GridPane.isFillHeight(target);
                if (fillW != null) GridPane.setFillWidth(wrapper, fillW);
                if (fillH != null) GridPane.setFillHeight(wrapper, fillH);
                GridPane.setMargin(wrapper, GridPane.getMargin(target));
            } else if (parent instanceof BorderPane) {
                BorderPane.setAlignment(wrapper, BorderPane.getAlignment(target));
                BorderPane.setMargin(wrapper, BorderPane.getMargin(target));
            } else if (parent instanceof AnchorPane) {
                copyAnchor(AnchorPane.getTopAnchor(target), AnchorPane::setTopAnchor, wrapper);
                copyAnchor(AnchorPane.getBottomAnchor(target), AnchorPane::setBottomAnchor, wrapper);
                copyAnchor(AnchorPane.getLeftAnchor(target), AnchorPane::setLeftAnchor, wrapper);
                copyAnchor(AnchorPane.getRightAnchor(target), AnchorPane::setRightAnchor, wrapper);
            } else if (parent instanceof FlowPane) {
                FlowPane.setMargin(wrapper, FlowPane.getMargin(target));
            } else if (parent instanceof TilePane) {
                TilePane.setMargin(wrapper, TilePane.getMargin(target));
                TilePane.setAlignment(wrapper, TilePane.getAlignment(target));
            } else if (parent instanceof StackPane) {
                StackPane.setMargin(wrapper, StackPane.getMargin(target));
                StackPane.setAlignment(wrapper, StackPane.getAlignment(target));
            }
        }

        private static void copyAnchor(Double value,
                                       java.util.function.BiConsumer<Node, Double> setter,
                                       Node wrapper) {
            if (value != null) {
                setter.accept(wrapper, value);
            }
        }

        /**
         * 设置遮罩层背景透明度。
         * <p>用法：{@code SpinAnt.overlay(node).overlayOpacity(OverlayOpacity.NONE).show();}</p>
         *
         * @param opacity 透明度档位，null 视为 {@link OverlayOpacity#NORMAL}（默认）
         * @return this
         */
        public Overlay overlayOpacity(OverlayOpacity opacity) {
            if (overlay == null) return this;
            // 先清除旧修饰类
            overlay.getStyleClass().removeAll(
                    JfxStyles.SPIN_BG_NONE, JfxStyles.SPIN_BG_LIGHT, JfxStyles.SPIN_BG_STRONG);
            if (opacity != null) {
                switch (opacity) {
                    case NONE -> overlay.getStyleClass().add(JfxStyles.SPIN_BG_NONE);
                    case LIGHT -> overlay.getStyleClass().add(JfxStyles.SPIN_BG_LIGHT);
                    case STRONG -> overlay.getStyleClass().add(JfxStyles.SPIN_BG_STRONG);
                    // NORMAL 不加修饰类，走 .jfx-spin-overlay 默认值
                }
            }
            return this;
        }

        /** 显示默认加载器。 */
        public void show() {
            show((String) null);
        }

        /** 显示带提示文字的默认加载器。 */
        public void show(String tip) {
            show(buildDefault(tip));
        }

        /** 显示自定义加载器节点（可用 SpinAnt.create().build() 构建）。 */
        public void show(Node spinner) {
            overlay.getChildren().setAll(spinner);
            overlay.setVisible(true);
            target.setDisable(true);
        }

        /** 隐藏加载器。 */
        public void hide() {
            overlay.setVisible(false);
            overlay.getChildren().clear();
            target.setDisable(false);
        }

        private Node buildDefault(String tip) {
            Builder b = SpinAnt.create().size(Size.LARGE);
            if (tip != null) b.tip(tip);
            return b.build();
        }
    }
}
