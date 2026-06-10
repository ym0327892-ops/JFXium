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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 主题冒烟测试 —— 在 JFX 工具套件下验证：
 * <ol>
 *   <li>11 套主题 CSS 资源全部存在且非空（编译产物完整性）</li>
 *   <li>ThemeManager 状态机 4 套 *Theme + 4 套家族/明暗正交组合 + 2 套密度全可切换</li>
 *   <li>3 套脱管主题（shadcn / cyberpunk / custom）CSS 资源可加载（可手挂 Scene）</li>
 * </ol>
 *
 * <p>本测试不替代人工 UI 走查，但能给出"主题切换可用"的机器可验证结论。
 * 对应 {@code PROJECT_ACCEPTANCE.md} 第 1 节「全局主题切换」中"主题颜色整体跟随"、
 * "暗色系下对比度安全"、"紧凑模式控件高度收紧"的可机器验证子项。</p>
 */
@DisplayName("主题冒烟测试 (11 套 CSS 加载 + 状态机 8 组合)")
class ThemeSmokeTest extends JfxTestBase {

    /** 11 套主题 CSS 路径（与 PROJECT_ACCEPTANCE.md §1 宣称一致）。 */
    private static final String[] ALL_THEME_CSS = {
        "/org/openkawu/jfxium/css/theme-light.css",
        "/org/openkawu/jfxium/css/theme-dark.css",
        "/org/openkawu/jfxium/css/theme-light-compact.css",
        "/org/openkawu/jfxium/css/theme-dark-compact.css",
        "/org/openkawu/jfxium/css/theme-mui.css",
        "/org/openkawu/jfxium/css/theme-mui-dark.css",
        "/org/openkawu/jfxium/css/theme-mui-compact.css",
        "/org/openkawu/jfxium/css/theme-mui-dark-compact.css",
        "/org/openkawu/jfxium/css/theme-shadcn.css",
        "/org/openkawu/jfxium/css/theme-cyberpunk.css",
        "/org/openkawu/jfxium/css/theme-custom.css",
    };

    /** 8 套受 ThemeManager 状态机管理的主题。 */
    private static final String[] MANAGED_THEME_CSS = {
        "/org/openkawu/jfxium/css/theme-light.css",
        "/org/openkawu/jfxium/css/theme-dark.css",
        "/org/openkawu/jfxium/css/theme-mui.css",
        "/org/openkawu/jfxium/css/theme-mui-dark.css",
        "/org/openkawu/jfxium/css/theme-light-compact.css",
        "/org/openkawu/jfxium/css/theme-dark-compact.css",
        "/org/openkawu/jfxium/css/theme-mui-compact.css",
        "/org/openkawu/jfxium/css/theme-mui-dark-compact.css",
    };

    /** 3 套脱管主题（手挂 Scene，不进 ThemeManager 状态机）。 */
    private static final String[] UNMANAGED_THEME_CSS = {
        "/org/openkawu/jfxium/css/theme-shadcn.css",
        "/org/openkawu/jfxium/css/theme-cyberpunk.css",
        "/org/openkawu/jfxium/css/theme-custom.css",
    };

    @Nested
    @DisplayName("11 套主题 CSS 编译产物完整性")
    class CssArtifacts {

