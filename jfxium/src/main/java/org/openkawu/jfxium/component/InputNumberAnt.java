package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Consumer;

/**
 * InputNumberAnt - 对标 Ant Design InputNumber。
 *
 * 重构：容器/前后缀/编辑框/+/- 按钮的视觉样式全部走 LESS，
 * hover/pressed 由 LESS 伪类控制，不再用 setOnMouseEntered/Pressed/Released 注入字符串。
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

        public Builder value(double value) { this.value = value; return this; }
        public Builder min(double min) { this.min = min; return this; }
        public Builder max(double max) { this.max = max; return this; }
        public Builder step(double step) { this.step = step; return this; }
        public Builder placeholder(String placeholder) { this.placeholder = placeholder; return this; }
        public Builder disabled(boolean disabled) { this.disabled = disabled; return this; }
        public Builder disabled() { return disabled(true); }
        public Builder readOnly(boolean readOnly) { this.readOnly = readOnly; return this; }
        public Builder size(Size size) { this.size = size; return this; }
        public Builder onChange(Consumer<Double> onChange) { this.onChange = onChange; return this; }
        public Builder precision(int precision) { this.precision = precision; return this; }
        public Builder prefix(String prefix) { this.prefix = prefix; return this; }
        public Builder suffix(String suffix) { this.suffix = suffix; return this; }

        public HBox build() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add(CssClasses.INPUT_NUMBER);
            if (disabled) container.getStyleClass().add(CssClasses.INPUT_NUMBER_DISABLED);

            if (prefix != null && !prefix.isEmpty()) {
                Label prefixLabel = new Label(prefix);
                prefixLabel.getStyleClass().add(CssClasses.INPUT_NUMBER_PREFIX);
                container.getChildren().add(prefixLabel);
            }

            TextField field = new TextField(formatValue(value));
            field.setAlignment(Pos.CENTER);
            field.setPrefWidth(80);
            field.getStyleClass().add(CssClasses.INPUT_NUMBER_FIELD);
            HBox.setHgrow(field, Priority.ALWAYS);

            field.setOnAction(e -> {
                try {
                    setValue(Double.parseDouble(field.getText()), field);
                } catch (NumberFormatException ex) {
                    field.setText(formatValue(value));
                }
            });
            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    try {
                        setValue(Double.parseDouble(field.getText()), field);
                    } catch (NumberFormatException ex) {
                        field.setText(formatValue(value));
                    }
                }
            });

            if (suffix != null && !suffix.isEmpty()) {
                Label suffixLabel = new Label(suffix);
                suffixLabel.getStyleClass().add(CssClasses.INPUT_NUMBER_SUFFIX);
                container.getChildren().add(suffixLabel);
            }

            Button decBtn = createButton("M2 8H14");
            decBtn.setOnAction(e -> adjustValue(-step, field));
            Button incBtn = createButton("M8 2V14 M2 8H14");
            incBtn.setOnAction(e -> adjustValue(step, field));

            container.getChildren().addAll(decBtn, field, incBtn);

            if (disabled) {
                decBtn.setDisable(true);
                incBtn.setDisable(true);
                field.setDisable(true);
            }
            return container;
        }

        /** 创建 +/- 按钮：尺寸结构性、视觉样式与 hover/armed 由 LESS 控制 */
        private Button createButton(String svgPath) {
            Button btn = new Button();
            btn.setMinSize(28, 32);
            btn.setPrefSize(28, 32);
            btn.setMaxSize(28, 32);
            btn.getStyleClass().add(CssClasses.INPUT_NUMBER_BTN);

            SVGPath path = new SVGPath();
            path.setContent(svgPath);
            path.getStyleClass().add(CssClasses.INPUT_NUMBER_ARROW);

            StackPane graphic = new StackPane(path);
            graphic.setAlignment(Pos.CENTER);
            btn.setGraphic(graphic);
            return btn;
        }

        private void adjustValue(double delta, TextField field) {
            double currentValue;
            try {
                currentValue = Double.parseDouble(field.getText());
            } catch (NumberFormatException e) {
                currentValue = value;
            }
            setValue(currentValue + delta, field);
        }

        private void setValue(double newValue, TextField field) {
            newValue = Math.max(min, Math.min(max, newValue));
            if (precision >= 0) {
                double factor = Math.pow(10, precision);
                newValue = Math.round(newValue * factor) / factor;
            }
            value = newValue;
            if (field != null) field.setText(formatValue(value));
            if (onChange != null) onChange.accept(value);
        }

        private String formatValue(double val) {
            if (precision >= 0) {
                return String.format("%." + precision + "f", val);
            }
            if (val == (long) val) return String.valueOf((long) val);
            return String.valueOf(val);
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
