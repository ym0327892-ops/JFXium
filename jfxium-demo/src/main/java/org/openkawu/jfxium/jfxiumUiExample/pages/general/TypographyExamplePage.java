package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Typography 排版 —— 标题 / 段落 / 文本类型。
 */
public class TypographyExamplePage extends VBoxAnt {

    public TypographyExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Typography 排版")
                .description("文本的基本格式：标题、段落、各种语义文本样式。")
                .sections(titleSection(), paragraphSection(), textTypeSection(), decorationSection(),
                        playgroundSection())
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
        Label secondary = TypographyAnt.text("Secondary 次要").type(TypographyAnt.TextColor.SECONDARY).build();
        Label success = TypographyAnt.text("Success 成功").type(TypographyAnt.TextColor.SUCCESS).build();
        Label warning = TypographyAnt.text("Warning 警告").type(TypographyAnt.TextColor.WARNING).build();
        Label danger = TypographyAnt.text("Danger 危险").type(TypographyAnt.TextColor.DANGER).build();
        Label disabled = TypographyAnt.text("Disabled 禁用").type(TypographyAnt.TextColor.DISABLED).build();
        Label strong = TypographyAnt.text("Strong 加粗").strong().build();
        Label code = TypographyAnt.text("Code 代码").code().build();
        Node demo = Demos.row(primary, secondary, success, warning, danger, disabled, strong, code);
        String codeStr = """
                TypographyAnt.text("Primary 默认").build();
                TypographyAnt.text("Secondary 次要").type(TypographyAnt.TextColor.SECONDARY).build();
                TypographyAnt.text("Success 成功").type(TypographyAnt.TextColor.SUCCESS).build();
                TypographyAnt.text("Warning 警告").type(TypographyAnt.TextColor.WARNING).build();
                TypographyAnt.text("Danger 危险").type(TypographyAnt.TextColor.DANGER).build();
                TypographyAnt.text("Disabled 禁用").type(TypographyAnt.TextColor.DISABLED).build();
                TypographyAnt.text("Strong 加粗").strong().build();
                TypographyAnt.text("Code 代码").code().build();
                """;
        return Demos.sectionWithCode("3. 文本类型",
                "type() 切换语义颜色；strong() / code() 等装饰方法。", codeStr, demo);
    }

    private Node decorationSection() {
        Label italic = TypographyAnt.text("Italic 斜体").italic().build();
        Label underline = TypographyAnt.text("Underline 下划线").underline().build();
        Label delete = TypographyAnt.text("Delete 删除线").delete().build();
        Label mark = TypographyAnt.text("Mark 高亮").mark().build();
        Label copyable = TypographyAnt.text("Copyable 点击复制").copyable().build();
        Node demo = Demos.column(
                Demos.row(italic, underline, delete, mark, copyable),
                TypographyAnt.text("提示：italic / underline / delete / mark 是纯装饰；copyable() 会接管 onMouseClicked 把文本复制到剪贴板。")
                        .type(TypographyAnt.TextColor.SECONDARY).build()
        );
        String codeStr = """
                TypographyAnt.text("斜体").italic().build();
                TypographyAnt.text("下划线").underline().build();
                TypographyAnt.text("删除线").delete().build();
                TypographyAnt.text("高亮").mark().build();
                TypographyAnt.text("可复制").copyable().build();
                """;
        return Demos.sectionWithCode("4. 富排版装饰",
                "italic() / underline() / delete() / mark() / copyable() 一键开启 5 种装饰；copyable 独占 onMouseClicked。",
                codeStr, demo);
    }

    /** 5. PlayGround：实时切换 variant / 语义色 / 装饰 / 截断行数（仅 paragraph 生效）。 */
    private Node playgroundSection() {
        Binder<String> variant = PlayGround.binder("paragraph");
        Binder<String> textType = PlayGround.binder("default");
        Binder<String> decoration = PlayGround.binder("none");
        Binder<String> ellipsisRows = PlayGround.binder("2");

        return PlayGround.rebindRebuild(
                () -> buildTypography(variant.get(), textType.get(), decoration.get(), ellipsisRows.get()),
                "变体 / 语义色 / 装饰 / 截断",
                PlayGround.row("变体", PlayGround.segmented(variant,
                        PlayGround.entry("title1", "标题 1"),
                        PlayGround.entry("title2", "标题 2"),
                        PlayGround.entry("title3", "标题 3"),
                        PlayGround.entry("title4", "标题 4"),
                        PlayGround.entry("title5", "标题 5"),
                        PlayGround.entry("paragraph", "段落"),
                        PlayGround.entry("text", "文本"))),
                PlayGround.row("语义色", PlayGround.segmented(textType,
                        PlayGround.entry("default", "默认"),
                        PlayGround.entry("secondary", "次要"),
                        PlayGround.entry("success", "成功"),
                        PlayGround.entry("warning", "警告"),
                        PlayGround.entry("danger", "危险"),
                        PlayGround.entry("disabled", "禁用"))),
                PlayGround.row("装饰", PlayGround.segmented(decoration,
                        PlayGround.entry("none", "无"),
                        PlayGround.entry("strong", "加粗"),
                        PlayGround.entry("italic", "斜体"),
                        PlayGround.entry("underline", "下划线"),
                        PlayGround.entry("delete", "删除线"),
                        PlayGround.entry("code", "代码"),
                        PlayGround.entry("mark", "高亮"),
                        PlayGround.entry("copyable", "可复制"))),
                PlayGround.row("截断行数", PlayGround.textField(ellipsisRows, "2", "仅 paragraph 生效")));
    }

    private Node buildTypography(String variant, String textType, String decoration, String ellipsisRowsStr) {
        // title/paragraph Builder 不支持 type()/strong()/italic()/underline()/delete()/code()/mark()/copyable()。
        // 所以 variant=title*|paragraph 时统一忽略 textType 与 decoration —— 由 TextBuilder 独占使用。
        TypographyAnt.TextColor type = parseType(textType);
        String text;
        Label result;

        switch (variant) {
            case "title1" -> result = TypographyAnt.title("标题 一级 / 38px", 1).build();
            case "title2" -> result = TypographyAnt.title("标题 二级 / 30px", 2).build();
            case "title3" -> result = TypographyAnt.title("标题 三级 / 24px", 3).build();
            case "title4" -> result = TypographyAnt.title("标题 四级 / 20px", 4).build();
            case "title5" -> result = TypographyAnt.title("标题 五级 / 16px", 5).build();
            case "paragraph" -> {
                text = "这是一段用于演示排版的文本内容。当开启 ellipsis 并设置 rows 时，"
                        + "超出指定行数的内容会被省略号截断；关闭 ellipsis 则按容器宽度自动换行。"
                        + "本字段是排版组件的演示载体，用于实时反映控制区参数的视觉效果。";
                TypographyAnt.ParagraphBuilder pb = TypographyAnt.paragraph(text);
                int rows;
                try { rows = Math.max(0, Integer.parseInt(ellipsisRowsStr)); }
                catch (NumberFormatException e) { rows = 0; }
                pb.ellipsis(rows > 0).rows(rows);
                result = pb.build();
                result.setMaxWidth(360);
            }
            default -> { // "text"
                text = "内联文本样式";
                TypographyAnt.TextBuilder tb = TypographyAnt.text(text).type(type);
                applyDecoration(tb, decoration);
                result = tb.build();
            }
        }
        return result;
    }

    private TypographyAnt.TextColor parseType(String name) {
        if (name == null) return TypographyAnt.TextColor.PRIMARY;
        return switch (name) {
            case "secondary" -> TypographyAnt.TextColor.SECONDARY;
            case "success"   -> TypographyAnt.TextColor.SUCCESS;
            case "warning"   -> TypographyAnt.TextColor.WARNING;
            case "danger"    -> TypographyAnt.TextColor.DANGER;
            case "disabled"  -> TypographyAnt.TextColor.DISABLED;
            default          -> TypographyAnt.TextColor.PRIMARY;
        };
    }

    /** 装饰方法只对 TextBuilder 生效（Title/Paragraph Builder 不支持）。 */
    private void applyDecoration(TypographyAnt.TextBuilder b, String decoration) {
        switch (decoration) {
            case "strong"    -> b.strong();
            case "italic"    -> b.italic();
            case "underline" -> b.underline();
            case "delete"    -> b.delete();
            case "code"      -> b.code();
            case "mark"      -> b.mark();
            case "copyable"  -> b.copyable();
            default          -> { /* none */ }
        }
    }
}