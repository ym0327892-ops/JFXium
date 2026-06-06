package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * 内部基础组件：消息提示卡片（图标 + 文本）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.overlay.MessageAnt} 使用，
 * 作为 TOP / BOTTOM / CENTER 位置的短暂提示 UI。</p>
 */
public class MessageCard {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO, LOADING
    }

    public static class Builder {
        private String content = "";
        private Type type = Type.INFO;
        private boolean closable = false;
        private Runnable onClose = null;

        public Builder content(String content) {
            this.content = content;
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

        public HBox build() {
            HBox card = new HBox(8);
            card.setAlignment(Pos.CENTER);
            card.getStyleClass().add(CssClasses.MESSAGE_CARD);

            if (type != Type.LOADING) {
                SVGPath icon = new SVGPath();
                icon.setContent(getIconPath(type));
                icon.setStyle("-fx-fill: " + getIconColor(type) + ";");
                card.getChildren().add(icon);
            }

            Label contentLabel = new Label(content);
            contentLabel.getStyleClass().add(CssClasses.MESSAGE_CARD_CONTENT);
            card.getChildren().add(contentLabel);

            if (closable && onClose != null) {
                CloseButton closeBtn = new CloseButton(onClose);
                card.getChildren().add(closeBtn);
            }

            return card;
        }

        private String getIconPath(Type type) {
            return switch (type) {
                case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
                case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
                case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
                case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
                case LOADING -> "M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z";
            };
        }

        private String getIconColor(Type type) {
            return switch (type) {
                case SUCCESS -> "-color-success-emphasis";
                case ERROR -> "-color-danger-emphasis";
                case WARNING -> "-color-warning-emphasis";
                case INFO -> "-color-accent-emphasis";
                case LOADING -> "-color-accent-emphasis";
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
