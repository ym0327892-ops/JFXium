package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TitledPaneAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * TitledPane 标题面板 —— 基础 / 展开/折叠 / 嵌套 / 与 Accordion 配合。
 */
public class TitledPaneExamplePage extends VBoxAnt {

    public TitledPaneExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("TitledPane 标题面板")
                .description("带标题的可折叠面板，可独立展示或放入 Accordion 中作为手风琴项使用。")
                .sections(
                        basicSection(),
                        nestedSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                TitledPaneAnt.create()
                        .title("基础面板")
                        .content(TypographyAnt.text("这是一个默认展开的面板，内容可以是任意 Node。")
                                .type(TypographyAnt.Type.SECONDARY).build())
                        .expanded(true)
                        .build(),
                TitledPaneAnt.create()
                        .title("折叠的面板")
                        .content(TypographyAnt.text("默认折叠，点击标题展开查看内容。")
                                .type(TypographyAnt.Type.SECONDARY).build())
                        .expanded(false)
                        .build()
        );
        String code = """
                TitledPaneAnt.create()
                    .title("基础面板")
                    .content(new Label("内容..."))
                    .expanded(true)
                    .build();

                TitledPaneAnt.create()
                    .title("折叠的面板")
                    .content(new Label("内容..."))
                    .expanded(false)
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "title() 设置面板标题；content() 设置面板内容；expanded() 控制初始展开/折叠状态。",
                code, demo);
    }

    private Node nestedSection() {
        Node inner = TitledPaneAnt.create()
                .title("内层面板")
                .content(TypographyAnt.text("这是嵌套在外部面板内的面板。")
                        .type(TypographyAnt.Type.SECONDARY).build())
                .expanded(false)
                .build();

        Node outer = TitledPaneAnt.create()
                .title("外层面板（展开看嵌套）")
                .content(Demos.column(
                        TypographyAnt.text("外层面板的内容区域，下面嵌套了一个子面板：").build(),
                        inner
                ))
                .expanded(true)
                .build();

        String code = """
                // 嵌套用法
                TitledPaneAnt inner = TitledPaneAnt.create()
                    .title("内层面板")
                    .content(...)
                    .build();

                TitledPaneAnt outer = TitledPaneAnt.create()
                    .title("外层面板")
                    .content(new VBox(new Label("..."), inner))
                    .build();
                """;
        return Demos.sectionWithCode("2. 嵌套使用",
                "TitledPane 支持多级嵌套，形成层级折叠结构。也可配合 AccordionAnt 实现互斥折叠。",
                code, outer);
    }
}
