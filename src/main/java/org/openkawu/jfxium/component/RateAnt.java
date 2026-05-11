package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * JFXium Rate Component
 * Inspired by Ant Design Rate
 */
public class RateAnt {

    public enum Size {
        SMALL(16), DEFAULT(24), LARGE(32);

        private final int value;

        Size(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Builder {
        private int count = 5;
        private double value = 0;
        private double defaultValue = 0;
        private boolean allowHalf = false;
        private boolean disabled = false;
        private Size size = Size.DEFAULT;
        private String activeColor = "-color-warning-emphasis";
        private String inactiveColor = "-color-border-default";
        private Consumer<Double> onChange = null;
        private Consumer<Double> onHoverChange = null;

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            this.defaultValue = value;
            return this;
        }

        public Builder defaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            return this;
        }

        public Builder allowHalf(boolean allowHalf) {
            this.allowHalf = allowHalf;
            return this;
        }

        public Builder allowHalf() {
            return allowHalf(true);
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder disabled() {
            return disabled(true);
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder onChange(Consumer<Double> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder onHoverChange(Consumer<Double> onHoverChange) {
            this.onHoverChange = onHoverChange;
            return this;
        }

        public HBox build() {
            HBox rateBox = new HBox(4);
            rateBox.setAlignment(Pos.CENTER_LEFT);
            rateBox.getStyleClass().add("rate");

            if (disabled) {
                rateBox.setOpacity(0.6);
            }

            int starSize = size.getValue();
            SVGPath[] stars = new SVGPath[count];

            for (int i = 0; i < count; i++) {
                final int starIndex = i + 1;
                SVGPath star = createStar(starSize);
                stars[i] = star;

                updateStarColor(star, starIndex, value, inactiveColor, activeColor);

                if (!disabled) {
                    final int index = i;
                    star.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                        double hoverValue = allowHalf ? calculateHalfValue(index, e.getX(), starSize) : starIndex;
                        for (int j = 0; j < count; j++) {
                            updateStarColor(stars[j], j + 1, hoverValue, inactiveColor, activeColor);
                        }
                        if (onHoverChange != null) {
                            onHoverChange.accept(hoverValue);
                        }
                    });

                    star.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                        for (int j = 0; j < count; j++) {
                            updateStarColor(stars[j], j + 1, value, inactiveColor, activeColor);
                        }
                    });

                    star.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        double newValue = allowHalf ? calculateHalfValue(index, e.getX(), starSize) : starIndex;
                        value = newValue;
                        for (int j = 0; j < count; j++) {
                            updateStarColor(stars[j], j + 1, value, inactiveColor, activeColor);
                        }
                        if (onChange != null) {
                            onChange.accept(value);
                        }
                    });
                }

                rateBox.getChildren().add(star);
            }

            return rateBox;
        }

        private SVGPath createStar(int size) {
            SVGPath star = new SVGPath();
            star.setContent("M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z");
            star.setScaleX(size / 24.0);
            star.setScaleY(size / 24.0);
            star.setStyle("-fx-cursor: hand;");
            return star;
        }

        private void updateStarColor(SVGPath star, int starIndex, double currentValue, String inactiveColor, String activeColor) {
            if (starIndex <= currentValue) {
                star.setFill(Color.web("#faad14"));
                star.setStyle("-fx-fill: " + activeColor + "; -fx-cursor: hand;");
            } else if (starIndex - 0.5 <= currentValue && allowHalf) {
                star.setFill(Color.web("#faad14"));
                star.setStyle("-fx-fill: " + activeColor + "; -fx-cursor: hand;");
            } else {
                star.setFill(Color.web("#d9d9d9"));
                star.setStyle("-fx-fill: " + inactiveColor + "; -fx-cursor: hand;");
            }
        }

        private double calculateHalfValue(int index, double x, int size) {
            return x < size / 2.0 ? index + 0.5 : index + 1.0;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
