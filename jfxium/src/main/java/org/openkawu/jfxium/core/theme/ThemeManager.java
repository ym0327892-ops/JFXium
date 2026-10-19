package org.openkawu.jfxium.core.theme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * JFXium 主题管理器 —— 负责全局主题切换、主题色注入和 Scene/Region 注册。
 *
 * <p>核心能力：</p>
 * <ul>
 *   <li>明暗切换（light/dark）× 密度（default/compact）</li>
 *   <li>运行时动态改主色（accent color），支持预设色板或任意 hex 颜色</li>
 *   <li>Scene / Region 注册机制 —— 主题切换时自动刷新所有已注册节点</li>
 * </ul>
 *
 * <p>设计参考 <a href="https://github.com/mkpaz/atlantafx">AtlantaFX</a> 和 Ant Design。</p>
 */
public class ThemeManager {

    private static ThemeManager instance;

    // 四个 Theme 实现均为无状态，缓存为单例避免反复 new
    // 六个 Theme 实现均为无状态，缓存为单例避免反复 new
    private static final Theme LIGHT_THEME = new LightTheme();
    private static final Theme DARK_THEME = new DarkTheme();
    private static final Theme FLUENT_THEME = new FluentTheme();
    private static final Theme FLUENT_DARK_THEME = new FluentDarkTheme();

    private Theme currentTheme;
    private ThemeColor currentThemeColor;
    private ThemeColor.Preset currentPrimaryPreset;
    private final List<Scene> registeredScenes = new ArrayList<>();
    private final List<Region> registeredRegions = new ArrayList<>();

    /**
     * 主题家族（设计风格）。
     *
     * <p>{@link #ANT_DESIGN}（历史遗留名）承载 Light/Dark/Custom/Tool 四个主题；
     * {@link #FLUENT} 承载 Fluent / Fluent Dark。家族 × 明暗两轴经
     * {@link #applyComposite()} 组合出具体 {@link Theme}。</p>
     */
    public enum Family {
        ANT_DESIGN("Ant Design") {
            @Override public String styleName(boolean dark) {
                return "Ant Design";
            }
            @Override public Theme theme(boolean dark) {
                return dark ? DARK_THEME : LIGHT_THEME;
            }
        },
        FLUENT("Fluent 2") {
            @Override public String styleName(boolean dark) {
                return "Fluent 2" + (dark ? " Dark" : "");
            }
            @Override public Theme theme(boolean dark) {
                return dark ? FLUENT_DARK_THEME : FLUENT_THEME;
            }
        };

        private final String displayName;
        Family(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }

        /** 该家族在给定明暗下的设计风格名称(用于状态展示)。 */
        public abstract String styleName(boolean dark);

        /** 该家族在给定明暗下对应的具体主题实例(无状态,可复用单例)。 */
        public abstract Theme theme(boolean dark);
    }

    // 两个正交维度的当前状态（family × dark），用于组合出具体 Theme（PC UI 规范 §12.3）
    // 密度（density）已从状态机拆出为独立维度，通过 setDensity() 切换（见 §15.3）。
    private Family currentFamily = Family.ANT_DESIGN;
    private boolean dark = false;
    private ThemeDensity density = ThemeDensity.DEFAULT;

    private ThemeManager() {
        this.currentTheme = LIGHT_THEME;
        this.currentPrimaryPreset = ThemeColor.Preset.BLUE;
        this.currentThemeColor = new ThemeColor(currentPrimaryPreset);
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

    /** 获取当前主色预设；若用户传入了自定义 hex，则返回 null。 */
    public ThemeColor.Preset getCurrentPrimaryPreset() {
        return currentPrimaryPreset;
    }

    /**
     * 注册 Scene —— 主题切换 / 主色更改时自动刷新，并立即注入当前 accent 和密度。
     * 在 {@code stage.setScene(scene)} 之后调用。
     */
    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            // 立即为新 Scene 注入当前 accent 色。密度已随全局 UA 样式表生效，无需单独处理。
            applyAccentToScene(scene);
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
     * <p>按当前密度选择样式表：{@link ThemeDensity#COMPACT} 时取主题的紧凑变体
     * （{@code theme-*-compact.css}），否则取默认变体。切换后自动重新应用主题色（accent），
     * 避免「换风格 / 换明暗 / 换密度后主题色丢失」。</p>
     */
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet(density));
        // 关键：userAgentStylesheet 重置后，之前 inline 注入的 accent 色阶会被覆盖，
        // 必须重新应用一次，否则切风格 / 切明暗后主题色回退到 CSS 默认蓝。
        applyPrimaryColorToAll();
        // 同步 family / dark 两轴，保证「直接 applyTheme 某具体主题」后
        // setDark / setFamily / getCurrentFamily 仍然自洽（不再回退到 Ant 蓝）。
        this.dark = theme.getType() == Theme.ThemeType.DARK;
        this.currentFamily = familyOf(theme);
        // applyTheme 与 setDark / setFamily 改的是同一批状态，通知语义必须一致；
        // 否则「直接 applyTheme」路径下监听者（如 CodeBlockAnt 的 AUTO 配色、
        // 示例页的状态回显）会一直拿到陈旧状态。
        notifyListeners();
    }

    /** 由具体主题实例反查其所属家族（按类匹配，允许非单例实例）；未知实现归入 ANT_DESIGN。 */
    private static Family familyOf(Theme theme) {
        Class<?> cls = theme.getClass();
        for (Family f : Family.values()) {
            if (f.theme(false).getClass() == cls || f.theme(true).getClass() == cls) {
                return f;
            }
        }
        return Family.ANT_DESIGN;
    }

