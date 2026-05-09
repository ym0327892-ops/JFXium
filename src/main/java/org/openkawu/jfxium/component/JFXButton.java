package org.openkawu.jfxium.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Button Component
 * Inspired by AtlantaFX and Ant Design
 *
 * Usage:
 * <pre>{@code
 * Button btn = JFXButton.create("Click me")
 *     .type(JFXButton.Type.PRIMARY)
 *     .size(JFXButton.Size.LARGE)
 *     .rounded()
 *     .icon(iconNode)
 *     .loading(true)
 *     .onClick(e -> System.out.println("Clicked!"))
 *     .build();
 * }</pre>
 */
public class JFXButton {

    public enum Type {
        DEFAULT,
        PRIMARY,
        ACCENT,
        OUTLINED,
        DASHED,
        TEXT,
        LINK
    }

    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder {
        private final String text;
        private Type type = Type.DEFAULT;
        private Size size = Size.DEFAULT;
        private boolean rounded = false;
        private boolean square = false;
        private boolean disabled = false;
        private boolean loading = false;
        private Node icon;
        private Node loadingIcon;
        private ContentDisplay contentDisplay = ContentDisplay.LEFT;
        private EventHandler<ActionEvent> onClick;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder(String text) {
            this.text = text;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder rounded() {
            this.rounded = true;
            this.square = false;
            return this;
        }

        public Builder square() {
            this.square = true;
            this.rounded = false;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        public Builder icon(Node icon) {
            this.icon = icon;
            return this;
        }

        public Builder loadingIcon(Node loadingIcon) {
            this.loadingIcon = loadingIcon;
            return this;
        }

        public Builder contentDisplay(ContentDisplay display) {
            this.contentDisplay = display;
            return this;
        }

        public Builder onClick(EventHandler<ActionEvent> handler) {
            this.onClick = handler;
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

        public Button build() {
            Button button = new Button(text);

            // Button type
            switch (type) {
                case PRIMARY:
                case ACCENT:
                    button.getStyleClass().add(CssClasses.BUTTON_ACCENT);
                    break;
                case OUTLINED:
                    button.getStyleClass().add(CssClasses.BUTTON_OUTLINED);
                    break;
                case DASHED:
                    button.getStyleClass().add(CssClasses.BUTTON_DASHED);
                    break;
                case TEXT:
                    button.getStyleClass().add(CssClasses.BUTTON_TEXT);
                    break;
                case LINK:
                    button.getStyleClass().add(CssClasses.BUTTON_LINK);
                    break;
                case DEFAULT:
                default:
                    button.getStyleClass().add(CssClasses.BUTTON_DEFAULT);
                    break;
            }

            // Button size
            if (size == Size.SMALL) {
                button.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                button.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Button shape
            if (rounded) {
                button.getStyleClass().add(CssClasses.SHAPE_ROUNDED);
            } else if (square) {
                button.getStyleClass().add(CssClasses.SHAPE_SQUARE);
            }

            // Extra classes
            button.getStyleClass().addAll(extraStyleClasses);

            // Inline style
            if (!style.isEmpty()) {
                button.setStyle(style);
            }

            // Icon handling
            if (loading && loadingIcon != null) {
                button.setGraphic(loadingIcon);
                button.setContentDisplay(contentDisplay);
                button.setDisable(true);
            } else if (icon != null) {
                button.setGraphic(icon);
                button.setContentDisplay(contentDisplay);
            }

            // Disabled state
            if (disabled) {
                button.setDisable(true);
            }

            // Click handler
            if (onClick != null) {
                button.setOnAction(onClick);
            }

            return button;
        }
    }
}
