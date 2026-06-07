package org.openkawu.jfxium.probe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.control.MenuBarAnt;

import java.io.File;
import javax.imageio.ImageIO;

/**
 * BUG #67 探针：实测 MenuBarAnt 当前 padding 下的实际像素高度。
 *
 * 用法：
 *   1) mvn install -pl jfxium -DskipTests
 *   2) cd jfxium-demo && mvn compile
 *   3) java -Dprism.order=sw -Dprism.text=t2k \
 *          -Dmonocle.platform=Headless \
 *          -cp "target/classes:../jfxium/target/classes:..." \
 *          org.openkawu.jfxium.probe.MenuBarProbe
 *
 * 输出：
 *   - 控制台打印 fontMetricsHeight / paddingTopY / paddingBottomY / totalHeight / 截图
 *   - 写 PNG 到 /tmp/menubar-probe.png
 */
public class MenuBarProbe extends Application {

    private static final double SCENE_W = 800;
    private static final double SCENE_H = 200;

    @Override
    public void start(Stage stage) {
        Font font = Font.getDefault();
        double fontPx = font.getSize();
        double fontLineHeight = computeLineHeight(font);
        System.out.println("=== FONT METRICS ===");
        System.out.println("  default font name: " + font.getName());
        System.out.println("  default font size: " + fontPx + "px");
        System.out.println("  estimated line height (ascent+descent): " + fontLineHeight + "px");

        // 构造 4 个顶级菜单的 MenuBarAnt
        MenuBarAnt menuBar = MenuBarAnt.create()
            .menu("File")
                .item("New File", () -> {})
                .item("Open...", () -> {})
                .divider()
                .item("Save", () -> {})
                .endMenu()
            .menu("Edit")
                .item("Undo", () -> {})
                .endMenu()
            .menu("View")
                .item("Toggle Terminal", () -> {})
                .endMenu()
            .menu("Help")
                .item("About", () -> {})
                .endMenu();

        // 用一个 VBox 包裹，背景白色，方便像素检测
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(menuBar);
        root.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(root, SCENE_W, SCENE_H);
        scene.getStylesheets().add(getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // 异步执行（让布局完成）
        Platform.runLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}

            // 取 menuBar 的 Bounds
            double mbHeight = menuBar.getBoundsInLocal().getHeight();
            double mbWidth = menuBar.getBoundsInLocal().getWidth();
            System.out.println();
            System.out.println("=== MENU BAR LAYOUT ===");
            System.out.println("  menuBar.getHeight() = " + mbHeight + "px");
            System.out.println("  menuBar.getWidth()  = " + mbWidth + "px");

            // 渲染 menuBar 自身 + 一条白边包围框
            WritableImage image = menuBar.snapshot(null, null);
            int w = (int) image.getWidth();
            int h = (int) image.getHeight();
            System.out.println("  snapshot size = " + w + " x " + h + "px");

            // 用 PixelReader 找实际内容高度（top / bottom 非透明像素位置）
            PixelReader pr = image.getPixelReader();
            int firstOpaqueY = -1, lastOpaqueY = -1;
            int firstOpaqueX = -1, lastOpaqueX = -1;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = pr.getColor(x, y);
                    if (c.getOpacity() > 0.01 && (c.getRed() < 0.99 || c.getGreen() < 0.99 || c.getBlue() < 0.99)) {
                        if (firstOpaqueY < 0) firstOpaqueY = y;
                        lastOpaqueY = y;
                        if (firstOpaqueX < 0 || x < firstOpaqueX) firstOpaqueX = x;
                        if (lastOpaqueX < x) lastOpaqueX = x;
                    }
                }
            }
            System.out.println();
            System.out.println("=== PIXEL ANALYSIS (snapshot 内非白非透明像素) ===");
            if (firstOpaqueY >= 0) {
                System.out.println("  Y range: " + firstOpaqueY + " .. " + lastOpaqueY
                    + "  (content height = " + (lastOpaqueY - firstOpaqueY + 1) + "px)");
                System.out.println("  X range: " + firstOpaqueX + " .. " + lastOpaqueX
                    + "  (content width  = " + (lastOpaqueX - firstOpaqueX + 1) + "px)");
            } else {
                System.out.println("  (no opaque non-white pixels found)");
            }

            // 写 PNG 给人工查看
            try {
                File out = new File("/tmp/menubar-probe.png");
                ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image, null), "png", out);
                System.out.println();
                System.out.println("=== SCREENSHOT ===");
                System.out.println("  Wrote: " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
            } catch (Exception ex) {
                System.out.println("  (failed to write screenshot: " + ex.getMessage() + ")");
            }

            System.out.println();
            System.out.println("=== DIAGNOSIS ===");
            System.out.println("  JavaFX MenuBarBar layout height: " + mbHeight + "px");
            System.out.println("  if ~25-30px → IDE 风格 ✅");
            System.out.println("  if > 32px    → 仍然太厚 ⚠️");

            Platform.exit();
        });
    }

    private double computeLineHeight(Font font) {
        // 用 X 字符测：boundsInLocal 包含 ascent + descent
        javafx.scene.text.Text sample = new javafx.scene.text.Text("File");
        sample.setFont(font);
        return sample.getBoundsInLocal().getHeight();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
