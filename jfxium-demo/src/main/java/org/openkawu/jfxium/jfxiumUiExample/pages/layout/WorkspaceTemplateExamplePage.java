package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.composite.BreadcrumbAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.control.StatusBarAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.style.Background;
import org.openkawu.jfxium.component.layout.GridAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.WorkspaceSettingsTemplate;
import org.openkawu.jfxium.template.WorkspaceTemplate;

/**
 * WorkspaceTemplate 工作台模板 —— 高可用后台壳示例。
 */
public class WorkspaceTemplateExamplePage extends VBoxAnt {

    public WorkspaceTemplateExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("WorkspaceTemplate 工作台模板")
                .description("AppShellAnt 负责骨架，WorkspaceTemplate 负责把壳子编排成可以直接上生产的工作台。Header 里的操作按钮建议用 SMALL / XS，底栏操作可直接走 StatusBarAnt.action() 的 LINK + XS。")
                .sections(
                        overviewSection(),
                        previewSection(),
                        settingsSection(),
                        codeSection()
                )
                .padding(24)
                .build());
    }

    private Node overviewSection() {
        Node intro = Demos.column(
                TypographyAnt.text("适合管理后台、运营台、CRM / CMS、桌面工具等需要 header + sider + content + footer 的主框架。")
                        .type(TypographyAnt.TextColor.SECONDARY).build(),
                TypographyAnt.text("如果你已经有一个 PageTemplate / CrudTemplate 页面，它可以直接塞进 WorkspaceTemplate 的 content 里。")
                        .type(TypographyAnt.TextColor.SECONDARY).build()
        );
        return Demos.section("1. 适用场景", "把工作台壳从具体页面中抽离出来，减少每个项目重复手写 header / sider / footer。", intro);
    }

    private Node previewSection() {
        Label syncState = TypographyAnt.text("状态：已就绪")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();

        Button refreshBtn = ButtonAnt.compactLink("刷新")
                .build();

        Button collapseBtn = ButtonAnt.compactLink("折叠侧栏")
                .build();

        Node userMenu = WorkspaceTemplate.userMenu("开发者", action ->
                syncState.setText("状态：" + action)).getTrigger();

        StatusBarAnt footer = StatusBarAnt.create()
                .info("就绪")
                .status("Java 21 | JavaFX 21 | WorkspaceTemplate")
                .action("UTF-8", () -> syncState.setText("状态：UTF-8"))
                .action("LF", () -> syncState.setText("状态：LF"))
                .build();

        Node sider = VBoxAnt.create()
                .spacing(4)
                .children(
                        navItem("工作台"),
                        navItem("用户管理"),
                        navItem("系统设置")
                );

        Node content = VBoxAnt.create()
                .spacing(16)
                .children(
                        Demos.placeholder("这里放路由 outlet / 首页统计 / 业务页", Background.DEFAULT),
                        Demos.placeholder("这里也可以放表格 / 表单 / 详情 / 空态兜底", Background.SUBTLE),
                        syncState
                );

        WorkspaceTemplate.Result shell = WorkspaceTemplate.create()
                .brand("JFXium Workspace", "高可用工作台模板")
                .headerCenter(BreadcrumbAnt.create().items("首页", "工作台").build())
                .headerRight(refreshBtn, collapseBtn, userMenu)
                .sider(sider, 220)
                .content(content)
                .footer(footer)
                .collapsible(true)
                .trigger(false)
                .breakpoint(GridAnt.Breakpoint.LG)
                .buildResult();

        collapseBtn.setOnAction(e -> shell.toggle());
        refreshBtn.setOnAction(e -> syncState.setText("状态：已刷新"));
        shell.collapsedProperty().addListener((obs, oldVal, newVal) ->
                collapseBtn.setText(newVal ? "展开侧栏" : "折叠侧栏"));

        String code = """
                Label syncState = TypographyAnt.text("状态：已就绪")
                        .type(TypographyAnt.TextColor.SECONDARY)
                        .build();

        Button refreshBtn = ButtonAnt.compactLink("刷新")
                .build();

        Button collapseBtn = ButtonAnt.compactLink("折叠侧栏")
                .build();

                Node userMenu = WorkspaceTemplate.userMenu("开发者", action ->
                        syncState.setText("状态：" + action)).getTrigger();

                WorkspaceTemplate.Result shell = WorkspaceTemplate.create()
                        .brand("JFXium Workspace", "高可用工作台模板")
                        .headerCenter(BreadcrumbAnt.create().items("首页", "工作台").build())
                        .headerRight(refreshBtn, collapseBtn, userMenu)
                        .sider(nav, 220)
                        .content(mainArea)
                        .footer(statusBar)
                        .collapsible(true)
                        .trigger(false)
                        .breakpoint(GridAnt.Breakpoint.LG)
                        .buildResult();

                collapseBtn.setOnAction(e -> shell.toggle());
                shell.collapsedProperty().addListener((obs, ov, nv) ->
                        collapseBtn.setText(nv ? "展开侧栏" : "折叠侧栏"));
                """;

        return Demos.sectionWithCode("2. 预览",
                "头部品牌 + 面包屑 + 操作按钮，左侧导航，右侧内容，底部状态栏。Header 动作按钮保持 XS / LINK，更符合桌面壳子压缩感。",
                code, shell.getRoot());
    }

    private Node codeSection() {
        String code = """
                WorkspaceTemplate.create()
                        .brand("JFXium Workspace", "高可用工作台模板")
                        .headerCenter(BreadcrumbAnt.create().items("首页", "工作台").build())
                        .headerRight(refreshBtn, collapseBtn)
                        .sider(nav, 220)
                        .content(mainArea)
                        .footer(statusBar)
                        .collapsible(true)
                        .trigger(false)
                        .breakpoint(GridAnt.Breakpoint.LG)
                        .buildResult();
                """;

        Node notes = Demos.column(
                TypographyAnt.text("• `WorkspaceTemplate` 适合应用壳子，不适合单页文档页。").type(TypographyAnt.TextColor.SECONDARY).build(),
                TypographyAnt.text("• 单页展示继续用 `PageTemplate`，列表页继续用 `CrudTemplate`。").type(TypographyAnt.TextColor.SECONDARY).build(),
                TypographyAnt.text("• 如果侧栏内容很多，content 自己再包一层 ScrollPane。").type(TypographyAnt.TextColor.SECONDARY).build()
        );

        return Demos.sectionWithCode("4. 使用代码",
                "核心就是把品牌、导航、内容、状态栏四件事都收口到模板里，页面里只管提供 Node。",
                code, notes);
    }

    private Node settingsSection() {
        Node settingsPanel = WorkspaceSettingsTemplate.create().build();

        String code = """
                WorkspaceSettingsTemplate.create()
                        .themeManager(ThemeManager.getInstance())
                        .watermarkVisibleSupplier(() -> watermarkVisible)
                        .onWatermarkVisibleChanged(this::setWatermarkVisible)
                        .build();

                DrawerAnt.create()
                        .title("外观设置")
                        .content(settingsPanel)
                        .placement(DrawerAnt.Placement.RIGHT)
                        .build()
                        .open(owner);
                """;

        Node notes = Demos.column(
                TypographyAnt.text("• 这个模板可以直接塞进 DrawerAnt，也可以作为独立页面展示。")
                        .type(TypographyAnt.TextColor.SECONDARY).build(),
                TypographyAnt.text("• 主题、密度、主色、水印都从同一处收口，避免 demo 里反复拼接。")
                        .type(TypographyAnt.TextColor.SECONDARY).build(),
                TypographyAnt.text("• 这一步之后，主窗口右上角头像弹出的面板和文档页里的预览是同一套代码。")
                        .type(TypographyAnt.TextColor.SECONDARY).build()
        );

        return Demos.sectionWithCode("3. 外观设置抽屉模板",
                "把工作台常用的全局偏好项收成一个可复用模板，主窗口和示例页共用同一套实现。",
                code, Demos.column(settingsPanel, notes));
    }

    private static Button navItem(String text) {
        Button btn = ButtonAnt.create(text)
                .type(ButtonAnt.Type.TEXT)
                .size(Size.SMALL)
                .build();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return btn;
    }
}
