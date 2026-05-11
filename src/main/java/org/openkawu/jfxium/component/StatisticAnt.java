package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * JFXium Statistic Component
 * Inspired by Ant Design Statistic
 * Used to display statistical data with title and value.
 */
public class StatisticAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class Builder {
        private String title = "";
        private String value = "";
        private String prefix = null;
        private String suffix = null;
        private String precision = null;
        private Size size = Size.DEFAULT;
        private Color valueColor = null;
        private Node prefixNode = null;
        private Node suffixNode = null;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder value(double value) {
            this.value = String.valueOf(value);
            return this;
        }

        public Builder value(int value) {
            this.value = String.valueOf(value);
            return this;
        }

        public Builder value(long value) {
            this.value = String.valueOf(value);
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder prefix(Node prefixNode) {
            this.prefixNode = prefixNode;
            return this;
        }

        public Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder suffix(Node suffixNode) {
            this.suffixNode = suffixNode;
            return this;
        }

        public Builder precision(int precision) {
            this.precision = String.valueOf(precision);
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder valueColor(Color color) {
            this.valueColor = color;
            return this;
        }

        public VBox build() {
            VBox statistic = new VBox(4);
            statistic.setAlignment(Pos.CENTER_LEFT);
            statistic.getStyleClass().add("statistic");

            double titleSize = size == Size.SMALL ? 12 : size == Size.LARGE ? 16 : 14;
            double valueSize = size == Size.SMALL ? 20 : size == Size.LARGE ? 36 : 24;

            // Title
            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add("statistic-title");
                titleLabel.setStyle(
                    "-fx-font-size: " + titleSize + "px;" +
                    "-fx-text-fill: -color-fg-muted;"
                );
                statistic.getChildren().add(titleLabel);
            }

            // Value row
            HBox valueRow = new HBox(4);
            valueRow.setAlignment(Pos.CENTER_LEFT);

            String valueColorStr = valueColor != null ? toHex(valueColor) : "-color-fg-default";
            String valueStyle = "-fx-font-size: " + valueSize + "px; -fx-font-weight: 600; -fx-text-fill: " + valueColorStr + ";";

            // Prefix
            if (prefixNode != null) {
                valueRow.getChildren().add(prefixNode);
            } else if (prefix != null && !prefix.isEmpty()) {
                Label prefixLabel = new Label(prefix);
                prefixLabel.setStyle("-fx-font-size: " + (valueSize - 4) + "px; -fx-text-fill: " + valueColorStr + ";");
                valueRow.getChildren().add(prefixLabel);
            }

            // Value
            Label valueLabel = new Label(value);
            valueLabel.getStyleClass().add("statistic-value");
            valueLabel.setStyle(valueStyle);
            valueRow.getChildren().add(valueLabel);

            // Suffix
            if (suffixNode != null) {
                valueRow.getChildren().add(suffixNode);
            } else if (suffix != null && !suffix.isEmpty()) {
                Label suffixLabel = new Label(suffix);
                suffixLabel.setStyle("-fx-font-size: " + (valueSize - 4) + "px; -fx-text-fill: -color-fg-muted;");
                valueRow.getChildren().add(suffixLabel);
            }

            statistic.getChildren().add(valueRow);

            return statistic;
        }

        private String toHex(Color color) {
            return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
