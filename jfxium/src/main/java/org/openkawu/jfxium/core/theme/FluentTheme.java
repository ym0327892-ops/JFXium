package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Fluent Theme —— 微软 Fluent 设计语言（Windows 11 / WinUI 3 桌面口径）亮色主题。
 *
 * <p>配色取自 <b>WinUI 3 theme resources</b>（{@code microsoft-ui-xaml} 的
 * {@code Common_themeresources_any.xaml}，"Light" 字典）：强调填充
 * {@code AccentFillColorDefault = SystemAccentColorDark1 = #0067c0}、链接文字
 * {@code AccentTextFillColorPrimary = SystemAccentColorDark2 = #003e92}、
 * 中性表面/文本/描边层（alpha 叠加折算为等效不透明值）、圆角 4px、动效 100/200/300ms。
 * 色阶手填官方档位而不走 {@link ThemeColor#generateColorScale()} —— 后者是
 * base→白/黑线性插值，生成不出 WinUI 的色相。</p>
 *
 * <p>注意这是 <b>Windows 11 桌面</b>口径，与 {@code microsoft/fluentui}
 * 的 {@code brandWeb} 色阶（{@code #0f6cbd}）是两条并列的设计线；桌面版强调色饱和度
 * 更高、更偏纯蓝。详见 {@code palette-fluent.less} 头部。</p>
 *
 * <p>进入 {@link ThemeManager} 状态机：支持密度派生（COMPACT →
 * {@code theme-fluent-compact.css}），暗色轴由 {@link FluentDarkTheme} 承担。</p>
 *
 * <pre>{@code
 * ThemeManager.getInstance().applyTheme(new FluentTheme());
 * }</pre>
 */
public class FluentTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Fluent";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-fluent.css")
        ).toExternalForm();
    }

    @Override
    public String getUserAgentStylesheet(ThemeDensity density) {
        String css = density == ThemeDensity.COMPACT
            ? "/org/openkawu/jfxium/css/theme-fluent-compact.css"
            : "/org/openkawu/jfxium/css/theme-fluent.css";
        return Objects.requireNonNull(getClass().getResource(css)).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }

    /**
     * Fluent 主题自带完整 WinUI 强调色阶（{@code -color-accent-*}），
     * 拒绝 ThemeManager 的通用预设主色注入，避免被 Ant 蓝覆盖。
     */
    @Override
    public boolean hasFixedAccent() {
        return true;
    }
}