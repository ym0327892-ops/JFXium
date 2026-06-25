package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.theme.ThemeDensity;
import org.openkawu.jfxium.core.theme.ThemeManager;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.ProjectDashboardTemplate;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectHeroTemplate;

/**
 * ProjectHeroTemplate 工程项目首屏门面示例页。
 */
public class ProjectHeroExamplePage extends VBoxAnt {

    public ProjectHeroExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectHero 工程项目首屏门面模板")
                .description("把软件图标、标题、状态标签、关键摘要和快捷动作统一收口成一张项目首屏卡。")
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
                "适合项目首页、控制台首页、产品首页、README 首屏这类需要先立住“门面感”的页面。",
                Demos.column(
                        Demos.labeled("首页定位", TypographyAnt.text("先让用户看见这是什么项目，再进入具体内容。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("复用方式", TypographyAnt.text("不同项目只换文案、状态和动作，不需要重写首屏结构。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("工程目标", TypographyAnt.text("把软件图标、状态标签、摘要信息和用户菜单统一封装。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label actionState = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();

        ThemeManager mgr = ThemeManager.getInstance();
        ProjectDashboardTemplate.Snapshot snapshot = ProjectDashboardTemplate.Snapshot.demo();
        String currentTheme = mgr.getCurrentFamily().getDisplayName();
        String currentPreset = mgr.getCurrentPrimaryPreset() != null
                ? mgr.getCurrentPrimaryPreset().getDisplayName()
                : "自定义";
        String themeHex = mgr.getCurrentThemeColor() != null ? mgr.getCurrentThemeColor().getHexColor() : "";

        Node hero = ProjectHeroTemplate.create()
                .title(snapshot.pageTitle())
                .subtitle(snapshot.pageSubtitle())
                .description("首屏先放软件图标、状态和快捷动作，再继续向下叠加概览、发布和入口卡片。")
                .icon(snapshot.icon())
                .status("编译通过", TagAnt.Type.SUCCESS)
                .status("零 FXML", TagAnt.Type.PRIMARY)
                .status("工程展示", TagAnt.Type.SUCCESS)
                .meta("当前主题", currentTheme + " / " + currentPreset)
                .meta("当前密度", mgr.getDensity() == ThemeDensity.COMPACT ? "紧凑" : "默认")
                .meta("主题色", themeHex)
                .action(ButtonAnt.compactLink("查看工作台")
                        .onClick(e -> actionState.setText("动作：查看工作台"))
                        .build())
                .action(ProjectHeroTemplate.userMenu(snapshot.currentUser(), action ->
                        actionState.setText("动作：" + action)).getTrigger())
                .build();

        return Demos.section(
                "2. 预览",
                "这张门面卡专门负责首屏第一眼的信息密度，后面的页面主体继续交给概览、发布和快捷入口模板。",
                hero,
                actionState
        );
    }

    private Node usageSection() {
        String code = """
                ProjectHeroTemplate.create()
                        .title("JFXium UI Example")
                        .subtitle("工程项目展示首页的首屏门面")
                        .description("首屏先放软件图标、状态和快捷动作")
                        .icon(IconAnt.Path.DASHBOARD)
                        .status("编译通过", TagAnt.Type.SUCCESS)
                        .meta("当前主题", "Ant Design / Light")
                        .action(ProjectHeroTemplate.userMenu("开发者", action -> System.out.println("动作：" + action)).getTrigger())
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "如果你的项目首页总是先写一段简介、一个图标和一组快捷动作，就可以直接复用这个模板。",
                code,
                Demos.column(
                        Demos.labeled("复用建议", TypographyAnt.text("配合 ProjectShowcaseTemplate / WorkspaceTemplate 使用，首页会更像真实工程壳。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("继续整合", TypographyAnt.text("如果多项目都要这个门面，可以把标题、状态、动作改成配置驱动。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }
}
