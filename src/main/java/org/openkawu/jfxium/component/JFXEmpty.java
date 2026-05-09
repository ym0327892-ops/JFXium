package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * JFXium Empty Component
 * Inspired by Ant Design Empty
 */
public class JFXEmpty {

    public static class Builder {
        private String description = "No Data";
        private String image = null;
        private javafx.scene.Node extra = null;

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder extra(javafx.scene.Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder extraButton(String text, Runnable action) {
            this.extra = JFXButton.create(text)
                .type(JFXButton.Type.PRIMARY)
                .onClick(e -> action.run())
                .build();
            return this;
        }

        public VBox build() {
            VBox empty = new VBox(16);
            empty.setAlignment(Pos.CENTER);
            empty.getStyleClass().add("empty");
            empty.setStyle("-fx-padding: 48px;");

            // Image/Icon
            javafx.scene.shape.SVGPath icon = new javafx.scene.shape.SVGPath();
            icon.setContent("M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z");
            icon.setScaleX(2);
            icon.setScaleY(2);
            icon.setStyle("-fx-fill: -color-fg-subtle;");

            empty.getChildren().add(icon);

            // Description
            Label descLabel = new Label(description);
            descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px;");
            empty.getChildren().add(descLabel);

            // Extra content
            if (extra != null) {
                empty.getChildren().add(extra);
            }

            return empty;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(String description) {
        return new Builder().description(description);
    }
}
