package org.openkawu.jfxium.component;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Progress Component
 * Inspired by Ant Design Progress
 *
 * Usage:
 * <pre>{@code
 * // 进度条
 * ProgressBar bar = ProgressAnt.bar()
 *     .progress(0.75)
 *     .size(ProgressAnt.Size.LARGE)
 *     .build();
 *
 * // 环形进度
 * ProgressIndicator indicator = ProgressAnt.circle()
 *     .progress(0.75)
 *     .size(80)
 *     .build();
 * }</pre>
 */
public class ProgressAnt {

    public enum Size {
        SMALL, DEFAULT, LARGE
    }

    public enum Type {
        LINE, CIRCLE
    }

    public enum Status {
        NORMAL, SUCCESS, ERROR, WARNING
    }

    public static BarBuilder bar() {
        return new BarBuilder();
    }

    public static CircleBuilder circle() {
        return new CircleBuilder();
    }

    public static class BarBuilder {
        private double progress = 0;
        private Size size = Size.DEFAULT;
        private Status status = Status.NORMAL;
        private boolean showInfo = true;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private BarBuilder() {}

        public BarBuilder progress(double progress) {
            this.progress = Math.max(0, Math.min(1, progress));
            return this;
        }

        public BarBuilder size(Size size) {
            this.size = size;
            return this;
        }

        public BarBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public BarBuilder showInfo(boolean show) {
            this.showInfo = show;
            return this;
        }

        public BarBuilder style(String style) {
            this.style = style;
            return this;
        }

        public BarBuilder styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public HBox build() {
            HBox container = new HBox(8);
            container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            ProgressBar progressBar = new ProgressBar(progress);
            progressBar.getStyleClass().add("jfx-progress-bar");
            progressBar.getStyleClass().addAll(extraStyleClasses);

            // Size
            switch (size) {
                case SMALL:
                    progressBar.setPrefHeight(4);
                    break;
                case LARGE:
                    progressBar.setPrefHeight(12);
                    break;
                default:
                    progressBar.setPrefHeight(8);
            }
            progressBar.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(progressBar, javafx.scene.layout.Priority.ALWAYS);

            // Status color
            String color = getStatusColor(status);
            progressBar.setStyle("-fx-accent: " + color + ";");

            if (!style.isEmpty()) {
                progressBar.setStyle(progressBar.getStyle() + style);
            }

            container.getChildren().add(progressBar);

            // Info label
            if (showInfo) {
                Label infoLabel = new Label(String.format("%.0f%%", progress * 100));
                infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
                container.getChildren().add(infoLabel);
            }

            return container;
        }
    }

    public static class CircleBuilder {
        private double progress = 0;
        private double size = 60;
        private Status status = Status.NORMAL;
        private boolean showInfo = true;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private CircleBuilder() {}

        public CircleBuilder progress(double progress) {
            this.progress = Math.max(0, Math.min(1, progress));
            return this;
        }

        public CircleBuilder size(double size) {
            this.size = size;
            return this;
        }

        public CircleBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public CircleBuilder showInfo(boolean show) {
            this.showInfo = show;
            return this;
        }

        public CircleBuilder style(String style) {
            this.style = style;
            return this;
        }

        public CircleBuilder styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public VBox build() {
            VBox container = new VBox(4);
            container.setAlignment(javafx.geometry.Pos.CENTER);

            ProgressIndicator indicator = new ProgressIndicator(progress);
            indicator.getStyleClass().add("jfx-progress-circle");
            indicator.getStyleClass().addAll(extraStyleClasses);
            indicator.setPrefSize(size, size);

            // Status color
            String color = getStatusColor(status);
            indicator.setStyle("-fx-progress-color: " + color + ";");

            if (!style.isEmpty()) {
                indicator.setStyle(indicator.getStyle() + style);
            }

            container.getChildren().add(indicator);

            // Info label
            if (showInfo) {
                Label infoLabel = new Label(String.format("%.0f%%", progress * 100));
                infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
                container.getChildren().add(infoLabel);
            }

            return container;
        }
    }

    private static String getStatusColor(Status status) {
        switch (status) {
            case SUCCESS:
                return "-color-success-emphasis";
            case ERROR:
                return "-color-danger-emphasis";
            case WARNING:
                return "-color-warning-emphasis";
            case NORMAL:
            default:
                return "-color-accent-emphasis";
        }
    }
}