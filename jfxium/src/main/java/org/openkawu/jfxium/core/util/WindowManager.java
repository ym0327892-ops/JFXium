package org.openkawu.jfxium.core.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 窗口管理器（M19.34 引入）—— 应用级图标统一 + 多窗口追踪。
 *
 * <h2>主要解决</h2>
 * <ol>
 *   <li><b>统一图标</b>：{@code setAppIcon} 一次配置，所有 {@code register} 的 Stage 自动加图标</li>
 *   <li><b>窗口追踪</b>：用 {@link Stage} 引用注册（不用字符串 ID），类型安全；
 *       窗口关闭时自动从注册表清理，防止内存泄漏</li>
 *   <li><b>批量关闭</b>：开发期 / 设置切换 / 主题重启等场景一键关所有副窗口</li>
 * </ol>
 *
 * <h2>不做的事（设计范围）</h2>
 * <ul>
 *   <li><b>不提供 createWindow 工厂方法</b>：JavaFX 原生 {@code new Stage()} 已经够简洁，
 *       封装一层 {@code (id, title, content, w, h)} 反而限制了 Stage 全部 API</li>
 *   <li><b>不记忆窗口位置 / 大小</b>：业务关切（用户偏好），不该框架兜底</li>
 *   <li><b>不区分 primary stage</b>：注册 = 平等管理；JavaFX 的 primary stage
 *       由 {@link javafx.application.Application#start(Stage)} 负责</li>
 * </ul>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * // 1. 启动时配置一次
 * WindowManager wm = WindowManager.getDefault();
 * wm.setAppIcon("/icons/app.png");
 *
 * // 2. 创建主窗口 + 注册（自动加图标）
 * Stage main = new Stage();
 * main.setScene(scene);
 * wm.register(main);
 * main.show();
 *
 * // 3. 创建副窗口（设置对话框等）
 * Stage settings = new Stage();
 * settings.setScene(settingsScene);
 * wm.register(settings);  // 关闭时自动从注册表清理
 * settings.show();
 *
 * // 4. 切主题 / 重启时关闭所有副窗口
 * wm.closeAll();
 *
 * // 5. 业务侧查询当前窗口数量
 * int n = wm.getOpenCount();
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>幂等注册</b>：同一个 Stage 重复 {@link #register(Stage)} 是 no-op</li>
 *   <li><b>自动清理</b>：监听 {@link Stage#showingProperty()} 而非 {@code setOnCloseRequest}——
 *       前者覆盖所有关闭路径（业务调 {@code stage.close()} 也能感知）</li>
 *   <li><b>线程安全</b>：注册表用 {@link ConcurrentHashMap#newKeySet()}，多线程注册/关闭安全</li>
 *   <li><b>单例 + 多实例并存</b>：默认 {@link #getDefault()}；
 *       需要模块隔离时自己 {@code new WindowManager()}（与 EventBus 一致）</li>
 * </ul>
 */
public final class WindowManager {

    private static final Logger LOGGER = Logger.getLogger(WindowManager.class.getName());

    /** 默认全局实例。需要模块隔离时自己 {@code new WindowManager()}。 */
    private static final WindowManager DEFAULT = new WindowManager();

    /** 获取默认全局实例。命名跟 {@link EventBus#getDefault()} 对齐。 */
    public static WindowManager getDefault() {
        return DEFAULT;
    }

    /** 应用级图标，{@code register} 的 Stage 自动加上。 */
    private Image appIcon;

    /**
     * 已注册窗口集合。
     *
     * <p>用 {@code Set<Stage>} 而非 {@code Map<String, Stage>}——
     * 直接用 Stage 引用更类型安全，且不需要业务侧自己造 ID 字符串。</p>
     */
    private final Set<Stage> registeredWindows = ConcurrentHashMap.newKeySet();

    /** public 构造：业务侧可以创建独立 manager 实例做模块隔离。 */
    public WindowManager() {
    }

    // ============================================================
    // 图标配置
    // ============================================================

    /**
     * 从 classpath 加载图标。
     *
     * <p>典型路径：{@code "/icons/app.png"}。加载失败只记 WARNING 不抛异常——
     * 图标缺失不该让应用启动失败。</p>
     *
     * @return 自身（链式）
     */
    public WindowManager setAppIcon(String classpathPath) {
        if (classpathPath == null) {
            this.appIcon = null;
            return this;
        }
        try (InputStream in = getClass().getResourceAsStream(classpathPath)) {
            if (in == null) {
                LOGGER.log(Level.WARNING,
                        "WindowManager: app icon not found in classpath: {0}", classpathPath);
                return this;
            }
            this.appIcon = new Image(in);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                    "WindowManager: failed to load app icon: " + classpathPath, e);
        }
        return this;
    }

    /** 直接传入 {@link Image} 实例（适合用户自行加载或动态生成图标）。 */
    public WindowManager setAppIcon(Image icon) {
        this.appIcon = icon;
        return this;
    }

    /** 当前应用图标，可能为 null。 */
    public Image getAppIcon() {
        return appIcon;
    }

    // ============================================================
    // 窗口注册
    // ============================================================

    /**
     * 注册一个 Stage：自动加图标 + 自动从注册表清理（窗口关闭时）。
     *
     * <p>已注册的 Stage 重复调用是 no-op。</p>
     *
     * @param stage 需要管理的窗口；null 直接返回 null
     * @return 同一个 stage 实例（便于链式 {@code wm.register(new Stage()).show()}）
     */
    public Stage register(Stage stage) {
        if (stage == null) return null;
        if (registeredWindows.add(stage)) {
            applyIcon(stage);
            // 监听 showing 变化：覆盖所有关闭路径（包括 stage.close() / 用户点 X / 系统关闭）
            // setOnCloseRequest 只能拦截"用户点 X"，缺其他路径
            stage.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (!isShowing) {
                    registeredWindows.remove(stage);
                }
            });
        }
        return stage;
    }

    /**
     * 取消注册一个 Stage（不关闭窗口，仅从管理器移除）。
     *
     * <p>典型场景：业务侧想"接管"某个 Stage 的生命周期，不再让 WindowManager 管。</p>
     */
    public void unregister(Stage stage) {
        if (stage != null) {
            registeredWindows.remove(stage);
        }
    }

    // ============================================================
    // 批量操作
    // ============================================================

    /**
     * 关闭所有已注册窗口（不影响未注册的窗口）。
     *
     * <p>关闭过程中 listener 会从注册表移除窗口；遍历用快照避免 ConcurrentModificationException。</p>
     */
    public void closeAll() {
        for (Stage s : Set.copyOf(registeredWindows)) {
            try {
                s.close();
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "WindowManager: failed to close stage", t);
            }
        }
    }

    // ============================================================
    // 查询
    // ============================================================

    /** 当前已注册且仍在显示的窗口数。 */
    public int getOpenCount() {
        return (int) registeredWindows.stream().filter(Stage::isShowing).count();
    }

    /**
     * 已注册窗口的不可变快照。
     *
     * <p>调用方可以安全地迭代——不受后续 register/unregister 影响。</p>
     */
    public Set<Stage> getRegisteredWindows() {
        return Set.copyOf(registeredWindows);
    }

    // ============================================================
    // 内部
    // ============================================================

    /** 把图标应用到 stage（如果尚未包含此图标）。 */
    private void applyIcon(Stage stage) {
        if (appIcon != null && !stage.getIcons().contains(appIcon)) {
            stage.getIcons().add(appIcon);
        }
    }
}
