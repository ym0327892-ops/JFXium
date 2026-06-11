package org.openkawu.jfxium.demo.admin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.util.WindowManager;

/**
 * AdminDemo — JFXium 管理后台完整示例。
 *
 * <p>集成 AppShellAnt + ToolBarAnt + StatusBarAnt + BreadcrumbAnt + WatermarkAnt +
 * MenuAnt 导航 + 真实业务页面（Dashboard / 用户管理 / 系统设置）。</p>
 *
 * <p>运行：在 IDE 或 {@code ./mvnw javafx:run -pl jfxium-demo} 后切到本类 main。</p>
 */
public class AdminApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ThemeManager.getInstance().applyTheme(new LightTheme());
        Stage stage = WindowManager.getDefault().register(primaryStage);

        AdminShell shell = new AdminShell();
        BorderPane root = shell.build();

        Scene scene = new Scene(root, 1280, 800);
        ThemeManager.getInstance().registerScene(scene);
        scene.getStylesheets().add(
                getClass().getResource("/org/openkawu/jfxium/jfxiumUiExample/demo.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("JFXium Admin Demo");
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
