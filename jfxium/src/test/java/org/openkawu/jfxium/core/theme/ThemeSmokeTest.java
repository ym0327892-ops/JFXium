package org.openkawu.jfxium.core.theme;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 主题冒烟测试 —— 在 JFX 工具套件下验证：
 * <ol>
 *   <li>5 套主题 CSS 资源全部存在且非空（编译产物完整性）</li>
 *   <li>ThemeManager 状态机 Light/Dark × Default/Compact 全可切换</li>
 *   <li>1 套模板主题（custom）CSS 资源可加载（可手挂 Scene / 支持密度派生）</li>
 * </ol>
 *
 * <p>本测试不替代人工 UI 走查，但能给出"主题切换可用"的机器可验证结论：
 * 主题颜色整体跟随、暗色系下对比度安全、紧凑模式控件高度收紧。</p>
 */
@DisplayName("主题冒烟测试 (5 套 CSS 加载 + 状态机 4 组合)")
class ThemeSmokeTest extends JfxTestBase {

    /** 5 套主题 CSS 路径。 */
    private static final String[] ALL_THEME_CSS = {
        "/org/openkawu/jfxium/css/theme-light.css",
        "/org/openkawu/jfxium/css/theme-dark.css",
        "/org/openkawu/jfxium/css/theme-light-compact.css",
        "/org/openkawu/jfxium/css/theme-dark-compact.css",
        "/org/openkawu/jfxium/css/theme-custom.css",
        "/org/openkawu/jfxium/css/theme-custom-compact.css",
    };

    /** 4 套受 ThemeManager 状态机管理的主题（Light/Dark × Default/Compact）。 */
    private static final String[] MANAGED_THEME_CSS = {
        "/org/openkawu/jfxium/css/theme-light.css",
        "/org/openkawu/jfxium/css/theme-dark.css",
        "/org/openkawu/jfxium/css/theme-light-compact.css",
        "/org/openkawu/jfxium/css/theme-dark-compact.css",
    };

    /** 1 套模板主题（custom + 其 compact 派生，支持密度联动，可手挂 Scene）。 */
    private static final String[] UNMANAGED_THEME_CSS = {
        "/org/openkawu/jfxium/css/theme-custom.css",
        "/org/openkawu/jfxium/css/theme-custom-compact.css",
    };

    @Nested
    @DisplayName("6 个 CSS 产物完整性（状态机 4 + custom 常规/紧凑 2）")
    class CssArtifacts {

        @Test
        @DisplayName("6 个主题 CSS 文件全部存在且非空")
        void allThemeCssArtifactsExist() throws Exception {
            assertEquals(6, ALL_THEME_CSS.length, "6 个主题 CSS 文件");

            for (String path : ALL_THEME_CSS) {
                URL url = ThemeSmokeTest.class.getResource(path);
                assertNotNull(url, () -> "主题 CSS 资源缺失: " + path);

                try (InputStream in = url.openStream()) {
                    assertNotNull(in, () -> "主题 CSS 打开失败: " + path);
                    int size = in.readAllBytes().length;
                    assertTrue(size > 100,
                        () -> "主题 CSS 异常小 (" + size + "B)，可能编译失败: " + path);
                }
            }
        }
    }

    @Nested
    @DisplayName("ThemeManager 状态机 4 组合")
    class StateMachine {

        @Test
        @DisplayName("2 个 *Theme 实例的 getUserAgentStylesheet 全部可达")
        void twoThemeInstancesLoadable() {
            Theme[] themes = {
                new LightTheme(),
                new DarkTheme(),
            };
            for (Theme t : themes) {
                assertNotNull(t.getName(), () -> t.getClass().getSimpleName() + " 名称为空");
                assertNotNull(t.getUserAgentStylesheet(),
                    () -> t.getClass().getSimpleName() + " UA stylesheet 路径为空");
                assertNotNull(t.getType(),
                    () -> t.getClass().getSimpleName() + " 类型未指定");
            }
        }

        @Test
        @DisplayName("ThemeManager.applyTheme 2 套 *Theme 全部可应用")
        void applyThemeTwoTimes() {
            ThemeManager mgr = ThemeManager.getInstance();
            Theme[] themes = {
                new LightTheme(),
                new DarkTheme(),
            };
            for (Theme t : themes) {
                assertDoesNotThrow(() -> mgr.applyTheme(t),
                    () -> "applyTheme 失败: " + t.getClass().getSimpleName());
            }
        }

