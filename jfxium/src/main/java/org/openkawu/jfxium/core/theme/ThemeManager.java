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

    // 三个正交维度的当前状态（family × dark × compact），用于组合出具体 Theme
    private Family currentFamily = Family.ANT_DESIGN;
    private boolean dark = false;
    private boolean compact = false;

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
    }

    // ============================================================
    // 主题风格 / 明暗 / 密度三维组合切换（M19.47）
    // ============================================================

    /**
     * 根据当前 family × dark × compact 三个维度组合出具体 Theme 并应用。
     * 8 个 Theme 类 = 2 家族(Ant/MUI) × 2 明暗 × 2 密度的笛卡尔积。
     */
    private void applyComposite() {
        Theme theme = switch (currentFamily) {
            case ANT_DESIGN -> dark
                    ? (compact ? new DarkCompactTheme() : new DarkTheme())
                    : (compact ? new LightCompactTheme() : new LightTheme());
            case MUI -> dark
                    ? (compact ? new MuiDarkCompactTheme() : new MuiDarkTheme())
                    : (compact ? new MuiCompactTheme() : new MuiTheme());
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

    /** 设置紧凑密度，保持家族 / 明暗 / 主题色不变。 */
    public void setCompactDensity(boolean compact) {
        this.compact = compact;
        applyComposite();
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

    /** 切换亮色 / 暗色主题（保持家族 / 密度 / 主题色不变）。 */
    public void toggleTheme() {
        setDark(!dark);
    }

    /** 切换默认 / 紧凑密度（保持家族 / 明暗 / 主题色不变）。 */
    public void toggleCompact() {
        setCompactDensity(!compact);
    }

    /** 当前是否为紧凑密度。 */
    public boolean isCompact() {
        return compact;
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
