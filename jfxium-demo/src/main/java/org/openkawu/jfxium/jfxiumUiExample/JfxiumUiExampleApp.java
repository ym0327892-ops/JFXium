package org.openkawu.jfxium.jfxiumUiExample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.util.WindowManager;
import org.openkawu.jfxium.jfxiumUiExample.view.LoginView;
import org.openkawu.jfxium.jfxiumUiExample.view.MainView;

/**
 * jfxiumUiExample - JFXium UI 库的示例项目入口（单 Stage 版）。
 *
 * <h2>窗口模型</h2>
 * <p>整个 app 全程只用一个 Stage（即 JavaFX 给的 {@code primaryStage}），
 * 登录页和主页通过 {@code stage.setScene(...)} 互相切换。这样：</p>
 * <ul>
 *   <li>不浪费 primaryStage（不再 close 它再造新的）</li>
 *   <li>Scene/Stage 生命周期统一在 App 里编排，View 不需要碰窗口</li>
 *   <li>登出 = 把 Scene 换回登录页，不需要重建 Stage</li>
 * </ul>
 *
 * <h2>职责划分</h2>
 * <table>
 *   <caption>三方分工</caption>
 *   <tr><th>角色</th><th>职责</th></tr>
 *   <tr><td>App（本类）</td><td>持有 Stage、应用主题、切换 Scene</td></tr>
 *   <tr><td>{@link LoginView}</td><td>build() 返回登录页 root Node + 提交回调</td></tr>
 *   <tr><td>{@link MainView}</td><td>build() 返回主页 root Node + 登出回调</td></tr>
 * </table>
 */
public class JfxiumUiExampleApp extends Application {

    /** 全 app 共用的 Stage —— 登录 / 主页都挂在它上面。 */
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        // 全局主题（影响所有后续 Scene）
        ThemeManager.getInstance().applyTheme(new LightTheme());

        // 把 Stage 交给 WindowManager 统一管理（自动加图标 + 关闭时清理）
        this.stage = WindowManager.getDefault().register(primaryStage);

        showLogin();
    }

    // ============================================================
    // 场景切换
    // ============================================================

    /** 切到登录页。 */
    private void showLogin() {
        Region root = new LoginView(this::onLoginSuccess).build();
        switchScene(root, 760, 520, "JFXium UI Example - 登录", false);
    }

    /** 登录成功 → 切到主页（用户名带过去显示在顶栏）。 */
    private void onLoginSuccess(String username) {
        Region root = new MainView(username, this::showLogin /* 登出回调：切回登录页 */).build();
        switchScene(root, 1280, 800, "JFXium UI Example - " + username, true);
    }

    /**
     * 切换 Scene 的统一入口 —— 复用同一个 Stage，避免来回创建窗口。
     *
     * @param root      新场景的根节点
     * @param w         窗口宽
     * @param h         窗口高
     * @param title     标题
     * @param resizable 是否允许拖拽缩放（登录页通常 false，主页 true）
     */
    private void switchScene(Region root, double w, double h, String title, boolean resizable) {
        Scene scene = new Scene(root, w, h);
        // 注册 Scene 以支持运行时主题色切换
        ThemeManager.getInstance().registerScene(scene);
        // 加载 demo 项目自身的辅助样式（仅 demo 用，与库内主题样式叠加）
        scene.getStylesheets().add(getClass().getResource("demo.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(resizable);
        // 切换时显式重设尺寸 + 居中：避免上一次窗口被用户拖大后留下尺寸残留
        stage.setWidth(w);
        stage.setHeight(h);
        stage.centerOnScreen();

        if (!stage.isShowing()) {
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
