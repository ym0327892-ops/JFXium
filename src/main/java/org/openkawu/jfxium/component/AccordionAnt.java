package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Accordion Component
 * 封装 JavaFX Accordion
 *
 * Usage:
 * <pre>{@code
 * Accordion accordion = AccordionAnt.create()
 *     .pane("Panel 1", new Label("Content 1"))
 *     .pane("Panel 2", new Label("Content 2"))
 *     .pane("Panel 3", new Label("Content 3"))
 *     .build();
 * }</pre>
 */
public class AccordionAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private final List<TitledPane> panes = new ArrayList<>();
        private String style = "";

        private Builder() {}

        public Builder pane(String title, Node content) {
            TitledPane pane = new TitledPane(title, content);
            panes.add(pane);
            return this;
        }

        public Builder pane(TitledPane pane) {
            panes.add(pane);
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Accordion build() {
            Accordion accordion = new Accordion();
            accordion.getPanes().addAll(panes);
            accordion.getStyleClass().add("jfx-accordion");

            if (!style.isEmpty()) {
                accordion.setStyle(style);
            }

            return accordion;
        }
    }
}
