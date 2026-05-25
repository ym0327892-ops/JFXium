package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium Progress 进度组件 - 对标 Ant Design Progress。
 *
 * <h2>修复说明</h2>
 * 原实现 Bar/Circle 双 Builder，每个 Builder 都通过 inline {@code setStyle} 拼接：
 * <ul>
 *   <li>{@code -fx-accent} / {@code -fx-progress-color} 按 status 注入</li>
 *   <li>info Label 的字号/颜色拼字符串</li>
 * </ul>
 *
 * 与此同时 {@code theme-base.less} 中已有 {@code .progress-bar.success} 等完整规则，
 * Java 端从未挂相应 styleClass，导致 LESS 死代码。
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>{@code ProgressBar/ProgressIndicator} 挂 status styleClass：success / warning / error</li>
 *   <li>info Label 挂 {@code jfx-progress-info} + status 修饰类，颜色由 LESS 切换</li>
 *   <li>删除 {@code getStatusColor()} 方法（颜色逻辑已搬到 LESS）</li>
 *   <li>双 Builder 接入 {@link AbstractStyleBuilder}</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * HBox bar = ProgressAnt.bar()
 *     .progress(0.75)
 *     .status(ProgressAnt.Status.SUCCESS)
 *     .build();
 *
 * VBox circle = ProgressAnt.circle()
 *     .progress(0.5)
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

    public static class BarBuilder extends AbstractStyleBuilder<BarBuilder> {
        private double progress = 0;
        private Size size = Size.DEFAULT;
        private Status status = Status.NORMAL;
        private boolean showInfo = true;

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

        public HBox build() {
            HBox container = new HBox(8);
            container.setAlignment(Pos.CENTER_LEFT);

            ProgressBar progressBar = new ProgressBar(progress);
            progressBar.getStyleClass().add(CssClasses.PROGRESS_BAR);
            // status 通过 styleClass 触发 LESS 端 .progress-bar.success/warning/error 选择器
            String statusClass = statusClassFor(status);
            if (statusClass != null) {
                progressBar.getStyleClass().add(statusClass);
            }

            // 高度由 size 决定（结构属性，不下沉到 LESS）
            switch (size) {
                case SMALL -> progressBar.setPrefHeight(4);
                case LARGE -> progressBar.setPrefHeight(12);
                default -> progressBar.setPrefHeight(8);
            }
            progressBar.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(progressBar, Priority.ALWAYS);

            container.getChildren().add(progressBar);

            // 百分比 Label：颜色由 LESS .jfx-progress-info.{success/warning/error} 切换
            if (showInfo) {
                Label infoLabel = new Label(String.format("%.0f%%", progress * 100));
                infoLabel.getStyleClass().add(CssClasses.PROGRESS_INFO);
                if (statusClass != null) {
                    infoLabel.getStyleClass().add(statusClass);
                }
                container.getChildren().add(infoLabel);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(container);
            return container;
        }
    }

    public static class CircleBuilder extends AbstractStyleBuilder<CircleBuilder> {
        private double progress = 0;
        private double size = 60;
        private Status status = Status.NORMAL;
        private boolean showInfo = true;

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

        public VBox build() {
            VBox container = new VBox(4);
            container.setAlignment(Pos.CENTER);

            ProgressIndicator indicator = new ProgressIndicator(progress);
            indicator.getStyleClass().add(CssClasses.PROGRESS_CIRCLE);
            // 利用 LESS .progress-indicator.success/warning/error 切换 -fx-progress-color
            String statusClass = statusClassFor(status);
            if (statusClass != null) {
                indicator.getStyleClass().add(statusClass);
            }
            indicator.setPrefSize(size, size);

            container.getChildren().add(indicator);

            if (showInfo) {
                Label infoLabel = new Label(String.format("%.0f%%", progress * 100));
                infoLabel.getStyleClass().add(CssClasses.PROGRESS_INFO);
                if (statusClass != null) {
                    infoLabel.getStyleClass().add(statusClass);
                }
                container.getChildren().add(infoLabel);
            }

            applyStyles(container);
            return container;
        }
    }

    /** 把 Status 映射到 styleClass 修饰类名，NORMAL 返回 null（不加额外类）。*/
    private static String statusClassFor(Status status) {
        if (status == null) return null;
        return switch (status) {
            case SUCCESS -> CssClasses.PROGRESS_SUCCESS;
            case WARNING -> CssClasses.PROGRESS_WARNING;
            case ERROR -> CssClasses.PROGRESS_ERROR;
            case NORMAL -> null;
        };
    }
}
