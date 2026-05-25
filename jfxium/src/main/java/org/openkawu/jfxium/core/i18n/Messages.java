package org.openkawu.jfxium.core.i18n;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JFXium i18n 入口（M19.18）—— 静态门面，按当前 Locale 取出面向用户的字符串。
 *
 * <h2>核心契约</h2>
 * <ul>
 *   <li><b>默认 Locale</b>：{@link Locale#SIMPLIFIED_CHINESE}（项目主用户群体为简体中文）</li>
 *   <li><b>资源 base name</b>：{@code org/openkawu/jfxium/i18n/messages}</li>
 *   <li><b>fallback 链</b>：当前 locale → {@code messages.properties}（无后缀）→ key 本身</li>
 *   <li><b>0 依赖</b>：仅依赖 JDK 自带 {@link ResourceBundle} / {@link MessageFormat} / {@link Locale}</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 取一段固定文案
 * String copy = Messages.get("codeblock.copy");          // "复制"
 *
 * // 参数化
 * String greeting = Messages.get("login.error.required", "用户名");
 *
 * // 切换语言（必须在 JavaFX 应用线程上运行）
 * Messages.setLocale(Locale.ENGLISH);
 *
 * // 让组件随 Locale 变化自动刷新
 * Messages.localeProperty().addListener((obs, old, val) -> rebuildUi());
 * }</pre>
 *
 * <h2>线程安全</h2>
 * 所有静态方法通过 {@code synchronized} 保证内部状态一致；
 * {@link #setLocale(Locale)} 在非 FX 线程调用时，会通过 {@link Platform#runLater(Runnable)}
 * 把 {@link #localeProperty()} 的变更通知投递回 FX 应用线程。
 */
public final class Messages {

    private static final Logger LOGGER = Logger.getLogger(Messages.class.getName());

    /** 资源 bundle 的 base name —— 与包路径平行，避免与未来其他资源冲突 */
    private static final String BUNDLE_BASE_NAME = "org.openkawu.jfxium.i18n.messages";

    /** 当前 Locale 的 JavaFX 属性 —— 暴露 ReadOnlyObjectProperty 让 UI 监听变化 */
    private static final ReadOnlyObjectWrapper<Locale> LOCALE_WRAPPER =
            new ReadOnlyObjectWrapper<>(Locale.SIMPLIFIED_CHINESE);

    /** 当前 locale 缓存的 bundle（避免每次 get 重新加载） */
    private static volatile ResourceBundle currentBundle;

    /** fallback bundle（无 locale 后缀的 messages.properties），永不为空 */
    private static volatile ResourceBundle fallbackBundle;

    private Messages() {
        // 静态门面，禁止实例化
    }

    /**
     * 当前 Locale 的只读属性。UI 组件可监听它实现自动刷新。
     */
    public static ReadOnlyObjectProperty<Locale> localeProperty() {
        return LOCALE_WRAPPER.getReadOnlyProperty();
    }

    /**
     * 取当前 Locale。默认为 {@link Locale#SIMPLIFIED_CHINESE}。
     */
    public static synchronized Locale getLocale() {
        return LOCALE_WRAPPER.get();
    }

    /**
     * 切换当前 Locale。
     *
     * <ul>
     *   <li>新旧 Locale 相同时不触发 propertyChange（避免无意义重渲染）</li>
     *   <li>非 FX 线程调用时会通过 {@link Platform#runLater} 投递到 FX 线程</li>
     *   <li>调用后 ResourceBundle 缓存被清空，下次 get 会按新 Locale 重新加载</li>
     * </ul>
     *
     * @throws NullPointerException 当 locale 为 null
     */
    public static void setLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale 不能为 null");
        synchronized (Messages.class) {
            Locale current = LOCALE_WRAPPER.get();
            if (current.equals(locale)) {
                return; // 同 Locale 直接返回，不触发变更通知
            }
            // 清空缓存，强制下次按新 Locale 加载
            currentBundle = null;
        }
        Runnable update = () -> LOCALE_WRAPPER.set(locale);
        // localeProperty 是 JavaFX Property，必须在 FX 线程上变更
        if (Platform.isFxApplicationThread()) {
            update.run();
        } else {
            Platform.runLater(update);
        }
    }

    /**
     * 按 key 取出当前 Locale 对应的字符串。
     *
     * <p>查找顺序：当前 Locale bundle → fallback bundle → key 本身（并打 WARNING 日志）。</p>
     *
     * @param key i18n key（如 {@code codeblock.copy}）
     * @return 翻译字符串，永不为 null（最坏情况返回 key 本身）
     */
    public static String get(String key) {
        Objects.requireNonNull(key, "key 不能为 null");
        // 1. 当前 locale bundle
        try {
            return resolveBundle().getString(key);
        } catch (MissingResourceException ignored) {
            // 落到 fallback
        }
        // 2. fallback bundle
        try {
            return resolveFallbackBundle().getString(key);
        } catch (MissingResourceException ignored) {
            // 落到 key 占位
        }
        // 3. 兜底返回 key 自身，并打 WARNING（开发阶段提示）
        LOGGER.log(Level.WARNING, "i18n key 缺失：{0}", key);
        return key;
    }

    /**
     * 参数化版本：通过 {@link MessageFormat} 替换占位符（{0}, {1}, ...）。
     *
     * @param key i18n key
     * @param args 占位符实参，按 0..n 顺序替换
     * @return 格式化后的字符串
     */
    public static String get(String key, Object... args) {
        String pattern = get(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        try {
            // 用当前 Locale 决定数字/日期等的本地化格式
            return new MessageFormat(pattern, getLocale()).format(args);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "i18n 格式化失败：" + key, e);
            return pattern;
        }
    }

    // ============================================================
    // 内部辅助
    // ============================================================

    private static ResourceBundle resolveBundle() {
        ResourceBundle bundle = currentBundle;
        if (bundle == null) {
            synchronized (Messages.class) {
                if (currentBundle == null) {
                    currentBundle = ResourceBundle.getBundle(
                            BUNDLE_BASE_NAME,
                            getLocale(),
                            Messages.class.getClassLoader()
                    );
                }
                bundle = currentBundle;
            }
        }
        return bundle;
    }

    private static ResourceBundle resolveFallbackBundle() {
        ResourceBundle bundle = fallbackBundle;
        if (bundle == null) {
            synchronized (Messages.class) {
                if (fallbackBundle == null) {
                    // ROOT locale 强制走 messages.properties（无后缀）
                    fallbackBundle = ResourceBundle.getBundle(
                            BUNDLE_BASE_NAME,
                            Locale.ROOT,
                            Messages.class.getClassLoader()
                    );
                }
                bundle = fallbackBundle;
            }
        }
        return bundle;
    }
}
