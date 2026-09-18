package org.openkawu.jfxium.core.theme;

/**
 * JFXium 主题接口。
 * 每个具体主题（LightTheme、DarkTheme 等）实现此接口，
 * 提供主题名称、CSS 样式表路径、明暗类型和密度。
 *
 * <p>设计参考 <a href="https://github.com/mkpaz/atlantafx">AtlantaFX</a>。</p>
 *
 * <p>密度（{@link ThemeDensity}）是 {@link Theme} 的可选属性，PC UI 规范 §12.2 定义。
 * 默认实现返回 {@link ThemeDensity#DEFAULT}。自 §15.3 P3 起，密度由
 * {@link ThemeManager} 作为正交维度独立管理，不再为每种密度派生子类。
 * 调用方应当通过 {@link ThemeManager#getDensity()} 读取「当前生效的密度」，
 * 因为密度可由 ThemeManager 在运行时独立于 Theme 对象切换。</p>
 */
public interface Theme {

    /** 获取主题名称（如 "JFXium Light"）。 */
    String getName();

    /** 获取 CSS 用户代理样式表的 classpath 路径（默认密度）。 */
    String getUserAgentStylesheet();

    /**
     * 按密度获取 CSS 用户代理样式表路径。
     * 默认实现忽略密度、回退到 {@link #getUserAgentStylesheet()}——
          * 适用于没有紧凑变体的主题（如无 compact 派生的自定义模板）。
     * 有紧凑变体的主题（Light / Dark）覆盖此方法，在 {@link ThemeDensity#COMPACT}
     * 时返回对应的 {@code theme-*-compact.css}。</p>
     */
    default String getUserAgentStylesheet(ThemeDensity density) {
        return getUserAgentStylesheet();
    }

    /** 获取主题类型（亮色 / 暗色）。 */
    ThemeType getType();

    /**
     * 获取本主题内置的密度。默认 {@link ThemeDensity#DEFAULT}。
     * 注：这是 Theme 对象的「自带」密度，与 ThemeManager 的「当前」密度是两回事。
     */
    default ThemeDensity getDensity() {
        return ThemeDensity.DEFAULT;
    }

    /** 主题明暗类型。 */
    enum ThemeType {
        /** 亮色主题 */
        LIGHT,
        /** 暗色主题 */
        DARK
    }
}
