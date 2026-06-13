package org.openkawu.jfxium.demo.admin;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.openkawu.jfxium.component.composite.*;
import org.openkawu.jfxium.component.control.*;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.demo.admin.pages.DashboardPage;
import org.openkawu.jfxium.demo.admin.pages.UserPage;
import org.openkawu.jfxium.demo.admin.pages.SettingsPage;
import org.openkawu.jfxium.layout.AppShellAnt;

/**
 * AdminShell — 管理后台骨架：AppShellAnt + ToolBarAnt + StatusBarAnt + BreadcrumbAnt + WatermarkAnt。
 */
public class AdminShell {

    private final StackPane contentArea = new StackPane();
    private final DashboardPage dashboardPage = new DashboardPage();
    private HBox breadcrumb;

    public BorderPane build() {
        breadcrumb = BreadcrumbAnt.create().items("首页").build();

        // ---- Header ----
        VBox headerBox = new VBox(0);
        headerBox.getStyleClass().add(JfxStyles.APP_SHELL_HEADER);

        HBox breadcrumbRow = new HBox(breadcrumb);
        breadcrumbRow.setPadding(new Insets(8, 16, 4, 16));
        breadcrumbRow.getStyleClass().add(Background.DEFAULT.styleClass());

        ToolBarAnt toolbar = ToolBarAnt.create()
                .item(new Label("JFXium Admin Demo"))
                .spacer()
                .item(new Label("v1.0-SNAPSHOT"))
                .build();
        HBox toolbarRow = new HBox(toolbar);
        toolbarRow.setPadding(new Insets(0, 16, 8, 16));
        toolbarRow.getStyleClass().add(Background.DEFAULT.styleClass());
        HBox.setHgrow(toolbar, Priority.ALWAYS);

        headerBox.getChildren().addAll(breadcrumbRow, toolbarRow);
        headerBox.getStyleClass().add(Background.DEFAULT.styleClass());

        // ---- Sider ----
        Node sider = buildSider();

        // ---- Content ----
        contentArea.getStyleClass().add(JfxStyles.APP_SHELL_CONTENT);
        showPage("home");

        // ---- Footer ----
        StatusBarAnt statusBar = StatusBarAnt.create()
                .info("就绪")
                .status("JFXium Admin Demo | Java 21 | JavaFX 21")
                .build();

        // ---- AppShell ----
        BorderPane shell = AppShellAnt.create()
                .header(headerBox)
                .sider(sider, 220)
                .content(contentArea)
                .footer(statusBar)
                .build();

        // ---- Watermark ----
        StackPane watermarked = WatermarkAnt.create()
                .content(shell)
                .text("JFXium Admin")
                .build();
        return new BorderPane(watermarked);
    }

    private Node buildSider() {
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(12, 8, 12, 8));
        nav.getStyleClass().add(Background.SUBTLE.styleClass());
        nav.getChildren().addAll(
                navBtn("📊 数据概览", "home"),
                navBtn("👤 用户管理", "users"),
                navBtn("⚙️ 系统设置", "settings")
        );
        return nav;
    }

    private Node navBtn(String label, String pageKey) {
        ButtonAnt btn = ButtonAnt.create(label).type(ButtonAnt.Type.TEXT).build();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-alignment: CENTER_LEFT;");
        btn.setOnAction(e -> {
            showPage(pageKey);
            updateBreadcrumb(pageKey);
        });
        return btn;
    }

    private void showPage(String key) {
        Node page = switch (key) {
            case "users" -> new UserPage();
            case "settings" -> new SettingsPage();
            default -> dashboardPage;
        };
        contentArea.getChildren().setAll(page);
    }

    private void updateBreadcrumb(String key) {
        String[] items = switch (key) {
            case "users" -> new String[]{"首页", "用户管理"};
            case "settings" -> new String[]{"首页", "系统设置"};
            default -> new String[]{"首页"};
        };
        breadcrumb = BreadcrumbAnt.create().items(items).build();
        // Update the breadcrumb in the header
        if (contentArea.getScene() != null) {
            // Repaint equivalent: just replace breadcrumb in the parent
            HBox breadcrumbRow = (HBox) breadcrumb.getParent();
            if (breadcrumbRow != null) {
                breadcrumbRow.getChildren().set(0, breadcrumb);
            }
        }
    }
}
