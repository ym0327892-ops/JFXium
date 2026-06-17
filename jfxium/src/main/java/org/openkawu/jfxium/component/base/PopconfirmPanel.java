package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * 内部基础组件：气泡确认面板（标题 + 描述 + 确认/取消按钮）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.overlay.PopconfirmAnt} 使用，
 * 在目标元素下方弹出，用于二次确认操作。</p>
 */
public class PopconfirmPanel {

    public static class Builder {
        private String title = "";
        private String description = "";
        private String okText = "Yes";
        private String cancelText = "No";
        private Runnable onConfirm = null;
        private Runnable onCancel = null;
        private String minWidth = "200px";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder okText(String okText) {
            this.okText = okText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder onConfirm(Runnable onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onCancel(Runnable onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public Builder minWidth(String minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public VBox build() {
            VBox panel = new VBox();
            panel.getStyleClass().add(JfxStyles.POPCONFIRM_PANEL);
            // M19.44 红线#1 修复：用尺寸 API 替代 inline CSS 宽度。
            panel.setMinWidth(parsePx(minWidth));

            HBox titleBox = new HBox();
            titleBox.setAlignment(Pos.CENTER_LEFT);

            SVGPath icon = new SVGPath();
            icon.setContent("M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z");
            icon.getStyleClass().add(JfxStyles.POPCONFIRM_ICON);
            titleBox.getChildren().add(icon);

            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add(JfxStyles.POPCONFIRM_TITLE);
            titleBox.getChildren().add(titleLabel);

            panel.getChildren().add(titleBox);

            if (!description.isEmpty()) {
                Label descLabel = new Label(description);
                descLabel.getStyleClass().add(JfxStyles.POPCONFIRM_DESC);
                descLabel.setWrapText(true);
                panel.getChildren().add(descLabel);
            }

            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button cancelBtn = ButtonAnt.create(cancelText)
                .type(ButtonAnt.Type.DEFAULT)
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    if (onCancel != null) {
                        onCancel.run();
                    }
                })
                .build();

            Button okBtn = ButtonAnt.create(okText)
                .type(ButtonAnt.Type.PRIMARY)
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .build();

            buttonBox.getChildren().addAll(cancelBtn, okBtn);
            panel.getChildren().add(buttonBox);

            return panel;
        }

        /** M19.44 解析 "200px" 格式的宽度字符串为 double。 */
        private static double parsePx(String value) {
            if (value == null || value.isEmpty()) return 200;
            String s = value.endsWith("px") ? value.substring(0, value.length() - 2) : value;
            return Double.parseDouble(s);
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
