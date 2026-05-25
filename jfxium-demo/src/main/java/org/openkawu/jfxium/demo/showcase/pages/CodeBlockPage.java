package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CodeBlockAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * CodeBlock 代码块展示页（M19.12）。
 */
public class CodeBlockPage implements ShowcasePage {

    @Override public String   key()      { return "code-block"; }
    @Override public String   title()    { return "CodeBlock 代码块"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("CodeBlock 代码块");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("展示代码片段——支持复制、行号、标题、可挂语言标签。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionWithLineNumbers(),
                        sectionWithTitle(),
                        sectionMultiLanguage()
                )
                .build();
    }

    private Node sectionBasic() {
        Node code = CodeBlockAnt.create()
                .language("java")
                .content("Button btn = ButtonAnt.create(\"Click\").type(ButtonAnt.Type.PRIMARY).build();")
                .copyable()
                .onCopy(c -> MessageAnt.success("已复制"))
                .build();

        return ShowcaseSection.create()
                .title("场景 1：基础（带复制按钮）")
                .description(".language + .content + .copyable() —— 右上角自动出现复制按钮")
                .demo(code)
                .code("""
                        CodeBlockAnt.create()
                            .language("java")
                            .content("Button btn = ButtonAnt.create(\\"Click\\").build();")
                            .copyable()
                            .onCopy(c -> notify("已复制"))
                            .build();
                        """)
                .build();
    }

    private Node sectionWithLineNumbers() {
        Node code = CodeBlockAnt.create()
                .language("java")
                .content("public class Hello {\n"
                        + "    public static void main(String[] args) {\n"
                        + "        System.out.println(\"Hello, JFXium!\");\n"
                        + "    }\n"
                        + "}")
                .showLineNumbers(true)
                .copyable()
                .build();

        return ShowcaseSection.create()
                .title("场景 2：显示行号")
                .description(".showLineNumbers(true) —— 多行代码场景必备")
                .demo(code)
                .code("""
                        CodeBlockAnt.create()
                            .language("java")
                            .content(multiLineCode)
                            .showLineNumbers(true)
                            .copyable()
                            .build();
                        """)
                .build();
    }

    private Node sectionWithTitle() {
        Node code = CodeBlockAnt.create()
                .title("UserService.java")
                .language("java")
                .content("@Service\n"
                        + "public class UserService {\n"
                        + "    public User findById(Long id) {\n"
                        + "        return userRepo.findById(id);\n"
                        + "    }\n"
                        + "}")
                .showLineNumbers(true)
                .copyable()
                .build();

        return ShowcaseSection.create()
                .title("场景 3：带文件名标题")
                .description(".title(\"...\") —— 顶部显示文件名/标题；类似 IDE 的代码片段展示")
                .demo(code)
                .code("""
                        CodeBlockAnt.create()
                            .title("UserService.java")
                            .language("java")
                            .content(code)
                            .showLineNumbers(true)
                            .copyable()
                            .build();
                        """)
                .build();
    }

    private Node sectionMultiLanguage() {
        Node json = CodeBlockAnt.create()
                .language("json")
                .content("{\n  \"name\": \"jfxium\",\n  \"version\": \"1.0.0\"\n}")
                .copyable()
                .build();
        Node bash = CodeBlockAnt.create()
                .language("bash")
                .content("$ mvn install -pl jfxium -DskipTests\n$ mvn javafx:run -pl jfxium-demo")
                .copyable()
                .build();
        Node css = CodeBlockAnt.create()
                .language("css")
                .content(".my-button {\n  -fx-background-color: -color-accent-emphasis;\n  -fx-text-fill: white;\n}")
                .copyable()
                .build();

        VBox col = VBoxBuilder.create().spacing(12).children(json, bash, css).build();

        return ShowcaseSection.create()
                .title("场景 4：多语言（json / bash / css）")
                .description(".language(...) —— 影响顶部语言标签；语法高亮按主题统一柔和配色")
                .demo(col)
                .code("""
                        CodeBlockAnt.create().language("json").content(...).build();
                        CodeBlockAnt.create().language("bash").content(...).build();
                        CodeBlockAnt.create().language("css").content(...).build();
                        """)
                .build();
    }
}
