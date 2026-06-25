package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectChangelogTemplate;

import static org.openkawu.jfxium.template.ProjectChangelogTemplate.changelog;

/**
 * ProjectChangelogTemplate 变更日志模板示例页。
 */
public class ProjectChangelogExamplePage extends VBoxAnt {

    public ProjectChangelogExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectChangelog 变更日志模板")
                .description("把项目的版本演进和近期变更做成结构化的变更日志面板，适合首页、发布页和 README 展示。")
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
                "适合展示版本变更日志、近期改动摘要、发布记录，常见于项目首页、发布页、README 演示页。",
                Demos.column(
                        Demos.labeled("展示方式", TypographyAnt.text("版本号 + 日期 + 带类型标签的变更条目列表。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("类型标注", TypographyAnt.text("新增(绿) / 优化(蓝) / 修复(黄) / 移除(红)，一眼区分变更性质。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("通用性", TypographyAnt.text("项目首页、发布页、CHANGELOG 展示都可以直接复用。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label actionState = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();

        Node changelogPanel = ProjectChangelogTemplate.create()
                .title("JFXium 变更日志")
                .description("项目近期版本与变更记录")
                .version("1.0.3", "2026-06-18",
                        changelog("新增", "ProjectFeatureTemplate 项目特性网格模板"),
                        changelog("新增", "ProjectChangelogTemplate 变更日志模板"),
                        changelog("新增", "ProjectQuickStartTemplate 快速上手步骤模板"),
                        changelog("优化", "ProjectDashboardTemplate 整合新模板到首页"))
                .version("1.0.2", "2026-06-15",
                        changelog("新增", "ProjectConsoleTemplate 工程控制台壳模板"),
                        changelog("新增", "ProjectHeroTemplate 工程项目首屏门面"),
                        changelog("优化", "MainView 升级为完整工程壳"))
                .version("1.0.1", "2026-06-10",
                        changelog("新增", "ProjectOverviewTemplate 工程概览模板"),
                        changelog("新增", "ProjectReleaseTemplate 发布节奏模板"),
                        changelog("修复", "主题切换时语义变量注入缺失"))
                .onAction(key -> actionState.setText("动作：" + key))
                .build();

        return Demos.section(
                "2. 预览",
                "下面这块就是项目首页常见的变更日志面板，用户可以快速看到项目的迭代情况。",
                changelogPanel,
                actionState
        );
    }

    private Node usageSection() {
        String code = """
                import static ProjectChangelogTemplate.changelog;

                ProjectChangelogTemplate.create()
                        .title("变更日志")
                        .description("项目近期版本与变更记录")
                        .version("1.0.2", "2026-06-18",
                                changelog("新增", "ProjectFeatureTemplate 特性网格模板"),
                                changelog("优化", "ProjectDashboardTemplate 整合新模板"))
                        .version("1.0.1", "2026-06-15",
                                changelog("新增", "ProjectOverviewTemplate 工程概览模板"))
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "这个模板把变更日志面板统一组织，避免首页和发布页重复写版本说明。",
                code,
                Demos.column(
                        Demos.labeled("复用建议", TypographyAnt.text("如果项目有 CHANGELOG 展示需求，可以直接复用这个模板。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("继续整合", TypographyAnt.text("若有多个项目共享变更日志面板，可以继续把版本数据从页面层迁到配置层。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }
}
