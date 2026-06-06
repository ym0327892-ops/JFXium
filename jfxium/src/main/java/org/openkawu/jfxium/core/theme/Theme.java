package org.openkawu.jfxium.core.theme;

/**
 * JFXium 主题接口。
 * 每个具体主题（LightTheme、DarkTheme、MuiTheme 等）实现此接口，
 * 提供主题名称、CSS 样式表路径和明暗类型。
 *
 * <p>设计参考 <a href="https://github.com/mkpaz/atlantafx">AtlantaFX</a>。</p>
 */
public interface Theme {

    /** 获取主题名称（如 "JFXium Light"）。 */
    String getName();

    /** 获取 CSS 用户代理样式表的 classpath 路径。 */
    String getUserAgentStylesheet();

    /** 获取主题类型（亮色 / 暗色）。 */
    ThemeType getType();

    /** 主题明暗类型。 */
    enum ThemeType {
        /** 亮色主题 */
        LIGHT,
        /** 暗色主题 */
        DARK
    }
}
