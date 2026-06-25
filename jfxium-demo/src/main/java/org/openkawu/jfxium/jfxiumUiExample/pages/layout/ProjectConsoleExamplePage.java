package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.UiExampleConstants;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectConsoleTemplate;
import org.openkawu.jfxium.template.WorkspaceSettingsTemplate;

/**
 * ProjectConsoleTemplate 工程控制台壳模板示例页。
 */
public class ProjectConsoleExamplePage extends VBoxAnt {

    public ProjectConsoleExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectConsole 工程控制台壳模板")
                .description("把顶部菜单、软件图标、头像入口、左侧导航、右侧展示区和底部状态栏统一收口成一个工程控制台骨架。")
                .sections(
                        overviewSection(),
                        previewSection(),
                        usageSection()
                )
                .padding(24)
                .build());
    }

    private Node overviewSection() {
        return Demos.section(
                "1. 适用场景",
                "适合工程主页、后台控制台、桌面管理工具、示例项目主窗口这类需要完整壳层的场景。",
                Demos.column(
                        Demos.labeled("首页定位", TypographyAnt.text("把 demo 从“页面集合”升级成“真实工程壳”。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("壳层组成", TypographyAnt.text("顶部菜单 + 品牌区 + 用户入口 + 左侧菜单 + 内容区 + 底部状态栏。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("复用建议", TypographyAnt.text("项目只需要替换菜单、路由和内容，不用重复拼窗口骨架。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label status = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();
        AtomicReference<Node> shellRef = new AtomicReference<>();
        Consumer<String> onAction = action -> {
            if ("settings".equals(action)) {
                openSettingsDrawer(shellRef.get(), status);
                return;
            }
            status.setText("动作：" + action);
        };

        Node headerActions = ProjectConsoleTemplate.headerActions("开发者", onAction);

        ProjectConsoleTemplate.SidebarResult siderResult = ProjectConsoleTemplate.sidebarPane(
                "console.home",
                route -> status.setText("动作：" + route),
                new ProjectConsoleTemplate.SidebarLink[]{
                        ProjectConsoleTemplate.sidebarLink("console.home", "控制台首页", IconAnt.path(IconAnt.Path.HOME, 16))
                },
                new ProjectConsoleTemplate.SidebarSection[]{
                        ProjectConsoleTemplate.sidebarSection(
                                "console.project",
                                "工程展示",
                                IconAnt.path(IconAnt.Path.DASHBOARD, 16),
                                true,
                                ProjectConsoleTemplate.sidebarLink("console.hero", "工程门面"),
                                ProjectConsoleTemplate.sidebarLink("console.overview", "工程概览"),
                                ProjectConsoleTemplate.sidebarLink("console.release", "发布节奏"),
                                ProjectConsoleTemplate.sidebarLink("console.showcase", "展示首页")),
                        ProjectConsoleTemplate.sidebarSection(
                                "console.tools",
                                "工具",
                                IconAnt.path(IconAnt.Path.SETTINGS, 16),
                                true,
                                ProjectConsoleTemplate.sidebarLink("console.watermark", "水印"),
                                ProjectConsoleTemplate.sidebarLink("console.settings", "外观设置"))
                });
        Pane sider = siderResult.root();

        Node content = VBoxAnt.create()
                .spacing(16)
                .children(
                        Demos.placeholder("这里是右侧主展示区，可以放路由出口、统计卡、表格、表单或欢迎页。",
                                org.openkawu.jfxium.core.css.Background.DEFAULT),
                        Demos.placeholder("控制台模板强调的是壳层结构，而不是单个控件。", org.openkawu.jfxium.core.css.Background.SUBTLE),
                        status
                );

        ProjectConsoleTemplate.Builder builder = ProjectConsoleTemplate.create()
                .brand("JFXium UI Example", "工程项目控制台")
                .brandIcon(IconAnt.Path.DASHBOARD)
                .menuBar(ProjectConsoleTemplate.standardMenuBar(onAction))
                .headerRight(headerActions)
                .sider(sider, UiExampleConstants.MAIN_SIDER_WIDTH)
                .content(content)
                .footer(ProjectConsoleTemplate.statusBar("就绪", "Java 21 | JavaFX 21 | ProjectConsoleTemplate"))
                .collapsible()
                .trigger(false)
                .breakpoint(org.openkawu.jfxium.component.layout.GridAnt.Breakpoint.LG);

        Node shell = builder.build();
        shellRef.set(shell);

        String code = """
                Label status = TypographyAnt.text("动作：未触发")
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build();
                AtomicReference<Node> shellRef = new AtomicReference<>();
                Consumer<String> onAction = action -> {
                    if ("settings".equals(action)) {
                        openSettingsDrawer(shellRef.get(), status);
                        return;
                    }
                    status.setText("动作：" + action);
                };
                ProjectConsoleTemplate.SidebarResult siderResult = ProjectConsoleTemplate.sidebarPane(
                        "console.home",
                        route -> status.setText("动作：" + route),
                        new ProjectConsoleTemplate.SidebarLink[]{
                                ProjectConsoleTemplate.sidebarLink("console.home", "控制台首页", IconAnt.path(IconAnt.Path.HOME, 16))
                        },
                        new ProjectConsoleTemplate.SidebarSection[]{
                                ProjectConsoleTemplate.sidebarSection(
                                        "console.project",
                                        "工程展示",
                                        IconAnt.path(IconAnt.Path.DASHBOARD, 16),
                                        true,
                                        ProjectConsoleTemplate.sidebarLink("console.hero", "工程门面"),
                                        ProjectConsoleTemplate.sidebarLink("console.overview", "工程概览"),
                                        ProjectConsoleTemplate.sidebarLink("console.release", "发布节奏"),
                                        ProjectConsoleTemplate.sidebarLink("console.showcase", "展示首页")),
                                ProjectConsoleTemplate.sidebarSection(
                                        "console.tools",
                                        "工具",
                                        IconAnt.path(IconAnt.Path.SETTINGS, 16),
                                        true,
                                        ProjectConsoleTemplate.sidebarLink("console.watermark", "水印"),
                                        ProjectConsoleTemplate.sidebarLink("console.settings", "外观设置"))
                        });
                Pane sider = siderResult.root();

                ProjectConsoleTemplate.Builder builder = ProjectConsoleTemplate.create()
                        .brand("JFXium UI Example", "工程项目控制台")
                        .brandIcon(IconAnt.Path.DASHBOARD)
                        .menuBar(ProjectConsoleTemplate.standardMenuBar(onAction))
                        .headerRight(ProjectConsoleTemplate.headerActions("开发者", onAction))
                        .sider(sider, UiExampleConstants.MAIN_SIDER_WIDTH)
                        .content(content)
                        .footer(ProjectConsoleTemplate.statusBar("就绪", "Java 21 | JavaFX 21 | ProjectConsoleTemplate"))
                        .collapsible()
                        .trigger(false)
                        .breakpoint(GridAnt.Breakpoint.LG);

                BorderPane shell = builder.build();
                shellRef.set(shell);
                """;

        return Demos.sectionWithCode(
                "2. 预览",
                "这个模板把工程壳层一次性收口，适合真实项目主窗口和示例项目首页。",
                code,
                Demos.column(shell, status)
        );
    }

    private Node usageSection() {
        String code = """
                ProjectConsoleTemplate.create()
                        .brand("JFXium UI Example", "工程项目控制台")
                        .brandIcon(IconAnt.Path.DASHBOARD)
                        .menuBar(ProjectConsoleTemplate.standardMenuBar(action -> System.out.println("菜单点击：" + action)))
                        .headerRight(ProjectConsoleTemplate.headerActions("开发者", action -> System.out.println("用户点击：" + action)))
                        .sider(menu, 240)
                        .content(mainArea)
                        .footer(ProjectConsoleTemplate.statusBar("就绪", "Java 21 | JavaFX 21"))
                        .collapsible()
                        .build();
                """;

        Node notes = Demos.column(
                Demos.labeled("一句话", TypographyAnt.text("调用方只需要提供菜单、路由和内容，壳层结构交给模板。")
                        .type(TypographyAnt.TextColor.SECONDARY).build()),
                Demos.labeled("配套模板", TypographyAnt.text("如果需要头像设置面板，可以直接接 WorkspaceSettingsTemplate。")
                        .type(TypographyAnt.TextColor.SECONDARY).build()),
                Demos.labeled("继续整合", TypographyAnt.text("后续可把项目菜单、状态栏规范继续沉淀成更多 helper。")
                        .type(TypographyAnt.TextColor.SECONDARY).build())
        );

        return Demos.sectionWithCode(
                "3. 使用代码",
                "这个模板不是替代 WorkspaceTemplate，而是把工程控制台最常见的组合提前命名。",
                code,
                notes
        );
    }

    private void openSettingsDrawer(Node owner, Label status) {
        if (owner == null) {
            return;
        }
        WorkspaceSettingsTemplate.drawer((int) UiExampleConstants.DRAWER_WIDTH,
                        () -> true,
                        visible -> status.setText("水印：" + (visible ? "显示" : "隐藏")))
                .open(owner);
    }
}
