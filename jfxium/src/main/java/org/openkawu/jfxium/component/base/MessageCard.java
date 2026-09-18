package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.core.style.JfxStyles;
import org.openkawu.jfxium.core.util.IconPath;

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
            HBox card = new HBox();
            card.setAlignment(Pos.CENTER);
            card.getStyleClass().add(JfxStyles.MESSAGE_CARD);

            if (type != Type.LOADING) {
                SVGPath icon = new SVGPath();
                icon.setContent(getIconPath(type));
                icon.getStyleClass().add(getIconStyleClass(type));
                card.getChildren().add(icon);
            }

            Label contentLabel = new Label(content);
            contentLabel.getStyleClass().add(JfxStyles.MESSAGE_CARD_CONTENT);
            card.getChildren().add(contentLabel);

            if (closable && onClose != null) {
                CloseButton closeBtn = new CloseButton(onClose);
                card.getChildren().add(closeBtn);
            }

            return card;
        }

        private String getIconPath(Type type) {
            return switch (type) {
                case SUCCESS -> IconPath.ICON_SUCCESS;
                case ERROR -> IconPath.ICON_ERROR;
                case WARNING -> IconPath.WARNING_TRIANGLE;
                case INFO -> IconPath.ICON_INFO;
                case LOADING -> IconPath.ICON_LOADING;
            };
        }

        private String getIconStyleClass(Type type) {
            return switch (type) {
                case SUCCESS -> JfxStyles.ICON_SUCCESS;
                case ERROR -> JfxStyles.ICON_DANGER;
                case WARNING -> JfxStyles.ICON_WARNING;
                case INFO, LOADING -> JfxStyles.ICON_INFO;
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
