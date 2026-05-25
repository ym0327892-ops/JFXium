package org.openkawu.jfxium.component;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium Slider 组件 - 全面对标 Ant Design Slider。
 *
 * <h2>修复说明</h2>
 * 原实现存在多处 inline {@code setStyle} 注入：
 * <ul>
 *   <li>{@code getSliderStyle()} 拼接 {@code -fx-control-inner-background} / {@code -fx-accent}</li>
 *   <li>{@code tipLabel} / {@code startLabel} / {@code endLabel} / {@code separator} / {@code marksRow} / {@code markLabel}
 *       6 处 setStyle 拼字号/颜色/padding</li>
 *   <li>{@code slider.setStyle(slider.getStyle() + style)} 在已有 inline 上叠加用户 style</li>
 * </ul>
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>删除 {@code getSliderStyle()}：{@code -fx-control-inner-background}/{@code -fx-accent}
 *       已搬到 {@code theme-base.less} 的 {@code .slider} 选择器</li>
 *   <li>tip/range labels/marks 全部改为挂 styleClass，颜色字号由 LESS 控制</li>
 *   <li>{@code disabled} 状态：通过 {@code .slider-disabled} styleClass 切换 opacity，
 *       不再 inline 拼字符串</li>
 *   <li>接入 {@link AbstractStyleBuilder}</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * Node slider = SliderAnt.create()
 *     .min(0).max(100).value(50)
 *     .build();
 *
 * Node ranged = SliderAnt.create()
 *     .range().min(0).max(100)
 *     .defaultValue(new double[]{20, 80})
 *     .build();
 * }</pre>
 */
