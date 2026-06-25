package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.composite.CodeBlockAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.template.ProjectQuickStartTemplate;

/**
 * ProjectQuickStartTemplate 快速上手步骤模板示例页。
 */
public class ProjectQuickStartExamplePage extends VBoxAnt {

    public ProjectQuickStartExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ProjectQuickStart 快速上手模板")
                .description("把项目的上手步骤做成「步骤条 + 代码片段」的引导面板，让新用户最快速度跑通项目。")
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
                "适合项目首页「快速开始」区域、README 展示页的 Getting Started、文档站首页的引导步骤。",
                Demos.column(
                        Demos.labeled("首页定位", TypographyAnt.text("让新用户 3 步跑通项目。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("展示方式", TypographyAnt.text("步骤条导航 + 每步说明 + 可选代码片段。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("通用性", TypographyAnt.text("任何项目的快速上手区域都可以直接复用。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }

    private Node previewSection() {
        Node quickStartPanel = ProjectQuickStartTemplate.create()
                .title("JFXium 快速上手")
                .description("3 步跑通 JFXium 项目")
                .step("添加依赖", "在 pom.xml 中引入 JFXium 核心依赖",
                        """
                        <dependency>
                            <groupId>org.openkawu</groupId>
                            <artifactId>jfxium</artifactId>
                            <version>1.0-SNAPSHOT</version>
                        </dependency>
                        """,
                        CodeBlockAnt.Language.XML)
                .step("创建入口", "编写 Application 启动类，使用 Builder API 构建 UI",
                        """
                        public class MyApp extends Application {
                            @Override
                            public void start(Stage stage) {
                                ButtonAnt btn = ButtonAnt.create("Hello JFXium")
                                        .type(ButtonAnt.Type.ACCENT)
                                        .onClick(e -> MessageAnt.success("Hello JFXium!"))
                                        .build();
                                Node root = VBoxAnt.create()
                                        .spacing(16).align(Pos.CENTER)
                                        .children(btn).build();
                                stage.setScene(new Scene(root, 640, 480));
                                ThemeManager.getInstance().applyTheme(new LightTheme());
                                stage.show();
                            }
                        }
                        """,
                        CodeBlockAnt.Language.JAVA)
                .step("运行项目", "执行 Maven 命令启动 JavaFX 应用",
                        """
                        ./mvnw javafx:run -pl my-app
                        """,
                        CodeBlockAnt.Language.SHELL)
                .build();

        return Demos.section(
                "2. 预览",
                "下面这块就是项目首页常见的快速上手引导，新用户照着步骤走就能跑通。",
                quickStartPanel
        );
    }

    private Node usageSection() {
        String code = """
                ProjectQuickStartTemplate.create()
                        .title("快速上手")
                        .description("3 步跑通项目")
                        .step("添加依赖", "在 pom.xml 中引入依赖",
                              "<dependency>...</dependency>", CodeBlockAnt.Language.XML)
                        .step("创建入口", "编写 Application 启动类",
                              "public class MyApp extends Application { ... }",
                              CodeBlockAnt.Language.JAVA)
                        .step("运行项目", "执行 Maven 命令",
                              "./mvnw javafx:run", CodeBlockAnt.Language.SHELL)
                        .build();
                """;

        return Demos.sectionWithCode(
                "3. 使用代码",
                "这个模板把快速上手步骤统一组织，避免首页和文档站重复写 Getting Started。",
                code,
                Demos.column(
                        Demos.labeled("复用建议", TypographyAnt.text("如果你的项目首页有 Getting Started 区域，可以直接复用这个模板。")
                                .type(TypographyAnt.TextColor.SECONDARY).build()),
                        Demos.labeled("继续整合", TypographyAnt.text("配合 ProjectFeatureTemplate 使用，先展示特性再引导上手。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                )
        );
    }
}