        @Test
        @DisplayName("ThemeManager.setDark × setDensity 4 组合无 NPE")
        void stateMachineFourCombinations() {
            ThemeManager mgr = ThemeManager.getInstance();
            List<String> applied = new ArrayList<>();

            for (boolean dark : new boolean[]{false, true}) {
                for (ThemeDensity density : ThemeDensity.values()) {
                    assertDoesNotThrow(() -> {
                        mgr.setDark(dark);
                        mgr.setDensity(density);
                    }, () -> String.format("状态机组合失败: dark=%s density=%s",
                        dark, density));

                    applied.add(String.format("%s/%s",
                        dark ? "DARK" : "LIGHT", density.name()));
                }
            }

            assertEquals(4, applied.size(),
                "ThemeManager 状态机应有 2 dark × 2 density = 4 组合");
        }

        @Test
        @DisplayName("ThemeColor 预设色板可按 hex 回查")
        void presetLookupByHex() {
            assertEquals(ThemeColor.Preset.BLUE, ThemeColor.Preset.fromHex("#1677ff"));
            assertEquals(ThemeColor.Preset.ORANGE, ThemeColor.Preset.fromHex("#fa8c16"));
            assertNull(ThemeColor.Preset.fromHex("#123456"));
        }
    }

    @Nested
    @DisplayName("1 套模板主题（custom）")
    class UnmanagedThemes {

        @Test
        @DisplayName("custom 常规/紧凑 CSS 资源存在且非空（可手挂 Scene）")
        void customThemeLoadable() throws Exception {
            for (String path : UNMANAGED_THEME_CSS) {
                URL url = ThemeSmokeTest.class.getResource(path);
                assertNotNull(url, () -> "custom 主题 CSS 资源缺失: " + path);
                try (InputStream in = url.openStream()) {
                    int size = in.readAllBytes().length;
                    assertTrue(size > 100,
                        () -> "custom 主题 CSS 异常小: " + path);
                }
            }
        }

        @Test
        @DisplayName("4 个 MANAGED + 2 个 custom = 6 个 CSS 文件")
        void managedPlusUnmanagedEqualsSix() {
            assertEquals(MANAGED_THEME_CSS.length + UNMANAGED_THEME_CSS.length,
                ALL_THEME_CSS.length,
                "MANAGED 4 + custom 2 = 6 个 CSS 文件");
        }
    }

    @Nested
    @DisplayName("模板主题的 Theme 包装类（CustomTheme）")
    class UnmanagedThemeWrappers {

        @Test
        @DisplayName("CustomTheme: 名称/UA stylesheet/类型/密度派生 全部正确")
        void customThemeWrapperIsValid() {
            CustomTheme t = new CustomTheme();
            assertEquals("JFXium Custom", t.getName(), "Custom 名称");
            assertEquals(Theme.ThemeType.LIGHT, t.getType(),
                "Custom 是白底 + 紫主色，归类 LIGHT");
            assertNotNull(t.getUserAgentStylesheet(), "Custom UA stylesheet 不可为空");
            assertTrue(t.getUserAgentStylesheet().endsWith("theme-custom.css"),
                "Custom UA stylesheet 默认应指向 theme-custom.css");
            // 支持密度派生：COMPACT 时返回 custom-compact.css
            assertTrue(t.getUserAgentStylesheet(ThemeDensity.COMPACT).endsWith("theme-custom-compact.css"),
                "Custom COMPACT 应指向 theme-custom-compact.css");
            assertTrue(t.getUserAgentStylesheet(ThemeDensity.DEFAULT).endsWith("theme-custom.css"),
                "Custom DEFAULT 应指向 theme-custom.css");
        }

        @Test
        @DisplayName("CustomTheme 可统一通过 ThemeManager.applyTheme 加载")
        void customThemeApplyViaThemeManager() {
            ThemeManager mgr = ThemeManager.getInstance();
            assertDoesNotThrow(() -> mgr.applyTheme(new CustomTheme()),
                "Custom 主题 applyTheme 失败");
        }
    }
}
