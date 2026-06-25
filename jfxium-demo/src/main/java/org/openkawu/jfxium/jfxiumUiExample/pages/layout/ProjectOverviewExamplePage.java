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
import org.openkawu.jfxium.template.ProjectOverviewTemplate;

/**
 * ProjectOverviewTemplate 工程概览模板示例页。
 */
public class ProjectOverviewExamplePage extends VBoxAnt {

    public ProjectOverviewExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectOverview 工程概览模板")
                .description("把一个工程首页常见的运行态、技术栈、里程碑和近期动作，沉淀成可复用模板。首页和单独示例页都可以直接复用这一层。")
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
                "适合项目首页、控制台首页、README 展示页、产品演示页这类需要让用户一眼看到项目状态的地方。",
                Demos.column(
                        Demos.labeled("工程壳", TypographyAnt.text("把首页从控件列表升级成真实工程控制台。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("通用性", TypographyAnt.text("作为模板可直接复用到不同项目，不依赖 demo 特定数据结构。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("收口策略", TypographyAnt.text("统计、标签、进度、时间线、列表都复用 JFxium 现有组件。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label actionState = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();
        ProjectDashboardTemplate.Snapshot snapshot = ProjectDashboardTemplate.Snapshot.demo();

        return Demos.section(
                "2. 预览",
                "下面这块就是首页同款工程概览板，展示当前工程状态、技术栈、里程碑和近期动作。",
                ProjectOverviewTemplate.create()
                        .title("JFXium 项目概览")
                        .description("本页把工程首页里最常看的信息做成一张卡片式总览板。")
                        .status("编译通过", TagAnt.Type.SUCCESS)
                        .status("零 FXML", TagAnt.Type.PRIMARY)
                        .status("模板化", TagAnt.Type.SUCCESS)
                        .meta("版本", snapshot.version())
                        .meta("分支", snapshot.branch())
                        .meta("构建", TagAnt.create("通过").type(TagAnt.Type.SUCCESS).build())
                        .meta("运行模式", "Showcase")
                        .metric("当前主题", "Ant Design", "主题与密度由 ThemeManager 统一驱动", IconAnt.Path.SETTINGS)
                        .metric("构建方式", "Maven", "编译 / 测试 / 发布都走标准 Maven 流程", IconAnt.Path.FILE)
                        .metric("组件总数", snapshot.componentCount(), "97 个 *Ant 组件 + 14 个 *Template + 1 个 FilterBarAnt", IconAnt.Path.HOME)
                        .tech("Java 21", TagAnt.Type.DEFAULT)
                        .tech("JavaFX 21", TagAnt.Type.DEFAULT)
                        .tech("LESS", TagAnt.Type.DEFAULT)
                        .tech("JFxium", TagAnt.Type.PRIMARY)
                        .progress("当前完成度", 0.9, ProgressAnt.Status.SUCCESS, "首页已从目录页升级为工程展示页。")
                        .milestone("工程概览板模板化", "完成", TimelineAnt.DotColor.GREEN)
                        .milestone("主页接入快捷入口", "完成", TimelineAnt.DotColor.GREEN)
                        .milestone("继续沉淀项目级公共 section", "进行中", TimelineAnt.DotColor.BLUE)
                        .activity(UiExampleConstants.ROUTE_WORKSPACE_TEMPLATE, "查看工作台模板", "双层 header + 侧栏 + 状态栏的完整壳层", "打开")
                        .activity(UiExampleConstants.ROUTE_HOME, "返回首页", "对比首页和模板页的职责边界", "打开")
                        .onAction(route -> actionState.setText("动作：" + route))
                        .build(),
                actionState
        );
    }

    private Node usageSection() {
        String code = """
                ProjectOverviewTemplate.create()
                        .title("JFXium 项目概览")
                        .description("工程首页总览板")
                        .status("编译通过", TagAnt.Type.SUCCESS)
                        .metric("当前主题", "Ant Design", "主题与密度由 ThemeManager 统一驱动", IconAnt.Path.SETTINGS)
                        .tech("Java 21")
                        .tech("JavaFX 21")
                        .progress("当前完成度", 0.9, ProgressAnt.Status.SUCCESS, "首页已从目录页升级为工程展示页。")
                        .milestone("工程概览板模板化", "完成", TimelineAnt.DotColor.GREEN)
                        .activity("layout.workspace", "查看工作台模板", "双层 header + 侧栏 + 状态栏的完整壳层", "打开")
                        .onAction(this::navigate)
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "这个模板不要求特定业务模型，只要给它一组状态标签、统计、进度和里程碑，就能直接出一张工程总览板。",
                code,
                Demos.column(
                        Demos.labeled("复用建议", TypographyAnt.text("如果项目首页和 README 页都要显示工程概览，直接复用这个模板。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("后续演进", TypographyAnt.text("如果出现更多重复块，可以继续沉淀成更细粒度的 section template。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }
}