public class SliderAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
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
        private Map<Double, String> marks = null;
        private Function<Double, String> tipFormatter = null;
        private boolean tooltipVisible = false;
        private Consumer<Double> onChange = null;
        private Consumer<Double> onChangeComplete = null;

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

        public Builder dots(boolean dots) {
            this.dots = dots;
            return this;
        }

        public Builder marks(Map<Double, String> marks) {
            this.marks = marks;
            return this;
        }

        public Builder tipFormatter(Function<Double, String> formatter) {
            this.tipFormatter = formatter;
            return this;
        }

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

        public Node build() {
            return range ? buildRangeSlider() : buildSingleSlider();
        }

        private Node buildSingleSlider() {
            Slider slider = new Slider(min, max, value);
            slider.setBlockIncrement(step);
            slider.setShowTickMarks(marks != null || dots);
            slider.setShowTickLabels(marks != null);
            slider.setOrientation(vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            slider.setDisable(disabled);
            slider.getStyleClass().add(CssClasses.SLIDER);
            // disabled 通过 styleClass 切换 opacity，不再 inline
            if (disabled) {
                slider.getStyleClass().add(CssClasses.SLIDER_DISABLED);
            }

            // 刻度标记：让 Slider 知道总刻度数（视觉刻度由 createMarksRow 自定义渲染）
            if (marks != null && !marks.isEmpty()) {
                slider.setMajorTickUnit(max - min);
                slider.setMinorTickCount(0);
            }

            if (onChange != null) {
                slider.valueProperty().addListener((obs, oldVal, newVal) ->
                        onChange.accept(newVal.doubleValue()));
            }
            if (onChangeComplete != null) {
                slider.setOnMouseReleased(e -> onChangeComplete.accept(slider.getValue()));
            }

            // 宽度约束：水平方向至少 200px、可拉伸；垂直方向固定高度 200
            if (!vertical) {
                slider.setPrefWidth(Region.USE_COMPUTED_SIZE);
                slider.setMinWidth(200);
                slider.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(slider, Priority.ALWAYS);
            } else {
                slider.setPrefHeight(200);
            }

            VBox contentBox = new VBox(4);
            contentBox.setAlignment(vertical ? Pos.CENTER : Pos.CENTER_LEFT);
            contentBox.getChildren().add(slider);

            // 用 StackPane 加 clip 防止 thumb 拖动时超出容器
            StackPane clipContainer = new StackPane(contentBox);
            clipContainer.layoutBoundsProperty().addListener((obs, old, newVal) -> {
                Rectangle clip = (Rectangle) clipContainer.getClip();
                if (clip == null) {
                    clip = new Rectangle();
                    clipContainer.setClip(clip);
                }
                clip.setWidth(newVal.getWidth());
                clip.setHeight(newVal.getHeight());
            });

            HBox wrapper = new HBox();
            wrapper.setAlignment(Pos.CENTER);
            wrapper.getStyleClass().add(CssClasses.SLIDER_WRAPPER);
            wrapper.getChildren().add(clipContainer);

            // 自定义刻度标签行（接管 Slider 默认刻度标签的渲染）
            if (marks != null && !marks.isEmpty()) {
                contentBox.getChildren().add(createMarksRow());
            }

            // 提示文字 Label（始终显示或带 formatter 时）
            if (tipFormatter != null || tooltipVisible) {
                Label tipLabel = new Label();
                tipLabel.getStyleClass().add(CssClasses.SLIDER_TIP);
                tipLabel.textProperty().bind(
                        Bindings.createStringBinding(
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
                contentBox.getChildren().add(tipLabel);
            }

            // 用户 style/styleClass 在所有内置类后应用，便于覆盖
            applyStyles(wrapper);
            return wrapper;
        }

        private Node buildRangeSlider() {
            HBox rangeBox = new HBox(8);
            rangeBox.setAlignment(Pos.CENTER_LEFT);
            rangeBox.getStyleClass().add(CssClasses.SLIDER_RANGE);

            double startVal = rangeValue != null && rangeValue.length >= 2 ? rangeValue[0] : min;
            double endVal = rangeValue != null && rangeValue.length >= 2 ? rangeValue[1] : max;

            Slider startSlider = new Slider(min, max, startVal);
            Slider endSlider = new Slider(min, max, endVal);

            // 两个 slider 共用同一套约束与 styleClass
            for (Slider slider : new Slider[]{startSlider, endSlider}) {
                slider.setBlockIncrement(step);
                slider.setShowTickMarks(false);
                slider.setShowTickLabels(false);
                slider.setDisable(disabled);
                slider.getStyleClass().add(CssClasses.SLIDER);
                if (disabled) {
                    slider.getStyleClass().add(CssClasses.SLIDER_DISABLED);
                }
                slider.setPrefWidth(Region.USE_COMPUTED_SIZE);
                slider.setMinWidth(100);
                slider.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(slider, Priority.ALWAYS);
            }

            Label startLabel = new Label(String.valueOf((int) startVal));
            Label endLabel = new Label(String.valueOf((int) endVal));
            Label separator = new Label("~");

            startLabel.getStyleClass().add(CssClasses.SLIDER_RANGE_LABEL);
            endLabel.getStyleClass().add(CssClasses.SLIDER_RANGE_LABEL);
            separator.getStyleClass().add(CssClasses.SLIDER_RANGE_SEPARATOR);

            // 双滑块联动：start 不能超过 end，end 不能小于 start
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

            applyStyles(rangeBox);
            return rangeBox;
        }

        /** 创建刻度标签行：每个 mark 对应一个 Label，水平等分 */
        private Node createMarksRow() {
            HBox marksRow = new HBox(0);
            marksRow.setAlignment(Pos.CENTER);
            marksRow.getStyleClass().add(CssClasses.SLIDER_MARKS);

            for (Map.Entry<Double, String> entry : marks.entrySet()) {
                Label markLabel = new Label(entry.getValue());
                markLabel.getStyleClass().add(CssClasses.SLIDER_MARK_LABEL);
                marksRow.getChildren().add(markLabel);
                HBox.setHgrow(markLabel, Priority.ALWAYS);
            }

            return marksRow;
        }
    }
}
