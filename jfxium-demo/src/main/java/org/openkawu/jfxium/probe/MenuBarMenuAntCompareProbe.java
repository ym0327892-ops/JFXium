package org.openkawu.jfxium.probe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.composite.MenuAnt;
import org.openkawu.jfxium.component.control.MenuBarAnt;

import java.io.File;
import javax.imageio.ImageIO;

/**
 * BUG #68 探针：同时实测 MenuBarAnt 和 MenuAnt.horizontal 的实际像素高度，对比一致性。
 *
 * 用法：
 *   mvn install -pl jfxium -DskipTests
 *   cd jfxium-demo && mvn compile
 *   java -Dprism.order=sw -Dprism.text=t2k \
 *        -cp "target/classes:$(cat /tmp/cp.txt)" \
 *        org.openkawu.jfxium.probe.MenuBarMenuAntCompareProbe
 *
 * 输出：两者高度对比 + PNG 截图
 */
public class MenuBarMenuAntCompareProbe extends Application {

    private static final double SCENE_W = 1000;
    private static final double SCENE_H = 300;

    @Override
    public void start(Stage stage) {
        // 1) MenuBarAnt — 走 JavaFX MenuBarSkin
        MenuBarAnt menuBar = MenuBarAnt.create()
            .menu("File")
                .item("New", () -> {})
                .item("Open", () -> {})
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

        // 2) MenuAnt.horizontal — 走完全自建 HBox (setMinHeight 48, setPrefHeight 48)
        Node menuAntH = MenuAnt.create()
            .mode(MenuAnt.Mode.HORIZONTAL)
            .item("home", "首页", () -> {})
            .subMenu("products", "产品中心")
                .item("p1", "产品 A", () -> {})
                .item("p2", "产品 B", () -> {})
                .endSubMenu()
            .item("about", "关于我们", () -> {})
            .build();

        // 3) MenuAnt.inline — 反例参考（看 INLINE 是不是 40px+）
        Node menuAntV = MenuAnt.create()
            .mode(MenuAnt.Mode.INLINE)
            .item("dashboard", "首页", () -> {})
            .subMenu("user", "用户管理")
                .item("user.list", "列表", () -> {})
                .item("user.add", "新增", () -> {})
                .endSubMenu()
            .build();

        // 标签 + 组件并排
        VBox mbBox = wrapWithLabel("MenuBarAnt (JavaFX MenuBar)", menuBar);
        VBox maHBox = wrapWithLabel("MenuAnt.horizontal (HBox hard-code 48)", menuAntH);
        VBox maVBox = wrapWithLabel("MenuAnt.inline (VBox padding 10/10)", menuAntV);

        VBox root = new VBox(20, mbBox, maHBox, maVBox);
        root.setStyle("-fx-background-color: white; -fx-padding: 20;");

        Scene scene = new Scene(root, SCENE_W, SCENE_H);
        scene.getStylesheets().add(
            getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            Font font = Font.getDefault();
            System.out.println("=== FONT ===");
            System.out.println("  default size: " + font.getSize() + "px");

            System.out.println();
            System.out.println("=== HEIGHT COMPARISON ===");
            double mbH = menuBar.getBoundsInLocal().getHeight();
            double maHH = menuAntH.getBoundsInLocal().getHeight();
            double maVH = menuAntV.getBoundsInLocal().getHeight();
            System.out.printf("  MenuBarAnt:          %.2fpx  (getHeight)%n", mbH);
            System.out.printf("  MenuAnt.horizontal:  %.2fpx  (getHeight)%n", maHH);
            System.out.printf("  MenuAnt.inline:      %.2fpx  (getHeight)%n", maVH);
            System.out.println();
            System.out.printf("  diff(MB - MA.h) = %.2fpx%n", mbH - maHH);
            System.out.printf("  diff(MA.h - MA.v) = %.2fpx%n", maHH - maVH);
            System.out.println();
            System.out.println("  EXPECTED IDE range: 24-30px");
            System.out.println("  EXPECTED for inline: 36-44px (vertical comfortable)");

            // 像素扫描（Y 范围）
            scanAndPrint("MenuBarAnt", menuBar);
            scanAndPrint("MenuAnt.horizontal", menuAntH);
            scanAndPrint("MenuAnt.inline", menuAntV);

            // 写整图
            try {
                WritableImage img = root.snapshot(null, null);
                File out = new File("/tmp/menu-compare.png");
                ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(img, null), "png", out);
                System.out.println();
                System.out.println("=== SCREENSHOT ===");
                System.out.println("  Wrote: " + out.getAbsolutePath() + " (" + out.length() + " bytes)");
            } catch (Exception ex) {
                System.out.println("  screenshot failed: " + ex.getMessage());
            }

            // 单独写 MenuAnt.horizontal 截图（看按钮）
            try {
                WritableImage imgH = menuAntH.snapshot(null, null);
                File outH = new File("/tmp/menu-ant-horizontal.png");
                ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(imgH, null), "png", outH);
                System.out.println("  Wrote: " + outH.getAbsolutePath() + " (" + outH.length() + " bytes)");
            } catch (Exception ex) {
                System.out.println("  horizontal screenshot failed: " + ex.getMessage());
            }

            Platform.exit();
        });
    }

    private VBox wrapWithLabel(String label, Node child) {
        javafx.scene.control.Label l = new javafx.scene.control.Label(label);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        VBox box = new VBox(4, l, child);
        box.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 4;");
        return box;
    }

    private void scanAndPrint(String name, Node node) {
        WritableImage img = node.snapshot(null, null);
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        PixelReader pr = img.getPixelReader();
        int firstY = -1, lastY = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = pr.getColor(x, y);
                if (c.getOpacity() > 0.01 && (c.getRed() < 0.99 || c.getGreen() < 0.99 || c.getBlue() < 0.99)) {
                    if (firstY < 0) firstY = y;
                    lastY = y;
                    break;
                }
            }
        }
        if (firstY >= 0) {
            System.out.printf("  %-22s Y: %d..%d (content %dpx)  snapshot %dx%d%n",
                name + ":", firstY, lastY, (lastY - firstY + 1), w, h);
        } else {
            System.out.printf("  %-22s (no opaque content)%n", name + ":");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
