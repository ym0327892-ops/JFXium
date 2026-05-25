package org.openkawu.jfxium.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

import java.util.function.Consumer;

/**
 * JFXium ListView Component
 * Inspired by Ant Design List
 *
 * Usage:
 * <pre>{@code
 * ListView<String> listView = ListViewAnt.<String>create()
 *     .items("Item 1", "Item 2", "Item 3")
 *     .onSelect(item -> System.out.println("Selected: " + item))
 *     .build();
 * }</pre>
 */
public class ListViewAnt<T> {

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    public static class Builder<T> extends AbstractStyleBuilder<Builder<T>> {
        private ObservableList<T> items = FXCollections.observableArrayList();
        private boolean editable = false;
        private boolean disabled = false;
        private Consumer<T> onSelect;

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

        public Builder<T> editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder<T> disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder<T> onSelect(Consumer<T> handler) {
            this.onSelect = handler;
            return this;
        }

        public ListView<T> build() {
            ListView<T> listView = new ListView<>(items);
            listView.setEditable(editable);
            listView.setDisable(disabled);

            if (onSelect != null) {
                listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        onSelect.accept(newVal);
                    }
                });
            }

            listView.getStyleClass().add("jfx-list-view");
            applyStyles(listView);
            return listView;
        }
    }
}
