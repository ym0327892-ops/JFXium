package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.css.JfxStyles;

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
            VBox card = new VBox(12);
            card.setAlignment(Pos.TOP_LEFT);
            card.getStyleClass().add(JfxStyles.NOTIFICATION_CARD);
            // M19.44 红线#1 修复：用 setMinWidth/setMaxWidth 替代 setStyle
            double w = parsePx(width);
            card.setMinWidth(w);
            card.setMaxWidth(w);

            HBox headerBox = new HBox(12);
            headerBox.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(headerBox, Priority.ALWAYS);

            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(type));
            icon.getStyleClass().add(getIconStyleClass(type));
            icon.setTranslateY(2);
            headerBox.getChildren().add(icon);

            VBox contentBox = new VBox(4);
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

            if (closable && onClose != null) {
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
                case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
                case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
                case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
                case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
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
