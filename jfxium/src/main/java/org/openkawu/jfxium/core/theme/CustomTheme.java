package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Custom 主题 —— 自定义企业紫主题示例(白底 + #8b41f5 紫主色)。
 *
 * <p>该主题属于「模板主题」:复制 {@code theme-custom.less} 并修改其中的色阶变量
 * 即可创建自己的家族成员。它不是 {@link ThemeManager} 状态机内置的 Light/Dark
 * 组合,但<strong>支持密度派生</strong>——通过 {@link #getUserAgentStylesheet(ThemeDensity)}
 * 在 COMPACT 时返回 {@code theme-custom-compact.css},因此可被
 * {@link ThemeManager#setDensity(ThemeDensity)} 联动。<strong>不参与 dark 轴
 * 派生</strong>(没有 dark 变体)。</p>
 *
 * <p>典型用法:</p>
 * <pre>{@code
 * // 1. 通过 ThemeManager 应用（支持密度联动）
 * ThemeManager.getInstance().applyTheme(new CustomTheme());
 *
 * // 2. 直接添加到 Scene（绕开状态机）
 * scene.getStylesheets().add(
 *     new CustomTheme().getUserAgentStylesheet());
 * }</pre>
 *
 * <p>该文件同时作为「如何派生自定义主题」的模板 —— 复制
 * {@code theme-custom.less} 并修改其中的色阶变量,即可创建自己的家族成员。</p>
 */
public class CustomTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Custom";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-custom.css")
        ).toExternalForm();
    }

    @Override
    public String getUserAgentStylesheet(ThemeDensity density) {
        String css = density == ThemeDensity.COMPACT
            ? "/org/openkawu/jfxium/css/theme-custom-compact.css"
            : "/org/openkawu/jfxium/css/theme-custom.css";
        return Objects.requireNonNull(getClass().getResource(css)).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}
