package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium shadcn/ui 主题 —— 现代极简风格,白底 + Zinc-900 高对比黑主色。
 *
 * <p>该主题属于「脱管主题」,<strong>不在</strong> {@link ThemeManager} 的
 * Family × dark × density 状态机内(没有 dark/compact 派生变体),仅作为对外
 * 完整 {@link Theme} API 入口存在,使 11 套主题可统一通过 {@link Theme} 抽象
 * 操作。</p>
 *
 * <p>典型用法:</p>
 * <pre>{@code
 * // 1. 通过 ThemeManager 应用(注意:applyTheme 会重置 UA CSS,
 * //    但 3 套脱管主题不会被状态机记住,需自行处理 setPrimaryColor)
 * ThemeManager.getInstance().applyTheme(new ShadcnTheme());
 *
 * // 2. 直接添加到 Scene(更常见的脱管主题用法,绕开状态机)
 * scene.getStylesheets().add(
 *     new ShadcnTheme().getUserAgentStylesheet());
 * }</pre>
 */
public class ShadcnTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Shadcn";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-shadcn.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}
