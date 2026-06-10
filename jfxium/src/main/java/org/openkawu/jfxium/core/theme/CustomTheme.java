package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Custom 主题 —— 自定义企业紫主题示例(白底 + #8b41f5 紫主色)。
 *
 * <p>该主题属于「脱管主题」,<strong>不在</strong> {@link ThemeManager} 的
 * Family × dark × density 状态机内(没有 dark/compact 派生变体),仅作为对外
 * 完整 {@link Theme} API 入口存在,使 11 套主题可统一通过 {@link Theme} 抽象
 * 操作。</p>
 *
 * <p>典型用法:</p>
 * <pre>{@code
 * // 1. 通过 ThemeManager 应用
 * ThemeManager.getInstance().applyTheme(new CustomTheme());
 *
 * // 2. 直接添加到 Scene(更常见的脱管主题用法,绕开状态机)
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
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}
