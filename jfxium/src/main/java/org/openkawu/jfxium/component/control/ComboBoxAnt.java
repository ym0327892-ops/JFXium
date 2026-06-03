package org.openkawu.jfxium.component.control;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Consumer;

/**
 * JFXium ComboBox Component
 * Inspired by Ant Design Select
 *
 * Usage:
 * <pre>{@code
 * ComboBox<String> comboBox = ComboBoxAnt.<String>create()
 *     .items("Option 1", "Option 2", "Option 3")
 *     .placeholder("Select an option")
 *     .size(ComboBoxAnt.Size.SMALL)         // M19.5：与 InputAnt/ButtonAnt 一致
 *     .onChange(value -> System.out.println("Selected: " + value))
 *     .build();
 * }</pre>
 */
public class ComboBoxAnt<T> {

    /** 尺寸枚举，与 InputAnt/ButtonAnt 完全一致（DEFAULT/SMALL/LARGE）。 */
    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        private ObservableList<T> items = FXCollections.observableArrayList();
        private T value = null;
        private String placeholder = "";
        private boolean editable = false;
        private boolean disabled = false;
        private Size size = Size.DEFAULT;
        private Consumer<T> onChange;
        private Property<T> bindProperty = null;

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

        public Builder<T> size(Size size) {
            this.size = size;
            return this;
        }

        public Builder<T> onChange(Consumer<T> handler) {
            this.onChange = handler;
            return this;
        }

        /** 双向绑定：控件值 ↔ Property 值实时同步。 */
        public Builder<T> bindValue(Property<T> property) {
            this.bindProperty = property;
            return this;
        }

        public ComboBox<T> build() {
            ComboBox<T> comboBox = new ComboBox<>(items);
            comboBox.setEditable(editable);
            comboBox.setDisable(disabled);

            if (value != null) {
                comboBox.setValue(value);
            }

            // 双向绑定（在初始值设置之后）
            if (bindProperty != null) {
                comboBox.valueProperty().bindBidirectional(bindProperty);
            }

            if (!placeholder.isEmpty()) {
                comboBox.setPromptText(placeholder);
            }

            if (onChange != null) {
                comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            // Size styleClass（复用 SIZE_SMALL/LARGE 通用常量，与 InputAnt/ButtonAnt 一致）
            if (size == Size.SMALL) {
                comboBox.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                comboBox.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            comboBox.getStyleClass().add("jfx-combo-box");
            applyStyles(comboBox);
            return comboBox;
        }
    }
}
