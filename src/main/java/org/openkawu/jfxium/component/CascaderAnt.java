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
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * CascaderAnt Component
 * Inspired by Ant Design Cascader
 * Used for selecting hierarchical data with cascading dropdowns.
 */
public class CascaderAnt {

    public static class Option {
        private final String value;
        private final String label;
        private final List<Option> children;
        private final boolean disabled;

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
            this.children = new ArrayList<>();
            this.disabled = false;
        }

        public Option(String value, String label, List<Option> children) {
            this.value = value;
            this.label = label;
            this.children = children != null ? children : new ArrayList<>();
            this.disabled = false;
        }

        public Option(String value, String label, List<Option> children, boolean disabled) {
            this.value = value;
            this.label = label;
            this.children = children != null ? children : new ArrayList<>();
            this.disabled = disabled;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public List<Option> getChildren() { return children; }
        public boolean isDisabled() { return disabled; }

        public boolean hasChildren() {
            return children != null && !children.isEmpty();
        }
    }

    public static class Builder {
        private List<Option> options = new ArrayList<>();
        private String placeholder = "Please select";
        private boolean disabled = false;
        private boolean allowClear = true;
        private boolean showSearch = false;
        private Consumer<List<String>> onChange = null;
        private List<String> selectedPath = new ArrayList<>();

        public Builder options(List<Option> options) {
            this.options = options;
            return this;
        }

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder disabled() {
            return disabled(true);
        }

        public Builder allowClear(boolean allowClear) {
            this.allowClear = allowClear;
            return this;
        }

        public Builder showSearch(boolean showSearch) {
            this.showSearch = showSearch;
            return this;
        }

        public Builder onChange(Consumer<List<String>> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder value(List<String> path) {
            this.selectedPath = path != null ? path : new ArrayList<>();
            return this;
        }

        public HBox build() {
            HBox container = new HBox(0);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("cascader");

            TextField field = new TextField();
            field.setPromptText(placeholder);
            field.setEditable(showSearch);
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

            // Display selected path
            if (!selectedPath.isEmpty()) {
                field.setText(String.join(" / ", selectedPath));
            }

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            HBox cascaderPanel = new HBox(0);
            cascaderPanel.setStyle(
                "-fx-background-color: -color-bg-default;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-radius: 8px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);"
            );

            popup.getContent().add(cascaderPanel);

            // Build cascader columns
            buildColumns(cascaderPanel, options, 0, field, popup);

            field.setOnMouseClicked(e -> {
                if (!disabled) {
                    if (popup.isShowing()) {
                        popup.hide();
                    } else {
                        Bounds bounds = field.localToScreen(field.getBoundsInLocal());
                        popup.show(field, bounds.getMinX(), bounds.getMaxY() + 4);
                    }
                }
            });

            field.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    popup.hide();
                }
            });

            container.getChildren().add(field);

            if (disabled) {
                field.setDisable(true);
                container.setStyle("-fx-opacity: 0.6;");
            }

            return container;
        }

        private void buildColumns(HBox panel, List<Option> currentOptions, int depth, TextField field, Popup popup) {
            panel.getChildren().clear();

            if (currentOptions == null || currentOptions.isEmpty()) {
                return;
            }

            VBox column = new VBox(0);
            column.setStyle("-fx-min-width: 160px; -fx-padding: 4px 0;");
            column.setPrefHeight(200);

            for (Option option : currentOptions) {
                HBox item = new HBox(8);
                item.setAlignment(Pos.CENTER_LEFT);
                item.setPadding(new Insets(8, 12, 8, 12));
                item.setStyle(
                    "-fx-cursor: " + (option.isDisabled() ? "default" : "hand") + ";" +
                    "-fx-background-color: transparent;"
                );

                Label label = new Label(option.getLabel());
                label.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: " + (option.isDisabled() ? "-color-fg-subtle" : "-color-fg-default") + ";"
                );
                item.getChildren().add(label);

                // Arrow for items with children
                if (option.hasChildren() && !option.isDisabled()) {
                    SVGPath arrow = new SVGPath();
                    arrow.setContent("M6 4L10 8L6 12");
                    arrow.setStyle("-fx-stroke: -color-fg-muted; -fx-stroke-width: 1.5; -fx-fill: none;");
                    HBox spacer = new HBox();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    item.getChildren().addAll(spacer, arrow);
                }

                if (!option.isDisabled()) {
                    item.setOnMouseEntered(e -> {
                        item.setStyle("-fx-cursor: hand; -fx-background-color: -color-bg-subtle;");
                    });

                    item.setOnMouseExited(e -> {
                        item.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");
                    });

                    item.setOnMouseClicked(e -> {
                        if (option.hasChildren()) {
                            // Show next level
                            List<String> newPath = new ArrayList<>(selectedPath);
                            if (depth < newPath.size()) {
                                newPath = newPath.subList(0, depth);
                            }
                            newPath.add(option.getLabel());
                            selectedPath = newPath;

                            // Rebuild columns from root
                            buildColumns(panel, options, 0, field, popup);

                            // Expand to show selected path
                            expandPath(panel, selectedPath, 0);
                        } else {
                            // Leaf node selected
                            List<String> newPath = new ArrayList<>();
                            for (int i = 0; i < depth && i < selectedPath.size(); i++) {
                                newPath.add(selectedPath.get(i));
                            }
                            newPath.add(option.getLabel());
                            selectedPath = newPath;

                            field.setText(String.join(" / ", selectedPath));
                            popup.hide();

                            if (onChange != null) {
                                List<String> values = new ArrayList<>();
                                collectValues(options, selectedPath, 0, values);
                                onChange.accept(values);
                            }
                        }
                    });
                }

                column.getChildren().add(item);
            }

            panel.getChildren().add(column);

            // Add divider between columns
            if (!selectedPath.isEmpty() && depth < selectedPath.size()) {
                javafx.scene.layout.Region divider = new javafx.scene.layout.Region();
                divider.setStyle("-fx-background-color: -color-border-default; -fx-min-width: 1px; -fx-pref-width: 1px;");
                panel.getChildren().add(divider);

                // Find selected option and show its children
                String selectedLabel = selectedPath.get(depth);
                for (Option option : currentOptions) {
                    if (option.getLabel().equals(selectedLabel) && option.hasChildren()) {
                        buildColumns(panel, option.getChildren(), depth + 1, field, popup);
                        break;
                    }
                }
            }
        }

        private void expandPath(HBox panel, List<String> path, int depth) {
            // This is called after rebuild to ensure the path is expanded
            // The buildColumns method already handles this by checking selectedPath
        }

        private boolean collectValues(List<Option> options, List<String> path, int depth, List<String> values) {
            if (depth >= path.size()) {
                return true;
            }

            String targetLabel = path.get(depth);
            for (Option option : options) {
                if (option.getLabel().equals(targetLabel)) {
                    values.add(option.getValue());
                    if (option.hasChildren() && depth + 1 < path.size()) {
                        return collectValues(option.getChildren(), path, depth + 1, values);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
