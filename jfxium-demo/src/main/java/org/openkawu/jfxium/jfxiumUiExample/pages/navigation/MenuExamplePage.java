package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.MenuAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * Menu 菜单 —— 内联 / 水平 / 暗色主题。
 */
public class MenuExamplePage extends VBoxAnt {

    public MenuExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Menu 菜单")
                .description("导航菜单组件，支持内联展开、水平排列、暗色主题。")
                .sections(
                        inlineSection(),
                        horizontalSection(),
                        darkSection(),
                        runtimeCollapseSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node inlineSection() {
        Node demo = MenuAnt.create()
                .mode(MenuAnt.Mode.INLINE)
                .item("home", "首页", () -> MessageAnt.info("导航: 首页"))
                .subMenu("sub1", "导航一")
                    .item("item1", "选项 1", () -> MessageAnt.info("导航: 选项 1"))
                    .item("item2", "选项 2", () -> MessageAnt.info("导航: 选项 2"))
                .endSubMenu()
                .subMenu("sub2", "导航二")
                    .item("item3", "选项 3", () -> MessageAnt.info("导航: 选项 3"))
                    .item("item4", "选项 4", () -> MessageAnt.info("导航: 选项 4"))
                .endSubMenu()
                .selectedKey("home")
                .build();
        String code = """
                MenuAnt.create()
                        .mode(MenuAnt.Mode.INLINE)
                        .item("home", "首页", () -> MessageAnt.info("导航: 首页"))
                        .subMenu("sub1", "导航一")
                            .item("item1", "选项 1", () -> MessageAnt.info("导航: 选项 1"))
                            .item("item2", "选项 2", () -> MessageAnt.info("导航: 选项 2"))
                        .endSubMenu()
                        .selectedKey("home")
                        .build();
                """;
        return Demos.sectionWithCode("1. 内联模式（INLINE）",
                "子菜单在菜单内展开，适合侧边栏导航。",
                code, demo);
    }

    private Node horizontalSection() {
        Node demo = MenuAnt.create()
                .mode(MenuAnt.Mode.HORIZONTAL)
                .item("mail", "邮件", () -> MessageAnt.info("导航: 邮件"))
                .item("app", "应用", () -> MessageAnt.info("导航: 应用"))
                .item("setting", "设置", () -> MessageAnt.info("导航: 设置"))
                .selectedKey("mail")
                .build();
        String code = """
                MenuAnt.create()
                        .mode(MenuAnt.Mode.HORIZONTAL)
                        .item("mail", "邮件", () -> MessageAnt.info("导航: 邮件"))
                        .item("app", "应用", () -> MessageAnt.info("导航: 应用"))
                        .item("setting", "设置", () -> MessageAnt.info("导航: 设置"))
                        .selectedKey("mail")
                        .build();
                """;
        return Demos.sectionWithCode("2. 水平模式（HORIZONTAL）",
                "菜单项水平排列，适合顶部导航栏。",
                code, demo);
    }

    private Node darkSection() {
        Node demo = MenuAnt.create()
                .mode(MenuAnt.Mode.INLINE)
                .theme(MenuAnt.Theme.DARK)
                .item("dashboard", "仪表盘", () -> MessageAnt.info("导航: 仪表盘"))
                .item("users", "用户管理", () -> MessageAnt.info("导航: 用户管理"))
                .item("settings", "系统设置", () -> MessageAnt.info("导航: 系统设置"))
                .selectedKey("dashboard")
                .build();
        String code = """
                MenuAnt.create()
                        .mode(MenuAnt.Mode.INLINE)
                        .theme(MenuAnt.Theme.DARK)
                        .item("dashboard", "仪表盘", () -> MessageAnt.info("导航: 仪表盘"))
                        .item("users", "用户管理", () -> MessageAnt.info("导航: 用户管理"))
                        .selectedKey("dashboard")
                        .build();
                """;
        return Demos.sectionWithCode("3. 暗色主题",
                "theme(DARK) 暗色背景菜单，适合深色侧边栏。",
                code, demo);
    }

    private Node runtimeCollapseSection() {
        MenuAnt.Builder builder = MenuAnt.create()
                .mode(MenuAnt.Mode.INLINE)
                .item("home", "首页", () -> MessageAnt.info("导航: 首页"))
                .subMenu("sub1", "导航一")
                    .item("item1", "选项 1", () -> MessageAnt.info("导航: 选项 1"))
                    .item("item2", "选项 2", () -> MessageAnt.info("导航: 选项 2"))
                .endSubMenu()
                .subMenu("sub2", "导航二")
                    .item("item3", "选项 3", () -> MessageAnt.info("导航: 选项 3"))
                    .item("item4", "选项 4", () -> MessageAnt.info("导航: 选项 4"))
                .endSubMenu()
                .selectedKey("home");

        Node menu = builder.build();
        MenuAnt.Controller controller = builder.controller();

        ButtonAnt toggleBtn = ButtonAnt.create("切换折叠")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> controller.toggleCollapsed())
                .build();

        Node demo = Demos.column(menu, toggleBtn);
        String code = """
                MenuAnt.Builder builder = MenuAnt.create()
                        .mode(MenuAnt.Mode.INLINE)
                        .item("home", "首页", () -> MessageAnt.info("导航: 首页"))
                        .subMenu("sub1", "导航一")
                            .item("item1", "选项 1", () -> MessageAnt.info("导航: 选项 1"))
                            .endSubMenu()
                        .selectedKey("home");

                Node menu = builder.build();
                MenuAnt.Controller controller = builder.controller();
                controller.toggleCollapsed();
                """;
        return Demos.sectionWithCode("4. 运行时折叠",
                "build() 后可以通过 controller.toggleCollapsed() 切换折叠态。",
                code, demo);
    }

