package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.openkawu.jfxium.component.composite.DescriptionsAnt;
import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.component.composite.SwitchAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TitleBarAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.InputAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.BorderPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.core.theme.DarkTheme;
import org.openkawu.jfxium.core.theme.FluentDarkTheme;
import org.openkawu.jfxium.core.theme.FluentTheme;
import org.openkawu.jfxium.core.theme.LightTheme;
import org.openkawu.jfxium.core.theme.Theme;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.core.util.IconPath;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Fluent 主题（Windows 11 / WinUI 3 风格）示例 —— 开关 / 取色实测 / 令牌对照。
 *
 * <p>本页是 Fluent 主题族的**可验收面**：不是静态截图，而是真机读取
 * {@code ThemeManager} 状态与节点取色，用于回答「切到 Fluent 之后，
 * 按钮到底是不是官方品牌蓝」这类问题。</p>
 *
 * <p>关键机制：Fluent 主题的 {@code Theme.hasFixedAccent()} 返回 {@code true}，
 * {@code ThemeManager} 不再注入运行时主色（默认注入的 Ant 蓝 {@code #1677ff}
 * 会覆盖主题 CSS，见 BUG #177），因此 Fluent 下按钮是 WinUI 3 强调色
 * （亮 {@code #0067c0} / 暗提亮 {@code #4cc2ff}）。</p>
 *
 * <p>状态回显是**活的**：监听 {@link ThemeManager#addListener}，在顶栏切换主题 /
 * 密度时页内状态行与分段器同步刷新；页面脱离场景树时自动注销监听，不泄漏。</p>
 */
public class FluentExamplePage extends VBoxAnt {

    private final ThemeManager manager = ThemeManager.getInstance();

    /** 页内状态回显（两轴 + accent 策略），由 {@link #syncFromManager()} 刷新。 */
    private Label stateLabel;
    private SegmentedAnt.Controller themeController;
    private SegmentedAnt.Controller densityController;

    public FluentExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Fluent 主题（Windows 11 风格）")
                .description("微软 Fluent 设计语言主题族：亮 / 暗两档 + 各自紧凑密度变体。"
                        + "口径取 WinUI 3 桌面 theme resources，非 Fluent 2 Web 的 brandWeb 色阶；"
                        + "中性表面/文本/描边层按官方 alpha 折算为等效不透明值，非近似手调。")
                .sections(
                        switchSection(),
                        swatchSection(),
                        compareSection(),
                        tokenSection(),
                        windowSection(),
                        scopeSection()
                )
                .padding(24)
                .build());

        bindToManager();
    }

    // ============================================================
    // 状态同步（活回显 + 不泄漏）
    // ============================================================

    private void bindToManager() {
        Runnable sync = this::syncFromManager;
        manager.addListener(sync);
        // 页面被移出场景树时注销：PageCatalog 每次导航都新建页面（factory 语义），
        // 不注销会随导航次数线性累积监听器。
        parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                manager.removeListener(sync);
            }
        });
        syncFromManager();
    }

    private void syncFromManager() {
        if (themeController != null) {
            themeController.setSelected(keyOf(manager.getCurrentTheme()));
        }
        if (densityController != null) {
            densityController.setSelected(manager.getDensity() == ThemeDensity.COMPACT ? "compact" : "default");
        }
        if (stateLabel != null) {
            stateLabel.setText(describe());
        }
    }

    // ============================================================
    // 1. 一键切换 + 当前状态实测
    // ============================================================

    private Node switchSection() {
        stateLabel = TypographyAnt.text("")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();

        Node themeSeg = SegmentedAnt.create()
                .size(Size.SMALL)
                .option("fluent", "Fluent 亮")
                .option("fluentDark", "Fluent 暗")
                .option("light", "Ant 亮")
                .option("dark", "Ant 暗")
                .selected(keyOf(manager.getCurrentTheme()))
                .onChange(val -> {
                    Theme target = switch (val) {
                        case "fluentDark" -> new FluentDarkTheme();
                        case "light" -> new LightTheme();
                        case "dark" -> new DarkTheme();
                        default -> new FluentTheme();
                    };
                    manager.applyTheme(target);
                })
                .build();
        themeController = SegmentedAnt.controllerOf(themeSeg);

        Node densitySeg = SegmentedAnt.create()
                .size(Size.SMALL)
                .option("default", "默认密度")
                .option("compact", "紧凑密度")
                .selected(manager.getDensity() == ThemeDensity.COMPACT ? "compact" : "default")
                .onChange(val -> manager.setDensity(
                        "compact".equals(val) ? ThemeDensity.COMPACT : ThemeDensity.DEFAULT))
                .build();
        densityController = SegmentedAnt.controllerOf(densitySeg);

        String code = """
                ThemeManager mgr = ThemeManager.getInstance();

                // 亮 / 暗两档（accent 固定为官方品牌蓝，见下方说明）
                mgr.applyTheme(new FluentTheme());
                mgr.applyTheme(new FluentDarkTheme());

                // 也可走「家族轴」，dark 轴会跟着走（不会回退到 Ant 系）
                mgr.setFamily(ThemeManager.Family.FLUENT);
                mgr.setDark(true);                  // -> Fluent Dark

                // 密度是正交维度：Fluent 同样支持紧凑
                mgr.setDensity(ThemeDensity.COMPACT);   // -> theme-fluent*-compact.css
                """;

        return Demos.sectionWithCode("1. 一键切换",
                "点上面的分段器直接改全局主题；下面的状态行是真机读 ThemeManager 的结果，"
                        + "顶栏切主题时这里也会同步刷新，不是写死文案。",
                code,
                Demos.column(
                        Demos.labeled("主题", themeSeg),
                        Demos.labeled("密度", densitySeg),
                        stateLabel
                ));
    }

    // ============================================================
    // 2. 品牌色阶 + 状态色实测
    // ============================================================

    private Node swatchSection() {
        Node accentRow = Demos.row(
                swatch("0", "jfx-demo-fluent-s0"),
                swatch("1", "jfx-demo-fluent-s1"),
                swatch("2", "jfx-demo-fluent-s2"),
                swatch("3", "jfx-demo-fluent-s3"),
                swatch("4", "jfx-demo-fluent-s4"),
                swatch("5", "jfx-demo-fluent-s5"),
                swatch("6", "jfx-demo-fluent-s6"),
                swatch("7", "jfx-demo-fluent-s7"),
                swatch("8", "jfx-demo-fluent-s8"),
                swatch("9", "jfx-demo-fluent-s9")
        );
        Node semanticRow = Demos.row(
                swatch("强调", "jfx-demo-fluent-accent"),
                swatch("成功", "jfx-demo-fluent-success"),
                swatch("警告", "jfx-demo-fluent-warning"),
                swatch("危险", "jfx-demo-fluent-danger"),
                swatch("表面", "jfx-demo-fluent-surface"),
                swatch("凹陷", "jfx-demo-fluent-inset")
        );
        Node componentsRow = Demos.row(
                ButtonAnt.create("主按钮").type(ButtonAnt.Type.PRIMARY).build(),
                ButtonAnt.create("强调").type(ButtonAnt.Type.ACCENT).build(),
                ButtonAnt.create("成功").type(ButtonAnt.Type.SUCCESS).build(),
                ButtonAnt.create("警告").type(ButtonAnt.Type.WARNING).build(),
                ButtonAnt.create("危险").type(ButtonAnt.Type.DANGER).build(),
                ButtonAnt.create("默认").build(),
                TagAnt.create("Brand").type(TagAnt.Type.PRIMARY).build(),
                TagAnt.create("Success").type(TagAnt.Type.SUCCESS).build(),
                TagAnt.create("Warning").type(TagAnt.Type.WARNING).build(),
                TagAnt.create("Error").type(TagAnt.Type.ERROR).build(),
                InputAnt.create().placeholder("Fluent 输入框").build(),
                SwitchAnt.create().selected(true).build()
        );

        return Demos.section("2. 品牌色阶与状态色",
                "上排是强调色 10 档（亮色 #e8f3fc → #0067c0 → #001a68；暗色由深到浅、填充 #4cc2ff），"
                        + "中间是语义槽，下排是真实组件取色。色块全部绑定主题令牌，"
                        + "切换主题 / 明暗会整体跟着变。",
                Demos.column(accentRow, semanticRow, componentsRow));
    }

    // ============================================================
    // 3. 两套设计语言对照
    // ============================================================

    private Node compareSection() {
        return Demos.section("3. 两套设计语言对照",
                "Fluent 与 Ant Design 在本项目里是并列的两种风格，走同一套 LESS 令牌管线、"
                        + "同一批 *Ant 组件 —— 换主题只换设计语言，不改组件代码。",
                DescriptionsAnt.create()
                        .title("Fluent 2（Windows 11）　vs　Ant Design（默认）")
                        .column(1)
                        .bordered()
                        .item("品牌蓝",
                                "Fluent：亮 #0067c0 / 暗提亮 #4cc2ff（accent 固定）　·　Ant：#1677ff（可运行时注入替换）")
                        .item("主色可换",
                                "Fluent：否 —— hasFixedAccent() 为 true，保品牌色　·　Ant：是 —— setPrimaryColor 即时生效")
                        .item("圆角",
                                "Fluent：控件 4px　·　Ant：4 / 6 / 8 三档")
                        .item("中性分层",
                                "Fluent：#ffffff › #f9f9f9 › #f3f3f3（WinUI 表面层）　·　Ant 系分层各自一套")
                        .item("状态色",
                                "Fluent：success 绿 / warning orange / danger cranberry　·　Ant：绿 / 金 / 红")
                        .item("动效时长",
                                "Fluent：100 / 200 / 300 ms（fast / normal / slow）　·　Ant 系随主题曲线令牌")
                        .build());
    }

    // ============================================================
    // 4. 当前主题对象实测
    // ============================================================

    private Node tokenSection() {
        Theme current = manager.getCurrentTheme();
        return Demos.section("4. 当前主题对象实测",
                "这一栏读的是打开本页那一刻 ThemeManager 的实时状态。切到 Fluent 时 "
                        + "hasFixedAccent() 为 true —— 这正是「按钮不会被改成 Ant 蓝」的原因。",
                DescriptionsAnt.create()
                        .title("ThemeManager 状态快照")
                        .column(2)
                        .bordered()
                        .item("getName()", String.valueOf(current.getName()))
                        .item("getType()", String.valueOf(current.getType()))
                        .item("hasFixedAccent()", String.valueOf(current.hasFixedAccent()))
                        .item("getActiveThemeName()", String.valueOf(manager.getActiveThemeName()))
                        .item("getCurrentFamily()", String.valueOf(manager.getCurrentFamily()))
                        .item("getDensity()", String.valueOf(manager.getDensity()))
                        .item("isDark()", String.valueOf(manager.isDark()))
                        .item("UA 样式表", stylesheetOf(current))
                        .build());
    }

    // ============================================================
    // 5. 窗口外壳（自绘标题栏 + 窗口按钮）
    // ============================================================

    private Node windowSection() {
        TitleBarAnt preview = TitleBarAnt.create()
                .icon(IconPath.create(IconPath.FOLDER))
                .title("JFXium — 自绘标题栏")
                .build();

        VBoxAnt previewShell = VBoxAnt.create()
                .spacing(0)
                .children(
                        preview,
                        VBoxAnt.create()
                                .padding(16)
                                .children(TypographyAnt.text("窗口内容区（预览：未绑定真实 Stage，拖动无效）")
                                        .type(TypographyAnt.TextColor.SECONDARY)
                                        .build())
                                .build());
        previewShell.getStyleClass().add("jfx-demo-window-shell");
        previewShell.prefW(480).maxW(480);

        Node open = ButtonAnt.create("打开无边框窗口（可真拖动 / 最大化 / 关闭）")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> openDemoWindow())
                .build();

        String code = """
                Stage stage = new Stage(StageStyle.UNDECORATED);

                TitleBarAnt bar = TitleBarAnt.create()
                    .icon(IconPath.create(IconPath.FOLDER))
                    .title("我的应用")
                    .stage(stage)          // 接线：拖动 + 双击最大化 + 三个窗口按钮
                    .build();

                BorderPaneAnt root = BorderPaneAnt.create();
                root.setTop(bar);
                root.setCenter(content);

                Scene scene = new Scene(root, 960, 640);
                ThemeManager.getInstance().registerScene(scene);   // accent 注入（Ant 系主题）
                stage.setScene(scene);
                stage.show();

                // 关闭按钮走标准语义：应用可拦截
                stage.setOnCloseRequest(e -> {
                    if (hasUnsavedChanges() && !confirmDiscard()) e.consume();
                });
                """;

        return Demos.sectionWithCode("5. 窗口外壳（Win11 标题栏 + 窗口按钮）",
                "TitleBarAnt / WindowControlAnt 是自绘无边框窗口的一套基础件：标题栏高 32（紧凑 28）、"
                        + "按钮 46×32、字形 10px，全部走主题令牌 —— 所以 Ant 系主题下关闭悬停是 Ant 红、"
                        + "Fluent 下是 WinUI 危险色。上面的预览只画外观；点按钮打开一个真正可拖动、"
                        + "可双击最大化、关闭走 WINDOW_CLOSE_REQUEST 的窗口。",
                code,
                Demos.column(
                        previewShell,
                        Demos.row(open)));
    }

    /** 打开一个 StageStyle.UNDECORATED 演示窗口，真实跑 TitleBarAnt 的三条交互。 */
    private void openDemoWindow() {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        if (getScene() != null && getScene().getWindow() != null) {
            stage.initOwner(getScene().getWindow());
        }

        TitleBarAnt bar = TitleBarAnt.create()
                .icon(IconPath.create(IconPath.FOLDER))
                .title("JFXium — Fluent 窗口")
                .stage(stage)
                .build();

        VBoxAnt content = VBoxAnt.create()
                .padding(24)
                .spacing(12)
                .background(Background.LAYOUT)
                .children(
                        TypographyAnt.text("这是一个无边框窗口").build(),
                        TypographyAnt.text("拖动标题栏移动　·　双击标题栏最大化 / 还原　·　右上角三个按钮可用")
                                .type(TypographyAnt.TextColor.SECONDARY)
                                .build(),
                        ButtonAnt.create("关闭")
                                .onClick(e -> stage.close())
                                .build());

        BorderPaneAnt root = BorderPaneAnt.create();
        root.setTop(bar);
        root.setCenter(content);

        Scene scene = new Scene(root, 720, 420);
        ThemeManager.getInstance().registerScene(scene);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    // ============================================================
    // 6. 能力边界（平台限制 vs 未实现，分开说）
    // ============================================================

    private Node scopeSection() {
        return Demos.section("6. 能力边界",
                "把「主题没做」和「平台做不到」分开列清楚：前者是可补的工时，后者是 JavaFX 的能力边界。",
                DescriptionsAnt.create()
                        .title("已实现 / 平台受限")
                        .column(1)
                        .bordered()
                        .item("已实现（主题层）",
                                "官方色阶与状态色、圆角、字号与控件尺寸阶梯、动效时长令牌、亮暗两档 + 各自紧凑密度变体")
                        .item("已实现（框架层）",
                                "主题族状态机 Family.FLUENT、密度正交派生、固定 accent 保护、"
                                        + "全部 *Ant 组件随样式表自动适配（组件侧零改动）")
                        .item("已实现（窗口外壳）",
                                "TitleBarAnt / WindowControlAnt：自绘标题栏、拖动移动、双击最大化、"
                                        + "最小化/最大化/关闭接线（关闭走 WINDOW_CLOSE_REQUEST，可被应用拦截）")
                        .item("窗口外壳未做",
                                "已最大化时拖动不会先还原再跟手；标题文字不自动跟随 Stage.title")
                        .item("平台受限：acrylic / Mica",
                                "属操作系统窗口合成层（backdrop-filter），JavaFX 只能窗口内绘制，"
                                        + "无法让窗口后方内容透出并模糊")
                        .item("平台受限：窗口圆角",
                                "由操作系统决定，JavaFX 不能控制 Stage 外形")
                        .item("未实现：reveal highlight",
                                "指针跟随高光需逐节点追踪鼠标位置并叠加径向渐变，属 Java 侧额外工时，本主题未纳入")
                        .build());
    }

    // ============================================================
    // 辅助
    // ============================================================

    /** 单个色块：颜色由 demo.css 的 .jfx-demo-fluent-* 规则绑定主题令牌。 */
    private static Node swatch(String label, String styleClass) {
        VBoxAnt box = VBoxAnt.create()
                .padding(0)
                .align(javafx.geometry.Pos.CENTER)
                .children(TypographyAnt.text(label).build());
        box.getStyleClass().add("jfx-demo-fluent-swatch");
        box.getStyleClass().add(styleClass);
        return box;
    }

    /** 两轴 + accent 策略的可读描述。 */
    private String describe() {
        Theme t = manager.getCurrentTheme();
        return "当前：" + manager.getActiveThemeName()
                + "　|　家族：" + manager.getCurrentFamily()
                + "　|　明暗：" + (manager.isDark() ? "暗色" : "亮色")
                + "　|　密度：" + (manager.getDensity() == ThemeDensity.COMPACT ? "紧凑" : "默认")
                + "　|　accent：" + (t != null && t.hasFixedAccent() ? "固定品牌色" : "可注入主色");
    }

    /** 当前主题实际加载的 UA 样式表文件名。 */
    private String stylesheetOf(Theme theme) {
        String path = theme.getUserAgentStylesheet(manager.getDensity());
        if (path == null) {
            return "(null)";
        }
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    /** 由主题实例反查分段器 key（页面可能以任意主题打开）。 */
    private static String keyOf(Theme theme) {
        if (theme instanceof FluentDarkTheme) return "fluentDark";
        if (theme instanceof FluentTheme) return "fluent";
        if (theme instanceof DarkTheme) return "dark";
        return "light";
    }
}
