package org.openkawu.jfxium.demo.menu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openkawu.jfxium.component.composite.MenuAnt;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.theme.LightCompactTheme;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;

/**
 * BUG #68 探针：测量 MenuAnt INLINE 模式下各 row 的实际渲染高度。
 *
 * 验证目标：
 *   - LIGHT (默认)      item/group row 高度 ≈ 30px
 *   - LIGHT-COMPACT     item/group row 高度 ≈ 26px
 *   - divider 行高     7px（3+1+3）
 *
 * 用法：修改 jfxium-demo/pom.xml 的 mainClass 为本类，跑 mvn javafx:run。
 *      跑完把 mainClass 改回 JfxiumUiExampleApp。
 */
public class MenuInlineProbe extends Application {

    @Override
    public void start(Stage stage) {
        // 测试菜单：item / group / divider / subMenu 都有
        MenuAnt.Builder b = MenuAnt.create()
                .group("导航")                                        // group 行
                .item("dashboard", "首页", () -> {})                  // item 行
                .item("profile", "个人资料", () -> {})                 // item 行
                .divider()                                            // divider 行
                .subMenu("settings", "设置")                           // subMenu header (默认收起)
                    .item("account", "账户设置", () -> {})              // 嵌套 item (收起时不显示)
                    .item("security", "安全设置", () -> {})            // 嵌套 item
                    .endSubMenu()
                .divider()                                            // divider 行
                .item("logout", "退出登录", () -> {})                   // item 行
                .selectedKey("dashboard");
        Pane menu = b.build();

        VBox root = new VBox(menu);
        Scene scene = new Scene(root, 320, 600);
        ThemeManager mgr = ThemeManager.getInstance();
        mgr.registerScene(scene);
        mgr.applyTheme(new LightTheme());  // 启动时直接设主题

        stage.setTitle("MenuAnt Inline Probe (BUG #68)");
        stage.setScene(scene);

        stage.setOnShown(e -> {
            // LIGHT 主题下测量
            measureAndPrint("LIGHT (default)", menu);

            // LIGHT-COMPACT 主题下测量（强制 re-layout）
            mgr.applyTheme(new LightCompactTheme());
            menu.applyCss();  // 强制 CSS 重新解析
            menu.requestLayout();
            Platform.runLater(() -> {
                Platform.runLater(() -> {
                    measureAndPrint("LIGHT-COMPACT", menu);
                    Platform.exit();
                });
            });
        });
        stage.show();
    }

    private void measureAndPrint(String label, Pane menu) {
        System.out.println();
        System.out.println("=== " + label + " ===");
        for (int i = 0; i < menu.getChildren().size(); i++) {
            Node child = menu.getChildren().get(i);
            String desc = describeNode(child);
            // 用 getHeight()（含 padding）+ 单独的 getPadding() + layoutBounds
            double totalH = child.getBoundsInLocal().getHeight();  // 含 padding
            double contentH = child.getLayoutBounds().getHeight(); // 不含 padding
            Insets padding = (child instanceof Region) ? ((Region) child).getPadding() : Insets.EMPTY;
            String fontSize = (child instanceof Region) ? String.format("fontSize=%s",
                    ((Region) child).getChildrenUnmodifiable().stream()
                            .filter(n -> n instanceof javafx.scene.text.Text)
                            .map(n -> ((javafx.scene.text.Text) n).getFont().getSize())
                            .findFirst().orElse(-1.0)) : "";
            System.out.printf("  [%d] %-30s → totalH=%5.1f  contentH=%5.1f  padding=%-22s %s%n",
                    i, desc, totalH, contentH, padding, fontSize);
        }
        double total = menu.getBoundsInLocal().getHeight();
        System.out.printf("  %-32s → TOTAL   = %5.1f px%n", "(menu)", total);
    }

    private String describeNode(Node node) {
        if (node instanceof VBox vb) {
            return "subMenu container (VBox)";
        }
        if (node instanceof HBox hb) {
            if (hb.getStyleClass().contains(JfxStyles.MENU_GROUP)) return "MENU_GROUP (HBox)";
            if (hb.getStyleClass().contains(JfxStyles.MENU_ITEM)) return "MENU_ITEM (HBox)";
            if (hb.getStyleClass().contains(JfxStyles.MENU_SUBMENU_HEADER)) return "MENU_SUBMENU_HEADER (HBox)";
            return "HBox(" + hb.getStyleClass() + ")";
        }
        if (node instanceof Region r) {
            if (r.getStyleClass().contains(JfxStyles.MENU_DIVIDER)) return "MENU_DIVIDER (Region)";
        }
        return node.getClass().getSimpleName() + "(" + node.getStyleClass() + ")";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
