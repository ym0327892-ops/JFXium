package org.openkawu.jfxium.component.composite;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JFXium Slider 组件 - 全面对标 Ant Design Slider。
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
        private Map<Double, String> marks = null;
        private Function<Double, String> tipFormatter = null;
        private boolean tooltipVisible = false;
        private Consumer<Double> onChange = null;
        private Consumer<Double> onChangeComplete = null;
        private DoubleProperty bindProperty = null;

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

        /** 双向绑定：控件值 ↔ Property 值实时同步。仅单滑块模式生效。 */
        public Builder bindValue(DoubleProperty property) {
            this.bindProperty = property;
            return this;
        }

        public Node build() {
            normalizeRange();
            step = Double.isFinite(step) && step > 0 ? step : 1;
            return range ? buildRangeSlider() : buildSingleSlider();
        }

        private Node buildSingleSlider() {
            value = clamp(value);
            Slider slider = new Slider(min, max, value);
            slider.setBlockIncrement(step);
            // 双向绑定（仅单滑块模式）
            if (bindProperty != null) {
                slider.valueProperty().bindBidirectional(bindProperty);
            }
            slider.setShowTickMarks(marks != null || dots);
            slider.setShowTickLabels(marks != null);
            slider.setOrientation(vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            applyReverse(slider);
            slider.setDisable(disabled);
            slider.getStyleClass().add(JfxStyles.SLIDER);
            // disabled 通过 styleClass 切换 opacity，不再 inline
            if (disabled) {
                slider.getStyleClass().add(JfxStyles.SLIDER_DISABLED);
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

            // 宽度约束：水平方向默认 240px、可在能拉伸的父容器里拉伸；垂直方向固定高度 200
            // 注：prefWidth 给定值（非 USE_COMPUTED_SIZE）——否则在不约束宽度的父容器（如居左 HBox）里
            //     maxWidth=MAX 会让 slider 无限膨胀撑破容器（M19.42 修 #6/#8）
            if (!vertical) {
                slider.setPrefWidth(240);
                slider.setMinWidth(120);
                slider.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(slider, Priority.ALWAYS);
            } else {
                slider.setPrefHeight(200);
            }

            VBox contentBox = new VBox();
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
            wrapper.getStyleClass().add(JfxStyles.SLIDER_WRAPPER);
            wrapper.getChildren().add(clipContainer);

            // 自定义刻度标签行（接管 Slider 默认刻度标签的渲染）
            if (marks != null && !marks.isEmpty()) {
                contentBox.getChildren().add(createMarksRow());
            }

            // 提示文字 Label（始终显示或带 formatter 时）
            if (tipFormatter != null || tooltipVisible) {
                Label tipLabel = new Label();
                tipLabel.getStyleClass().add(JfxStyles.SLIDER_TIP);
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
            HBox rangeBox = new HBox();
            rangeBox.setAlignment(Pos.CENTER_LEFT);
            rangeBox.getStyleClass().add(JfxStyles.SLIDER_RANGE);
            // rangeBox 填充父容器宽度（maxWidth=MAX）但内部 slider 各自限宽 160，
            //   所以整组内容靠左、宽卡片右侧留白，窄卡片里 HBox 自动收缩 slider，不溢出。
            rangeBox.setMaxWidth(Double.MAX_VALUE);

            double startVal = rangeValue != null && rangeValue.length >= 2 ? rangeValue[0] : min;
            double endVal = rangeValue != null && rangeValue.length >= 2 ? rangeValue[1] : max;
            startVal = clamp(startVal);
            endVal = clamp(endVal);
            if (startVal > endVal) {
                double tmp = startVal;
                startVal = endVal;
                endVal = tmp;
            }

            Slider startSlider = new Slider(min, max, startVal);
            Slider endSlider = new Slider(min, max, endVal);

            // 两个 slider 共用同一套约束与 styleClass
            for (Slider slider : new Slider[]{startSlider, endSlider}) {
                slider.setBlockIncrement(step);
                slider.setShowTickMarks(false);
                slider.setShowTickLabels(false);
                applyReverse(slider);
                slider.setDisable(disabled);
                slider.getStyleClass().add(JfxStyles.SLIDER);
                if (disabled) {
                    slider.getStyleClass().add(JfxStyles.SLIDER_DISABLED);
                }
                // M19.43.1 #6 二次修复：maxWidth 钉在 pref(160) 而非 MAX。
                //   上一版 maxWidth=MAX + Hgrow 让两个 slider 在宽卡片里无限拉伸、铺满整行
                //   （截图：轨道顶到卡片左右边、"20" 标签被挤到角落）。
                //   改成 maxWidth=160：宽卡片里每个 slider 最多 160，整组 ~520px 居左、留白正常；
                //   窄卡片里 HBox 仍会把 slider 收缩到 minWidth(60)（收缩不需要 Hgrow），不溢出。
                slider.setPrefWidth(160);
                slider.setMinWidth(60);
                slider.setMaxWidth(160);
            }

            Label startLabel = new Label(String.valueOf((int) startVal));
            Label endLabel = new Label(String.valueOf((int) endVal));
            Label separator = new Label("~");

            startLabel.getStyleClass().add(JfxStyles.SLIDER_RANGE_LABEL);
            endLabel.getStyleClass().add(JfxStyles.SLIDER_RANGE_LABEL);
            separator.getStyleClass().add(JfxStyles.SLIDER_RANGE_SEPARATOR);

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

            if (onChangeComplete != null) {
                startSlider.setOnMouseReleased(e -> onChangeComplete.accept(startSlider.getValue()));
                endSlider.setOnMouseReleased(e -> onChangeComplete.accept(endSlider.getValue()));
            }

            rangeBox.getChildren().addAll(startLabel, startSlider, separator, endSlider, endLabel);

            applyStyles(rangeBox);
            return rangeBox;
        }

        private void normalizeRange() {
            if (!Double.isFinite(min)) {
                min = 0;
            }
            if (!Double.isFinite(max)) {
                max = 100;
            }
            if (max < min) {
                double oldMin = min;
                min = max;
                max = oldMin;
            }
            if (!Double.isFinite(value)) {
                value = min;
            }
            if (rangeValue != null && rangeValue.length >= 2) {
                if (!Double.isFinite(rangeValue[0])) {
                    rangeValue[0] = min;
                }
                if (!Double.isFinite(rangeValue[1])) {
                    rangeValue[1] = max;
                }
            }
        }

        private double clamp(double rawValue) {
            double candidate = Double.isFinite(rawValue) ? rawValue : min;
            return Math.max(min, Math.min(max, candidate));
        }

        private void applyReverse(Slider slider) {
            if (!reverse) {
                return;
            }
            if (vertical) {
                slider.setScaleY(-1);
            } else {
                slider.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }
        }

        /** 创建刻度标签行：每个 mark 对应一个 Label，水平等分 */
        private Node createMarksRow() {
            HBox marksRow = new HBox(0);
            marksRow.setAlignment(Pos.CENTER);
            marksRow.getStyleClass().add(JfxStyles.SLIDER_MARKS);

            for (Map.Entry<Double, String> entry : marks.entrySet()) {
                Label markLabel = new Label(entry.getValue());
                markLabel.getStyleClass().add(JfxStyles.SLIDER_MARK_LABEL);
                marksRow.getChildren().add(markLabel);
                HBox.setHgrow(markLabel, Priority.ALWAYS);
            }

            return marksRow;
        }
    }
}
