package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectFeatureTemplate;

/**
 * ProjectFeatureTemplate 项目特性/亮点展示模板示例页。
 */
public class ProjectFeatureExamplePage extends VBoxAnt {

    public ProjectFeatureExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectFeature 项目特性模板")
                .description("把项目的核心能力和技术亮点做成「图标 + 标题 + 说明」的卡片网格，一眼扫完项目卖点。")
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
                "适合项目首页「为什么选择 XXX」区域、产品 Landing Page 的特性矩阵、README 首页的功能亮点。",
                Demos.column(
                        Demos.labeled("首页定位", TypographyAnt.text("用卡片网格快速传达项目核心价值。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("复用方式", TypographyAnt.text("不同项目只需要替换特性列表，不需要重写布局。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("通用性", TypographyAnt.text("项目展示页、产品介绍页、README 首页都可以直接复用。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Label actionState = TypographyAnt.text("动作：未触发")
                .type(TypographyAnt.TextColor.SECONDARY)
                .build();

        Node featurePanel = ProjectFeatureTemplate.create()
                .title("JFXium 核心特性")
                .description("为 JavaFX 开发者提供的一站式 UI 增强能力")
                .columns(2)
                .feature("builder", "Builder 模式", "所有组件统一 Builder API，链式调用一目了然", IconAnt.Path.SETTINGS)
                .feature("theme", "11 套主题", "Ant Design / Material / Shadcn 等风格一键切换", IconAnt.Path.DASHBOARD)
                .feature("components", "97+ 组件", "控件、组合、弹层、布局、模板全覆盖", IconAnt.Path.HOME)
                .feature("nofxml", "零 FXML", "纯 Java 代码构建 UI，不需要 XML 配置文件", IconAnt.Path.FILE)
                .feature("defensive", "防御性编程", "null 安全、输入校验、尺寸钳制，全链路防御", IconAnt.Path.BELL)
                .feature("i18n", "i18n 国际化", "内置中文/英文资源包，运行时切换语言", IconAnt.Path.EDIT)
                .onAction(key -> actionState.setText("动作：" + key))
                .build();

        return Demos.section(
                "2. 预览",
                "下面这块就是项目首页常见的特性矩阵，用户可以一眼看到项目的核心能力。",
                featurePanel,
                actionState
        );
    }

    private Node usageSection() {
        String code = """
                ProjectFeatureTemplate.create()
                        .title("核心特性")
                        .description("项目提供的核心能力")
                        .columns(2)
                        .feature("Builder 模式", "所有组件统一 Builder API", IconAnt.Path.SETTINGS)
                        .feature("11 套主题", "多种风格一键切换", IconAnt.Path.DASHBOARD)
                        .feature("97+ 组件", "控件、组合、弹层全覆盖", IconAnt.Path.HOME)
                        .feature("零 FXML", "纯 Java 构建 UI", IconAnt.Path.FILE)
                        .onAction(key -> MessageAnt.info("点击了特性: " + key))
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "如果你的项目首页需要展示特性亮点，可以直接复用这个模板，只需要替换特性列表。",
                code,
                Demos.column(
                        Demos.labeled("复用建议", TypographyAnt.text("配合 ProjectShowcaseTemplate 使用，把特性网格放在首页中间位置。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("继续整合", TypographyAnt.text("如果多个项目都用特性网格，可以把特性数据从页面层迁到配置层。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }
}
