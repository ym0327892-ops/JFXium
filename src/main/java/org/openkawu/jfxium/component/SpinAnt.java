package org.openkawu.jfxium.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * JFXium Spin Component
 * Inspired by Ant Design Spin
 * Used for loading states with various indicators.
 */
public class SpinAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public enum Indicator {
        SPINNER, DOTS, BARS
    }

    public static class Builder {
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
            spin.getStyleClass().add("spin");

            if (fullscreen) {
                spin.getStyleClass().add("spin-fullscreen");
                spin.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-min-width: 100%; -fx-min-height: 100%;");
            }

            double scale = size == Size.SMALL ? 0.6 : size == Size.LARGE ? 1.4 : 1.0;

            javafx.scene.Node indicatorNode = createIndicator(indicator, scale);
            spin.getChildren().add(indicatorNode);

            if (tip != null && !tip.isEmpty()) {
                Label tipLabel = new Label(tip);
                tipLabel.getStyleClass().add("spin-tip");
                tipLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted;");
                spin.getChildren().add(tipLabel);
            }

            return spin;
        }

        private javafx.scene.Node createIndicator(Indicator type, double scale) {
            switch (type) {
                case DOTS:
                    return createDotsIndicator(scale);
                case BARS:
                    return createBarsIndicator(scale);
                case SPINNER:
                default:
                    return createSpinnerIndicator(scale);
            }
        }

        private javafx.scene.Node createSpinnerIndicator(double scale) {
            javafx.scene.layout.StackPane container = new javafx.scene.layout.StackPane();
            container.setPrefSize(32 * scale, 32 * scale);

            Arc arc = new Arc(16 * scale, 16 * scale, 14 * scale, 14 * scale, 0, 270);
            arc.setType(ArcType.OPEN);
            arc.setFill(null);
            arc.setStroke(javafx.scene.paint.Color.web("#1677ff"));
            arc.setStrokeWidth(3);
            arc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

            Rotate rotate = new Rotate(0, 16 * scale, 16 * scale);
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

        private javafx.scene.Node createDotsIndicator(double scale) {
            javafx.scene.layout.HBox dots = new javafx.scene.layout.HBox(6 * scale);
            dots.setAlignment(Pos.CENTER);

            double dotSize = 8 * scale;
            javafx.scene.paint.Color color = javafx.scene.paint.Color.web("#1677ff");

            for (int i = 0; i < 3; i++) {
                Circle dot = new Circle(dotSize / 2, color);
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

        private javafx.scene.Node createBarsIndicator(double scale) {
            javafx.scene.layout.HBox bars = new javafx.scene.layout.HBox(3 * scale);
            bars.setAlignment(Pos.BOTTOM_CENTER);

            double barWidth = 4 * scale;
            javafx.scene.paint.Color color = javafx.scene.paint.Color.web("#1677ff");

            for (int i = 0; i < 5; i++) {
                Rectangle bar = new Rectangle(barWidth, 16 * scale, color);
                bar.setOpacity(0.3);
                bar.setArcWidth(2);
                bar.setArcHeight(2);

                Timeline timeline = new Timeline();
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.setAutoReverse(true);

                double baseHeight = 16 * scale;
                double maxHeight = 28 * scale;

                KeyFrame start = new KeyFrame(Duration.millis(i * 100), new KeyValue(bar.heightProperty(), baseHeight));
                KeyFrame mid = new KeyFrame(Duration.millis(i * 100 + 300), new KeyValue(bar.heightProperty(), maxHeight));
                KeyFrame end = new KeyFrame(Duration.millis(i * 100 + 600), new KeyValue(bar.heightProperty(), baseHeight));

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
