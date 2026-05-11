package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Carousel Component
 * Inspired by Ant Design Carousel
 * A carousel for cycling through elements.
 */
public class CarouselAnt {

    public enum Effect {
        SCROLL, FADE
    }

    public static class Builder {
        private List<Node> items = new ArrayList<>();
        private boolean autoplay = false;
        private Duration autoplayInterval = Duration.seconds(3);
        private boolean dots = true;
        private boolean arrows = true;
        private Effect effect = Effect.SCROLL;
        private int initialIndex = 0;

        public Builder item(Node item) {
            this.items.add(item);
            return this;
        }

        public Builder items(Node... items) {
            this.items.addAll(List.of(items));
            return this;
        }

        public Builder autoplay(boolean autoplay) {
            this.autoplay = autoplay;
            return this;
        }

        public Builder autoplay() {
            return autoplay(true);
        }

        public Builder autoplayInterval(Duration interval) {
            this.autoplayInterval = interval;
            return this;
        }

        public Builder dots(boolean dots) {
            this.dots = dots;
            return this;
        }

        public Builder noDots() {
            return dots(false);
        }

        public Builder arrows(boolean arrows) {
            this.arrows = arrows;
            return this;
        }

        public Builder noArrows() {
            return arrows(false);
        }

        public Builder effect(Effect effect) {
            this.effect = effect;
            return this;
        }

        public Builder initialIndex(int index) {
            this.initialIndex = index;
            return this;
        }

        public StackPane build() {
            if (items.isEmpty()) {
                StackPane empty = new StackPane(new Label("No items"));
                empty.setStyle("-fx-min-height: 200px;");
                return empty;
            }

            StackPane carousel = new StackPane();
            carousel.getStyleClass().add("carousel");
            carousel.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 8px; -fx-overflow: hidden;");
            carousel.setPrefHeight(300);

            // Content container
            StackPane contentPane = new StackPane();
            contentPane.setStyle("-fx-background-radius: 8px;");

            for (Node item : items) {
                item.setVisible(false);
                contentPane.getChildren().add(item);
            }

            // Show initial item
            if (initialIndex >= 0 && initialIndex < items.size()) {
                items.get(initialIndex).setVisible(true);
            } else {
                items.get(0).setVisible(true);
            }

            carousel.getChildren().add(contentPane);

            // Navigation state
            final int[] currentIndex = {initialIndex >= 0 && initialIndex < items.size() ? initialIndex : 0};

            // Arrow buttons
            if (arrows && items.size() > 1) {
                Button prevBtn = createArrowButton("<");
                Button nextBtn = createArrowButton(">");

                StackPane.setAlignment(prevBtn, Pos.CENTER_LEFT);
                StackPane.setAlignment(nextBtn, Pos.CENTER_RIGHT);
                StackPane.setMargin(prevBtn, new javafx.geometry.Insets(0, 0, 0, 8));
                StackPane.setMargin(nextBtn, new javafx.geometry.Insets(0, 8, 0, 0));

                prevBtn.setOnAction(e -> {
                    int newIndex = currentIndex[0] - 1;
                    if (newIndex < 0) newIndex = items.size() - 1;
                    navigateTo(items, currentIndex, newIndex, effect, contentPane);
                });

                nextBtn.setOnAction(e -> {
                    int newIndex = currentIndex[0] + 1;
                    if (newIndex >= items.size()) newIndex = 0;
                    navigateTo(items, currentIndex, newIndex, effect, contentPane);
                });

                carousel.getChildren().addAll(prevBtn, nextBtn);
            }

            // Dots
            if (dots && items.size() > 1) {
                HBox dotsBox = new HBox(8);
                dotsBox.setAlignment(Pos.CENTER);
                dotsBox.setStyle("-fx-padding: 12px;");
                StackPane.setAlignment(dotsBox, Pos.BOTTOM_CENTER);

                List<Circle> dotCircles = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    Circle dot = new Circle(4);
                    final int index = i;
                    dot.setOnMouseClicked(e -> navigateTo(items, currentIndex, index, effect, contentPane));
                    dot.setStyle("-fx-cursor: hand;");
                    dotCircles.add(dot);
                    dotsBox.getChildren().add(dot);
                }

                // Update dots
                updateDots(dotCircles, currentIndex[0]);

                // Hook into navigation to update dots
                carousel.getProperties().put("dotCircles", dotCircles);
                carousel.getProperties().put("currentIndex", currentIndex);

                carousel.getChildren().add(dotsBox);
            }

            // Autoplay
            if (autoplay && items.size() > 1) {
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(autoplayInterval, e -> {
                        int newIndex = currentIndex[0] + 1;
                        if (newIndex >= items.size()) newIndex = 0;
                        navigateTo(items, currentIndex, newIndex, effect, contentPane);

                        // Update dots
                        @SuppressWarnings("unchecked")
                        List<Circle> dotCircles = (List<Circle>) carousel.getProperties().get("dotCircles");
                        if (dotCircles != null) {
                            updateDots(dotCircles, newIndex);
                        }
                    })
                );
                timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
                timeline.play();

                // Pause on hover
                carousel.setOnMouseEntered(e -> timeline.pause());
                carousel.setOnMouseExited(e -> timeline.play());
            }

            return carousel;
        }

        private Button createArrowButton(String text) {
            Button btn = new Button(text);
            btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.7);" +
                "-fx-background-radius: 9999px;" +
                "-fx-min-width: 32px;" +
                "-fx-min-height: 32px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.9);" +
                "-fx-background-radius: 9999px;" +
                "-fx-min-width: 32px;" +
                "-fx-min-height: 32px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.7);" +
                "-fx-background-radius: 9999px;" +
                "-fx-min-width: 32px;" +
                "-fx-min-height: 32px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
            ));
            return btn;
        }

        private void navigateTo(List<Node> items, int[] currentIndex, int newIndex, Effect effect, StackPane contentPane) {
            if (newIndex == currentIndex[0]) return;

            Node currentItem = items.get(currentIndex[0]);
            Node newItem = items.get(newIndex);

            if (effect == Effect.FADE) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentItem);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    currentItem.setVisible(false);
                    newItem.setVisible(true);
                    newItem.setOpacity(0);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newItem);
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                // Scroll effect
                currentItem.setVisible(false);
                newItem.setVisible(true);
                newItem.setTranslateX(contentPane.getWidth());
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newItem);
                slideIn.setFromX(contentPane.getWidth());
                slideIn.setToX(0);
                slideIn.setInterpolator(Interpolator.EASE_OUT);
                slideIn.play();
            }

            currentIndex[0] = newIndex;
        }

        private void updateDots(List<Circle> dots, int activeIndex) {
            for (int i = 0; i < dots.size(); i++) {
                if (i == activeIndex) {
                    dots.get(i).setStyle("-fx-fill: -color-accent-emphasis;");
                    dots.get(i).setRadius(5);
                } else {
                    dots.get(i).setStyle("-fx-fill: -color-border-default;");
                    dots.get(i).setRadius(4);
                }
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
