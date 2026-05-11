package org.openkawu.jfxium.component;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * AutoCompleteAnt Component
 * Inspired by Ant Design AutoComplete
 * Used for autocomplete input with dropdown suggestions.
 */
public class AutoCompleteAnt {

    public static class Builder<T> {
        private String placeholder = "";
        private String value = "";
        private List<T> options = new ArrayList<>();
        private Function<T, String> optionToString = Object::toString;
        private Function<String, List<T>> filter = null;
        private boolean disabled = false;
        private Consumer<String> onChange = null;
        private Consumer<T> onSelect = null;
        private int maxSuggestions = 10;

        public Builder<T> placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder<T> value(String value) {
            this.value = value;
            return this;
        }

        public Builder<T> options(List<T> options) {
            this.options = options;
            return this;
        }

        public Builder<T> optionToString(Function<T, String> converter) {
            this.optionToString = converter;
            return this;
        }

        public Builder<T> filter(Function<String, List<T>> filter) {
            this.filter = filter;
            return this;
        }

        public Builder<T> disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder<T> disabled() {
            return disabled(true);
        }

        public Builder<T> onChange(Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder<T> onSelect(Consumer<T> onSelect) {
            this.onSelect = onSelect;
            return this;
        }

        public Builder<T> maxSuggestions(int max) {
            this.maxSuggestions = max;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("auto-complete");

            TextField field = new TextField(value);
            field.setPromptText(placeholder);
            field.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: -color-fg-default;" +
                "-fx-padding: 8px 12px;"
            );
            HBox.setHgrow(field, Priority.ALWAYS);

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            VBox suggestionsBox = new VBox(0);
            suggestionsBox.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 8px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);"
            );
            suggestionsBox.setPadding(new Insets(4, 0, 4, 0));
            suggestionsBox.setPrefWidth(200);

            popup.getContent().add(suggestionsBox);

            field.textProperty().addListener((obs, oldVal, newVal) -> {
                if (onChange != null) {
                    onChange.accept(newVal);
                }

                List<T> filtered = filter != null
                    ? filter.apply(newVal)
                    : filterOptions(newVal);

                updateSuggestions(filtered, suggestionsBox, popup, field);
            });

            field.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {
                    // Navigation handled by popup
                } else if (e.getCode() == KeyCode.ESCAPE) {
                    popup.hide();
                }
            });

            field.setOnMouseClicked(e -> {
                if (!field.getText().isEmpty()) {
                    List<T> filtered = filter != null
                        ? filter.apply(field.getText())
                        : filterOptions(field.getText());
                    updateSuggestions(filtered, suggestionsBox, popup, field);
                }
            });

            container.getChildren().add(field);

            if (disabled) {
                field.setDisable(true);
                container.setStyle("-fx-opacity: 0.6;");
            }

            return container;
        }

        private List<T> filterOptions(String query) {
            if (query == null || query.isEmpty()) {
                return options.subList(0, Math.min(maxSuggestions, options.size()));
            }
            List<T> filtered = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            for (T option : options) {
                if (optionToString.apply(option).toLowerCase().contains(lowerQuery)) {
                    filtered.add(option);
                    if (filtered.size() >= maxSuggestions) break;
                }
            }
            return filtered;
        }

        private void updateSuggestions(List<T> filtered, VBox suggestionsBox, Popup popup, TextField field) {
            suggestionsBox.getChildren().clear();

            if (filtered.isEmpty()) {
                popup.hide();
                return;
            }

            for (T option : filtered) {
                String text = optionToString.apply(option);
                Label label = new Label(text);
                label.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: -color-fg-default;" +
                    "-fx-padding: 8px 12px;" +
                    "-fx-cursor: hand;"
                );
                label.setMaxWidth(Double.MAX_VALUE);

                label.setOnMouseEntered(e -> {
                    label.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default; -fx-padding: 8px 12px; -fx-cursor: hand; -fx-background-color: -color-bg-subtle;");
                });

                label.setOnMouseExited(e -> {
                    label.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default; -fx-padding: 8px 12px; -fx-cursor: hand; -fx-background-color: transparent;");
                });

                label.setOnMouseClicked(e -> {
                    field.setText(text);
                    popup.hide();
                    if (onSelect != null) {
                        onSelect.accept(option);
                    }
                    if (onChange != null) {
                        onChange.accept(text);
                    }
                });

                suggestionsBox.getChildren().add(label);
            }

            if (!popup.isShowing()) {
                Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
            }
        }
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }
}
