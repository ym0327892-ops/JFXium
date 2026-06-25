package org.openkawu.jfxium.component.base;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

/**
 * 面板头部基础组件
 * 微型化设计：只负责头部布局（标题 + extra + 关闭按钮）
 * 可组合到 Modal、Drawer 等组件中
 *
 * <p>样式全部走 CSS 类（{@link JfxStyles}），支持主题切换覆盖。</p>
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
        private Insets padding = new Insets(16, 24, 16, 24);

        public Builder title(String title) {
            this.title = title != null ? title : "";
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

        /**
         * 设置内边距（四边独立）。默认 16px 24px。
         */
        public Builder padding(double top, double right, double bottom, double left) {
            this.padding = new Insets(TextUtils.safeNonNegative(top, 0), TextUtils.safeNonNegative(right, 0), TextUtils.safeNonNegative(bottom, 0), TextUtils.safeNonNegative(left, 0));
            return this;
        }

        public HBox build() {
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            header.getStyleClass().add(JfxStyles.PANEL_HEADER);
            header.setPadding(padding);

            // Title - 左侧
            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add(JfxStyles.PANEL_TITLE);
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            header.getChildren().add(titleLabel);

            // Extra - 中间
            if (extra != null) {
                header.getChildren().add(extra);
            }

            // Close Button - 右侧
            if (onClose != null) {
                CloseButton closeBtn = CloseButton.create(onClose);
                closeBtn.getStyleClass().add(JfxStyles.PANEL_CLOSE_BTN);
                header.getChildren().add(closeBtn);
            }

            return header;
        }

        // safeSize 统一改用 TextUtils.safeNonNegative,见 P0-23。
    }
}
