package org.openkawu.jfxium.component.composite;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.function.Supplier;

/**
 * WatermarkAnt - 对标 Ant Design / Element Plus Watermark。
 *
 * <h2>实现哲学（M16 重写）</h2>
 * <p>对齐 Element Plus 标准做法：</p>
 * <ol>
 *   <li><b>1 张 Canvas snapshot 成 Image</b>，不再循环创建 80+ 个 Label 节点</li>
 *   <li><b>对角错位平铺图块</b>：在 fCanvas 上画 3 次形成"无网格感"的 tile</li>
 *   <li><b>Region.background = BackgroundImage(REPEAT)</b>：JavaFX 原生平铺，零开销</li>
 *   <li><b>DPR 适配</b>：高分屏（Retina）2x/3x 像素清晰</li>
 * </ol>
 *
 * <h2>性能对比</h2>
 * <table border="1" summary="对比">
 *   <tr><th></th><th>旧实现</th><th>新实现（M16）</th></tr>
 *   <tr><td>节点数</td><td>80+ 个 Label</td><td>1 个 Region + 1 张 Image</td></tr>
 *   <tr><td>滚动/缩放</td><td>每次重建 80+ 节点</td><td>零开销（CSS 自动平铺）</td></tr>
 *   <tr><td>错位效果</td><td>❌ 网格对齐</td><td>✅ 对角错位（自然）</td></tr>
 *   <tr><td>DPR 高分屏</td><td>❌ 模糊</td><td>✅ 清晰</td></tr>
 * </table>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * StackPane wm = WatermarkAnt.create()
 *     .content(myContent)
 *     .text("机密文档")
 *     .build();
 * }</pre>
 *
 * @since 1.0
 */
public class WatermarkAnt {

    public static class Builder extends AbstractStyleBuilder<Builder> {
        // ============================================================
        // 输入参数
        // ============================================================
        private Node content;
        private String[] textLines;
        private String imagePath;
        private Supplier<Node> customNodeSupplier;
        private double rotate = -22;
        private double opacity = 0.15;
        private double fontSize = 16;
        private double gapX = 100;
        private double gapY = 100;
        private double imageWidth = 120;
        private double imageHeight = 64;
        private boolean preventRemoval = false;
        private Color textColor = null;            // null 时用 -color-fg-default
        private String fontFamily = "System";
        private FontWeight fontWeight = FontWeight.NORMAL;
        /** 多行文字行间距（默认 3）。 */
        private double fontGap = 3;

        // ============================================================
        // 内容设置
        // ============================================================
        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder text(String text) {
            this.textLines = new String[]{text};
            this.imagePath = null;
            this.customNodeSupplier = null;
            return this;
        }

        public Builder text(String... lines) {
            this.textLines = lines;
            this.imagePath = null;
            this.customNodeSupplier = null;
            return this;
        }

        public Builder image(String imagePath) {
            this.imagePath = imagePath;
            this.textLines = null;
            this.customNodeSupplier = null;
            return this;
        }

        public Builder customNode(Supplier<Node> nodeSupplier) {
            this.customNodeSupplier = nodeSupplier;
            this.textLines = null;
            this.imagePath = null;
            return this;
        }

        // ============================================================
        // 样式设置
        // ============================================================
        public Builder rotate(double rotate) { this.rotate = rotate; return this; }

        public Builder opacity(double opacity) {
            this.opacity = Math.max(0, Math.min(1, opacity));
            return this;
        }

        public Builder fontSize(double fontSize) { this.fontSize = fontSize; return this; }
        public Builder gapX(double gapX) { this.gapX = gapX; return this; }
        public Builder gapY(double gapY) { this.gapY = gapY; return this; }
        public Builder imageWidth(double width) { this.imageWidth = width; return this; }
        public Builder imageHeight(double height) { this.imageHeight = height; return this; }
        public Builder color(Color color) { this.textColor = color; return this; }
        public Builder fontFamily(String fontFamily) { this.fontFamily = fontFamily; return this; }
        public Builder fontWeight(FontWeight w) { this.fontWeight = w; return this; }
        public Builder fontGap(double gap) { this.fontGap = gap; return this; }

