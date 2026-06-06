package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * 内部基础组件：气泡卡片面板（标题 + 内容 + 关闭按钮）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.overlay.PopoverAnt} 使用，
 * 在目标元素旁弹出，支持 CLICK / HOVER 触发。</p>
 */
public class PopoverPanel {

    public static class Builder {
        private String title = "";
        private Node content = null;
        private boolean closable = false;
        private Runnable onClose = null;
        private String minWidth = "200px";
        private String maxWidth = "300px";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
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

        public Builder minWidth(String minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public Builder maxWidth(String maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public VBox build() {
            VBox panel = new VBox(0);
            panel.getStyleClass().add(CssClasses.POPOVER_PANEL);
            panel.setStyle("-fx-min-width: " + minWidth + ";-fx-max-width: " + maxWidth + ";");

            if (!title.isEmpty()) {
                VBox titleBox = new VBox(0);
                titleBox.setAlignment(Pos.CENTER_LEFT);
                titleBox.getStyleClass().add(CssClasses.POPOVER_TITLE_BOX);

                if (closable && onClose != null) {
                    HBox titleRow = new HBox();
                    titleRow.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(titleRow, Priority.ALWAYS);

                    Label titleLabel = new Label(title);
                    titleLabel.getStyleClass().add(CssClasses.POPOVER_TITLE_LABEL);
                    HBox.setHgrow(titleLabel, Priority.ALWAYS);
                    titleRow.getChildren().add(titleLabel);

                    CloseButton closeBtn = new CloseButton(onClose);
                    titleRow.getChildren().add(closeBtn);
                    titleBox.getChildren().add(titleRow);
                } else {
                    Label titleLabel = new Label(title);
                    titleLabel.getStyleClass().add(CssClasses.POPOVER_TITLE_LABEL);
                    titleBox.getChildren().add(titleLabel);
                }
                panel.getChildren().add(titleBox);
            }

            if (content != null) {
                VBox contentBox = new VBox(content);
                contentBox.setStyle("-fx-padding: 12px 16px;");
                panel.getChildren().add(contentBox);
            }

            return panel;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