    /**
     * 交互演示 section（M19.PlayGround）—— rebuild 模式样板。
     *
     * <p>MenuAnt 的 Controller 暴露了 {@code setSelectedKey / setCollapsed / expandKey /
     * collapseKey} 等运行时 API —— 这些都已能用。但 {@code mode / theme} 是 build 期决定的
     * 属性，没有 setMode / setTheme 可原地切换。为了一次性演示 3 个维度的可变性，
     * playground 采用 rebuild 策略（任何属性变化都重新 build 一个新 Menu）。</p>
     *
     * <p>{@link PlayGround#rebindRebuild} 把这套流程封装成「声明式 API」。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> mode       = PlayGround.binder("inline");
        Binder<String> theme      = PlayGround.binder("light");
        Binder<String> selectedKey = PlayGround.binder("home");

        // 2. display 工厂 —— 每次 binder 变化都重 build
        Supplier<Node> factory = () -> {
            MenuAnt.Builder b = MenuAnt.create()
                    .mode(parseMode(mode.get()))
                    .theme(parseTheme(theme.get()))
                    .selectedKey(selectedKey.get());
            // items：根据 mode 决定菜单项结构（HORIZONTAL 不支持 subMenu）
            if ("horizontal".equals(mode.get())) {
                b.item("mail",    "邮件",   () -> MessageAnt.info("导航: 邮件"));
                b.item("app",     "应用",   () -> MessageAnt.info("导航: 应用"));
                b.item("setting", "设置",   () -> MessageAnt.info("导航: 设置"));
            } else {
                b.item("home", "首页", () -> MessageAnt.info("导航: 首页"));
                b.subMenu("sub1", "导航一")
                    .item("item1", "选项 1", () -> MessageAnt.info("导航: 选项 1"))
                    .item("item2", "选项 2", () -> MessageAnt.info("导航: 选项 2"))
                .endSubMenu();
                b.subMenu("sub2", "导航二")
                    .item("item3", "选项 3", () -> MessageAnt.info("导航: 选项 3"))
                    .item("item4", "选项 4", () -> MessageAnt.info("导航: 选项 4"))
                .endSubMenu();
            }
            return b.build();
        };

        // 3. 串起来 —— rebindRebuild 内部自动提取 binder 并注册监听
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Menu 的布局模式 / 主题 / 选中项 —— mode/theme 是 build 期属性，display 整棵重建。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("布局", PlayGround.segmented(mode,
                                PlayGround.entry("inline",    "Inline"),
                                PlayGround.entry("horizontal", "Horizontal"))),
                        PlayGround.row("主题", PlayGround.segmented(theme,
                                PlayGround.entry("light", "Light"),
                                PlayGround.entry("dark",  "Dark"))),
                        PlayGround.row("选中", PlayGround.segmented(selectedKey,
                                PlayGround.entry("home",  "首页"),
                                PlayGround.entry("sub1",  "导航一"),
                                PlayGround.entry("sub2",  "导航二")))));
    }

    // ============================================================
    // 枚举解析 helpers
    // ============================================================

    private static MenuAnt.Mode parseMode(String v) {
        if (v == null) return MenuAnt.Mode.INLINE;
        return switch (v) {
            case "horizontal" -> MenuAnt.Mode.HORIZONTAL;
            default          -> MenuAnt.Mode.INLINE;
        };
    }

    private static MenuAnt.Theme parseTheme(String v) {
        if (v == null) return MenuAnt.Theme.LIGHT;
        return switch (v) {
            case "dark" -> MenuAnt.Theme.DARK;
            default     -> MenuAnt.Theme.LIGHT;
        };
    }
}
