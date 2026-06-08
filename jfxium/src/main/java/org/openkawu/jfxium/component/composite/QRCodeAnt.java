package org.openkawu.jfxium.component.composite;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

/**
 * JFXium 二维码组件 - 对标 Ant Design QRCode
 *
 * 生成二维码图像（简化实现，使用 Canvas 绘制模拟二维码）
 *
 * 使用示例：
 * <pre>{@code
 * // 基础二维码
 * StackPane qrCode = QRCodeAnt.create()
 *     .value("https://example.com")
 *     .size(160)
 *     .build();
 *
 * // 带颜色的二维码
 * StackPane qrCode = QRCodeAnt.create()
 *     .value("Hello World")
 *     .size(200)
 *     .color(Color.BLACK)
 *     .bgColor(Color.WHITE)
 *     .build();
 * }</pre>
 */
public class QRCodeAnt {

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String value = "";
        private int size = 160;
        private Color color = Color.BLACK;
        private Color bgColor = Color.WHITE;
        private String icon = null;
        private boolean bordered = true;

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder bgColor(Color bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder bordered(boolean bordered) {
            this.bordered = bordered;
            return this;
        }

        public StackPane build() {
            StackPane container = new StackPane();
            container.getStyleClass().add(JfxStyles.QR_CODE);

            // 默认背景走 .jfx-qr-code 修饰类（LESS 中 -color-bg-default），用户自定义颜色才走 setStyle
            if (bgColor != null && !bgColor.equals(Color.WHITE)) {
                container.setStyle("-fx-background-color: " + toHex(bgColor) + ";");
            }
            if (bordered) {
                container.getStyleClass().add(JfxStyles.QR_CODE_BORDERED);
            }

            // Create canvas for QR code simulation
            Canvas canvas = new Canvas(size, size);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Fill background
            gc.setFill(bgColor);
            gc.fillRect(0, 0, size, size);

            // Generate pseudo-random pattern based on value hash
            int cells = 25;
            double cellSize = (double) size / cells;
            int seed = value.hashCode();

            gc.setFill(color);

            // Draw finder patterns (corners)
            drawFinderPattern(gc, 0, 0, cellSize);
            drawFinderPattern(gc, cells - 7, 0, cellSize);
            drawFinderPattern(gc, 0, cells - 7, cellSize);

            // Draw data pattern
            java.util.Random random = new java.util.Random(seed);
            for (int row = 0; row < cells; row++) {
                for (int col = 0; col < cells; col++) {
                    // Skip finder pattern areas
                    if ((row < 7 && col < 7) ||
                        (row < 7 && col >= cells - 7) ||
                        (row >= cells - 7 && col < 7)) {
                        continue;
                    }

                    if (random.nextBoolean()) {
                        gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                    }
                }
            }

            container.getChildren().add(canvas);
            return container;
        }

        private void drawFinderPattern(GraphicsContext gc, int startRow, int startCol, double cellSize) {
            // Outer square
            gc.fillRect(startCol * cellSize, startRow * cellSize, 7 * cellSize, 7 * cellSize);

            // Inner white square
            gc.setFill(bgColor);
            gc.fillRect((startCol + 1) * cellSize, (startRow + 1) * cellSize, 5 * cellSize, 5 * cellSize);

            // Inner black square
            gc.setFill(color);
            gc.fillRect((startCol + 2) * cellSize, (startRow + 2) * cellSize, 3 * cellSize, 3 * cellSize);
        }

        private String toHex(Color color) {
            return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
