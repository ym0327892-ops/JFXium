package org.openkawu.jfxium.core.theme;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ThemeManager")
class ThemeManagerTest extends JfxTestBase {

    @Test
    @DisplayName("切换主色时只保留一份 accent data URI stylesheet")
    void setPrimaryColor_keepsSingleAccentStylesheet() {
        ThemeManager manager = ThemeManager.getInstance();
        Scene scene = new Scene(new StackPane());

        runOnFxThreadAndWait(() -> {
            manager.registerScene(scene);
            manager.setPrimaryColor("#ff5722");
            manager.setPrimaryColor("#722ed1");
        });

        long accentStylesheetCount = scene.getStylesheets().stream()
                .filter(stylesheet -> stylesheet.startsWith("data:text/css;base64,"))
                .count();

        assertEquals(1, accentStylesheetCount);

        runOnFxThreadAndWait(() -> manager.setPrimaryColor(ThemeColor.Preset.BLUE));
    }

    @Test
    @DisplayName("applyTheme 每次状态变更都通知监听器（与 setDark / setFamily 语义一致）")
    void applyTheme_notifiesListenersOncePerStateChange() {
        ThemeManager manager = ThemeManager.getInstance();
        int[] calls = {0};
        Runnable listener = () -> calls[0]++;

        runOnFxThreadAndWait(() -> {
            manager.addListener(listener);
            try {
                manager.applyTheme(new LightTheme());
                manager.applyTheme(new DarkTheme());
                manager.applyTheme(new LightTheme());
            } finally {
                manager.removeListener(listener);
            }
        });

        // 三次相互不同的状态（明→暗→明），每次恰好通知一次：少了会留下陈旧 UI，
        // 多了会让监听者（如 CodeBlockAnt 的 AUTO 配色）重复重算。
        assertEquals(3, calls[0]);
    }

    @Test
    @DisplayName("切密度只通知一次（applyTheme 已负责通知，不得重复）")
    void setDensity_notifiesExactlyOnce() {
        ThemeManager manager = ThemeManager.getInstance();
        int[] calls = {0};
        Runnable listener = () -> calls[0]++;

        runOnFxThreadAndWait(() -> {
            manager.addListener(listener);
            try {
                manager.setDensity(ThemeDensity.COMPACT);
                manager.setDensity(ThemeDensity.COMPACT); // 同值 → 忽略
            } finally {
                manager.removeListener(listener);
                manager.setDensity(ThemeDensity.DEFAULT);
            }
        });

        assertEquals(1, calls[0]);
    }

    @Test
    @DisplayName("applyTheme 同步 family / dark 两轴，监听者读到的即当前主题")
    void applyTheme_syncsFamilyAndDarkAxes() {
        ThemeManager manager = ThemeManager.getInstance();
        String[] seen = new String[2];
        Runnable listener = () -> seen[0] = manager.getActiveThemeName();

        runOnFxThreadAndWait(() -> {
            manager.addListener(listener);
            try {
                manager.applyTheme(new FluentTheme());
                assertTrue(manager.getCurrentTheme().hasFixedAccent(),
                        "Fluent 系必须声明固定 accent，否则运行时主色注入会覆盖品牌蓝");
                assertEquals(FluentTheme.class, manager.getCurrentTheme().getClass());

                manager.setDark(true);
                assertEquals(FluentDarkTheme.class, manager.getCurrentTheme().getClass(),
                        "Fluent 参与 dark 轴：setDark 不得回退到 Ant Design 系");
            } finally {
                manager.removeListener(listener);
                manager.applyTheme(new LightTheme());
            }
        });

        assertEquals("JFXium Fluent Dark", seen[0], "监听器必须在状态同步之后才被调用");
        assertFalse(manager.getCurrentTheme().hasFixedAccent());
    }
}
