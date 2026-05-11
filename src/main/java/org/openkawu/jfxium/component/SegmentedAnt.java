package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Segmented Component
 * Inspired by Ant Design Segmented
 * Used to display a group of options in a segmented control.
 */
public class SegmentedAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public static class Option {
        private final String value;
        private final String label;
        private final javafx.scene.Node icon;

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
            this.icon = null;
        }

        public Option(String value, String label, javafx.scene.Node icon) {
            this.value = value;
            this.label = label;
            this.icon = icon;
        }

        public String getValue() { return value; }
        public String getLabel() { return label; }
        public javafx.scene.Node getIcon() { return icon; }
    }

    public static class Builder {
        private List<Option> options = new ArrayList<>();
        private String selectedValue = null;
        private Size size = Size.DEFAULT;
        private boolean disabled = false;
        private boolean block = false;
        private Consumer<String> onChange = null;

        public Builder option(String value, String label) {
            this.options.add(new Option(value, label));
            return this;
        }

        public Builder option(String value, String label, javafx.scene.Node icon) {
            this.options.add(new Option(value, label, icon));
            return this;
        }

        public Builder options(List<Option> options) {
            this.options = options;
            return this;
        }

        public Builder selected(String value) {
            this.selectedValue = value;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder disabled() {
            return disabled(true);
        }

        public Builder block(boolean block) {
            this.block = block;
            return this;

        }

        public Builder block() {
            return block(true);
        }

        public Builder onChange(Consumer<String> onChange) {
            this.onChange = onChange;
            return this;
        }

        public HBox build() {
            HBox segmented = new HBox(2);
            segmented.setAlignment(Pos.CENTER);
            segmented.getStyleClass().add("segmented");
            segmented.setStyle(
                "-fx-background-color: -color-bg-subtle;" +
                "-fx-padding: 2px;" +
                "-fx-background-radius: 6px;"
            );

            if (block) {
                segmented.setStyle(segmented.getStyle() + "-fx-pref-width: 100%;");
            }

            double padding = size == Size.SMALL ? 4 : size == Size.LARGE ? 12 : 8;
            double fontSize = size == Size.SMALL ? 12 : size == Size.LARGE ? 16 : 14;

            for (Option option : options) {
                boolean isSelected = selectedValue != null && selectedValue.equals(option.getValue());

                StackPane optionPane = new StackPane();
                optionPane.setAlignment(Pos.CENTER);
                optionPane.getStyleClass().add("segmented-item");

                String bgStyle = isSelected
                    ? "-fx-background-color: -color-bg-default; -fx-background-radius: 4px;"
                    : "-fx-background-color: transparent; -fx-background-radius: 4px;";

                optionPane.setStyle(bgStyle + "-fx-cursor: " + (disabled ? "default" : "hand") + ";");

                HBox content = new HBox(4);
                content.setAlignment(Pos.CENTER);
                content.setPadding(new Insets(padding, padding + 4, padding, padding + 4));

                if (option.getIcon() != null) {
                    content.getChildren().add(option.getIcon());
                }

                Label label = new Label(option.getLabel());
                label.setStyle(
                    "-fx-font-size: " + fontSize + "px;" +
                    "-fx-text-fill: " + (isSelected ? "-color-fg-default" : "-color-fg-muted") + ";" +
                    "-fx-font-weight: " + (isSelected ? "500" : "400") + ";"
                );
                content.getChildren().add(label);

                optionPane.getChildren().add(content);

                if (!disabled) {
                    optionPane.setOnMouseClicked(e -> {
                        if (onChange != null) {
                            onChange.accept(option.getValue());
                        }
                    });

                    optionPane.setOnMouseEntered(e -> {
                        if (!isSelected) {
                            optionPane.setStyle("-fx-background-color: -color-bg-overlay; -fx-background-radius: 4px; -fx-cursor: hand;");
                        }
                    });

                    optionPane.setOnMouseExited(e -> {
                        if (!isSelected) {
                            optionPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 4px; -fx-cursor: hand;");
                        }
                    });
                }

                if (block) {
                    optionPane.setStyle(optionPane.getStyle() + "-fx-pref-width: 100%;");
                }

                segmented.getChildren().add(optionPane);
            }

            if (disabled) {
                segmented.setStyle(segmented.getStyle() + "-fx-opacity: 0.6;");
            }

            return segmented;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