    // ============================================================
    // 主题风格 / 明暗 / 密度三维组合切换（M19.47）
    // ============================================================

    /**
     * 根据当前 family × dark 二维状态组合出具体 Theme 并应用。
     * 家族决定候选主题（Ant Design / Fluent），dark 决定取候选中的亮/暗变体。
     * 密度不再参与主题类选择（见 §15.3）。
     */
    private void applyComposite() {
        applyTheme(currentFamily.theme(dark));
    }

    /**
     * 当前生效设计风格的展示名（供状态卡 / 设置页读取）。
     *
     * <p>取自当前主题自身的 {@link Theme#getName()}，而不是遗留的 family 枚举名 ——
     * 后者在 Fluent 主题下会继续显示「Ant Design」。</p>
     */
    public String getActiveThemeName() {
        return currentTheme == null || currentTheme.getName() == null
                ? currentFamily.getDisplayName()
                : currentTheme.getName();
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
     * 切换密度（PC UI 规范 §12.2）。
     * 保持家族 / 明暗 / 主题色不变；通过重新应用当前主题的密度对应样式表实现
     * （{@link ThemeDensity#COMPACT} → {@code theme-*-compact.css}）。
     *
     * @param density {@link ThemeDensity#DEFAULT} 或 {@link ThemeDensity#COMPACT}；null 忽略
     */
    public void setDensity(ThemeDensity density) {
        if (density == null || density == this.density) return;
        this.density = density;
        // 重新应用当前主题：applyTheme 按新密度挑整套 UA 样式表，并负责发出变更通知。
        applyTheme(currentTheme);
    }

    /** 获取当前密度。 */
    public ThemeDensity getDensity() {
        return density;
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
        applyPrimaryColor(color, ThemeColor.Preset.fromHex(color));
    }

    /** 按预设色板更换主色（accent）。 */
    public void setPrimaryColor(ThemeColor.Preset preset) {
        if (preset == null) {
            return;
        }
        applyPrimaryColor(preset.getHexColor(), preset);
    }

    private void applyPrimaryColor(String color, ThemeColor.Preset preset) {
        if (color == null) {
            return;
        }
        if (Objects.equals(color, currentThemeColor.getHexColor()) && Objects.equals(preset, currentPrimaryPreset)) {
            return;
        }
        this.currentThemeColor.setHexColor(color);
        this.currentPrimaryPreset = preset;
        applyPrimaryColorToAll();
        notifyListeners();
    }

    /** 将当前主色应用到所有已注册的 Scene 和 Region。 */
    private void applyPrimaryColorToAll() {
        String hex = currentThemeColor.getHexColor();
        Theme.ThemeType currentType = currentTheme.getType();
        String previousAccentStylesheet = accentStylesheet;

        // 固定 accent 的主题（如 Fluent）自带完整品牌色阶，注入会把它覆盖成
        // 通用预设蓝，故这里「只撤旧、不注新」。
        if (currentTheme.hasFixedAccent()) {
            for (Scene scene : registeredScenes) {
                if (previousAccentStylesheet != null) {
                    scene.getStylesheets().remove(previousAccentStylesheet);
                }
            }
            accentStylesheet = null;
            cachedAccentHex = null;
            cachedAccentThemeType = null;
            cachedAccentFixed = true;
            return;
        }
        // 从「固定 accent 主题」切回可注入主题时，绕过缓存强制重建 data-URI。
        if (cachedAccentFixed) {
            cachedAccentHex = null;
            cachedAccentThemeType = null;
            cachedAccentFixed = false;
        }

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
        }

        // 替换每个 scene 上一次注入的 accent stylesheet（先移除旧的，再加新的）
        for (Scene scene : registeredScenes) {
            if (previousAccentStylesheet != null) {
                scene.getStylesheets().remove(previousAccentStylesheet);
            }
            scene.getStylesheets().remove(dataUri);
            scene.getStylesheets().add(dataUri);
        }
        accentStylesheet = dataUri;
    }

    /** 上一次注入决策是否为「固定 accent 主题」（用于切回可注入主题时强制重建 data-URI）。 */
    private boolean cachedAccentFixed;
    /** 上一次注入的 accent data-URI stylesheet，用于切换时移除旧的。 */
    private String accentStylesheet;

    /** 生成当前 accentStylesheet 所用的 hex 值，用于短路：hex 不变则复用缓存 data-URI。 */
    private String cachedAccentHex;
    /** 生成 cachedAccentHex 时的明暗类型，用于检测 light↔dark 切换需重算色阶。 */
    private Theme.ThemeType cachedAccentThemeType;

    /** 状态变更监听器列表（线程安全）。 */
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /** 将当前 accent data-URI 注入单个 Scene（用于 registerScene 即时补齐）。 */
    private void applyAccentToScene(Scene scene) {
        if (accentStylesheet != null) {
            scene.getStylesheets().remove(accentStylesheet);
            scene.getStylesheets().add(accentStylesheet);
        }
    }

    // ============================================================
    // 公共 API：refresh / 监听器
    // ============================================================

    /**
     * 对已注册的 Scene 重应用当前主题（含密度）和 accent 色。
     * 适用于外部修改了 scene 的 stylesheets 后需要恢复主题状态的场景。
     */
    public void refresh() {
        applyTheme(currentTheme);
    }

    /**
     * @deprecated 名字有歧义且对 Fluent 主题会误报。请改用
     *             {@link #getCurrentFamily()} 取家族对象，
     *             或 {@link #getActiveThemeName()} 取当前主题展示名。
     */
    @Deprecated(since = "1.35.0", forRemoval = false)
    public String getCurrentThemeFamily() {
        return currentFamily.getDisplayName();
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

}
