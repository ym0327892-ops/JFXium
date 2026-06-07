package org.openkawu.jfxium.probe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.control.MenuBarAnt;

/**
 * 探针：打印 MenuBarAnt 完整子节点树 + 实际 padding/insets/bounds
 * 定位 BUG #68：为什么 .jfx-menu-bar CSS 命中后高度反而从 41 → 52？
 */
public class MenuBarNodeTreeProbe extends Application {

    @Override
    public void start(Stage stage) {
        MenuBarAnt menuBar = MenuBarAnt.create()
            .menu("File")
                .item("New", () -> {})
                .endMenu()
            .menu("Edit")
                .item("Undo", () -> {})
                .endMenu();

        Scene scene = new Scene(menuBar, 600, 100);
        scene.getStylesheets().add(
            getClass().getResource("/org/openkawu/jfxium/css/theme-light.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            System.out.println("=== MenuBarAnt Node Tree ===");
            System.out.printf("  ROOT: MenuBarAnt h=%.2f w=%.2f  classes=%s%n",
                menuBar.getBoundsInLocal().getHeight(),
                menuBar.getBoundsInLocal().getWidth(),
                menuBar.getStyleClass());
            printTree(menuBar, 0);

            System.out.println();
            System.out.println("=== CSS applied check ===");
            dumpStyle(menuBar, "MenuBarAnt (.jfx-menu-bar)");
            for (Node child : menuBar.lookupAll(".jfx-menu-bar > .container")) {
                dumpStyle(child, "container (.jfx-menu-bar > .container)");
                for (Node btn : child.lookupAll(".menu-button")) {
                    dumpStyle(btn, "menu-button (.jfx-menu-bar > .container > .menu-button)");
                }
            }

            Platform.exit();
        });
    }

    private void printTree(Node node, int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("  ");
        sb.append(String.format("%s h=%.2f w=%.2f classes=%s",
            node.getClass().getSimpleName(),
            node.getBoundsInLocal().getHeight(),
            node.getBoundsInLocal().getWidth(),
            node.getStyleClass()));
        if (node instanceof Region) {
            Region r = (Region) node;
            String insetsStr = "null";
            Background bg = r.getBackground();
            if (bg != null && !bg.getFills().isEmpty()) {
                StringBuilder isb = new StringBuilder("[");
                for (BackgroundFill bf : bg.getFills()) {
                    isb.append(bf.getInsets()).append(",");
                }
                isb.append("]");
                insetsStr = isb.toString();
            }
            sb.append(String.format(" padding=%s insets=%s minH=%.1f prefH=%.1f",
                r.getPadding(), insetsStr, r.getMinHeight(), r.getPrefHeight()));
        }
        System.out.println(sb);
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                printTree(child, depth + 1);
            }
        }
    }

    private void dumpStyle(Node node, String selector) {
        String pad = readStyle(node, "-fx-padding");
        String minH = readStyle(node, "-fx-min-height");
        String prefH = readStyle(node, "-fx-pref-height");
        String bgColor = readStyle(node, "-fx-background-color");
        String bgInsets = readStyle(node, "-fx-background-insets");
        System.out.printf("  %-50s%n", selector);
        System.out.printf("    -fx-padding        = %s%n", pad);
        System.out.printf("    -fx-min-height     = %s%n", minH);
        System.out.printf("    -fx-pref-height    = %s%n", prefH);
        System.out.printf("    -fx-background-color   = %s%n", bgColor);
        System.out.printf("    -fx-background-insets  = %s%n", bgInsets);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String readStyle(Node node, String prop) {
        if (!(node instanceof Styleable)) return "(not styleable)";
        try {
            for (javafx.css.CssMetaData meta : node.getCssMetaData()) {
                if (prop.equals(meta.getProperty())) {
                    StyleableProperty<?> val = meta.getStyleableProperty((Styleable) node);
                    if (val == null) return "(unresolved -> default)";
                    return val.getValue().toString();
                }
            }
            return "(prop not in meta)";
        } catch (Exception e) {
            return "(err: " + e.getMessage() + ")";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
