package org.openkawu.jfxium.core.theme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

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
        this.currentTheme = new LightTheme();
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
     * 注册 Scene —— 主题切换 / 主色更改时自动刷新。
     * 在 {@code stage.setScene(scene)} 之后调用。
     */
    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
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
        // 密度同样需要重应用：densityStylesheet 在 scene.getStylesheets() 上，UA CSS 重置不被清，
        // 但为了保持 applyTheme() 后状态唯一源仍是 ThemeManager，这里依然走 applyDensityToAll()。
        // 该方法是幂等的：DEFAULT 状态下只是 remove 之前的 COMPACT，no-op。
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
            case ANT_DESIGN -> dark ? new DarkTheme() : new LightTheme();
            case MUI        -> dark ? new MuiDarkTheme() : new MuiTheme();
        };
        applyTheme(theme);
    }

    /** 切换主题家族（设计风格），保持明暗 / 密度 / 主题色不变。 */
    public void setFamily(Family family) {
        if (family != null) {
            this.currentFamily = family;
            applyComposite();
        }
    }

    /** 设置明暗，保持家族 / 密度 / 主题色不变。 */
    public void setDark(boolean dark) {
        this.dark = dark;
        applyComposite();
    }

    /**
     * 切换密度（PC UI 规范 §12.2 / §15.3）。
     * 保持家族 / 明暗 / 主题色不变；密度通过 CSS 变量注入而非独立主题类实现。
     *
     * @param density {@link ThemeDensity#DEFAULT} 或 {@link ThemeDensity#COMPACT}；null 忽略
     */
    public void setDensity(ThemeDensity density) {
        if (density == null) return;
        this.density = density;
        applyDensityToAll();
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
        this.currentThemeColor.setHexColor(color);
        applyPrimaryColorToAll();
    }

    /** 按预设色板更换主色（accent）。 */
    public void setPrimaryColor(ThemeColor.Preset preset) {
        setPrimaryColor(preset.getHexColor());
    }

    /** 将当前主色应用到所有已注册的 Scene 和 Region。 */
    private void applyPrimaryColorToAll() {
        String[] lightScale = currentThemeColor.generateColorScale();
        String[] darkScale = currentThemeColor.generateDarkColorScale();

        boolean isDark = currentTheme.getType() == Theme.ThemeType.DARK;
        String[] scale = isDark ? darkScale : lightScale;

        // ============================================================
        // M19.51：用 data-URI stylesheet 注入 accent 变量，而非 Node.setStyle() inline。
        //
        // 为什么不用 inline setStyle？
        //   JavaFX 的 looked-up color 解析有个陷阱：节点 inline style 里定义的变量集合
        //   会「遮蔽」该节点从 user-agent stylesheet (.root) 继承的变量查找路径。
        //   于是 root 上 inline 注入 accent 后，子节点（含 ComboBox 显示区 / 弹层 popup）
        //   解析 -color-fg-default / -color-border-* 时反而找不到（这些定义在 UA CSS 的 .root），
        //   导致 ClassCastException(String→Paint) + "Could not resolve -color-fg-default"。
        //
        // 正解：把 accent 变量也写成 .root { ... } 规则，做成 data-URI 加到 scene.getStylesheets()。
        //   这样 accent 与 fg/border 等都在 stylesheet 的 .root 作用域合并解析，无遮蔽问题，
        //   且 popup 弹层（独立 window 但共享 scene.getStylesheets()）也能正确解析。
        // ============================================================
        StringBuilder rule = new StringBuilder(".root {");
        for (int i = 0; i < 10; i++) {
            rule.append(String.format("-color-accent-%d: %s;", i, scale[i]));
        }
        rule.append("-color-accent-emphasis:").append(scale[5]).append(";");
        rule.append("-color-accent-muted:").append(scale[2]).append(";");
        rule.append("-color-accent-subtle:").append(scale[0]).append(";");
        rule.append("}");

        String dataUri = "data:text/css;base64,"
                + java.util.Base64.getEncoder().encodeToString(
                        rule.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));

        // 替换每个 scene 上一次注入的 accent stylesheet（先移除旧的，再加新的）
        for (Scene scene : registeredScenes) {
            if (accentStylesheet != null) {
                scene.getStylesheets().remove(accentStylesheet);
            }
            scene.getStylesheets().add(dataUri);
        }
        accentStylesheet = dataUri;
    }

    /** 上一次注入的 accent data-URI stylesheet，用于切换时移除旧的。 */
    private String accentStylesheet;

    /** 上一次注入的 density data-URI stylesheet，用于切换 / 恢复时移除旧的。 */
    private String densityStylesheet;

    /**
     * 将当前 density 注入到所有已注册的 Scene 上（PC UI 规范 §15.3.3）。
     *
     * <p>与 {@link #applyPrimaryColorToAll()} 同套路：构造一份 data-URI stylesheet，
     * 写入每个 scene.getStylesheets()，并在再次切换时移除旧引用保持幂等。</p>
     *
     * <p>幂等性：</p>
     * <ul>
     *   <li>DEFAULT 状态下不注入新 stylesheet（走 LESS 默认值），仅 remove 之前的 COMPACT。</li>
     *   <li>切回 COMPACT 后再切回 DEFAULT 不会残留旧 stylesheet。</li>
     *   <li>applyTheme() 后调用是 no-op（UA CSS 重置不波及 scene.getStylesheets()）。</li>
     * </ul>
     */
    private void applyDensityToAll() {
        // 1. 移除上一次的 density stylesheet（无论是否相同）
        if (densityStylesheet != null) {
            for (Scene scene : registeredScenes) {
                scene.getStylesheets().remove(densityStylesheet);
            }
            densityStylesheet = null;
        }

        // 2. DEFAULT 状态不注入新 stylesheet（走 LESS 默认值）
        if (density == ThemeDensity.DEFAULT) return;

        // 3. 构造 density CSS（PC UI 规范 §5.2 / §6 联动算法）
        String css = buildDensityCss(density);
        String dataUri = "data:text/css;base64,"
                + java.util.Base64.getEncoder().encodeToString(
                        css.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        // 4. 注入到所有 scene
        for (Scene scene : registeredScenes) {
            scene.getStylesheets().add(dataUri);
        }
        densityStylesheet = dataUri;
    }

    /**
     * 构造 density 对应的 CSS 规则（PC UI 规范 §5.2 / §6 联动算法）。
     * 复用 JavaFX looked-up color 变量名（无 {@code -fx-} 前缀），与 LESS {@code @control-height} 命名一致。
     */
    private String buildDensityCss(ThemeDensity d) {
        return switch (d) {
            case DEFAULT -> "";
            case COMPACT -> """
                    .root {
                        -control-height: 28px;
                        -control-height-sm: 24px;
                        -control-height-lg: 36px;
                        -control-height-xs: 18px;
                        -spacing-xs: 2px;
                        -spacing-sm: 6px;
                        -spacing-md: 8px;
                        -spacing-lg: 12px;
                        -spacing-xl: 16px;
                        -table-header-height: 28px;
                        -table-row-height: 28px;
                    }
                    """;
        };
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
        this.currentFamily = Family.MUI;
        this.dark = false;
        applyComposite();
    }

    /** 切换到 MUI 暗色主题家族。 */
    public void switchToMuiDark() {
        this.currentFamily = Family.MUI;
        this.dark = true;
        applyComposite();
    }

    /** 获取当前主题家族名称。 */
    public String getCurrentThemeFamily() {
        return currentFamily.getDisplayName();
    }
}
