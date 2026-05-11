package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Timeline Component
 * Inspired by Ant Design Timeline
 * Display a list of events in chronological order.
 */
public class TimelineAnt {

    public enum Mode {
        LEFT, RIGHT, ALTERNATE
    }

    public enum DotColor {
        BLUE, RED, GREEN, GRAY
    }

    public static class TimelineItem {
        String content;
        String label;
        javafx.scene.Node dot;
        DotColor dotColor;

        public TimelineItem(String content, String label, javafx.scene.Node dot, DotColor dotColor) {
            this.content = content;
            this.label = label;
            this.dot = dot;
            this.dotColor = dotColor;
        }
    }

    public static class Builder {
        private List<TimelineItem> items = new ArrayList<>();
        private Mode mode = Mode.LEFT;
        private boolean pending = false;
        private String pendingText = "Loading...";

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder pending(boolean pending) {
            this.pending = pending;
            return this;
        }

        public Builder pending(String pendingText) {
            this.pending = true;
            this.pendingText = pendingText;
            return this;
        }

        public Builder item(String content) {
            items.add(new TimelineItem(content, null, null, DotColor.BLUE));
            return this;
        }

        public Builder item(String content, String label) {
            items.add(new TimelineItem(content, label, null, DotColor.BLUE));
            return this;
        }

        public Builder item(String content, DotColor dotColor) {
            items.add(new TimelineItem(content, null, null, dotColor));
            return this;
        }

        public Builder item(String content, String label, DotColor dotColor) {
            items.add(new TimelineItem(content, label, null, dotColor));
            return this;
        }

        public Builder item(String content, javafx.scene.Node dot) {
            items.add(new TimelineItem(content, null, dot, DotColor.BLUE));
            return this;
        }

        public VBox build() {
            VBox timeline = new VBox(0);
            timeline.getStyleClass().add("timeline");
            timeline.setStyle("-fx-padding: 16px;");

            for (int i = 0; i < items.size(); i++) {
                TimelineItem item = items.get(i);
                boolean isLast = i == items.size() - 1 && !pending;
                timeline.getChildren().add(buildItem(item, isLast));
            }

            // Pending item
            if (pending) {
                timeline.getChildren().add(buildPendingItem());
            }

            return timeline;
        }

        private HBox buildItem(TimelineItem item, boolean isLast) {
            HBox row = new HBox(0);
            row.setAlignment(Pos.TOP_LEFT);
            row.setStyle("-fx-padding: 0 0 16px 0;");

            // Left side (label for alternate mode)
            if (mode == Mode.ALTERNATE || mode == Mode.RIGHT) {
                VBox leftBox = new VBox();
                leftBox.setPrefWidth(100);
                leftBox.setAlignment(Pos.TOP_RIGHT);
                leftBox.setStyle("-fx-padding: 0 16px 0 0;");

                if (item.label != null) {
                    Label label = new Label(item.label);
                    label.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
                    leftBox.getChildren().add(label);
                }

                row.getChildren().add(leftBox);
            }

            // Center dot + line
            VBox centerBox = new VBox(0);
            centerBox.setAlignment(Pos.TOP_CENTER);
            centerBox.setPrefWidth(24);

            // Dot
            javafx.scene.Node dot;
            if (item.dot != null) {
                dot = item.dot;
            } else {
                Circle circle = new Circle(6);
                circle.setStyle("-fx-fill: " + getDotColor(item.dotColor) + ";");
                dot = circle;
            }
            centerBox.getChildren().add(dot);

            // Line
            if (!isLast) {
                Line line = new Line(0, 0, 0, 40);
                line.setStyle("-fx-stroke: -color-border-muted; -fx-stroke-width: 2px;");
                centerBox.getChildren().add(line);
            }

            row.getChildren().add(centerBox);

            // Right side (content)
            VBox rightBox = new VBox(4);
            rightBox.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);
            rightBox.setStyle("-fx-padding: 0 0 0 16px;");

            Label contentLabel = new Label(item.content);
            contentLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px;");
            contentLabel.setWrapText(true);
            rightBox.getChildren().add(contentLabel);

            if (mode == Mode.LEFT && item.label != null) {
                Label label = new Label(item.label);
                label.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
                rightBox.getChildren().add(label);
            }

            row.getChildren().add(rightBox);

            return row;
        }

        private HBox buildPendingItem() {
            HBox row = new HBox(0);
            row.setAlignment(Pos.TOP_LEFT);

            // Left spacer
            if (mode == Mode.ALTERNATE || mode == Mode.RIGHT) {
                VBox leftBox = new VBox();
                leftBox.setPrefWidth(100);
                row.getChildren().add(leftBox);
            }

            // Center dot
            VBox centerBox = new VBox(0);
            centerBox.setAlignment(Pos.TOP_CENTER);
            centerBox.setPrefWidth(24);

            Circle circle = new Circle(6);
            circle.setStyle("-fx-fill: transparent; -fx-stroke: -color-border-default; -fx-stroke-width: 2px;");
            centerBox.getChildren().add(circle);

            row.getChildren().add(centerBox);

            // Content
            VBox rightBox = new VBox();
            rightBox.setStyle("-fx-padding: 0 0 0 16px;");
            Label contentLabel = new Label(pendingText);
            contentLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px;");
            rightBox.getChildren().add(contentLabel);

            row.getChildren().add(rightBox);

            return row;
        }

        private String getDotColor(DotColor color) {
            return switch (color) {
                case BLUE -> "-color-accent-emphasis";
                case RED -> "-color-danger-emphasis";
                case GREEN -> "-color-success-emphasis";
                case GRAY -> "-color-fg-muted";
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
