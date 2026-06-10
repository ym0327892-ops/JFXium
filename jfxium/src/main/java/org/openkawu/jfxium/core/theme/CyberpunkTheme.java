package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium 赛博朋克 / Geek 主题 —— 深色底 + Cyan 霓虹主色。
 *
 * <p>该主题属于「脱管主题」,<strong>不在</strong> {@link ThemeManager} 的
 * Family × dark × density 状态机内(没有 dark/compact 派生变体),仅作为对外
 * 完整 {@link Theme} API 入口存在,使 11 套主题可统一通过 {@link Theme} 抽象
 * 操作。</p>
 *
 * <p>典型用法:</p>
 * <pre>{@code
 * // 1. 通过 ThemeManager 应用
 * ThemeManager.getInstance().applyTheme(new CyberpunkTheme());
 *
 * // 2. 直接添加到 Scene(更常见的脱管主题用法,绕开状态机)
 * scene.getStylesheets().add(
 *     new CyberpunkTheme().getUserAgentStylesheet());
 * }</pre>
 */
public class CyberpunkTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Cyberpunk";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-cyberpunk.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.DARK;
    }
}
