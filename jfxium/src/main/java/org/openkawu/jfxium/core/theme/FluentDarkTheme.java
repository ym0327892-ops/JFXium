package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Fluent Dark Theme —— 微软 Fluent 设计语言（Windows 11 / WinUI 3 桌面口径）暗色主题。
 *
 * <p>与 {@link FluentTheme} 同族，但强调色遵循 WinUI 3 暗色口径：暗底上品牌色<b>提亮</b>为
 * 亮青 —— 填充 {@code AccentFillColorDefault = SystemAccentColorDark1 = #4cc2ff}、
 * 链接文字 {@code AccentTextFillColorPrimary = SystemAccentColorLight3 = #99ebff}，
 * 反色文字（{@code fg-on-emphasis}）因此改为黑。中性分层（等效不透明值）为
 * {@code #2c2c2c > #282828 > #202020 > #1c1c1c}。</p>
 *
 * <p>支持密度派生（COMPACT → {@code theme-fluent-dark-compact.css}）。</p>
 *
 * <pre>{@code
 * ThemeManager.getInstance().applyTheme(new FluentDarkTheme());
 * }</pre>
 */
public class FluentDarkTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Fluent Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-fluent-dark.css")
        ).toExternalForm();
    }

    @Override
    public String getUserAgentStylesheet(ThemeDensity density) {
        String css = density == ThemeDensity.COMPACT
            ? "/org/openkawu/jfxium/css/theme-fluent-dark-compact.css"
            : "/org/openkawu/jfxium/css/theme-fluent-dark.css";
        return Objects.requireNonNull(getClass().getResource(css)).toExternalForm();
    }

    /**
     * Fluent Dark 自带完整 WinUI 强调色阶（accent 固定为暗色口径 {@code #4cc2ff}），
     * 拒绝 ThemeManager 的通用预设主色注入。
     */
    @Override
    public boolean hasFixedAccent() {
        return true;
    }

    @Override
    public ThemeType getType() {
        return ThemeType.DARK;
    }
}