        public Builder preventRemoval(boolean preventRemoval) {
            this.preventRemoval = preventRemoval;
            return this;
        }
        public Builder preventRemoval() { return preventRemoval(true); }

        // ============================================================
        // 构建
        // ============================================================
        public StackPane build() {
            if (content == null) {
                throw new IllegalStateException("content 不能为空，请调用 content() 方法设置内容节点");
            }

            StackPane root = new StackPane();
            root.getStyleClass().add(CssClasses.WATERMARK);
            applyStyles(root);

            // 1. 内容层（底层）
            root.getChildren().add(content);

            // 2. 水印层（顶层）：仅 1 个 Region + BackgroundImage 平铺
            Region watermarkLayer = createWatermarkLayer();
            watermarkLayer.setMouseTransparent(true);
            root.getChildren().add(watermarkLayer);

            // 3. 防删除保护
            if (preventRemoval) {
                enableRemovalProtection(root, watermarkLayer);
            }

            return root;
        }

        /**
         * 创建水印层：生成 tile Image → 设为 Region 的 BackgroundImage（REPEAT 平铺）。
         */
        private Region createWatermarkLayer() {
            Region layer = new Region();
            layer.getStyleClass().add(CssClasses.WATERMARK_LAYER);

            // 异步加载图片：图片水印需要等 Image 加载完成才能 snapshot
            if (imagePath != null) {
                Image img = new Image(imagePath, imageWidth, imageHeight, true, true, true);
                if (img.isBackgroundLoading()) {
                    img.progressProperty().addListener((obs, oldP, newP) -> {
                        if (newP.doubleValue() >= 1.0) {
                            applyTileBackground(layer, img);
                        }
                    });
                    if (img.getProgress() >= 1.0) {
                        applyTileBackground(layer, img);
                    }
                } else {
                    applyTileBackground(layer, img);
                }
            } else {
                applyTileBackground(layer, null);
            }

            return layer;
        }

        /**
         * 生成水印 tile 并设为 layer 的背景。
         *
         * @param layer    水印层 Region
         * @param imgOrNull 图片水印对应的 Image（可为 null）
         */
        private void applyTileBackground(Region layer, Image imgOrNull) {
            WritableImage tile = renderTile(imgOrNull);
            if (tile == null) return;

            BackgroundImage bg = new BackgroundImage(
                    tile,
                    BackgroundRepeat.REPEAT,
                    BackgroundRepeat.REPEAT,
                    BackgroundPosition.DEFAULT,
                    // size：用真实像素，不让 JavaFX 自动缩放（DPR 高分屏关键）
                    new BackgroundSize(
                            tile.getWidth() / dpr(),
                            tile.getHeight() / dpr(),
                            false, false, false, false)
            );
            layer.setBackground(new Background(bg));
        }

        // ============================================================
        // 核心算法：对应 Element Plus useClips.ts 的 getClips()
        // ============================================================

