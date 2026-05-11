package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

/**
 * JFXium TitledPane Component
 * 封装 JavaFX TitledPane
 *
 * Usage:
 * <pre>{@code
 * TitledPane titledPane = TitledPaneAnt.create()
 *     .title("Title")
 *     .content(new Label("Content"))
 *     .expanded(true)
 *     .build();
 * }</pre>
 */
public class TitledPaneAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String title = "";
        private Node content;
        private boolean expanded = true;
        private boolean animated = true;
        private boolean collapsible = true;
        private String style = "";

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder expanded(boolean expanded) {
            this.expanded = expanded;
            return this;
        }

        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        public Builder collapsible(boolean collapsible) {
            this.collapsible = collapsible;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public TitledPane build() {
            TitledPane titledPane = new TitledPane(title, content);
            titledPane.setExpanded(expanded);
            titledPane.setAnimated(animated);
            titledPane.setCollapsible(collapsible);
            titledPane.getStyleClass().add("jfx-titled-pane");

            if (!style.isEmpty()) {
                titledPane.setStyle(style);
            }

            return titledPane;
        }
    }
}
