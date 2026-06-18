package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.composite.ProgressAnt;
import org.openkawu.jfxium.component.composite.TagAnt;
import org.openkawu.jfxium.component.composite.TimelineAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.UiExampleConstants;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.ProjectDashboardTemplate;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectReleaseTemplate;

/**
 * ProjectReleaseTemplate 发布节奏模板示例页。
 */
public class ProjectReleaseExamplePage extends VBoxAnt {

    public ProjectReleaseExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectRelease 发布节奏模板")
                .description("把项目的版本、分支、发布进度和近期变更整理成一个可复用的发布节奏面板。")
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
                "适合展示版本发布、分支稳定性、变更摘要、发布准备度等信息，常见于项目首页、发布页、README 演示页。",
                Demos.column(
                        Demos.labeled("项目主页", TypographyAnt.text("让用户看到项目不仅能跑，还在持续发布与演进。")
                                .type(TypographyAnt.Type.SECONDARY).build()),
                        Demos.labeled("复用方式", TypographyAnt.text("首页、控制台页和发布页都可以共用这块模板。")
                                .type(TypographyAnt.Type.SECONDARY).build()),
                        Demos.labeled("信息密度", TypographyAnt.text("用发布信息 + 时间线 + 变更列表，代替分散的文字说明。")
                                .type(TypographyAnt.Type.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label actionState = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.Type.SECONDARY)
                .build();
        ProjectDashboardTemplate.Snapshot snapshot = ProjectDashboardTemplate.Snapshot.demo();

        return Demos.section(
                "2. 预览",
                "这块展示版本演进、最近变更和发布准备度，能直接挂到工程首页或单独发布页。",
                ProjectReleaseTemplate.create()
                        .title("JFXium 发布节奏")
                        .description("项目持续演进的发布信息面板。")
                        .status("稳定", TagAnt.Type.SUCCESS)
                        .status("可演示", TagAnt.Type.PRIMARY)
                        .meta("版本", snapshot.version())
                        .meta("分支", snapshot.branch())
                        .meta("构建", TagAnt.create("通过").type(TagAnt.Type.SUCCESS).build())
                        .meta("更新时间", snapshot.updatedAt())
                        .release(snapshot.version(), "当前演示版本", TimelineAnt.DotColor.GREEN)
                        .release("1.0.1", "引入项目概览板与快捷入口", TimelineAnt.DotColor.BLUE)
                        .release("1.0.2", "补齐发布节奏板模板", TimelineAnt.DotColor.BLUE)
                        .change("workspace-template", "工作台模板", "继续收口 header / sider / footer 的工程壳层", "打开")
                        .change("overview-template", "项目概览模板", "展示运行态、技术栈和里程碑", "打开")
                        .change("release-template", "发布节奏模板", "展示版本、变更和发布准备度", "打开")
                        .readiness("当前发布准备度", 0.78, ProgressAnt.Status.SUCCESS, "核心展示链路已完整，继续沉淀更细的公共 section。")
                        .onAction(route -> actionState.setText("动作：" + route))
                        .build(),
                actionState
        );
    }

    private Node usageSection() {
        String code = """
                ProjectReleaseTemplate.create()
                        .title("JFXium 发布节奏")
                        .description("项目持续演进的发布信息面板")
                        .status("稳定", TagAnt.Type.SUCCESS)
                        .meta("版本", "1.0-SNAPSHOT")
                        .meta("分支", "main")
                        .release("1.0-SNAPSHOT", "当前演示版本", TimelineAnt.DotColor.GREEN)
                        .release("1.0.1", "引入项目概览板与快捷入口", TimelineAnt.DotColor.BLUE)
                        .change("workspace-template", "工作台模板", "继续收口 header / sider / footer 的工程壳层", "打开")
                        .readiness("当前发布准备度", 0.78, ProgressAnt.Status.SUCCESS, "核心展示链路已完整")
                        .onAction(this::navigate)
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "这个模板把发布页里最常见的几块信息统一组织起来，避免首页和发布页重复写版本说明。",
                code,
                Demos.column(
                        Demos.labeled("复用建议", TypographyAnt.text("如果项目有版本页、发布页或对外展示页，都可以直接复用。")
                                .type(TypographyAnt.Type.SECONDARY).build()),
                        Demos.labeled("继续整合", TypographyAnt.text("若有多个项目共享同类发布面板，可以进一步抽成更底层的公共 section。")
                                .type(TypographyAnt.Type.SECONDARY).build())
                )
        );
    }
}
