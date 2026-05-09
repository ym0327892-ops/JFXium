package org.openkawu.jfxium.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.function.Consumer;

/**
 * JFXium ComboBox Component
 * Inspired by Ant Design Select
 *
 * Usage:
 * <pre>{@code
 * ComboBox<String> comboBox = JFXComboBox.<String>create()
 *     .items("Option 1", "Option 2", "Option 3")
 *     .placeholder("Select an option")
 *     .onChange(value -> System.out.println("Selected: " + value))
 *     .build();
 * }</pre>
 */
public class JFXComboBox<T> {

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private ObservableList<T> items = FXCollections.observableArrayList();
        private T value = null;
        private String placeholder = "";
        private boolean editable = false;
        private boolean disabled = false;
        private Consumer<T> onChange;
        private String style = "";

        private Builder() {}

        @SafeVarargs
        public final Builder<T> items(T... items) {
            this.items = FXCollections.observableArrayList(items);
            return this;
        }

        public Builder<T> items(ObservableList<T> items) {
            this.items = items;
            return this;
        }

        public Builder<T> value(T value) {
            this.value = value;
            return this;
        }

        public Builder<T> placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder<T> editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder<T> disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder<T> onChange(Consumer<T> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder<T> style(String style) {
            this.style = style;
            return this;
        }

        public ComboBox<T> build() {
            ComboBox<T> comboBox = new ComboBox<>(items);
            comboBox.setEditable(editable);
            comboBox.setDisable(disabled);

            if (value != null) {
                comboBox.setValue(value);
            }

            if (!placeholder.isEmpty()) {
                comboBox.setPromptText(placeholder);
            }

            if (onChange != null) {
                comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            comboBox.getStyleClass().add("jfx-combo-box");

            if (!style.isEmpty()) {
                comboBox.setStyle(style);
            }

            return comboBox;
        }
    }
}