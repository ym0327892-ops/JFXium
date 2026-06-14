package org.openkawu.jfxium.component.base;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * 面板底部基础组件
 * 微型化设计：只负责底部布局（左内容 + 右按钮组）
 * 可组合到 Modal、Drawer 等组件中
 * 
 * 使用示例：
 * <pre>{@code
 * HBox footer = PanelFooter.create()
 *     .right(ButtonAnt.create("OK").type(ButtonAnt.Type.PRIMARY).build())
 *     .alignment(Pos.CENTER_RIGHT)
 *     .hasBorder(true)
 *     .build();
 * }</pre>
 */
public class PanelFooter {
    
    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Node left = null;
        private Node right = null;
        private Insets padding = null;
        private boolean hasBorder = true;
        private Pos alignment = Pos.CENTER_RIGHT;

        public Builder left(Node left) {
            this.left = left;
            return this;
        }

        public Builder right(Node right) {
            this.right = right;
            return this;
        }

        public Builder padding(Insets padding) {
            this.padding = padding;
            return this;
        }

        public Builder padding(String padding) {
            this.padding = parsePadding(padding);
            return this;
        }

        public Builder hasBorder(boolean hasBorder) {
            this.hasBorder = hasBorder;
            return this;
        }

        public Builder alignment(Pos alignment) {
            this.alignment = alignment;
            return this;
        }

        public HBox build() {
            HBox footer = new HBox(8);
            footer.setAlignment(alignment);
            footer.getStyleClass().add(JfxStyles.PANEL_FOOTER);
            if (hasBorder) {
                footer.getStyleClass().add(JfxStyles.PANEL_FOOTER_BORDERED);
            }
            if (padding != null) {
                footer.setPadding(padding);
            }

            if (left != null) {
                HBox leftBox = new HBox(left);
                javafx.scene.layout.HBox.setHgrow(leftBox, javafx.scene.layout.Priority.ALWAYS);
                footer.getChildren().add(leftBox);
            }

            if (right != null) {
                footer.getChildren().add(right);
            }

            return footer;
        }

        private Insets parsePadding(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            String[] parts = value.trim().split("\\s+");
            if (parts.length < 1 || parts.length > 4) {
                throw new IllegalArgumentException("padding 仅支持 1 到 4 个值: " + value);
            }
            double[] values = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                values[i] = parseCssLength(parts[i]);
            }
            return switch (values.length) {
                case 1 -> new Insets(values[0]);
                case 2 -> new Insets(values[0], values[1], values[0], values[1]);
                case 3 -> new Insets(values[0], values[1], values[2], values[1]);
                case 4 -> new Insets(values[0], values[1], values[2], values[3]);
                default -> throw new IllegalStateException("Unexpected padding length: " + values.length);
            };
        }

        private double parseCssLength(String token) {
            String normalized = token.trim().toLowerCase();
            if (normalized.endsWith("px")) {
                normalized = normalized.substring(0, normalized.length() - 2);
            }
            return Double.parseDouble(normalized);
        }
    }
}
