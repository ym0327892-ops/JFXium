package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 时间线组件 - 对标 Ant Design Timeline。
 *
 * <p><b>定位</b>：垂直时间线展示事件序列，常用于操作日志、订单流程、
 * 版本历史等场景。支持多种颜色圆点和布局模式。</p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *   <li><b>模式</b>：LEFT（默认）/ RIGHT / ALTERNATE（左右交替）</li>
 *   <li><b>圆点颜色</b>：BLUE / RED / GREEN / GRAY（通过 CSS 修饰类切换）</li>
 *   <li><b>自定义圆点</b>：dot(Node) 可放入任意自定义节点</li>
 *   <li><b>标签</b>：label(text) 右侧时间标签</li>
 *   <li><b>内容</b>：支持字符串或自定义 Node</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * VBox timeline = TimelineAnt.create()
 *     .item("创建订单", "2024-01-01 10:00", TimelineAnt.DotColor.BLUE)
 *     .item("支付成功", "2024-01-01 10:05", TimelineAnt.DotColor.GREEN)
 *     .item("已发货", "2024-01-02 14:00", TimelineAnt.DotColor.BLUE)
 *     .item("待签收", "", TimelineAnt.DotColor.GRAY)
 *     .build();
 * }</pre>
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

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private List<TimelineItem> items = new ArrayList<>();
        private Mode mode = Mode.LEFT;
        private boolean pending = false;
        private String pendingText = "Loading...";

        public Builder mode(Mode mode) { this.mode = mode != null ? mode : Mode.LEFT; return this; }
        public Builder pending(boolean pending) { this.pending = pending; return this; }
        public Builder pending(String pendingText) {
            this.pending = true;
            this.pendingText = pendingText != null ? pendingText : "";
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
            items.add(new TimelineItem(content, label, null, dotColor != null ? dotColor : DotColor.BLUE));
            return this;
        }

        public Builder item(String content, Node dot) {
            items.add(new TimelineItem(content, null, dot, DotColor.BLUE));
            return this;
        }

        public VBox build() {
            VBox timeline = new VBox(0);
            timeline.getStyleClass().add(JfxStyles.TIMELINE);

            for (int i = 0; i < items.size(); i++) {
                TimelineItem item = items.get(i);
                boolean isLast = i == items.size() - 1 && !pending;
                timeline.getChildren().add(buildItem(item, isLast));
            }

            if (pending) {
                timeline.getChildren().add(buildPendingItem());
            }
            applyStyles(timeline);
            return timeline;
        }

        private HBox buildItem(TimelineItem item, boolean isLast) {
            if (item == null) {
                item = new TimelineItem("", null, null, DotColor.BLUE);
            }
            HBox row = new HBox(0);
            row.setAlignment(Pos.TOP_LEFT);
            row.getStyleClass().add(JfxStyles.TIMELINE_ITEM);

            // 左侧 label（alternate / right 模式）
            if (mode == Mode.ALTERNATE || mode == Mode.RIGHT) {
                VBox leftBox = new VBox();
                leftBox.setAlignment(Pos.TOP_RIGHT);
                leftBox.getStyleClass().add(JfxStyles.TIMELINE_LABEL_BOX);
                if (item.label != null) {
                    Label label = new Label(item.label);
                    label.getStyleClass().add(JfxStyles.TIMELINE_LABEL);
                    leftBox.getChildren().add(label);
                }
                row.getChildren().add(leftBox);
            }

            // 中间：dot + line
            VBox centerBox = new VBox(0);
            centerBox.setAlignment(Pos.TOP_CENTER);
            centerBox.getStyleClass().add(JfxStyles.TIMELINE_CENTER_BOX);

            Node dot;
            if (item.dot != null) {
                dot = item.dot;
            } else {
                Circle circle = new Circle(6);
                circle.getStyleClass().add(JfxStyles.TIMELINE_DOT);
                circle.getStyleClass().add(dotColorClassFor(item.dotColor));
                dot = circle;
            }
            centerBox.getChildren().add(dot);

            if (!isLast) {
                Line line = new Line(0, 0, 0, 40);
                line.getStyleClass().add(JfxStyles.TIMELINE_LINE);
                centerBox.getChildren().add(line);
            }
            row.getChildren().add(centerBox);

            // 右侧 content
            VBox rightBox = new VBox();
            rightBox.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(rightBox, Priority.ALWAYS);
            rightBox.getStyleClass().add(JfxStyles.TIMELINE_CONTENT_BOX);

            Label contentLabel = new Label(item.content != null ? item.content : "");
            contentLabel.getStyleClass().add(JfxStyles.TIMELINE_CONTENT);
            contentLabel.setWrapText(true);
            rightBox.getChildren().add(contentLabel);

            if (mode == Mode.LEFT && item.label != null) {
                Label label = new Label(item.label);
                label.getStyleClass().add(JfxStyles.TIMELINE_LABEL);
                rightBox.getChildren().add(label);
            }
            row.getChildren().add(rightBox);
            return row;
        }

        private HBox buildPendingItem() {
            HBox row = new HBox(0);
            row.setAlignment(Pos.TOP_LEFT);
            row.getStyleClass().add(JfxStyles.TIMELINE_ITEM);

            if (mode == Mode.ALTERNATE || mode == Mode.RIGHT) {
                VBox leftBox = new VBox();
                leftBox.getStyleClass().add(JfxStyles.TIMELINE_LABEL_BOX);
                row.getChildren().add(leftBox);
            }

            VBox centerBox = new VBox(0);
            centerBox.setAlignment(Pos.TOP_CENTER);
            centerBox.getStyleClass().add(JfxStyles.TIMELINE_CENTER_BOX);

            Circle circle = new Circle(6);
            circle.getStyleClass().add(JfxStyles.TIMELINE_DOT);
            circle.getStyleClass().add(JfxStyles.TIMELINE_DOT_PENDING);
            centerBox.getChildren().add(circle);
            row.getChildren().add(centerBox);

            VBox rightBox = new VBox();
            rightBox.getStyleClass().add(JfxStyles.TIMELINE_CONTENT_BOX);
            Label contentLabel = new Label(pendingText);
            contentLabel.getStyleClass().add(JfxStyles.TIMELINE_PENDING_TEXT);
            rightBox.getChildren().add(contentLabel);
            row.getChildren().add(rightBox);
            return row;
        }

        private static String dotColorClassFor(DotColor color) {
            DotColor effectiveColor = color != null ? color : DotColor.BLUE;
            return switch (effectiveColor) {
                case BLUE -> JfxStyles.TIMELINE_DOT_BLUE;
                case RED -> JfxStyles.TIMELINE_DOT_RED;
                case GREEN -> JfxStyles.TIMELINE_DOT_GREEN;
                case GRAY -> JfxStyles.TIMELINE_DOT_GRAY;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
