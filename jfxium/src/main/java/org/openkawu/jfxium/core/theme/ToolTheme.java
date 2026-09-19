package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Tool 主题 —— "紧凑工具列"密度变体（对比实验用）。
 *
 * <p>目标:模拟真实桌面工具(IntelliJ/DBeaver/Qt 桌面)的紧凑观感 —— 「字号不缩、
 * 行高压薄、cell 内边距打薄」,而非 {@link ThemeDensity#COMPACT} 的「整体缩小一号」。
 * 配色与 Light 完全一致,只调整尺寸 token。</p>
 *
 * <p>属「模板主题」衍生物:支持密度派生(返回 {@code theme-light-tool.css})、
 * 参与 {@link ThemeDensity#COMPACT} 联动?——本主题自身已是紧凑档,不参与 dark 轴。
 * 通过 {@link ThemeManager#applyTheme(Theme)} 应用。</p>
 */
public class ToolTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Tool";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-light-tool.css")
        ).toExternalForm();
    }

    @Override
    public String getUserAgentStylesheet(ThemeDensity density) {
        return getUserAgentStylesheet();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}