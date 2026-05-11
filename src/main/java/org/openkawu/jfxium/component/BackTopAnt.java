package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * JFXium BackTop Component
 * Inspired by Ant Design BackTop
 * Returns the user to the top of the page.
 */
public class BackTopAnt {

    public static class Builder {
        private int visibilityHeight = 400;
        private Duration duration = Duration.millis(450);
        private Node target = null;
        private double bottom = 40;
        private double right = 40;

        public Builder visibilityHeight(int visibilityHeight) {
            this.visibilityHeight = visibilityHeight;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Builder bottom(double bottom) {
            this.bottom = bottom;
            return this;
        }

        public Builder right(double right) {
            this.right = right;
            return this;
        }

        public StackPane build() {
            StackPane backTop = new StackPane();
            backTop.getStyleClass().add("back-top");
            backTop.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-background-radius: 9999px;" +
                "-fx-border-radius: 9999px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-padding: 12px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
            );
            backTop.setPrefSize(44, 44);
            backTop.setMaxSize(44, 44);
            backTop.setVisible(false);
            backTop.setManaged(false);
            backTop.setOpacity(0);

            // Arrow icon
            SVGPath arrow = new SVGPath();
            arrow.setContent("M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z");
            arrow.setScaleX(1.5);
            arrow.setScaleY(1.5);
            arrow.setStyle("-fx-fill: -color-fg-default;");
            backTop.getChildren().add(arrow);

            // Hover effect
            backTop.setOnMouseEntered(e -> {
                backTop.setStyle(
                    "-fx-background-color: -color-bg-subtle;" +
                    "-fx-background-radius: 9999px;" +
                    "-fx-border-radius: 9999px;" +
                    "-fx-border-color: -color-border-default;" +
                    "-fx-border-width: 1px;" +
                    "-fx-padding: 12px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);"
                );
            });
            backTop.setOnMouseExited(e -> {
                backTop.setStyle(
                    "-fx-background-color: -color-bg-overlay;" +
                    "-fx-background-radius: 9999px;" +
                    "-fx-border-radius: 9999px;" +
                    "-fx-border-color: -color-border-default;" +
                    "-fx-border-width: 1px;" +
                    "-fx-padding: 12px;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
                );
            });

            // Click to scroll to top
            backTop.setOnMouseClicked(e -> scrollToTop());

            // Setup scroll listener
            setupScrollListener(backTop);

            return backTop;
        }

        private void setupScrollListener(StackPane backTop) {
            if (target instanceof ScrollPane scrollPane) {
                scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
                    double scrollY = newVal.doubleValue() * (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());
                    updateVisibility(backTop, scrollY);
                });
            }
        }

        private void updateVisibility(StackPane backTop, double scrollY) {
            boolean shouldShow = scrollY > visibilityHeight;
            if (shouldShow && !backTop.isVisible()) {
                backTop.setVisible(true);
                backTop.setManaged(true);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), backTop);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.setInterpolator(Interpolator.EASE_OUT);
                fadeIn.play();
            } else if (!shouldShow && backTop.isVisible()) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), backTop);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setInterpolator(Interpolator.EASE_IN);
                fadeOut.setOnFinished(e -> {
                    backTop.setVisible(false);
                    backTop.setManaged(false);
                });
                fadeOut.play();
            }
        }

        private void scrollToTop() {
            if (target instanceof ScrollPane scrollPane) {
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                        Duration.ZERO,
                        new javafx.animation.KeyValue(scrollPane.vvalueProperty(), scrollPane.getVvalue())
                    ),
                    new javafx.animation.KeyFrame(
                        duration,
                        new javafx.animation.KeyValue(scrollPane.vvalueProperty(), 0, Interpolator.EASE_BOTH)
                    )
                );
                timeline.play();
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }

    /**
     * Install BackTop on a ScrollPane
     */
    public static StackPane install(ScrollPane scrollPane) {
        StackPane backTop = create().target(scrollPane).build();

        // Add to the ScrollPane's parent container
        if (scrollPane.getParent() instanceof StackPane parent) {
            StackPane.setAlignment(backTop, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(backTop, new Insets(0, 40, 40, 0));
            parent.getChildren().add(backTop);
        }

        return backTop;
    }
}
