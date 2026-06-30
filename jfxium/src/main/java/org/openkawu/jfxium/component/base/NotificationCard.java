package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.IconPath;

/**
 * 内部基础组件：通知卡片（图标 + 标题 + 描述 + 关闭按钮）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.overlay.NotificationAnt} 使用，
 * 作为 TOP_LEFT / TOP_RIGHT / BOTTOM_LEFT / BOTTOM_RIGHT 通知的 UI 面板。</p>
 */
public class NotificationCard {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public static class Builder {
        private String title = "";
        private String description = "";
        private Type type = Type.INFO;
        private boolean closable = true;
        private Runnable onClose = null;
        private Node extra = null;
        private String width = "384px";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder onClose(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder content(Node content) {
            this.extra = content;
            return this;
        }

        public Builder width(String width) {
            this.width = width;
            return this;
        }

        public VBox build() {
            VBox card = new VBox();
            card.setAlignment(Pos.TOP_LEFT);
            card.getStyleClass().add(JfxStyles.NOTIFICATION_CARD);
            // M19.44 红线#1 修复：用 setMinWidth/setMaxWidth 替代 setStyle
            double w = parsePx(width);
            card.setMinWidth(w);
            card.setMaxWidth(w);

            HBox headerBox = new HBox();
            headerBox.getStyleClass().add(JfxStyles.NOTIFICATION_CARD_HEADER);
            headerBox.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(headerBox, Priority.ALWAYS);

            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(type));
            icon.getStyleClass().add(getIconStyleClass(type));
            icon.setTranslateY(2);
            headerBox.getChildren().add(icon);

            VBox contentBox = new VBox();
            contentBox.getStyleClass().add(JfxStyles.NOTIFICATION_CARD_CONTENT);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(JfxStyles.NOTIFICATION_CARD_TITLE);
                contentBox.getChildren().add(titleLabel);
            }

            if (!description.isEmpty()) {
                Label descLabel = new Label(description);
                descLabel.getStyleClass().add(JfxStyles.NOTIFICATION_CARD_DESC);
                descLabel.setWrapText(true);
                contentBox.getChildren().add(descLabel);
            }

            if (extra != null) {
                contentBox.getChildren().add(extra);
            }

            headerBox.getChildren().add(contentBox);

            // 修 Bug（M19.51）：原条件 if (closable && onClose != null) 要求两条件同时为真,
            // 但 NotificationAnt 透传时 onClose 可能为 null,导致 X 按钮永远不渲染。
            // 改为单条件 closable —— X 按钮仅由 closable 控制,onClose 可为 null(只关闭不回调)。
            if (closable) {
                CloseButton closeBtn = new CloseButton(onClose);
                headerBox.getChildren().add(closeBtn);
            }

            card.getChildren().add(headerBox);

            return card;
        }

        /** M19.44 解析 "384px" 格式的宽度字符串为 double。 */
        private static double parsePx(String value) {
            if (value == null || value.isEmpty()) return 384;
            String s = value.endsWith("px") ? value.substring(0, value.length() - 2) : value;
            return Double.parseDouble(s);
        }

        private String getIconPath(Type type) {
            return switch (type) {
                case SUCCESS -> IconPath.ICON_SUCCESS;
                case ERROR -> IconPath.ICON_ERROR;
                case WARNING -> IconPath.WARNING_TRIANGLE;
                case INFO -> IconPath.ICON_INFO;
            };
        }

        private String getIconStyleClass(Type type) {
            return switch (type) {
                case SUCCESS -> JfxStyles.ICON_SUCCESS;
                case ERROR -> JfxStyles.ICON_DANGER;
                case WARNING -> JfxStyles.ICON_WARNING;
                case INFO -> JfxStyles.ICON_INFO;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
