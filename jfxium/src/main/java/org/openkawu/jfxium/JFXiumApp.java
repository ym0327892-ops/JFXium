package org.openkawu.jfxium;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.Theme;
import org.openkawu.jfxium.core.theme.ThemeManager;

/**
 * JFXium 应用基类 — 提供主题自动加载、场景初始化和常用工具方法。
 *
 * <h2>使用方式</h2>
 * <pre>{@code
 * public class MyApp extends JFXiumApp {
 *     @Override
 *     protected void initUI(Stage stage) {
 *         Parent root = buildYourUI();
 *         setSceneRoot(root, 800, 600); // 自动加载主题 + 注册场景
 *         stage.setTitle("My App");
 *         stage.show();
 *     }
 * }
 * }</pre>
 *
 * <p>如需完全自定义场景创建流程，可直接重写 {@link #start(Stage)}，
 * 然后调用 {@link #applyThemeTo(Scene)} 手动应用主题。</p>
 */
public abstract class JFXiumApp extends Application {

    /** 默认主题：Light */
    private Theme defaultTheme = new LightTheme();

    /** 当前主舞台，子类可通过 {@link #getPrimaryStage()} 访问 */
    private Stage primaryStage;

    /** 当前主场景，子类可通过 {@link #getScene()} 访问 */
    private Scene scene;

    // ============================================================
    // 生命周期（final，子类不可覆盖）
    // ============================================================

    @Override
    public final void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        initUI(stage);
    }

    // ============================================================
    // 子类必须/可以覆盖的方法
    // ============================================================

    /**
     * 子类实现 UI 初始化。框架已保证 {@link #primaryStage} 可用。
     *
     * <p>常用流程：</p>
     * <pre>{@code
     * protected void initUI(Stage stage) {
     *     VBox root = ...;
     *     setSceneRoot(root, 800, 600);
     *     stage.setTitle("App");
     *     stage.show();
     * }
     * }</pre>
     *
     * @param stage 主舞台（已注入 primaryStage）
     */
    protected abstract void initUI(Stage stage);

    /**
     * 设置默认主题（在 {@link #setSceneRoot} 之前调用生效）。
     * 默认值为 {@link LightTheme}。
     */
    protected void setDefaultTheme(Theme theme) {
        this.defaultTheme = theme != null ? theme : new LightTheme();
    }

    // ============================================================
    // 便捷工具方法
    // ============================================================

    /**
     * 创建场景、应用主题、注册到 ThemeManager，并设置到 Stage。
     *
     * <p>一站式完成：</p>
     * <ol>
     *   <li>new Scene(root, width, height)</li>
     *   <li>应用默认主题（通过 ThemeManager）</li>
     *   <li>注册场景到 ThemeManager（支持动态主题色切换）</li>
     *   <li>stage.setScene(scene)</li>
     * </ol>
     *
     * @param root   场景根节点
     * @param width  场景宽度
     * @param height 场景高度
     * @return 创建的 Scene 实例
     */
    protected Scene setSceneRoot(Parent root, double width, double height) {
        Scene newScene = new Scene(root, width, height);
        this.scene = newScene;
        applyThemeTo(newScene);
        primaryStage.setScene(newScene);
        return newScene;
    }

    /**
     * 对指定场景应用当前默认主题，并注册到 ThemeManager。
     *
     * <p>子类在需要手动创建 Scene 时调用此方法。</p>
     *
     * @param targetScene 目标场景
     */
    protected void applyThemeTo(Scene targetScene) {
        ThemeManager tm = ThemeManager.getInstance();
        tm.applyTheme(defaultTheme);
        tm.registerScene(targetScene);
    }

    /**
     * 获取主舞台。
     */
    protected Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * 获取当前主场景（仅在 {@link #setSceneRoot} 调用后可用）。
     */
    protected Scene getScene() {
        return scene;
    }

    // ============================================================
    // 静态便捷方法
    // ============================================================

    /**
     * 应用指定主题（全局）。
     */
    public static void applyTheme(Theme theme) {
        ThemeManager.getInstance().applyTheme(theme);
    }

    /**
     * 切换明暗主题（保持家族/密度/主题色不变）。
     */
    public static void toggleTheme() {
        ThemeManager.getInstance().toggleTheme();
    }
}
