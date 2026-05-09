package org.openkawu.jfxium.component;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JFXium DatePicker Component
 * Inspired by Ant Design DatePicker
 *
 * Usage:
 * <pre>{@code
 * DatePicker datePicker = JFXDatePicker.create()
 *     .placeholder("Select date")
 *     .value(LocalDate.now())
 *     .onChange(date -> System.out.println("Selected: " + date))
 *     .build();
 * }</pre>
 */
public class JFXDatePicker {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String placeholder = "";
        private LocalDate value = null;
        private boolean editable = true;
        private boolean showWeekNumbers = false;
        private java.util.function.Consumer<LocalDate> onChange;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

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

        public Builder onChange(java.util.function.Consumer<LocalDate> handler) {
            this.onChange = handler;
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

            datePicker.getStyleClass().add("jfx-date-picker");
            datePicker.getStyleClass().addAll(extraStyleClasses);

            if (!style.isEmpty()) {
                datePicker.setStyle(style);
            }

            return datePicker;
        }
    }
}