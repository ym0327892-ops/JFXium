package org.openkawu.jfxium.component;

import javafx.scene.control.DatePicker;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.time.LocalDate;

/**
 * JFXium DatePicker Component
 * Inspired by Ant Design DatePicker
 *
 * Usage:
 * <pre>{@code
 * DatePicker datePicker = DatePickerAnt.create()
 *     .placeholder("Select date")
 *     .value(LocalDate.now())
 *     .size(DatePickerAnt.Size.SMALL)       // M19.5：与 InputAnt/ButtonAnt 一致
 *     .onChange(date -> System.out.println("Selected: " + date))
 *     .build();
 * }</pre>
 */
public class DatePickerAnt {

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
        private String placeholder = "";
        private LocalDate value = null;
        private boolean editable = true;
        private boolean showWeekNumbers = false;
        private Size size = Size.DEFAULT;
        private java.util.function.Consumer<LocalDate> onChange;

        private Builder() {}

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder value(LocalDate value) {
            this.value = value;
            return this;
        }

        public Builder editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder showWeekNumbers(boolean show) {
            this.showWeekNumbers = show;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder onChange(java.util.function.Consumer<LocalDate> handler) {
            this.onChange = handler;
            return this;
        }

        public DatePicker build() {
            DatePicker datePicker = new DatePicker();

            if (!placeholder.isEmpty()) {
                datePicker.setPromptText(placeholder);
            }

            if (value != null) {
                datePicker.setValue(value);
            }

            datePicker.setEditable(editable);
            datePicker.setShowWeekNumbers(showWeekNumbers);

            if (onChange != null) {
                datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            // Size styleClass
            if (size == Size.SMALL) {
                datePicker.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                datePicker.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            datePicker.getStyleClass().add("jfx-date-picker");
            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(datePicker);
            return datePicker;
        }
    }
}
