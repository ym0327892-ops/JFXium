package org.openkawu.jfxium.component;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

public class SwitchAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private boolean selected = false;
        private boolean disabled = false;
        private String checkedText = "";
        private String uncheckedText = "";
        private Consumer<Boolean> onChange;
        private String style = "";

        private Builder() {}

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder checkedText(String text) {
            this.checkedText = text;
            return this;
        }

        public Builder uncheckedText(String text) {
            this.uncheckedText = text;
            return this;
        }

        public Builder onChange(Consumer<Boolean> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public HBox build() {
            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("jfx-switch-container");

            Region track = new Region();
            track.setMinSize(44, 22);
            track.setMaxSize(44, 22);
            track.setPrefSize(44, 22);
            track.getStyleClass().add("jfx-switch-track");

            StackPane thumbContainer = new StackPane();
            thumbContainer.setMinSize(44, 22);
            thumbContainer.setMaxSize(44, 22);
            thumbContainer.setPrefSize(44, 22);
            thumbContainer.setAlignment(Pos.CENTER_LEFT);

            Region thumb = new Region();
            thumb.setMinSize(18, 18);
            thumb.setMaxSize(18, 18);
            thumb.setPrefSize(18, 18);
            thumb.getStyleClass().add("jfx-switch-thumb");
            thumb.setLayoutX(2);

            thumbContainer.getChildren().add(thumb);

            StackPane switchPane = new StackPane(track, thumbContainer);
            switchPane.setMinSize(44, 22);
            switchPane.setMaxSize(44, 22);
            switchPane.setPrefSize(44, 22);
            switchPane.getStyleClass().add("jfx-switch");
            switchPane.setStyle("-fx-cursor: hand;");

            if (selected) {
                switchPane.getStyleClass().add("switch-selected");
            }

            if (disabled) {
                switchPane.setStyle("-fx-opacity: 0.5; -fx-cursor: default;");
            }

            switchPane.setOnMouseClicked(e -> {
                if (!disabled) {
                    toggle(switchPane, thumb, !selected);
                    if (onChange != null) {
                        onChange.accept(!selected);
                    }
                }
            });

            Label textLabel = new Label();
            textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default;");

            HBox.setHgrow(switchPane, Priority.NEVER);
            container.getChildren().add(switchPane);

            if (!checkedText.isEmpty() || !uncheckedText.isEmpty()) {
                Label statusLabel = new Label(selected ? checkedText : uncheckedText);
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: -color-fg-default; -fx-padding: 0 0 0 8px;");
                statusLabel.getProperties().put("switchLabel", true);
                container.getChildren().add(statusLabel);
            }

            if (!style.isEmpty()) {
                container.setStyle(style);
            }

            return container;
        }

        private void toggle(StackPane switchPane, Region thumb, boolean isSelected) {
            if (isSelected) {
                switchPane.getStyleClass().add("switch-selected");
            } else {
                switchPane.getStyleClass().remove("switch-selected");
            }

            TranslateTransition slide = new TranslateTransition(Duration.millis(200), thumb);
            if (isSelected) {
                slide.setFromX(0);
                slide.setToX(24);
            } else {
                slide.setFromX(24);
                slide.setToX(0);
            }
            slide.play();

            Label statusLabel = null;
            for (javafx.scene.Node child : switchPane.getParent().getChildrenUnmodifiable()) {
                if (child instanceof Label && child.getProperties().containsKey("switchLabel")) {
                    statusLabel = (Label) child;
                    break;
                }
            }
            if (statusLabel != null) {
                statusLabel.setText(isSelected ? checkedText : uncheckedText);
            }
        }
    }
}
