package org.openkawu.jfxium.component.composite;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * JFXium Skeleton Component
 * Inspired by Ant Design Skeleton
 */
public class SkeletonAnt {

    public enum Variant {
        TEXT, CIRCULAR, RECTANGULAR, ROUNDED
    }

    public static class Builder {
        private Variant variant = Variant.TEXT;
        private double width = 200;
        private double height = 16;
        private boolean animated = true;

        public Builder variant(Variant variant) {
            this.variant = variant;
            return this;
        }

        public Builder width(double width) {
            this.width = width;
            return this;
        }

        public Builder height(double height) {
            this.height = height;
            return this;
        }

        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        public Builder noAnimation() {
            return animated(false);
        }

        public StackPane build() {
            StackPane skeleton = new StackPane();
            skeleton.getStyleClass().add("skeleton");
            skeleton.setPrefSize(width, height);
            skeleton.setMaxSize(width, height);

            Rectangle rect = new Rectangle(width, height);
            String baseColor = "-color-bg-subtle";
            String highlightColor = "-color-base-2";

            rect.setStyle("-fx-fill: " + baseColor + ";");

            switch (variant) {
                case CIRCULAR -> {
                    double size = Math.min(width, height);
                    rect.setWidth(size);
                    rect.setHeight(size);
                    rect.setArcWidth(size);
                    rect.setArcHeight(size);
                }
                case ROUNDED -> {
                    rect.setArcWidth(8);
                    rect.setArcHeight(8);
                }
                case RECTANGULAR -> {
                    rect.setArcWidth(0);
                    rect.setArcHeight(0);
                }
                default -> {
                    rect.setArcWidth(4);
                    rect.setArcHeight(4);
                }
            }

            skeleton.getChildren().add(rect);

            if (animated) {
                Rectangle shimmer = new Rectangle(width, height);
                shimmer.setFill(Color.web("#ffffff", 0.1));
                shimmer.setTranslateX(-width);

                switch (variant) {
                    case CIRCULAR -> {
                        double size = Math.min(width, height);
                        shimmer.setWidth(size);
                        shimmer.setHeight(size);
                        shimmer.setArcWidth(size);
                        shimmer.setArcHeight(size);
                    }
                    case ROUNDED -> {
                        shimmer.setArcWidth(8);
                        shimmer.setArcHeight(8);
                    }
                    default -> {
                        shimmer.setArcWidth(4);
                        shimmer.setArcHeight(4);
                    }
                }

                Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(shimmer.translateXProperty(), -width)),
                    new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(shimmer.translateXProperty(), width * 2))
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                skeleton.getChildren().add(shimmer);
            }

            return skeleton;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    /**
     * Create a skeleton paragraph with multiple lines
     */
    public static VBox paragraph(int lines) {
        return paragraph(lines, 200, 16);
    }

    public static VBox paragraph(int lines, double width, double lineHeight) {
        VBox container = new VBox(8);
        for (int i = 0; i < lines; i++) {
            double lineWidth = (i == lines - 1) ? width * 0.6 : width;
            container.getChildren().add(
                SkeletonAnt.create()
                    .width(lineWidth)
                    .height(lineHeight)
                    .build()
            );
        }
        return container;
    }

    /**
     * Create a skeleton avatar + text combination
     */
    public static HBox avatarText() {
        HBox container = new HBox(12);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.getChildren().addAll(
            SkeletonAnt.create()
                .variant(Variant.CIRCULAR)
                .width(40)
                .height(40)
                .build(),
            SkeletonAnt.paragraph(2, 160, 12)
        );
        return container;
    }
}
