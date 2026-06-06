package org.openkawu.jfxium.component.composite;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.function.Consumer;

/**
 * JFXium 评分组件 - 对标 Ant Design Rate（组合式，Builder 模式）。
 *
 * <p><b>定位</b>：星级评分控件，用多个图标（默认星星）表示 0-N 分，
 * 支持点击、悬浮预览、半星。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>尺寸</b>：SMALL(16px) / DEFAULT(24px) / LARGE(32px)</li>
 *   <li><b>分数</b>：可设最大值（默认 5）、当前值、允许半星（allowHalf）</li>
 *   <li><b>只读</b>：disabled 模式（展示评分不可修改）</li>
 *   <li><b>自定义图标</b>：可替换为心形、拇指等其他图标</li>
 * </ul>
 *
 * <h2>典型场景</h2>
 * <ul>
 *   <li>商品评分展示 / 评价</li>
 *   <li>用户满意度打分</li>
 *   <li>评分筛选（列表页筛选器）</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * Node rate = RateAnt.create()
 *     .value(3.5)
 *     .count(5)
 *     .allowHalf(true)
 *     .size(RateAnt.Size.LARGE)
 *     .onChange(val -> System.out.println("评分：" + val))
 *     .build();
 * }</pre>
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

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private int count = 5;
        private double value = 0;
        private double defaultValue = 0;
        private boolean allowHalf = false;
        private boolean disabled = false;
        private Size size = Size.DEFAULT;
        private String activeColor = "-color-warning-emphasis";
        private String inactiveColor = "-color-border-default";

        /** 判断颜色是否为 CSS 变量（以 "-" 开头） */
        private static boolean isCssVar(String color) {
            return color != null && color.startsWith("-");
        }

        private Consumer<Double> onChange = null;
        private Consumer<Double> onHoverChange = null;
        private DoubleProperty bindProperty = null;

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

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(DoubleProperty property) {
            this.bindProperty = property;
            return this;
        }

        public HBox build() {
            HBox rateBox = new HBox(4);
            rateBox.setAlignment(Pos.CENTER_LEFT);
            rateBox.getStyleClass().add(JfxStyles.RATE);

            if (disabled) {
                rateBox.setOpacity(0.6);
            }

            // 双向绑定初始同步
            if (bindProperty != null && !Double.isNaN(bindProperty.get())) {
                value = bindProperty.get();
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
                        if (bindProperty != null) {
                            bindProperty.set(value);
                        }
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
            star.getStyleClass().add(JfxStyles.RATE_STAR);
            return star;
        }

        private void updateStarColor(SVGPath star, int starIndex, double currentValue,
                                      String inactiveColor, String activeColor) {
            boolean isActive = starIndex <= currentValue
                    || (allowHalf && starIndex - 0.5 <= currentValue);

            if (isCssVar(activeColor) && isCssVar(inactiveColor)) {
                // 主题色：通过 styleClass 切换，避免 setStyle 无法解析 CSS 变量
                star.getStyleClass().removeAll(JfxStyles.RATE_ACTIVE, JfxStyles.RATE_INACTIVE);
                star.getStyleClass().add(isActive ? JfxStyles.RATE_ACTIVE : JfxStyles.RATE_INACTIVE);
                star.setStyle("-fx-cursor: hand;");
            } else {
                // 用户自定义颜色（hex）：直接使用 setStyle
                String fill = isActive ? activeColor : inactiveColor;
                star.setStyle("-fx-fill: " + fill + "; -fx-cursor: hand;");
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