        /**
         * 渲染水印 tile（对角错位 3 次平铺）。
         *
         * <p>对齐 Element Plus useClips.ts 实现：</p>
         * <ol>
         *   <li>contentWidth × contentHeight：原始内容（文字/图片）</li>
         *   <li>maxSize × maxSize：旋转后的工作画布</li>
         *   <li>cutWidth × cutHeight：旋转后内容的紧凑边界</li>
         *   <li>filledWidth × filledHeight：错位 3 次平铺后的最终 tile</li>
         * </ol>
         */
        private WritableImage renderTile(Image imgOrNull) {
            double ratio = dpr();

            // -------- 1. 算出 contentWidth, contentHeight --------
            double contentW;
            double contentH;
            double space = 0;

            if (imgOrNull != null) {
                contentW = imageWidth;
                contentH = imageHeight;
            } else if (customNodeSupplier != null) {
                // 自定义节点：先 snapshot 测尺寸
                Node n = customNodeSupplier.get();
                Image snap = snapshotNode(n);
                imgOrNull = snap;
                contentW = snap.getWidth() / ratio;
                contentH = snap.getHeight() / ratio;
            } else {
                String[] lines = textLines == null || textLines.length == 0
                        ? new String[]{"JFXium"} : textLines;
                double[] size = measureText(lines, fontSize, fontFamily, fontWeight, fontGap);
                contentW = size[0];
                contentH = size[1];
                // 旋转后多出来的空间（Element Plus 同款）
                double angleRad = Math.toRadians(rotate);
                space = Math.ceil(Math.abs(Math.sin(angleRad) * contentH) / 2);
                contentW += space;
            }

            if (contentW <= 0 || contentH <= 0) return null;

            // -------- 2. 画到 contentCanvas --------
            Canvas contentCanvas = new Canvas(contentW * ratio, contentH * ratio);
            GraphicsContext cctx = contentCanvas.getGraphicsContext2D();
            cctx.scale(ratio, ratio);  // DPR 适配
            cctx.setGlobalAlpha(opacity);
            if (imgOrNull != null) {
                cctx.drawImage(imgOrNull, 0, 0, contentW, contentH);
            } else {
                String[] lines = textLines;
                Color fillColor = textColor != null ? textColor : Color.web("#000000");
                cctx.setFill(fillColor);
                cctx.setFont(Font.font(fontFamily, fontWeight, fontSize));
                cctx.setTextBaseline(javafx.geometry.VPos.TOP);
                for (int i = 0; i < lines.length; i++) {
                    cctx.fillText(lines[i], 0, i * (fontSize + fontGap));
                }
            }
            WritableImage contentImg = new WritableImage(
                    (int) Math.ceil(contentCanvas.getWidth()),
                    (int) Math.ceil(contentCanvas.getHeight())
            );
            SnapshotParameters spClear = new SnapshotParameters();
            spClear.setFill(Color.TRANSPARENT);
            contentCanvas.snapshot(spClear, contentImg);

            // -------- 3. 旋转：把 contentImg 画到 maxSize × maxSize 的 rCanvas --------
            double maxSize = Math.max(contentW, contentH);
            Canvas rCanvas = new Canvas(maxSize * ratio, maxSize * ratio);
            GraphicsContext rctx = rCanvas.getGraphicsContext2D();
            rctx.scale(ratio, ratio);
            rctx.translate(maxSize / 2, maxSize / 2);
            rctx.rotate(rotate);
            if (contentImg.getWidth() > 0 && contentImg.getHeight() > 0) {
                rctx.drawImage(contentImg, -contentW / 2, -contentH / 2, contentW, contentH);
            }
            WritableImage rotatedImg = new WritableImage(
                    (int) Math.ceil(rCanvas.getWidth()),
                    (int) Math.ceil(rCanvas.getHeight())
            );
            rCanvas.snapshot(spClear, rotatedImg);

            // -------- 4. 算旋转后的紧凑边界（cutLeft/cutTop/cutWidth/cutHeight）--------
            double angle = Math.toRadians(rotate);
            double halfW = contentW / 2;
            double halfH = contentH / 2;
            double[][] points = {
                    {-halfW, -halfH},
                    { halfW, -halfH},
                    { halfW,  halfH},
                    {-halfW,  halfH}
            };
            double left = 0, right = 0, top = 0, bottom = 0;
            for (double[] p : points) {
                double tx = p[0] * Math.cos(angle) - p[1] * Math.sin(angle);
                double ty = p[0] * Math.sin(angle) + p[1] * Math.cos(angle);
                left = Math.min(left, tx);
                right = Math.max(right, tx);
                top = Math.min(top, ty);
                bottom = Math.max(bottom, ty);
            }
            double cutLeft = left + maxSize / 2;
            double cutTop = top + maxSize / 2;
            double cutWidth = right - left;
            double cutHeight = bottom - top;

            // -------- 5. 错位 3 次画到 fCanvas（最终 tile）--------
            double filledW = (cutWidth + gapX) * 2;
            double filledH = cutHeight + gapY;

            Canvas fCanvas = new Canvas(filledW * ratio, filledH * ratio);
            GraphicsContext fctx = fCanvas.getGraphicsContext2D();
            fctx.scale(ratio, ratio);

            // 主块
            drawClip(fctx, rotatedImg, cutLeft, cutTop, cutWidth, cutHeight, 0, 0, ratio);
            // 错位上
            drawClip(fctx, rotatedImg, cutLeft, cutTop, cutWidth, cutHeight,
                    cutWidth + gapX, -cutHeight / 2 - gapY / 2, ratio);
            // 错位下
            drawClip(fctx, rotatedImg, cutLeft, cutTop, cutWidth, cutHeight,
                    cutWidth + gapX, +cutHeight / 2 + gapY / 2, ratio);

            WritableImage finalImg = new WritableImage(
                    (int) Math.ceil(fCanvas.getWidth()),
                    (int) Math.ceil(fCanvas.getHeight())
            );
            fCanvas.snapshot(spClear, finalImg);
            return finalImg;
        }

