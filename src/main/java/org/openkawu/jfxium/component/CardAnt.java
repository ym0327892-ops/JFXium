package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Card Component
 * Inspired by AtlantaFX and Ant Design
 *
 * Usage:
 * <pre>{@code
 * VBox card = CardAnt.create()
 *     .title("Card Title")
 *     .content(new Label("Card content"))
 *     .bordered(true)
 *     .shadow(CardAnt.Shadow.MEDIUM)
 *     .hoverable(true)
 *     .build();
 * }</pre>
 */
public class CardAnt {

    public enum Shadow {
        NONE,
        SMALL,
        MEDIUM,
        LARGE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String title = "";
        private Node extra;
        private Node content;
        private boolean bordered = false;
        private Shadow shadow = Shadow.NONE;
        private boolean hoverable = false;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public Builder shadow(Shadow shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder hoverable(boolean hoverable) {
            this.hoverable = hoverable;
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

        public VBox build() {
            VBox card = new VBox();
            card.setSpacing(8);

            // Base card class
            card.getStyleClass().add(CssClasses.CARD);

            // Bordered
            if (bordered) {
                card.getStyleClass().add(CssClasses.CARD_BORDERED);
            }

            // Hoverable
            if (hoverable) {
                card.getStyleClass().add(CssClasses.CARD_HOVERABLE);
            }

            // Shadow
            switch (shadow) {
                case SMALL:
                    card.getStyleClass().add(CssClasses.CARD_SHADOW_SM);
                    break;
                case MEDIUM:
                    card.getStyleClass().add(CssClasses.CARD_SHADOW_MD);
                    break;
                case LARGE:
                    card.getStyleClass().add(CssClasses.CARD_SHADOW_LG);
                    break;
                case NONE:
                default:
                    break;
            }

            // Extra classes
            card.getStyleClass().addAll(extraStyleClasses);

            // Inline style
            if (!style.isEmpty()) {
                card.setStyle(style);
            }

            // Title section
            if (!title.isEmpty() || extra != null) {
                javafx.scene.layout.HBox header = new javafx.scene.layout.HBox();
                header.setSpacing(8);
                header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                if (!title.isEmpty()) {
                    Label titleLabel = new Label(title);
                    titleLabel.getStyleClass().add(CssClasses.CARD_TITLE);
                    header.getChildren().add(titleLabel);
                }

                if (extra != null) {
                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    header.getChildren().addAll(spacer, extra);
                }

                card.getChildren().add(header);
            }

            // Content
            if (content != null) {
                if (content.getStyleClass().isEmpty()) {
                    content.getStyleClass().add(CssClasses.CARD_CONTENT);
                }
                card.getChildren().add(content);
            }

            return card;
        }
    }
}
