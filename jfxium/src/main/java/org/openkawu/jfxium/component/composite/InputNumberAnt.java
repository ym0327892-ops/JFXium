package org.openkawu.jfxium.component.composite;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 数字输入框组件 - 对标 Ant Design InputNumber（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：带 +/- 按钮的数字输入控件，支持范围限制、步长、精度控制，
 * 与 SpinnerAnt（加载旋转）严格区分。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>范围限制</b>：min / max</li>
 *   <li><b>步长</b>：step（默认 1）</li>
 *   <li><b>精度</b>：precision（小数位数）</li>
 *   <li><b>尺寸</b>：SMALL / DEFAULT / LARGE</li>
 *   <li><b>前缀/后缀</b>：prefix / suffix（如￥、%）</li>
 *   <li><b>禁用/只读</b>：disabled / readOnly</li>
 *   <li>视觉样式走 LESS（{@code .jfx-input-number} 系列）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node numInput = InputNumberAnt.create()
 *     .value(10)
 *     .min(0).max(100)
 *     .step(5)
 *     .suffix("%")
 *     .size(InputNumberAnt.Size.SMALL)
 *     .onChange(val -> System.out.println("数值：" + val))
 *     .build();
 * }</pre>
 */
public class InputNumberAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
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
        private DoubleProperty bindProperty = null;

        public Builder value(double value) { this.value = value; return this; }
        public Builder min(double min) { this.min = min; return this; }
        public Builder max(double max) { this.max = max; return this; }
        public Builder step(double step) { this.step = Double.isFinite(step) && step > 0 ? step : 1; return this; }
        public Builder placeholder(String placeholder) { this.placeholder = placeholder != null ? placeholder : ""; return this; }
        public Builder disabled(boolean disabled) { this.disabled = disabled; return this; }
        public Builder disabled() { return disabled(true); }
        public Builder readOnly(boolean readOnly) { this.readOnly = readOnly; return this; }
        public Builder size(Size size) { this.size = size != null ? size : Size.DEFAULT; return this; }
        public Builder onChange(Consumer<Double> onChange) { this.onChange = onChange; return this; }
        public Builder precision(int precision) { this.precision = precision; return this; }
        public Builder prefix(String prefix) { this.prefix = prefix; return this; }
        public Builder suffix(String suffix) { this.suffix = suffix; return this; }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(DoubleProperty property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            normalizeRange();
            if (bindProperty != null && Double.isFinite(bindProperty.get())) {
                value = normalizeValue(bindProperty.get());
                if (Double.compare(bindProperty.get(), value) != 0) {
                    bindProperty.set(value);
                }
            } else {
                value = normalizeValue(value);
            }

            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add(JfxStyles.INPUT_NUMBER);
            if (disabled) container.getStyleClass().add(JfxStyles.INPUT_NUMBER_DISABLED);
            if (size == Size.SMALL) container.getStyleClass().add(JfxStyles.INPUT_NUMBER_SMALL);
            if (size == Size.LARGE) container.getStyleClass().add(JfxStyles.INPUT_NUMBER_LARGE);

            if (prefix != null && !prefix.isEmpty()) {
                Label prefixLabel = new Label(prefix);
                prefixLabel.getStyleClass().add(JfxStyles.INPUT_NUMBER_PREFIX);
                container.getChildren().add(prefixLabel);
            }

            TextField field = new TextField(formatValue(value));
            field.setAlignment(Pos.CENTER);
            field.setPromptText(placeholder != null ? placeholder : "");
            field.setEditable(!readOnly);
            field.setPrefWidth(80);
            field.getStyleClass().add(JfxStyles.INPUT_NUMBER_FIELD);
            HBox.setHgrow(field, Priority.ALWAYS);

            if (bindProperty != null) {
                bindProperty.addListener((obs, oldVal, newVal) -> {
                    double normalized = normalizeValue(newVal.doubleValue());
                    if (Double.compare(normalized, value) != 0) {
                        value = normalized;
                        field.setText(formatValue(value));
                    }
                    if (Double.compare(newVal.doubleValue(), normalized) != 0) {
                        bindProperty.set(normalized);
                    }
                });
            }

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

            Button decBtn = createButton("M2 8H14");
            decBtn.setOnAction(e -> adjustValue(-step, field));
            Button incBtn = createButton("M8 2V14 M2 8H14");
            incBtn.setOnAction(e -> adjustValue(step, field));

            container.getChildren().addAll(decBtn, field, incBtn);

            if (suffix != null && !suffix.isEmpty()) {
                Label suffixLabel = new Label(suffix);
                suffixLabel.getStyleClass().add(JfxStyles.INPUT_NUMBER_SUFFIX);
                container.getChildren().add(suffixLabel);
            }

            if (disabled) {
                decBtn.setDisable(true);
                incBtn.setDisable(true);
                field.setDisable(true);
            }
            applyStyles(container);
            return container;
        }

        /** 创建 +/- 按钮：尺寸结构性、视觉样式与 hover/armed 由 LESS 控制 */
        private Button createButton(String svgPath) {
            Button btn = new Button();
            btn.setMinSize(28, 32);
            btn.setPrefSize(28, 32);
            btn.setMaxSize(28, 32);
            btn.getStyleClass().add(JfxStyles.INPUT_NUMBER_BTN);

            SVGPath path = new SVGPath();
            path.setContent(svgPath);
            path.getStyleClass().add(JfxStyles.INPUT_NUMBER_ARROW);

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
            newValue = normalizeValue(newValue);
            boolean changed = Double.compare(value, newValue) != 0;
            value = newValue;
            if (bindProperty != null) {
                bindProperty.set(value);
            }
            if (field != null) field.setText(formatValue(value));
            if (changed && onChange != null) onChange.accept(value);
        }

        private void normalizeRange() {
            if (!Double.isFinite(min)) {
                min = Double.NEGATIVE_INFINITY;
            }
            if (!Double.isFinite(max)) {
                max = Double.POSITIVE_INFINITY;
            }
            if (max < min) {
                double oldMin = min;
                min = max;
                max = oldMin;
            }
        }

        private double normalizeValue(double rawValue) {
            double candidate = Double.isFinite(rawValue) ? rawValue : 0;
            double normalized = Math.max(min, Math.min(max, candidate));
            if (precision >= 0) {
                double factor = Math.pow(10, precision);
                normalized = Math.round(normalized * factor) / factor;
            }
            if (!Double.isFinite(normalized)) {
                normalized = 0;
            }
            return normalized;
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
