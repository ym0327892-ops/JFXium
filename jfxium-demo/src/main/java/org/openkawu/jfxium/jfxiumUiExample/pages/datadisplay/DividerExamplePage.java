package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.DividerAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * Divider 分割线 —— 水平 / 带文本 / 文本位置 / 垂直。
 */
public class DividerExamplePage extends VBoxAnt {

    public DividerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Divider 分割线")
                .description("区隔内容的视觉分割线，支持水平/垂直方向、带文本标注、文本位置控制。")
                .sections(
                        basicSection(),
                        textSection(),
                        positionSection(),
                        verticalSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                TypographyAnt.text("上方内容").build(),
                DividerAnt.create().build(),
                TypographyAnt.text("下方内容").build()
        );
        String code = """
                DividerAnt.create().build();
                """;
        return Demos.sectionWithCode("1. 基础水平分割线",
                "无参 create() 默认生成水平分割线。",
                code, demo);
    }

    private Node textSection() {
        Node demo = Demos.column(
                Demos.column(
                        DividerAnt.create().text("居中文本").build()
                ),
                Demos.column(
                        DividerAnt.create().text("这是章节分隔").build()
                )
        );
        String code = """
                DividerAnt.create().text("居中文本").build();
                DividerAnt.create().text("这是章节分隔").build();
                """;
        return Demos.sectionWithCode("2. 带文本",
                "text(String) 在分割线中间显示文字标注，常用于'或'、章节名等。",
                code, demo);
    }

    private Node positionSection() {
        Node demo = Demos.column(
                DividerAnt.create().text("靠左").position(DividerAnt.Position.LEFT).build(),
                DividerAnt.create().text("居中（默认）").position(DividerAnt.Position.CENTER).build(),
                DividerAnt.create().text("靠右").position(DividerAnt.Position.RIGHT).build()
        );
        String code = """
                DividerAnt.create().text("靠左")
                        .position(DividerAnt.Position.LEFT).build();
                DividerAnt.create().text("居中（默认）")
                        .position(DividerAnt.Position.CENTER).build();
                DividerAnt.create().text("靠右")
                        .position(DividerAnt.Position.RIGHT).build();
                """;
        return Demos.sectionWithCode("3. 文本位置",
                "position(LEFT/CENTER/RIGHT) 控制分割线中文本的位置，对应 Ant Design 的 orientation 属性。",
                code, demo);
    }

    private Node verticalSection() {
        Node demo = Demos.row(
                TypographyAnt.text("项目一").build(),
                DividerAnt.create().vertical().build(),
                TypographyAnt.text("项目二").build(),
                DividerAnt.create().vertical().build(),
                TypographyAnt.text("项目三").build()
        );
        String code = """
                HBox box = HBoxAnt.create().spacing(12).children(
                        TypographyAnt.text("项目一").build(),
                        DividerAnt.create().vertical().build(),
                        TypographyAnt.text("项目二").build(),
                        DividerAnt.create().vertical().build(),
                        TypographyAnt.text("项目三").build()
                );
                """;
        return Demos.sectionWithCode("4. 垂直分割线",
                "vertical() 创建垂直方向分割线，适合在横向排列的元素之间插入。",
                code, demo);
    }
}
