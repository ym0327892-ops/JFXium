package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium Alert 警告提示组件 - 对标 Ant Design Alert。
 *
 * <h2>修复说明</h2>
 * 原实现存在严重 inline style 集中：alert 容器（normal+banner 各一段）、icon Label、
 * title Label、message Label、close button 共 ~7 处 setStyle 拼字符串，
 * 颜色/字号/字重/边框/圆角全靠 Java 端字符串拼接。
 *
 * 与此同时，{@code theme-base.less} 中已有 {@code .alert-success/info/warning/error} 等
 * 选择器，但 Java 端从未给容器加这些 styleClass，全部 LESS 规则成为死代码。
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>容器：挂 {@code jfx-alert} + 类型修饰类 {@code alert-success/info/warning/error}，
 *       配合新增的 {@code alert-banner} 修饰类支持 banner 形态</li>
 *   <li>子节点：icon/title/message/close 各自挂语义化 styleClass，颜色随 type 由 LESS 切换</li>
 *   <li>状态文字色：通过 LESS 选择器 {@code .jfx-alert.alert-success .alert-title} 等组合实现，
 *       Java 端不再用 {@code getTextColor()} 拼字符串</li>
 *   <li>接入 {@link AbstractStyleBuilder}</li>
 *   <li>删除 {@code getBackgroundColor/getBorderColor/getTextColor} 三个方法（颜色逻辑已搬到 LESS）</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * VBox alert = AlertAnt.success("操作成功", "数据已保存")
 *     .closable(true)
 *     .showIcon(true)
 *     .build();
 * }</pre>
 */
public class AlertAnt {

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    public static Builder success(String message) {
        return new Builder(Type.SUCCESS, "Success", message);
    }

    public static Builder success(String title, String message) {
        return new Builder(Type.SUCCESS, title, message);
    }

    public static Builder info(String message) {
        return new Builder(Type.INFO, "Info", message);
    }

    public static Builder info(String title, String message) {
        return new Builder(Type.INFO, title, message);
    }

    public static Builder warning(String message) {
        return new Builder(Type.WARNING, "Warning", message);
    }

    public static Builder warning(String title, String message) {
        return new Builder(Type.WARNING, title, message);
    }

    public static Builder error(String message) {
        return new Builder(Type.ERROR, "Error", message);
    }

    public static Builder error(String title, String message) {
        return new Builder(Type.ERROR, title, message);
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final Type type;
        private final String title;
        private final String message;
        private boolean closable = false;
        private boolean showIcon = true;
        private boolean banner = false;
        private Node action = null;
        private Runnable onClose;

        private Builder(Type type, String title, String message) {
            this.type = type;
            this.title = title;
            this.message = message;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder showIcon(boolean show) {
            this.showIcon = show;
            return this;
        }

        public Builder banner(boolean banner) {
            this.banner = banner;
            return this;
        }

        public Builder banner() {
            return banner(true);
        }

        public Builder action(Node action) {
            this.action = action;
            return this;
        }

        public Builder onClose(Runnable handler) {
            this.onClose = handler;
            return this;
        }

        public VBox build() {
            VBox alert = new VBox(4);
            alert.setAlignment(Pos.CENTER_LEFT);
            alert.getStyleClass().add(CssClasses.ALERT);
            alert.getStyleClass().add(typeClassFor(type));

            if (banner) {
                alert.getStyleClass().add(CssClasses.ALERT_BANNER);
                HBox.setHgrow(alert, Priority.ALWAYS);
            }

            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);

            // 图标 Label：通过 styleClass 控制字号与文字色（LESS 端按 type 切换文字色）
            if (showIcon) {
                Label iconLabel = new Label(iconFor(type));
                iconLabel.getStyleClass().add(CssClasses.ALERT_ICON);
                header.getChildren().add(iconLabel);
            }

            // 标题 Label：styleClass 控制字号/字重/文字色
            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add(CssClasses.ALERT_TITLE);
            header.getChildren().add(titleLabel);

            // action 节点（用户自定义按钮）放在标题之后，spacer 推到右侧
            if (action != null) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                header.getChildren().addAll(spacer, action);
            }

            // 关闭按钮：如果没有 action 也需要 spacer 把它推到右侧
            if (closable) {
                if (action == null) {
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    header.getChildren().add(spacer);
                }

                Button closeBtn = new Button("✕");
                closeBtn.getStyleClass().add(CssClasses.ALERT_CLOSE_BTN);
                closeBtn.setOnAction(e -> closeWithFade(alert));
                header.getChildren().add(closeBtn);
            }

            alert.getChildren().add(header);

            // 描述文字：styleClass 控制字号 + 文字色（按 type 切换）
            if (message != null && !message.isEmpty()) {
                Label messageLabel = new Label(message);
                messageLabel.setWrapText(true);
                messageLabel.getStyleClass().add(CssClasses.ALERT_MESSAGE);
                if (showIcon) {
                    // 描述左侧缩进对齐图标后内容（图标 16 + spacing 8 = 24，仅作为视觉细节保留）
                    messageLabel.setPadding(new Insets(0, 0, 0, 24));
                }
                alert.getChildren().add(messageLabel);
            }

            // 用户 style/styleClass 在内置类后应用，便于覆盖
            applyStyles(alert);
            return alert;
        }

        /** 渐隐关闭：200ms 渐隐后从父容器移除，并触发 onClose 回调 */
        private void closeWithFade(VBox alert) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), alert);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> {
                if (alert.getParent() instanceof Pane parent) {
                    parent.getChildren().remove(alert);
                }
                if (onClose != null) {
                    onClose.run();
                }
            });
            fadeOut.play();
        }

        private static String typeClassFor(Type type) {
            return switch (type) {
                case SUCCESS -> CssClasses.ALERT_SUCCESS;
                case INFO -> CssClasses.ALERT_INFO;
                case WARNING -> CssClasses.ALERT_WARNING;
                case ERROR -> CssClasses.ALERT_ERROR;
            };
        }

        private static String iconFor(Type type) {
            return switch (type) {
                case SUCCESS -> "✓";
                case INFO -> "ℹ";
                case WARNING -> "⚠";
                case ERROR -> "✕";
            };
        }
    }
}