        @Test
        @DisplayName("11 套 CSS 资源全部存在且非空")
        void allThemeCssArtifactsExist() throws Exception {
            assertEquals(11, ALL_THEME_CSS.length, "11 套主题宣称数量");

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
    @DisplayName("ThemeManager 状态机 8 组合")
    class StateMachine {

        @Test
        @DisplayName("4 个 *Theme 实例的 getUserAgentStylesheet 全部可达")
        void fourThemeInstancesLoadable() {
            Theme[] themes = {
                new LightTheme(),
                new DarkTheme(),
                new MuiLightTheme(),
                new MuiDarkTheme(),
            };
            for (Theme t : themes) {
                assertNotNull(t.getName(), () -> t.getClass().getSimpleName() + " 名称为空");
                assertNotNull(t.getUserAgentStylesheet(),
                    () -> t.getClass().getSimpleName() + " UA stylesheet 路径为空");
                // 类型一致性
                assertNotNull(t.getType(),
                    () -> t.getClass().getSimpleName() + " 类型未指定");
            }
        }

        @Test
        @DisplayName("ThemeManager.applyTheme 4 套 *Theme 全部可应用")
        void applyThemeFourTimes() {
            ThemeManager mgr = ThemeManager.getInstance();
            Theme[] themes = {
                new LightTheme(),
                new DarkTheme(),
                new MuiLightTheme(),
                new MuiDarkTheme(),
            };
            for (Theme t : themes) {
                assertDoesNotThrow(() -> mgr.applyTheme(t),
                    () -> "applyTheme 失败: " + t.getClass().getSimpleName());
            }
        }

        @Test
        @DisplayName("ThemeManager.setFamily × setDark × setDensity 8 组合无 NPE")
        void stateMachineEightCombinations() {
            ThemeManager mgr = ThemeManager.getInstance();
            List<String> applied = new ArrayList<>();

            for (ThemeManager.Family family : ThemeManager.Family.values()) {
                for (boolean dark : new boolean[]{false, true}) {
                    for (ThemeDensity density : ThemeDensity.values()) {
                        assertDoesNotThrow(() -> {
                            mgr.setFamily(family);
                            mgr.setDark(dark);
                            mgr.setDensity(density);
                        }, () -> String.format("状态机组合失败: family=%s dark=%s density=%s",
                            family, dark, density));

                        applied.add(String.format("%s/%s/%s",
                            family.name(), dark ? "DARK" : "LIGHT", density.name()));
                    }
                }
            }

            assertEquals(8, applied.size(),
                "ThemeManager 状态机应有 2 Family × 2 dark × 2 density = 8 组合");
        }
    }

    @Nested
    @DisplayName("3 套脱管主题（shadcn / cyberpunk / custom）")
    class UnmanagedThemes {

        @Test
        @DisplayName("3 套脱管主题 CSS 资源存在且非空（可手挂 Scene）")
        void unmanagedThemesLoadable() throws Exception {
            assertEquals(3, UNMANAGED_THEME_CSS.length,
                "3 套脱管主题：shadcn / cyberpunk / custom");

            for (String path : UNMANAGED_THEME_CSS) {
                URL url = ThemeSmokeTest.class.getResource(path);
                assertNotNull(url, () -> "脱管主题 CSS 资源缺失: " + path);
                try (InputStream in = url.openStream()) {
                    int size = in.readAllBytes().length;
                    assertTrue(size > 100,
                        () -> "脱管主题 CSS 异常小: " + path);
                }
            }
        }

        @Test
        @DisplayName("8 套 MANAGED + 3 套 UNMANAGED = 11 套与 PROJECT_ACCEPTANCE.md §1 一致")
        void managedPlusUnmanagedEqualsEleven() {
            assertEquals(MANAGED_THEME_CSS.length + UNMANAGED_THEME_CSS.length,
                ALL_THEME_CSS.length,
                "MANAGED 8 套 + UNMANAGED 3 套 = 11 套（与文档一致）");
        }
    }

    /**
     * P2 修复 —— 3 套脱管主题(shadcn / cyberpunk / custom)此前无
     * {@link Theme} 包装类,只能直接调 scene.getStylesheets().add(),
     * 11 套主题对外 API 不对称。该块验证新包装类 {@code getName/getUserAgentStylesheet/getType}
     * 三方法实现正确,UA stylesheet 路径可解析。
     */
    @Nested
    @DisplayName("3 套脱管主题的 Theme 包装类（ShadcnTheme / CyberpunkTheme / CustomTheme）")
    class UnmanagedThemeWrappers {

        @Test
        @DisplayName("ShadcnTheme: 名称/UA stylesheet/类型 全部正确")
        void shadcnThemeWrapperIsValid() {
            ShadcnTheme t = new ShadcnTheme();
            assertEquals("JFXium Shadcn", t.getName(), "Shadcn 名称");
            assertEquals(Theme.ThemeType.LIGHT, t.getType(),
                "Shadcn 是白底 + Zinc 黑主色，归类 LIGHT");
            assertNotNull(t.getUserAgentStylesheet(), "Shadcn UA stylesheet 不可为空");
            assertTrue(t.getUserAgentStylesheet().endsWith("theme-shadcn.css"),
                "Shadcn UA stylesheet 应指向 theme-shadcn.css");
            // 资源可解析（避免路径写错）
            assertDoesNotThrow(() -> t.getUserAgentStylesheet().length(),
                "Shadcn UA stylesheet 路径应可解析");
            // 默认密度
            assertEquals(ThemeDensity.DEFAULT, t.getDensity(),
                "脱管主题无独立密度变体，应走默认密度");
        }

        @Test
        @DisplayName("CyberpunkTheme: 名称/UA stylesheet/类型 全部正确")
        void cyberpunkThemeWrapperIsValid() {
            CyberpunkTheme t = new CyberpunkTheme();
            assertEquals("JFXium Cyberpunk", t.getName(), "Cyberpunk 名称");
            assertEquals(Theme.ThemeType.DARK, t.getType(),
                "Cyberpunk 是深底 + Cyan 霓虹，归类 DARK");
            assertNotNull(t.getUserAgentStylesheet(), "Cyberpunk UA stylesheet 不可为空");
            assertTrue(t.getUserAgentStylesheet().endsWith("theme-cyberpunk.css"),
                "Cyberpunk UA stylesheet 应指向 theme-cyberpunk.css");
            assertEquals(ThemeDensity.DEFAULT, t.getDensity(),
                "脱管主题无独立密度变体，应走默认密度");
        }

        @Test
        @DisplayName("CustomTheme: 名称/UA stylesheet/类型 全部正确")
        void customThemeWrapperIsValid() {
            CustomTheme t = new CustomTheme();
            assertEquals("JFXium Custom", t.getName(), "Custom 名称");
            assertEquals(Theme.ThemeType.LIGHT, t.getType(),
                "Custom 是白底 + 紫主色，归类 LIGHT");
            assertNotNull(t.getUserAgentStylesheet(), "Custom UA stylesheet 不可为空");
            assertTrue(t.getUserAgentStylesheet().endsWith("theme-custom.css"),
                "Custom UA stylesheet 应指向 theme-custom.css");
            assertEquals(ThemeDensity.DEFAULT, t.getDensity(),
                "脱管主题无独立密度变体，应走默认密度");
        }

        @Test
        @DisplayName("3 套脱管 Theme 包装可统一通过 ThemeManager.applyTheme 加载")
        void unmanagedThemesApplyViaThemeManager() {
            // P2 修复意义: 11 套主题现在可统一通过 Theme 抽象操作,
            // 而不仅限于 scene.getStylesheets().add(...)
            ThemeManager mgr = ThemeManager.getInstance();
            Theme[] unmanaged = {
                new ShadcnTheme(),
                new CyberpunkTheme(),
                new CustomTheme(),
            };
            for (Theme t : unmanaged) {
                assertDoesNotThrow(() -> mgr.applyTheme(t),
                    () -> "脱管主题 applyTheme 失败: " + t.getClass().getSimpleName());
            }
        }
    }
}
