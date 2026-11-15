package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.style.JfxStyles;

/**
 * 内部基础组件：气泡卡片面板（标题 + 内容）。
 *
 * <p>供 {@link org.openkawu.jfxium.component.overlay.PopoverAnt} 使用，
 * 在目标元素旁弹出，支持 CLICK / HOVER 触发。
 * 关闭靠点击外部（Popup.setAutoHide=true），不对齐 Ant Design 的内置 X 按钮。</p>
 */
public class PopoverPanel {

    /**
     * 面板容器：宽度设计值为 [minWidth, maxWidth]。
     *
     * <p><b>为什么不能直接 setPrefWidth/setMaxWidth 钳死</b>：Region 不会裁剪子节点。
     * 一旦内容的最小宽度超过 maxWidth（如 500px 宽的表格 / 长文本），VBox 仍会按
     * 子节点的最小宽度排布，内容就画到气泡边框之外（实测右溢 333px）。
     * 因此这里让「设计宽度」退让给「内容最小宽度」：内容压不下去时宁可气泡变宽，
     * 也不让内容出框；内容可压缩时（wrapText 文本）仍保持设计宽度。</p>
     */
    private static final class Panel extends VBox {
        private final double designWidth;
        private final double designMaxWidth;

        private Panel(double designWidth, double designMaxWidth) {
            super(0);
            this.designWidth = designWidth;
            this.designMaxWidth = designMaxWidth;
        }

        @Override
        protected double computePrefWidth(double height) {
            return Math.max(designWidth, super.computeMinWidth(height));
        }

        @Override
        protected double computeMaxWidth(double height) {
            return Math.max(designMaxWidth, super.computeMinWidth(height));
        }
    }

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
            // 宽度契约由 Panel 的 computePrefWidth/computeMaxWidth 表达（见 Panel 注释），
            // 不再用 setMinWidth/setMaxWidth 钉死，避免内容画到气泡边框之外。
            VBox panel = new Panel(minWidth, maxWidth);
            panel.getStyleClass().add(JfxStyles.POPOVER_PANEL);

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
