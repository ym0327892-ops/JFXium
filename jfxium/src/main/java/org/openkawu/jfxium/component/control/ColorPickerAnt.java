package org.openkawu.jfxium.component.control;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Consumer;

/**
 * JFXium ColorPicker Component
 * 封装 JavaFX ColorPicker
 *
 * Usage:
 * <pre>{@code
 * ColorPicker colorPicker = ColorPickerAnt.create()
 *     .value(Color.BLUE)
 *     .size(ColorPickerAnt.Size.SMALL)      // M19.5：与 InputAnt/ButtonAnt 一致
 *     .onChange(color -> System.out.println("Color: " + color))
 *     .build();
 * }</pre>
 */
public class ColorPickerAnt {

    /** 尺寸枚举，与 InputAnt/ButtonAnt 完全一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Color value = Color.WHITE;
        private boolean disabled = false;
        private Size size = Size.DEFAULT;
        private Consumer<Color> onChange;
        private ObjectProperty<Color> bindProperty = null;

        private Builder() {}

        public Builder value(Color value) {
            this.value = value;
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

        public Builder onChange(Consumer<Color> handler) {
            this.onChange = handler;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder bindValue(ObjectProperty<Color> property) {
            this.bindProperty = property;
            return this;
        }

        public ColorPicker build() {
            ColorPicker colorPicker = new ColorPicker(value);
            colorPicker.setDisable(disabled);

            // 双向绑定（在初始值设置之后）
            if (bindProperty != null) {
                colorPicker.valueProperty().bindBidirectional(bindProperty);
            }

            // Size styleClass
            if (size == Size.SMALL) {
                colorPicker.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                colorPicker.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            colorPicker.getStyleClass().add("jfx-color-picker");

            if (onChange != null) {
                colorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(colorPicker);
            return colorPicker;
        }
    }
}
