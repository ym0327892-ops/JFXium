package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * 面板头部基础组件
 * 微型化设计：只负责头部布局（标题 + extra + 关闭按钮）
 * 可组合到 Modal、Drawer 等组件中
 * 
 * 使用示例：
 * <pre>{@code
 * HBox header = PanelHeader.create()
 *     .title("Modal Title")
 *     .extra(someNode)
 *     .onClose(() -> modal.close())
 *     .build();
 * }</pre>
 */
public class PanelHeader {
    
    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String title = "";
        private Node extra = null;
        private Runnable onClose = null;
        private String padding = "16px 24px";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder onClose(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder padding(String padding) {
            this.padding = padding;
            return this;
        }

        public HBox build() {
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setStyle("-fx-padding: " + padding + "; " +
                           "-fx-border-color: transparent transparent -color-border-muted transparent; " +
                           "-fx-border-width: 0 0 1px 0;");

            // Title - 左侧
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: -color-fg-default;");
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            header.getChildren().add(titleLabel);

            // Extra - 中间
            if (extra != null) {
                header.getChildren().add(extra);
            }

            // Close Button - 右侧
            if (onClose != null) {
                javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("×");
                closeBtn.setStyle("-fx-background-color: transparent; " +
                                 "-fx-text-fill: -color-fg-muted; " +
                                 "-fx-font-size: 20px; " +
                                 "-fx-cursor: hand; " +
                                 "-fx-padding: 0 4px;");
                closeBtn.setOnAction(e -> onClose.run());
                header.getChildren().add(closeBtn);
            }

            return header;
        }
    }
}
