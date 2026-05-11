package org.openkawu.jfxium.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * CollapseAnt Component
 * Inspired by Ant Design Collapse
 * Used to display collapsible content panels.
 */
public class CollapseAnt {

    public static class Panel {
        private final String key;
        private final String header;
        private final Node content;
        private boolean disabled;

        public Panel(String key, String header, Node content) {
            this.key = key;
            this.header = header;
            this.content = content;
            this.disabled = false;
        }

        public Panel disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public String getKey() { return key; }
        public String getHeader() { return header; }
        public Node getContent() { return content; }
        public boolean isDisabled() { return disabled; }
    }

    public static class Builder {
        private List<Panel> panels = new ArrayList<>();
        private boolean accordion = false;
        private List<String> activeKeys = new ArrayList<>();

        public Builder panel(String key, String header, Node content) {
            this.panels.add(new Panel(key, header, content));
            return this;
        }

        public Builder panel(String key, String header, Node content, boolean disabled) {
            Panel panel = new Panel(key, header, content);
            panel.disabled(disabled);
            this.panels.add(panel);
            return this;
        }

        public Builder panels(List<Panel> panels) {
            this.panels = panels;
            return this;
        }

        public Builder accordion(boolean accordion) {
            this.accordion = accordion;
            return this;
        }

        public Builder accordion() {
            return accordion(true);
        }

        public Builder activeKey(String key) {
            this.activeKeys.add(key);
            return this;
        }

        public Builder activeKeys(List<String> keys) {
            this.activeKeys = keys;
            return this;
        }

        public VBox build() {
            VBox collapse = new VBox(0);
            collapse.getStyleClass().add("collapse");
            collapse.setStyle("-fx-background-color: -color-bg-default; -fx-background-radius: 8px; -fx-border-color: -color-border-default; -fx-border-radius: 8px;");

            for (int i = 0; i < panels.size(); i++) {
                Panel panel = panels.get(i);
                boolean isActive = activeKeys.contains(panel.getKey());

                VBox panelBox = new VBox(0);
                panelBox.getStyleClass().add("collapse-panel");

                // Header
                HBox header = new HBox(8);
                header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                header.setPadding(new Insets(12, 16, 12, 16));
                header.setStyle("-fx-cursor: " + (panel.isDisabled() ? "default" : "hand") + ";");

                // Arrow icon
                SVGPath arrow = new SVGPath();
                arrow.setContent("M4 6L8 10L12 6");
                arrow.setStyle("-fx-stroke: -color-fg-muted; -fx-stroke-width: 2; -fx-fill: none;");
                if (isActive) {
                    arrow.setRotate(180);
                }

                Label headerLabel = new Label(panel.getHeader());
                headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: " + (panel.isDisabled() ? "-color-fg-subtle" : "-color-fg-default") + ";");

                header.getChildren().addAll(arrow, headerLabel);

                // Content container (initially hidden if not active)
                VBox contentBox = new VBox(0);
                contentBox.setPadding(new Insets(0, 16, 16, 40));
                contentBox.setStyle("-fx-background-color: -color-bg-default;");
                if (panel.getContent() != null) {
                    contentBox.getChildren().add(panel.getContent());
                }
                contentBox.setVisible(isActive);
                contentBox.setManaged(isActive);
                contentBox.setOpacity(isActive ? 1 : 0);

                if (!panel.isDisabled()) {
                    header.setOnMouseClicked(e -> {
                        boolean expanding = !contentBox.isVisible();

                        if (accordion && expanding) {
                            // Close all other panels
                            for (javafx.scene.Node node : collapse.getChildren()) {
                                if (node instanceof VBox && node != panelBox) {
                                    VBox otherPanel = (VBox) node;
                                    for (javafx.scene.Node child : otherPanel.getChildren()) {
                                        if (child instanceof VBox && child != contentBox) {
                                            VBox otherContent = (VBox) child;
                                            if (otherContent.isVisible()) {
                                                animatePanel(otherContent, false);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        animatePanel(contentBox, expanding);

                        // Rotate arrow
                        Timeline arrowAnim = new Timeline(
                            new KeyFrame(Duration.millis(200),
                                new KeyValue(arrow.rotateProperty(), expanding ? 180 : 0))
                        );
                        arrowAnim.play();
                    });
                }

                panelBox.getChildren().addAll(header, contentBox);

                // Add divider between panels (except last)
                if (i < panels.size() - 1) {
                    javafx.scene.layout.Region divider = new javafx.scene.layout.Region();
                    divider.setStyle("-fx-background-color: -color-border-default; -fx-min-height: 1px; -fx-pref-height: 1px;");
                    panelBox.getChildren().add(divider);
                }

                collapse.getChildren().add(panelBox);
            }

            return collapse;
        }

        private void animatePanel(VBox contentBox, boolean show) {
            contentBox.setVisible(true);
            contentBox.setManaged(true);

            Timeline timeline = new Timeline();
            if (show) {
                timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(contentBox.opacityProperty(), 0)),
                    new KeyFrame(Duration.millis(200), new KeyValue(contentBox.opacityProperty(), 1))
                );
            } else {
                timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(contentBox.opacityProperty(), 1)),
                    new KeyFrame(Duration.millis(200), new KeyValue(contentBox.opacityProperty(), 0))
                );
            }

            timeline.setOnFinished(e -> {
                if (!show) {
                    contentBox.setVisible(false);
                    contentBox.setManaged(false);
                }
            });

            timeline.play();
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
