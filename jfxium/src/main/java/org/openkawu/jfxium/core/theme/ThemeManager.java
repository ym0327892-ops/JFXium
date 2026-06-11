package org.openkawu.jfxium.core.theme;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * JFXium 主题管理器 —— 负责全局主题切换、主题色注入和 Scene/Region 注册。
 *
 * <p>核心能力：</p>
 * <ul>
 *   <li>二维主题切换：家族（Ant/MUI）× 明暗（light/dark）× 密度（default/compact）</li>
 *   <li>运行时动态改主色（accent color），支持预设色板或任意 hex 颜色</li>
 *   <li>Scene / Region 注册机制 —— 主题切换时自动刷新所有已注册节点</li>
 * </ul>
 *
 * <p>设计参考 <a href="https://github.com/mkpaz/atlantafx">AtlantaFX</a> 和 Ant Design。</p>
 */
public class ThemeManager {

    private static ThemeManager instance;

    // 四个 Theme 实现均为无状态，缓存为单例避免反复 new
    private static final Theme LIGHT_THEME = new LightTheme();
    private static final Theme DARK_THEME = new DarkTheme();
    private static final Theme MUI_LIGHT_THEME = new MuiLightTheme();
    private static final Theme MUI_DARK_THEME = new MuiDarkTheme();

    private Theme currentTheme;
    private ThemeColor currentThemeColor;
    private final List<Scene> registeredScenes = new ArrayList<>();
    private final List<Region> registeredRegions = new ArrayList<>();

    /** 主题家族（设计风格）。 */
    public enum Family {
        ANT_DESIGN("Ant Design"),
        MUI("MUI");

        private final String displayName;
        Family(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    // 两个正交维度的当前状态（family × dark），用于组合出具体 Theme（PC UI 规范 §12.3）
    // 密度（density）已从状态机拆出为独立维度，通过 setDensity() 切换（见 §15.3）。
    private Family currentFamily = Family.ANT_DESIGN;
    private boolean dark = false;
    private ThemeDensity density = ThemeDensity.DEFAULT;

    private ThemeManager() {
        this.currentTheme = LIGHT_THEME;
        this.currentThemeColor = new ThemeColor(ThemeColor.Preset.BLUE);
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            synchronized (ThemeManager.class) {
                if (instance == null) {
                    instance = new ThemeManager();
                }
            }
        }
        return instance;
    }

    /** 获取当前主题。 */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /** 获取当前主题色。 */
    public ThemeColor getCurrentThemeColor() {
        return currentThemeColor;
    }

