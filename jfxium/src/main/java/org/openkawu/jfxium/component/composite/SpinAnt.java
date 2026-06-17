package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

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
 *   <li><b>主题色</b>：所有颜色通过 LESS 变量控制，支持主题切换</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 简单加载器
 * StackPane spin = SpinAnt.create()
 *     .size(SpinAnt.Size.LARGE)
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

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public enum Indicator {
        SPINNER, DOTS, BARS
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String tip = null;
        private Size size = Size.DEFAULT;
        private Indicator indicator = Indicator.SPINNER;
        private boolean fullscreen = false;
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

        public VBox build() {
            VBox spin = new VBox();
            spin.setAlignment(Pos.CENTER);
            spin.getStyleClass().add(JfxStyles.SPIN);

            if (fullscreen) {
                spin.getStyleClass().add(JfxStyles.SPIN_FULLSCREEN);
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
            for (int i = 0; i < 5; i++) {
                // 使用 Region 替代 Rectangle，通过 CSS 控制颜色
                Region bar = new Region();
                bar.setPrefSize(barWidth, 16 * scale);
                bar.setMaxSize(barWidth, 16 * scale);
                bar.setMinSize(barWidth, 16 * scale);
                bar.getStyleClass().add(JfxStyles.SPIN_INDICATOR_BAR);
                bar.setOpacity(0.3);

                Timeline timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.setAutoReverse(true);

                double baseHeight = 16 * scale;
                double maxHeight = 28 * scale;

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
        private final Parent originalParent;
        private final int originalIndex;
        private final StackPane wrapper;
        private final StackPane overlay;

        private Overlay(Node target) {
            this.target = target;
            this.originalParent = target.getParent();
            if (originalParent == null) {
                throw new IllegalArgumentException("SpinAnt.overlay() requires a node already in a scene graph");
            }

            // 找到 target 在父容器中的位置
            if (originalParent instanceof javafx.scene.layout.Pane pane) {
                this.originalIndex = pane.getChildren().indexOf(target);
                pane.getChildren().remove(originalIndex);

                // 包裹层
                overlay = new StackPane();
                overlay.getStyleClass().add(JfxStyles.SPIN_OVERLAY);
                overlay.setVisible(false);

                wrapper = new StackPane(target, overlay);
                pane.getChildren().add(originalIndex, wrapper);
            } else {
                // Parent 但不是 Pane（如 Group），直接替换子节点列表
                this.originalIndex = 0;
                this.wrapper = null;
                this.overlay = null;
                throw new IllegalArgumentException("SpinAnt.overlay() target parent must be a Pane subclass");
            }
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
