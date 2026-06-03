package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium Spin Component - 对标 Ant Design Spin（加载状态）。
 *
 * <h2>修复说明</h2>
 * 原实现 5 处 inline {@code setStyle}：
 * <ul>
 *   <li>fullscreen 模式半透明遮罩拼字符串</li>
 *   <li>tipLabel 字号 + 颜色拼字符串</li>
 *   <li>3 个 indicator（spinner/dots/bars）通过 {@code Color.web("#1677ff")} 硬编码主题色</li>
 * </ul>
 *
 * <h2>本次改动（M7）</h2>
 * <ul>
 *   <li>容器/fullscreen/tip 样式搬到 LESS 选择器</li>
 *   <li>接入 {@link AbstractStyleBuilder}</li>
 *   <li>✅ 修复 indicator 硬编码主题色：改用 Region + CSS 变量（替代 Shape + Color.web）</li>
 * </ul>
 *
 * <h2>技术方案</h2>
 * 使用 Region + CSS {@code -fx-background-color} 替代 Shape + {@code Color.web("#1677ff")}：
 * <ul>
 *   <li>Spinner: 用 Region + CSS border + rotate 动画</li>
 *   <li>Dots: 用 Region + CSS border-radius: 50% 实现圆点</li>
 *   <li>Bars: 用 Region + CSS background-color 实现矩形条</li>
 * </ul>
 * 所有颜色通过 LESS 变量 {@code -color-accent-emphasis} 控制，支持主题切换。
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
            this.size = size;
            return this;
        }

        public Builder indicator(Indicator indicator) {
            this.indicator = indicator;
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
            VBox spin = new VBox(8);
            spin.setAlignment(Pos.CENTER);
            spin.getStyleClass().add(CssClasses.SPIN);

            if (fullscreen) {
                spin.getStyleClass().add(CssClasses.SPIN_FULLSCREEN);
            }

            double scale = size == Size.SMALL ? 0.6 : size == Size.LARGE ? 1.4 : 1.0;
            spin.getChildren().add(createIndicator(indicator, scale));

            if (tip != null && !tip.isEmpty()) {
                Label tipLabel = new Label(tip);
                tipLabel.getStyleClass().add(CssClasses.SPIN_TIP);
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
            arc.getStyleClass().add(CssClasses.SPIN_INDICATOR_SPINNER);

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
                dot.getStyleClass().add(CssClasses.SPIN_INDICATOR_DOT);
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
                bar.getStyleClass().add(CssClasses.SPIN_INDICATOR_BAR);
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
}
