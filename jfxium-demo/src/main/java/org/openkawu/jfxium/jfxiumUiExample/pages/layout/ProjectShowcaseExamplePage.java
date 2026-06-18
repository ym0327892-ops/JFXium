package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectDashboardTemplate;

/**
 * ProjectShowcaseTemplate 工程项目展示首页模板示例页。
 *
 * <p>现在这一页更偏向“工程项目展示成品模板”的演示：
 * 不再手写 hero / overview / release / launchPad 的拼装，而是直接展示
 * {@link ProjectDashboardTemplate} 这一层聚合模板如何把常见首页组合一把收口。</p>
 */
public class ProjectShowcaseExamplePage extends VBoxAnt {

    public ProjectShowcaseExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectShowcase 工程项目展示首页模板")
                .description("把工程展示首页从单独模板拼装，进一步收口成一个更成品化的项目首页模板。")
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
                "适合工程主页、产品展示页、仓库首页、项目文档首页这类需要把多个模板拼成一页的场景。",
                Demos.column(
                        Demos.labeled("首页定位", TypographyAnt.text("把页面从“控件目录”升级成“工程门面”。")
                                .type(TypographyAnt.Type.SECONDARY).build()),
                        Demos.labeled("组合方式", TypographyAnt.text("现在可以直接复用 ProjectDashboardTemplate，一次性得到门面、概览、发布和快捷入口。")
                                .type(TypographyAnt.Type.SECONDARY).build()),
                        Demos.labeled("复用价值", TypographyAnt.text("不同项目的首页只需要替换数据，不需要重写壳层。")
                                .type(TypographyAnt.Type.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label actionState = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.Type.SECONDARY)
                .build();

        Node dashboard = ProjectDashboardTemplate.create()
                .snapshot(ProjectDashboardTemplate.Snapshot.demo())
                .onAction(action -> actionState.setText("动作：" + action))
                .build();

        return Demos.section(
                "2. 预览",
                "下面这块就是“成品工程首页”的默认形态：默认门面、默认概览、默认发布节奏和默认快捷入口都已经收口。",
                dashboard,
                actionState
        );
    }

    private Node usageSection() {
        String code = """
                ProjectDashboardTemplate.create()
                        .snapshot(ProjectDashboardTemplate.Snapshot.demo())
                        .onAction(action -> System.out.println("动作：" + action))
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "这个模板把工程首页最常重复的组合继续上提一层，适合真实项目主页和示例项目首页。",
                code,
                Demos.column(
                        Demos.labeled("落地建议", TypographyAnt.text("如果你的首页总是写概览 + 发布 + 快捷入口，可以直接复用这个模板。")
                                .type(TypographyAnt.Type.SECONDARY).build()),
                        Demos.labeled("继续整合", TypographyAnt.text("如果多个项目都要工程门面，可以继续把数据从页面层迁到配置层。")
                                .type(TypographyAnt.Type.SECONDARY).build())
                )
        );
    }
}