        /** 从 rotatedImg 裁剪指定区域，画到 fCanvas 的 (targetX, targetY) 位置。 */
        private void drawClip(GraphicsContext fctx, Image src,
                              double sx, double sy, double sw, double sh,
                              double dx, double dy, double ratio) {
            // src 是 ratio 倍的 image，源坐标也要 *ratio
            fctx.drawImage(src,
                    sx * ratio, sy * ratio, sw * ratio, sh * ratio,
                    dx, dy, sw, sh);
        }

        // ============================================================
        // 工具方法
        // ============================================================

        /** 测量多行文字的最大宽 + 总高（不入场景，用 Text.getLayoutBounds）。 */
        private static double[] measureText(String[] lines, double fontSize,
                                            String family, FontWeight weight,
                                            double lineGap) {
            Font font = Font.font(family, weight, fontSize);
            double maxW = 0;
            double maxH = 0;
            for (String line : lines) {
                Text t = new Text(line == null ? "" : line);
                t.setFont(font);
                Bounds b = t.getLayoutBounds();
                maxW = Math.max(maxW, b.getWidth());
                maxH = Math.max(maxH, b.getHeight());
            }
            double totalH = maxH * lines.length + (lines.length - 1) * lineGap;
            return new double[]{maxW, totalH};
        }

        /** Snapshot 任意 Node 成 Image（用于 customNode 水印）。 */
        private Image snapshotNode(Node n) {
            // 强制 layout：customNode 创建后未入场景，bounds 是 0
            n.applyCss();
            if (n instanceof javafx.scene.Parent p) {
                p.layout();
            }
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            return n.snapshot(sp, null);
        }

        /** 设备像素比（高分屏适配，Retina 通常返回 2.0）。 */
        private static double dpr() {
            try {
                return Screen.getPrimary().getOutputScaleX();
            } catch (Exception ex) {
                return 1.0;
            }
        }

        /** 防删除保护：监听 root.children 变化，水印层被移除则恢复。 */
        private void enableRemovalProtection(StackPane root, Region watermarkLayer) {
            root.getChildren().addListener(
                    (javafx.collections.ListChangeListener<Node>) change -> {
                        while (change.next()) {
                            if (change.wasRemoved() && change.getRemoved().contains(watermarkLayer)) {
                                javafx.application.Platform.runLater(() -> {
                                    if (!root.getChildren().contains(watermarkLayer)) {
                                        root.getChildren().add(watermarkLayer);
                                        System.out.println("[WatermarkAnt] 水印层被移除，已自动恢复");
                                    }
                                });
                            }
                        }
                    });
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
