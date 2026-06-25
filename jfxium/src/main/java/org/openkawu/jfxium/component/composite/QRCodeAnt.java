package org.openkawu.jfxium.component.composite;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.util.TextUtils;

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
    private static final String CONTROLLER_KEY = QRCodeAnt.class.getName() + ".controller";

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String value = "";
        private int size = 160;
        private Color color = Color.BLACK;
        private Color bgColor = Color.WHITE;
        private String icon = null;
        private boolean bordered = true;

        public Builder value(String value) {
            this.value = TextUtils.safeText(value);
            return this;
        }

        public Builder size(int size) {
            this.size = size > 0 ? size : 160;
            return this;
        }

        public Builder color(Color color) {
            this.color = color != null ? color : Color.BLACK;
            return this;
        }

        public Builder bgColor(Color bgColor) {
            this.bgColor = bgColor != null ? bgColor : Color.WHITE;
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
            Color resolvedBgColor = bgColor != null ? bgColor : Color.WHITE;
            Color resolvedColor = color != null ? color : Color.BLACK;

            if (!resolvedBgColor.equals(Color.WHITE)) {
                container.setBackground(new Background(new BackgroundFill(
                        resolvedBgColor,
                        CornerRadii.EMPTY,
                        javafx.geometry.Insets.EMPTY)));
            }
            if (bordered) {
                container.getStyleClass().add(JfxStyles.QR_CODE_BORDERED);
            }

            // Create canvas for QR code simulation
            Canvas canvas = new Canvas(size, size);
            drawCode(canvas, value, resolvedColor, resolvedBgColor);

            container.getChildren().add(canvas);
            container.getProperties().put(CONTROLLER_KEY,
                    new Controller(canvas, normalizeValue(value), resolvedColor, resolvedBgColor));
            applyStyles(container);
            return container;
        }
    }

    public static Controller controllerOf(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("QRCodeAnt.controllerOf(node) 的 node 不能为 null");
        }
        Object controller = node.getProperties().get(CONTROLLER_KEY);
        if (controller instanceof Controller qrController) {
            return qrController;
        }
        throw new IllegalArgumentException("node 不是 QRCodeAnt.build() 返回的二维码组件");
    }

    public static class Controller {
        private final Canvas canvas;
        private String value;
        private Color color;
        private Color bgColor;

        private Controller(Canvas canvas, String value, Color color, Color bgColor) {
            this.canvas = canvas;
            this.value = value;
            this.color = color;
            this.bgColor = bgColor;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = normalizeValue(value);
            drawCode(canvas, this.value, color, bgColor);
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color != null ? color : Color.BLACK;
            drawCode(canvas, value, this.color, bgColor);
        }

        public Color getBgColor() {
            return bgColor;
        }

        public void setBgColor(Color bgColor) {
            this.bgColor = bgColor != null ? bgColor : Color.WHITE;
            drawCode(canvas, value, color, this.bgColor);
        }

        public int getSize() {
            return (int) Math.round(canvas.getWidth());
        }

        public void setSize(int size) {
            canvas.setWidth(size);
            canvas.setHeight(size);
            drawCode(canvas, value, color, bgColor);
        }
    }

    public static Builder create() {
        return new Builder();
    }

    private static void drawCode(Canvas canvas, String value, Color color, Color bgColor) {
        int size = (int) Math.round(canvas.getWidth());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(bgColor);
        gc.fillRect(0, 0, size, size);

        int cells = 25;
        double cellSize = (double) size / cells;
        int seed = normalizeValue(value).hashCode();

        gc.setFill(color);
        drawFinderPattern(gc, bgColor, color, 0, 0, cellSize);
        drawFinderPattern(gc, bgColor, color, cells - 7, 0, cellSize);
        drawFinderPattern(gc, bgColor, color, 0, cells - 7, cellSize);

        java.util.Random random = new java.util.Random(seed);
        for (int row = 0; row < cells; row++) {
            for (int col = 0; col < cells; col++) {
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
    }

    private static void drawFinderPattern(GraphicsContext gc, Color bgColor, Color color,
                                          int startRow, int startCol, double cellSize) {
        gc.fillRect(startCol * cellSize, startRow * cellSize, 7 * cellSize, 7 * cellSize);

        gc.setFill(bgColor);
        gc.fillRect((startCol + 1) * cellSize, (startRow + 1) * cellSize, 5 * cellSize, 5 * cellSize);

        gc.setFill(color);
        gc.fillRect((startCol + 2) * cellSize, (startRow + 2) * cellSize, 3 * cellSize, 3 * cellSize);
    }

    private static String normalizeValue(String value) {
        return TextUtils.safeText(value);
    }
}
