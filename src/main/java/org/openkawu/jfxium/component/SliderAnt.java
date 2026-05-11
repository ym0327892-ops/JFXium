package org.openkawu.jfxium.component;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium Slider 组件 - 全面对标 Ant Design Slider
 *
 * 支持特性：
 * - 基础滑动条
 * - 双滑块范围选择 (range)
 * - 刻度标记 (marks)
 * - 提示框 (tooltip)
 * - 禁用状态
 * - 垂直方向
 * - 反向
 *
 * 使用示例：
 * <pre>{@code
 * // 基础滑动条
 * Node slider = SliderAnt.create()
 *     .min(0)
 *     .max(100)
 *     .value(50)
 *     .build();
 *
 * // 带刻度标记
 * Node slider = SliderAnt.create()
 *     .min(0)
 *     .max(100)
 *     .marks(java.util.Map.of(0, "0°C", 26, "26°C", 37, "37°C", 100, "100°C"))
 *     .build();
 *
 * // 范围选择
 * Node rangeSlider = SliderAnt.create()
 *     .range(true)
 *     .min(0)
 *     .max(100)
 *     .defaultValue(new double[]{20, 80})
 *     .build();
 * }</pre>
 */
public class SliderAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private double min = 0;
        private double max = 100;
        private double value = 0;
        private double[] rangeValue = null;
        private double step = 1;
        private boolean range = false;
        private boolean disabled = false;
        private boolean vertical = false;
        private boolean reverse = false;
        private boolean dots = false;
        private boolean included = true;
        private java.util.Map<Double, String> marks = null;
        private Function<Double, String> tipFormatter = null;
        private boolean tooltipVisible = false;
        private Consumer<Double> onChange = null;
        private Consumer<Double> onChangeComplete = null;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder() {}

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public Builder defaultValue(double[] rangeValue) {
            this.rangeValue = rangeValue;
            return this;
        }

        public Builder step(double step) {
            this.step = step;
            return this;
        }

        /**
         * 双滑块模式
         */
        public Builder range(boolean range) {
            this.range = range;
            return this;
        }

        public Builder range() {
            return range(true);
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder vertical(boolean vertical) {
            this.vertical = vertical;
            return this;
        }

        public Builder reverse(boolean reverse) {
            this.reverse = reverse;
            return this;
        }

        /**
         * 是否只能拖拽到刻度上
         */
        public Builder dots(boolean dots) {
            this.dots = dots;
            return this;
        }

        /**
         * 刻度标记
         */
        public Builder marks(java.util.Map<Double, String> marks) {
            this.marks = marks;
            return this;
        }

        /**
         * 提示框格式化函数
         */
        public Builder tipFormatter(Function<Double, String> formatter) {
            this.tipFormatter = formatter;
            return this;
        }

        /**
         * 始终显示提示框
         */
        public Builder tooltipVisible(boolean visible) {
            this.tooltipVisible = visible;
            return this;
        }

        public Builder onChange(Consumer<Double> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder onChangeComplete(Consumer<Double> handler) {
            this.onChangeComplete = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Builder styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public Node build() {
            if (range) {
                return buildRangeSlider();
            } else {
                return buildSingleSlider();
            }
        }

        private Node buildSingleSlider() {
            Slider slider = new Slider(min, max, value);
            slider.setBlockIncrement(step);
            slider.setShowTickMarks(marks != null || dots);
            slider.setShowTickLabels(marks != null);
            slider.setOrientation(vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            slider.setDisable(disabled);

            // 设置轨道样式 - 对标 Ant Design
            slider.setStyle(getSliderStyle());

            // 刻度标记
            if (marks != null && !marks.isEmpty()) {
                slider.setMajorTickUnit(max - min);
                slider.setMinorTickCount(0);
            }

            // 事件监听
            if (onChange != null) {
                slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal.doubleValue());
                });
            }

            if (onChangeComplete != null) {
                slider.setOnMouseReleased(e -> onChangeComplete.accept(slider.getValue()));
            }

            // 包装容器 - 确保宽度约束
            VBox wrapper = new VBox(4);
            wrapper.setAlignment(vertical ? Pos.CENTER : Pos.CENTER_LEFT);
            wrapper.getStyleClass().add("jfx-slider-wrapper");

            // 设置宽度约束 - 对标 Ant Design 默认宽度
            if (!vertical) {
                slider.setPrefWidth(Region.USE_COMPUTED_SIZE);
                slider.setMinWidth(200);
                slider.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(slider, Priority.ALWAYS);
            } else {
                slider.setPrefHeight(200);
            }

            wrapper.getChildren().add(slider);

            // 添加刻度标签（自定义）
            if (marks != null && !marks.isEmpty()) {
                wrapper.getChildren().add(createMarksRow());
            }

            // 提示框
            if (tipFormatter != null || tooltipVisible) {
                Label tipLabel = new Label();
                tipLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-muted;");
                tipLabel.textProperty().bind(
                    javafx.beans.binding.Bindings.createStringBinding(
                        () -> {
                            double val = slider.getValue();
                            if (tipFormatter != null) {
                                return tipFormatter.apply(val);
                            }
                            return String.valueOf((int) val);
                        },
                        slider.valueProperty()
                    )
                );
                wrapper.getChildren().add(tipLabel);
            }

            slider.getStyleClass().addAll("jfx-slider");
            slider.getStyleClass().addAll(extraStyleClasses);

            if (!style.isEmpty()) {
                slider.setStyle(slider.getStyle() + style);
            }

            return wrapper;
        }

        private Node buildRangeSlider() {
            // 范围选择使用两个滑块模拟
            HBox rangeBox = new HBox(8);
            rangeBox.setAlignment(Pos.CENTER_LEFT);

            double startVal = rangeValue != null && rangeValue.length >= 2 ? rangeValue[0] : min;
            double endVal = rangeValue != null && rangeValue.length >= 2 ? rangeValue[1] : max;

            Slider startSlider = new Slider(min, max, startVal);
            Slider endSlider = new Slider(min, max, endVal);

            for (Slider slider : new Slider[]{startSlider, endSlider}) {
                slider.setBlockIncrement(step);
                slider.setShowTickMarks(false);
                slider.setShowTickLabels(false);
                slider.setDisable(disabled);
                slider.setStyle(getSliderStyle());
                slider.setPrefWidth(Region.USE_COMPUTED_SIZE);
                slider.setMinWidth(100);
                slider.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(slider, Priority.ALWAYS);
            }

            Label startLabel = new Label(String.valueOf((int) startVal));
            Label endLabel = new Label(String.valueOf((int) endVal));
            Label separator = new Label("~");

            startLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default;");
            endLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default;");
            separator.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-muted;");

            startSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                double newStart = newVal.doubleValue();
                if (newStart > endSlider.getValue()) {
                    newStart = endSlider.getValue();
                    startSlider.setValue(newStart);
                }
                startLabel.setText(String.valueOf((int) newStart));
                if (onChange != null) {
                    onChange.accept(newStart);
                }
            });

            endSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                double newEnd = newVal.doubleValue();
                if (newEnd < startSlider.getValue()) {
                    newEnd = startSlider.getValue();
                    endSlider.setValue(newEnd);
                }
                endLabel.setText(String.valueOf((int) newEnd));
                if (onChange != null) {
                    onChange.accept(newEnd);
                }
            });

            rangeBox.getChildren().addAll(startLabel, startSlider, separator, endSlider, endLabel);
            rangeBox.getStyleClass().add("jfx-slider-range");

            return rangeBox;
        }

        private String getSliderStyle() {
            StringBuilder sb = new StringBuilder();

            // 轨道样式 - 对标 Ant Design
            sb.append("-fx-control-inner-background: -color-border-muted;"); // railBg
            sb.append("-fx-control-inner-background-alt: -color-border-muted;");

            // 滑块样式
            sb.append("-fx-accent: -color-accent-emphasis;"); // trackBg

            // 禁用状态
            if (disabled) {
                sb.append("-fx-opacity: 0.5;");
            }

            return sb.toString();
        }

        private Node createMarksRow() {
            HBox marksRow = new HBox(0);
            marksRow.setAlignment(Pos.CENTER);
            marksRow.setStyle("-fx-padding: 4px 0 0 0;");

            // 创建刻度标签
            for (java.util.Map.Entry<Double, String> entry : marks.entrySet()) {
                Label markLabel = new Label(entry.getValue());
                markLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-muted;");
                marksRow.getChildren().add(markLabel);
                HBox.setHgrow(markLabel, Priority.ALWAYS);
            }

            return marksRow;
        }
    }
}
