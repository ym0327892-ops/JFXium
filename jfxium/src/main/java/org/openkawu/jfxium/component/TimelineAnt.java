package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Timeline - 对标 Ant Design Timeline。
 *
 * 重构：所有 inline {@code setStyle}（item padding / label / content / line / dot 颜色）
 * 改为挂 LESS styleClass。dot 颜色通过 {@code timeline-dot-blue/red/green/gray/pending} 修饰类切换。
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
        Node dot;
        DotColor dotColor;

        public TimelineItem(String content, String label, Node dot, DotColor dotColor) {
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

        public Builder mode(Mode mode) { this.mode = mode; return this; }
        public Builder pending(boolean pending) { this.pending = pending; return this; }
        public Builder pending(String pendingText) { this.pending = true; this.pendingText = pendingText; return this; }

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

        public Builder item(String content, Node dot) {
            items.add(new TimelineItem(content, null, dot, DotColor.BLUE));
            return this;
        }

        public VBox build() {
            VBox timeline = new VBox(0);
            timeline.getStyleClass().add(CssClasses.TIMELINE);

            for (int i = 0; i < items.size(); i++) {
                TimelineItem item = items.get(i);
                boolean isLast = i == items.size() - 1 && !pending;
                timeline.getChildren().add(buildItem(item, isLast));
            }

            if (pending) {
                timeline.getChildren().add(buildPendingItem());
            }
            return timeline;
        }

        private HBox buildItem(TimelineItem item, boolean isLast) {
            HBox row = new HBox(0);
            row.setAlignment(Pos.TOP_LEFT);
            row.getStyleClass().add(CssClasses.TIMELINE_ITEM);

            // 左侧 label（alternate / right 模式）
            if (mode == Mode.ALTERNATE || mode == Mode.RIGHT) {
                VBox leftBox = new VBox();
                leftBox.setPrefWidth(100);
                leftBox.setAlignment(Pos.TOP_RIGHT);
                leftBox.setPadding(new javafx.geometry.Insets(0, 16, 0, 0));
                if (item.label != null) {
                    Label label = new Label(item.label);
                    label.getStyleClass().add(CssClasses.TIMELINE_LABEL);
                    leftBox.getChildren().add(label);
                }
                row.getChildren().add(leftBox);
            }

            // 中间：dot + line
            VBox centerBox = new VBox(0);
            centerBox.setAlignment(Pos.TOP_CENTER);
            centerBox.setPrefWidth(24);

            Node dot;
            if (item.dot != null) {
                dot = item.dot;
            } else {
                Circle circle = new Circle(6);
                circle.getStyleClass().add(CssClasses.TIMELINE_DOT);
                circle.getStyleClass().add(dotColorClassFor(item.dotColor));
                dot = circle;
            }
            centerBox.getChildren().add(dot);

            if (!isLast) {
                Line line = new Line(0, 0, 0, 40);
                line.getStyleClass().add(CssClasses.TIMELINE_LINE);
                centerBox.getChildren().add(line);
            }
            row.getChildren().add(centerBox);

            // 右侧 content
            VBox rightBox = new VBox(4);
            rightBox.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(rightBox, Priority.ALWAYS);
            rightBox.setPadding(new javafx.geometry.Insets(0, 0, 0, 16));

            Label contentLabel = new Label(item.content);
            contentLabel.getStyleClass().add(CssClasses.TIMELINE_CONTENT);
            contentLabel.setWrapText(true);
            rightBox.getChildren().add(contentLabel);

            if (mode == Mode.LEFT && item.label != null) {
                Label label = new Label(item.label);
                label.getStyleClass().add(CssClasses.TIMELINE_LABEL);
                rightBox.getChildren().add(label);
            }
            row.getChildren().add(rightBox);
            return row;
        }

        private HBox buildPendingItem() {
            HBox row = new HBox(0);
            row.setAlignment(Pos.TOP_LEFT);

            if (mode == Mode.ALTERNATE || mode == Mode.RIGHT) {
                VBox leftBox = new VBox();
                leftBox.setPrefWidth(100);
                row.getChildren().add(leftBox);
            }

            VBox centerBox = new VBox(0);
            centerBox.setAlignment(Pos.TOP_CENTER);
            centerBox.setPrefWidth(24);

            Circle circle = new Circle(6);
            circle.getStyleClass().add(CssClasses.TIMELINE_DOT);
            circle.getStyleClass().add(CssClasses.TIMELINE_DOT_PENDING);
            centerBox.getChildren().add(circle);
            row.getChildren().add(centerBox);

            VBox rightBox = new VBox();
            rightBox.setPadding(new javafx.geometry.Insets(0, 0, 0, 16));
            Label contentLabel = new Label(pendingText);
            contentLabel.getStyleClass().add(CssClasses.TIMELINE_PENDING_TEXT);
            rightBox.getChildren().add(contentLabel);
            row.getChildren().add(rightBox);
            return row;
        }

        private static String dotColorClassFor(DotColor color) {
            return switch (color) {
                case BLUE -> CssClasses.TIMELINE_DOT_BLUE;
                case RED -> CssClasses.TIMELINE_DOT_RED;
                case GREEN -> CssClasses.TIMELINE_DOT_GREEN;
                case GRAY -> CssClasses.TIMELINE_DOT_GRAY;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
