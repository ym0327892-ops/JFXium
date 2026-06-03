package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * Typography 排版 —— 标题 / 段落 / 文本类型。
 */
public class TypographyExamplePage extends VBoxAnt {

    public TypographyExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Typography 排版")
                .description("文本的基本格式：标题、段落、各种语义文本样式。")
                .sections(titleSection(), paragraphSection(), textTypeSection())
                .padding(24)
                .build());
    }

    private Node titleSection() {
        Label h1 = TypographyAnt.title("h1. 一级标题", 1).build();
        Label h2 = TypographyAnt.title("h2. 二级标题", 2).build();
        Label h3 = TypographyAnt.title("h3. 三级标题", 3).build();
        Label h4 = TypographyAnt.title("h4. 四级标题", 4).build();
        Label h5 = TypographyAnt.title("h5. 五级标题", 5).build();
        Node demo = Demos.column(h1, h2, h3, h4, h5);
        String code = """
                TypographyAnt.title("h1. 一级标题", 1).build();
                TypographyAnt.title("h2. 二级标题", 2).build();
                TypographyAnt.title("h3. 三级标题", 3).build();
                TypographyAnt.title("h4. 四级标题", 4).build();
                TypographyAnt.title("h5. 五级标题", 5).build();
                """;
        return Demos.sectionWithCode("1. 标题 Title",
                "5 级标题，level 1~5 对应字号 38~16px。", code, demo);
    }

    private Node paragraphSection() {
        Label p = TypographyAnt.paragraph(
                "JFXium 是一套基于 JavaFX 的企业级 UI 组件库，对标 Ant Design 的设计语言，"
                        + "提供丰富的组件和主题系统，帮助开发者快速构建桌面应用。"
        ).build();
        Label ellipsis = TypographyAnt.paragraph(
                "这是一段很长的文字，设置了 ellipsis + rows 限制最大行数。超出部分会被截断，"
                        + "适合在卡片等有限空间内展示摘要信息。这是一段很长的文字用于演示截断效果。"
        ).ellipsis(true).rows(2).build();
        Node demo = Demos.column(p, ellipsis);
        String code = """
                TypographyAnt.paragraph("正文内容...").build();
                TypographyAnt.paragraph("长文本...")
                        .ellipsis(true).rows(2).build();
                """;
        return Demos.sectionWithCode("2. 段落 Paragraph",
                "自动换行；ellipsis + rows 限制最大行数。", code, demo);
    }

    private Node textTypeSection() {
        Label primary = TypographyAnt.text("Primary 默认").build();
        Label secondary = TypographyAnt.text("Secondary 次要").type(TypographyAnt.Type.SECONDARY).build();
        Label success = TypographyAnt.text("Success 成功").type(TypographyAnt.Type.SUCCESS).build();
        Label warning = TypographyAnt.text("Warning 警告").type(TypographyAnt.Type.WARNING).build();
        Label danger = TypographyAnt.text("Danger 危险").type(TypographyAnt.Type.DANGER).build();
        Label disabled = TypographyAnt.text("Disabled 禁用").type(TypographyAnt.Type.DISABLED).build();
        Label strong = TypographyAnt.text("Strong 加粗").strong().build();
        Label code = TypographyAnt.text("Code 代码").code().build();
        Node demo = Demos.row(primary, secondary, success, warning, danger, disabled, strong, code);
        String codeStr = """
                TypographyAnt.text("Primary 默认").build();
                TypographyAnt.text("Secondary 次要").type(TypographyAnt.Type.SECONDARY).build();
                TypographyAnt.text("Success 成功").type(TypographyAnt.Type.SUCCESS).build();
                TypographyAnt.text("Warning 警告").type(TypographyAnt.Type.WARNING).build();
                TypographyAnt.text("Danger 危险").type(TypographyAnt.Type.DANGER).build();
                TypographyAnt.text("Disabled 禁用").type(TypographyAnt.Type.DISABLED).build();
                TypographyAnt.text("Strong 加粗").strong().build();
                TypographyAnt.text("Code 代码").code().build();
                """;
        return Demos.sectionWithCode("3. 文本类型",
                "type() 切换语义颜色；strong() / code() 等装饰方法。", codeStr, demo);
    }
}