    /**
     * 注册 Scene —— 主题切换 / 主色更改时自动刷新，并立即注入当前 accent 和密度。
     * 在 {@code stage.setScene(scene)} 之后调用。
     */
    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            // 立即为新 Scene 注入当前 accent 色和密度状态，
            // 确保无论注册时序，新 Scene 都能拿到当前主题的全部状态。
            applyAccentToScene(scene);
            applyDensityToScene(scene);
        }
    }

    /** 注册 Region —— 主题切换 / 主色更改时自动刷新（用于非 Scene 管理的独立节点）。 */
    public void registerRegion(Region region) {
        if (!registeredRegions.contains(region)) {
            registeredRegions.add(region);
        }
    }

    /**
     * 全局应用主题 —— 调用 {@link javafx.application.Application#setUserAgentStylesheet(String)}。
     * <p>切换主题后会自动重新应用当前主题色（accent），避免「换风格 / 换明暗后主题色丢失」。</p>
     */
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
        // 关键：userAgentStylesheet 重置后，之前 inline 注入的 accent 色阶会被覆盖，
        // 必须重新应用一次，否则切风格 / 切明暗后主题色回退到 CSS 默认蓝。
        applyPrimaryColorToAll();
        // 密度同掉重应用：jfx-compact styleClass 在 scene.getRoot() 上，UA CSS 重置不影响
        // styleClass 列表，但 applyTheme() 后再走一次保证状态唯一源仍是 ThemeManager。
        // DEFAULT 状态等价于「移除 jfx-compact」，幂等 no-op。
        applyDensityToAll();
    }

    // ============================================================
    // 主题风格 / 明暗 / 密度三维组合切换（M19.47）
    // ============================================================

    /**
     * 根据当前 family × dark 二维状态组合出具体 Theme 并应用。
     * 4 个 Theme 类 = 2 家族(Ant/MUI) × 2 明暗。密度不再参与主题类选择（见 §15.3）。
     */
    private void applyComposite() {
        Theme theme = switch (currentFamily) {
            case ANT_DESIGN -> dark ? DARK_THEME : LIGHT_THEME;
            case MUI        -> dark ? MUI_DARK_THEME : MUI_LIGHT_THEME;
        };
        applyTheme(theme);
    }

    /** 切换主题家族（设计风格），保持明暗 / 密度 / 主题色不变。 */
    public void setFamily(Family family) {
        if (family == null || family == currentFamily) return;
        this.currentFamily = family;
        applyComposite();
        notifyListeners();
    }

    /** 设置明暗，保持家族 / 密度 / 主题色不变。 */
    public void setDark(boolean dark) {
        if (dark == this.dark) return;
        this.dark = dark;
        applyComposite();
        notifyListeners();
    }

    /**
     * 切换密度（PC UI 规范 §12.2 / §15.3）。
     * 保持家族 / 明暗 / 主题色不变；密度通过 CSS 变量注入而非独立主题类实现。
     *
     * @param density {@link ThemeDensity#DEFAULT} 或 {@link ThemeDensity#COMPACT}；null 忽略
     */
    public void setDensity(ThemeDensity density) {
        if (density == null || density == this.density) return;
        this.density = density;
        applyDensityToAll();
        notifyListeners();
    }

    /** 获取当前密度。 */
    public ThemeDensity getDensity() {
        return density;
    }

    /**
     * @deprecated 自 v1.0 起密度升级为一等状态，使用 {@link #setDensity(ThemeDensity)}。
     *             保留 2 个版本以兼容旧 API，内部委托给 setDensity()。
     */
    @Deprecated
    public void setCompactDensity(boolean compact) {
        setDensity(compact ? ThemeDensity.COMPACT : ThemeDensity.DEFAULT);
    }

    public Family getCurrentFamily() { return currentFamily; }
    public boolean isDark() { return dark; }

    /**
     * 运行时动态更换主色（accent）。
     * 以 data-URI 方式注入 CSS 变量到所有已注册 Scene 的样式表中。
     *
     * @param color 十六进制颜色字符串（如 "#1677ff"、"#722ed1"）
     */
    public void setPrimaryColor(String color) {
        if (color == null || color.equals(currentThemeColor.getHexColor())) return;
        this.currentThemeColor.setHexColor(color);
        applyPrimaryColorToAll();
        notifyListeners();
    }

    /** 按预设色板更换主色（accent）。 */
    public void setPrimaryColor(ThemeColor.Preset preset) {
        setPrimaryColor(preset.getHexColor());
    }

    /** 将当前主色应用到所有已注册的 Scene 和 Region。 */
    private void applyPrimaryColorToAll() {
        String hex = currentThemeColor.getHexColor();
        Theme.ThemeType currentType = currentTheme.getType();

        // 短路：hex + 明暗类型均未变化时复用缓存 data-URI
        String dataUri;
        if (hex.equals(cachedAccentHex) && currentType == cachedAccentThemeType && accentStylesheet != null) {
            dataUri = accentStylesheet;
        } else {
            String[] lightScale = currentThemeColor.generateColorScale();
            String[] darkScale = currentThemeColor.generateDarkColorScale();

            boolean isDark = currentTheme.getType() == Theme.ThemeType.DARK;
            String[] scale = isDark ? darkScale : lightScale;

            StringBuilder rule = new StringBuilder(".root {");
            for (int i = 0; i < 10; i++) {
                rule.append(String.format("-color-accent-%d: %s;", i, scale[i]));
            }
            rule.append("-color-accent-emphasis:").append(scale[5]).append(";");
            rule.append("-color-accent-hover:").append(scale[0]).append(";");
            rule.append("-color-accent-active:").append(scale[6]).append(";");
            rule.append("-color-accent-muted:").append(scale[2]).append(";");
            rule.append("-color-accent-subtle:").append(scale[0]).append(";");
            rule.append("}");

            dataUri = "data:text/css;base64,"
                    + Base64.getEncoder().encodeToString(
                            rule.toString().getBytes(StandardCharsets.UTF_8));

            cachedAccentHex = hex;
            cachedAccentThemeType = currentType;
            accentStylesheet = dataUri;
        }

        // 替换每个 scene 上一次注入的 accent stylesheet（先移除旧的，再加新的）
        String oldUri = accentStylesheet;
        for (Scene scene : registeredScenes) {
            scene.getStylesheets().remove(oldUri);
            scene.getStylesheets().add(dataUri);
        }
    }

    /** 上一次注入的 accent data-URI stylesheet，用于切换时移除旧的。 */
    private String accentStylesheet;

    /** 生成当前 accentStylesheet 所用的 hex 值，用于短路：hex 不变则复用缓存 data-URI。 */
    private String cachedAccentHex;
    /** 生成 cachedAccentHex 时的明暗类型，用于检测 light↔dark 切换需重算色阶。 */
    private Theme.ThemeType cachedAccentThemeType;

    /** 状态变更监听器列表（线程安全）。 */
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /**
     * 将当前 density 应用到所有已注册的 Scene（PC UI 规范 §12.2 / §15.3 实施）。
     *
     * <p>与 P2 初版不同：不再用 data-URI 注入 CSS 变量。
     * LESS 用的是编译期变量（{@code @control-height}），运行时注入 JavaFX looked-up color
     * （{@code -control-height}）不会被任何规则读取，注入等于 no-op（见 PROJECT_BUG.md #68 根因）。</p>
     *
     * <p>新方案：直接在 scene.getRoot() 上挂/卸 {@code jfx-compact} 修饰类。
     * LESS 端在 theme-base.less 末尾提供 {@code .root.jfx-compact { ... }} 覆盖块，
     * 用字面量 px 值覆盖控件高 / 行高 / padding，比运行时 looked-up color 链路短、稳。</p>
     *
     * <p>幂等性：</p>
     * <ul>
     *   <li>DEFAULT → 从 root 卸 jfx-compact，CSS 走 LESS 默认值</li>
     *   <li>COMPACT → 在 root 挂 jfx-compact，CSS 走 .root.jfx-compact 覆盖</li>
     *   <li>applyTheme() 后调用是 no-op（UA CSS 重置不碰 root 的 styleClass 列表）</li>
     * </ul>
     */
    private void applyDensityToAll() {
        boolean compact = density == ThemeDensity.COMPACT;
        for (Scene scene : registeredScenes) {
            if (scene.getRoot() == null) continue;
            ObservableList<String> classes = scene.getRoot().getStyleClass();
            if (compact) {
                if (!classes.contains(JfxStyles.DENSITY_COMPACT)) {
                    classes.add(JfxStyles.DENSITY_COMPACT);
                }
            } else {
                classes.remove(JfxStyles.DENSITY_COMPACT);
            }
        }
    }

    /** 将当前 accent data-URI 注入单个 Scene（用于 registerScene 即时补齐）。 */
    private void applyAccentToScene(Scene scene) {
        if (accentStylesheet != null) {
            scene.getStylesheets().remove(accentStylesheet);
            scene.getStylesheets().add(accentStylesheet);
        }
    }

    /** 将当前密度应用到单个 Scene（用于 registerScene 即时补齐）。 */
    private void applyDensityToScene(Scene scene) {
        if (scene.getRoot() == null) return;
        boolean compact = density == ThemeDensity.COMPACT;
        ObservableList<String> classes = scene.getRoot().getStyleClass();
        if (compact) {
            if (!classes.contains(JfxStyles.DENSITY_COMPACT)) {
                classes.add(JfxStyles.DENSITY_COMPACT);
            }
        } else {
            classes.remove(JfxStyles.DENSITY_COMPACT);
        }
    }

    // ============================================================
    // 公共 API：refresh / 监听器
    // ============================================================

    /**
     * 对已注册的 Scene 重应用当前 accent 色和密度（不重设 UA CSS）。
     * 适用于外部修改了 scene 的 stylesheets 后需要恢复主题状态的场景。
     */
    public void refresh() {
        applyPrimaryColorToAll();
        applyDensityToAll();
    }

    /**
     * 注册状态变更监听器。在 Family / Dark / Density / PrimaryColor 任一维度变更时触发。
     * 监听器在 JavaFX Application Thread 上执行。
     */
    public void addListener(Runnable listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /** 移除状态变更监听器。 */
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    /** 通知所有监听器状态已变更。 */
    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    /** 切换亮色 / 暗色主题（保持家族 / 密度 / 主题色不变）。 */
    public void toggleTheme() {
        setDark(!dark);
    }

    /**
     * @deprecated 使用 {@link #setDensity(ThemeDensity)} 代替。
     */
    @Deprecated
    public void toggleCompact() {
        setDensity(isCompact() ? ThemeDensity.DEFAULT : ThemeDensity.COMPACT);
    }

    /**
     * @deprecated 使用 {@code getDensity() == ThemeDensity.COMPACT} 代替。
     */
    @Deprecated
    public boolean isCompact() {
        return density == ThemeDensity.COMPACT;
    }

    /** 切换到 MUI 亮色主题家族。 */
    public void switchToMui() {
        if (currentFamily == Family.MUI && !dark) return;
        this.currentFamily = Family.MUI;
        this.dark = false;
        applyComposite();
        notifyListeners();
    }

    /** 切换到 MUI 暗色主题家族。 */
    public void switchToMuiDark() {
        if (currentFamily == Family.MUI && dark) return;
        this.currentFamily = Family.MUI;
        this.dark = true;
        applyComposite();
        notifyListeners();
    }

    /** 获取当前主题家族名称。 */
    public String getCurrentThemeFamily() {
        return currentFamily.getDisplayName();
    }
}
