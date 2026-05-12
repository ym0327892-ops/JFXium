package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * InputNumberAnt Component
 * Inspired by Ant Design InputNumber
 * Used to enter numeric values with increment/decrement controls.
 */
public class InputNumberAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class Builder {
        private double value = 0;
        private double min = Double.NEGATIVE_INFINITY;
        private double max = Double.POSITIVE_INFINITY;
        private double step = 1;
        private String placeholder = "";
        private boolean disabled = false;
        private boolean readOnly = false;
        private Size size = Size.DEFAULT;
        private Consumer<Double> onChange = null;
        private int precision = -1;
        private String prefix = null;
        private String suffix = null;

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public Builder step(double step) {
            this.step = step;
            return this;
        }

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder disabled() {
            return disabled(true);
        }

        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder onChange(Consumer<Double> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder precision(int precision) {
            this.precision = precision;
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("input-number");

            double padding = size == Size.SMALL ? 4 : size == Size.LARGE ? 12 : 8;
            double fontSize = size == Size.SMALL ? 12 : size == Size.LARGE ? 16 : 14;

            // Prefix
            if (prefix != null && !prefix.isEmpty()) {
                javafx.scene.control.Label prefixLabel = new javafx.scene.control.Label(prefix);
                prefixLabel.setStyle("-fx-font-size: " + fontSize + "px; -fx-text-fill: -color-fg-muted; -fx-padding: 0 4px 0 8px;");
                container.getChildren().add(prefixLabel);
            }

            // Text field
            TextField field = new TextField(formatValue(value));
            field.setAlignment(Pos.CENTER);
            field.setPrefWidth(80);
            field.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-font-size: " + fontSize + "px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-padding: " + padding + "px 4px;"
            );
            HBox.setHgrow(field, Priority.ALWAYS);

            field.setOnAction(e -> {
                try {
                    double newValue = Double.parseDouble(field.getText());
                    setValue(newValue, field);
                } catch (NumberFormatException ex) {
                    field.setText(formatValue(value));
                }
            });

            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    try {
                        double newValue = Double.parseDouble(field.getText());
                        setValue(newValue, field);
                    } catch (NumberFormatException ex) {
                        field.setText(formatValue(value));
                    }
                }
            });

            // Suffix
            if (suffix != null && !suffix.isEmpty()) {
                javafx.scene.control.Label suffixLabel = new javafx.scene.control.Label(suffix);
                suffixLabel.setStyle("-fx-font-size: " + fontSize + "px; -fx-text-fill: -color-fg-muted; -fx-padding: 0 8px 0 4px;");
                container.getChildren().add(suffixLabel);
            }

            // Decrement button (left side)
            Button decBtn = createButton("M2 8H14", "minus");
            decBtn.setOnAction(e -> adjustValue(-step, field));

            // Increment button (right side)
            Button incBtn = createButton("M8 2V14 M2 8H14", "plus");
            incBtn.setOnAction(e -> adjustValue(step, field));

            container.getChildren().addAll(decBtn, field, incBtn);

            container.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                (disabled ? "-fx-opacity: 0.6;" : "")
            );

            if (disabled) {
                decBtn.setDisable(true);
                incBtn.setDisable(true);
                field.setDisable(true);
            }

            return container;
        }

        private Button createButton(String svgPath, String type) {
            Button btn = new Button();
            btn.setMinSize(28, 32);
            btn.setPrefSize(28, 32);
            btn.setMaxSize(28, 32);
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4px;"
            );

            SVGPath path = new SVGPath();
            path.setContent(svgPath);
            path.setStyle("-fx-stroke: -color-fg-muted; -fx-stroke-width: 1.5; -fx-fill: none;");

            StackPane graphic = new StackPane(path);
            graphic.setAlignment(Pos.CENTER);
            btn.setGraphic(graphic);

            btn.setOnMousePressed(e -> {
                if (!disabled) {
                    btn.setStyle(
                        "-fx-background-color: -color-bg-subtle;" +
                        "-fx-border-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4px;"
                    );
                }
            });

            btn.setOnMouseReleased(e -> {
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: transparent;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 4px;"
                );
            });

            btn.setOnMouseEntered(e -> {
                if (!disabled) {
                    btn.setStyle(
                        "-fx-background-color: -color-bg-subtle;" +
                        "-fx-border-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 4px;"
                    );
                }
            });

            btn.setOnMouseExited(e -> {
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: transparent;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 4px;"
                );
            });

            return btn;
        }

        private void adjustValue(double delta, TextField field) {
            double currentValue;
            try {
                currentValue = Double.parseDouble(field.getText());
            } catch (NumberFormatException e) {
                currentValue = value;
            }
            double newValue = currentValue + delta;
            setValue(newValue, field);
        }

        private void setValue(double newValue, TextField field) {
            newValue = Math.max(min, Math.min(max, newValue));
            if (precision >= 0) {
                double factor = Math.pow(10, precision);
                newValue = Math.round(newValue * factor) / factor;
            }
            value = newValue;

            if (field != null) {
                field.setText(formatValue(value));
            }

            if (onChange != null) {
                onChange.accept(value);
            }
        }

        private String formatValue(double val) {
            if (precision >= 0) {
                return String.format("%." + precision + "f", val);
            }
            if (val == (long) val) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
