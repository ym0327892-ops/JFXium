package org.openkawu.jfxium.component.control;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Consumer;

/**
 * JFXium RadioButton Component
 * Inspired by Ant Design Radio
 *
 * Usage:
 * <pre>{@code
 * ToggleGroup group = new ToggleGroup();
 *
 * RadioButton option1 = RadioButtonAnt.create("Option 1")
 *     .toggleGroup(group)
 *     .selected(true)
 *     .size(RadioButtonAnt.Size.SMALL)        // M19.6：与 InputAnt/ButtonAnt 一致
 *     .build();
 *
 * RadioButton option2 = RadioButtonAnt.create("Option 2")
 *     .toggleGroup(group)
 *     .build();
 * }</pre>
 */
public class RadioButtonAnt {

    /** 尺寸枚举，与 ButtonAnt/InputAnt 完全一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    /**
     * 形状枚举（M19.20）。
     * <ul>
     *   <li>{@link #DEFAULT}：圆形（Ant Design 默认）</li>
     *   <li>{@link #SQUARE}：方形（与 CheckBox 同款外形，但仍是 RadioButton 的单选语义）</li>
     *   <li>{@link #ROUNDED}：圆角方形</li>
     * </ul>
     */
    public enum Shape {
        DEFAULT,
        SQUARE,
        ROUNDED
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final String text;
        private boolean selected = false;
        private boolean disabled = false;
        private Size size = Size.DEFAULT;
        private Shape shape = Shape.DEFAULT;
        private ToggleGroup toggleGroup;
        private Consumer<Boolean> onChange;
        private BooleanProperty bindProperty = null;

        private Builder(String text) {
            this.text = text;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        /**
         * 设置形状（M19.20）。默认 {@link Shape#DEFAULT}（圆形，对齐 Ant Design）。
         */
        public Builder shape(Shape shape) {
            this.shape = shape != null ? shape : Shape.DEFAULT;
            return this;
        }

        public Builder toggleGroup(ToggleGroup toggleGroup) {
            this.toggleGroup = toggleGroup;
            return this;
        }

        public Builder onChange(Consumer<Boolean> handler) {
            this.onChange = handler;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(BooleanProperty property) {
            this.bindProperty = property;
            return this;
        }

        public RadioButton build() {
            RadioButton radioButton = new RadioButton(text);
            radioButton.setSelected(selected);
            radioButton.setDisable(disabled);

            // 双向绑定（在初始值设置之后）
            if (bindProperty != null) {
                radioButton.selectedProperty().bindBidirectional(bindProperty);
            }

            if (toggleGroup != null) {
                radioButton.setToggleGroup(toggleGroup);
            }

            if (onChange != null) {
                radioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            // Size styleClass（复用通用常量，与 ButtonAnt/InputAnt 一致）
            if (size == Size.SMALL) {
                radioButton.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                radioButton.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Shape styleClass（M19.20）
            switch (shape) {
                case SQUARE  -> radioButton.getStyleClass().add("shape-square");
                case ROUNDED -> radioButton.getStyleClass().add("shape-rounded");
                default      -> { /* DEFAULT 不挂额外类 */ }
            }

            radioButton.getStyleClass().add("jfx-radio-button");
            applyStyles(radioButton);
            return radioButton;
        }
    }
}
