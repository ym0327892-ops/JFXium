package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * 内部基础组件：气泡卡片面板（标题 + 内容）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.overlay.PopoverAnt} 使用，
 * 在目标元素旁弹出，支持 CLICK / HOVER 触发。
 * 关闭靠点击外部（Popup.setAutoHide=true），不对齐 Ant Design 的内置 X 按钮。</p>
 */
public class PopoverPanel {

    public static class Builder {
        private String title = "";
        private Node content = null;
        private double minWidth = 200;
        private double maxWidth = 300;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder minWidth(double minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public Builder maxWidth(double maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public VBox build() {
            VBox panel = new VBox(0);
            panel.getStyleClass().add(JfxStyles.POPOVER_PANEL);
            // M19.44+ 红线#1 修复：用尺寸 API 替代 inline CSS 宽度。
            panel.setMinWidth(minWidth);
            panel.setMaxWidth(maxWidth);
            panel.setPrefWidth(minWidth);

            if (!title.isEmpty()) {
                VBox titleBox = new VBox(0);
                titleBox.setAlignment(Pos.CENTER_LEFT);
                titleBox.getStyleClass().add(JfxStyles.POPOVER_TITLE_BOX);

                Label titleLabel = new Label(title);
                titleLabel.getStyleClass().add(JfxStyles.POPOVER_TITLE_LABEL);
                titleBox.getChildren().add(titleLabel);
                panel.getChildren().add(titleBox);
            }

            if (content != null) {
                VBox contentBox = new VBox(content);
                contentBox.getStyleClass().add(JfxStyles.POPOVER_CONTENT);
                panel.getChildren().add(contentBox);
            }

            return panel;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
