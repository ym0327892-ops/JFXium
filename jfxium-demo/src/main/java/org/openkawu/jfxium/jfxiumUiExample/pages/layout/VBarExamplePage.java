package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;

import org.openkawu.jfxium.component.composite.HBarAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.IconAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.builder.Radius;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * VBar 竖向条状容器 —— 上 / 中 / 下三段式布局原子，适合卡片、侧栏、设置页。
 */
public class VBarExamplePage extends VBoxAnt {

    public VBarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("VBar 竖向条状容器")
                .description("上-中-下三段式布局原子，语义上对应 VBarAnt。中间自动撑开。适合卡片、侧栏面板、设置区等纵向组合。")
                .sections(
                        twoSegmentSection(),
                        threeSegmentSection(),
                        cardSection(),
                        multiNodeSection(),
                        sizingSection()
                )
                .padding(24)
                .build());
    }

    // ============================================================
    // 1. 两段式（上 + 下）—— 不设 center，自动退化
    // ============================================================
    private Node twoSegmentSection() {
        Node demo = VBarAnt.create()
                .top(TypographyAnt.text("顶部标题").build())
                .bottom(ButtonAnt.create("底部操作").type(ButtonAnt.Type.PRIMARY).build())
                .gap(8)
                .padding(12, 16, 12, 16)
                .minH(180)
                .background(Background.DEFAULT)
                .borderRadius(Radius.MD)
                .build();
        String code = """
                VBarAnt.create()
                        .top(TypographyAnt.text("顶部标题").build())
                        .bottom(ButtonAnt.create("底部操作").type(ButtonAnt.Type.PRIMARY).build())
                        .gap(8)
                        .padding(12, 16, 12, 16)
                        .minH(180)
                        .build();
                """;
        return Demos.sectionWithCode("1. 两段式（上+下）",
                "只设 top + bottom，中间由弹性 spacer 自动撑开。不调 center() 即自动退化。",
                code, demo);
    }

    // ============================================================
    // 2. 三段式（上 + 中 + 下）—— center 真正居中
    // ============================================================
    private Node threeSegmentSection() {
        Node header = HBarAnt.create()
                .left(TypographyAnt.text("标题").build())
                .right(TypographyAnt.text("状态：可编辑").build())
                .gap(8)
                .build();

        Node footer = HBarAnt.create()
                .right(ButtonAnt.create("确认").type(ButtonAnt.Type.PRIMARY).build())
                .gap(8)
                .build();

        Node demo = VBarAnt.create()
                .top(header)
                .center(TypographyAnt.text("中间内容").build())
                .bottom(footer)
                .gap(8)
                .padding(12, 16, 12, 16)
                .minH(200)
                .background(Background.SUBTLE)
                .borderRadius(Radius.MD)
                .build();
        String code = """
                Node header = HBarAnt.create()
                        .left(TypographyAnt.text("标题").build())
                        .right(TypographyAnt.text("状态：可编辑").build())
                        .gap(8)
                        .build();

                Node footer = HBarAnt.create()
                        .right(ButtonAnt.create("确认").type(ButtonAnt.Type.PRIMARY).build())
                        .gap(8)
                        .build();

                VBarAnt.create()
                        .top(header)
                        .center(TypographyAnt.text("中间内容").build())
                        .bottom(footer)
                        .gap(8)
                        .padding(12, 16, 12, 16)
                        .minH(200)
                        .build();
                """;
        return Demos.sectionWithCode("2. 三段式（上+中+下）",
                "top / bottom 常常直接放 HBar 语义的横向条（当前实现名 HBarAnt），负责 header / footer 的左右分栏；VBarAnt 负责纵向结构，center 真正居中。",
                code, demo);
    }

    // ============================================================
    // 3. 卡片组合 —— 最常见场景
    // ============================================================
    private Node cardSection() {
        Node body = VBoxAnt.create()
                .spacing(8)
                .children(
                        TypographyAnt.text("这是卡片的主体内容。").build(),
                        TypographyAnt.text("可放描述、表单项、列表摘要或预览信息。").build()
                )
                .build();

        Node header = HBarAnt.create()
                .left(
                        IconAnt.path(IconAnt.Path.FILE, 20),
                        TypographyAnt.title("配置卡片", 4).build()
                )
                .right(ButtonAnt.create("更多").type(ButtonAnt.Type.TEXT).build())
                .gap(8)
                .build();

        Node footer = HBarAnt.create()
                .right(
                        ButtonAnt.create("取消").build(),
                        ButtonAnt.create("保存").type(ButtonAnt.Type.PRIMARY).build()
                )
                .gap(8)
                .build();

        Node demo = VBarAnt.create()
                .top(header)
                .center(body)
                .bottom(footer)
                .gap(12)
                .padding(16)
                .background(Background.DEFAULT)
                .borderRadius(Radius.MD)
                .maxW(420)
                .build();
        String code = """
                Node header = HBarAnt.create()
                        .left(icon, title)
                        .right(ButtonAnt.create("更多").type(ButtonAnt.Type.TEXT).build())
                        .gap(8)
                        .build();

                VBarAnt.create()
                        .top(header)
                        .center(bodyBox)
                        .bottom(
                                HBarAnt.create()
                                        .right(cancelBtn, saveBtn)
                                        .gap(8)
                                        .build()
                        )
                        .gap(12)
                        .padding(16)
                        .background(Background.DEFAULT)
                        .borderRadius(Radius.MD)
                        .maxW(420)
                        .build();
                """;
        return Demos.sectionWithCode("3. 卡片组合",
                "最常见的卡片场景：header / body / footer。顶部和底部通常继续用 HBar 语义的横向条（当前实现名 HBarAnt），VBarAnt 只负责纵向骨架。",
                code, demo);
    }

    // ============================================================
    // 4. 每段多节点 —— 纵向依然可混排
    // ============================================================
    private Node multiNodeSection() {
        Node demo = VBarAnt.create()
                .top(
                        TypographyAnt.title("项目概览", 5).build(),
                        TypographyAnt.text("这里可以继续加一行副标题").build()
                )
                .center(
                        TypographyAnt.text("中间内容 1").build(),
                        TypographyAnt.text("中间内容 2").build(),
                        TypographyAnt.text("中间内容 3").build()
                )
                .bottom(
                        ButtonAnt.create("刷新").build(),
                        ButtonAnt.create("展开").build()
                )
                .gap(6)
                .padding(12, 16, 12, 16)
                .minH(220)
                .background(Background.SUBTLE)
                .borderRadius(Radius.MD)
                .build();
        String code = """
                VBarAnt.create()
                        .top(title, subtitle)
                        .center(row1, row2, row3)
                        .bottom(refreshBtn, expandBtn)
                        .gap(6)
                        .build();
                """;
        return Demos.sectionWithCode("4. 每段多节点",
                "top / center / bottom 都支持可变参数，按添加顺序纵向排列。",
                code, demo);
    }

    // ============================================================
    // 5. 尺寸控制 —— gap / padding / minH
    // ============================================================
    private Node sizingSection() {
        Node demo1 = VBarAnt.create()
                .top(TypographyAnt.text("padding(8)").build())
                .bottom(ButtonAnt.create("按钮").build())
                .padding(8)
                .minH(140)
                .background(Background.DEFAULT)
                .borderRadius(Radius.MD)
                .build();
        Node demo2 = VBarAnt.create()
                .top(TypographyAnt.text("padding(12,16,12,16)").build())
                .bottom(ButtonAnt.create("按钮").build())
                .padding(12, 16, 12, 16)
                .gap(12)
                .minH(160)
                .background(Background.DEFAULT)
                .borderRadius(Radius.MD)
                .build();
        Node demo3 = VBarAnt.create()
                .top(TypographyAnt.text("minH=220").build())
                .center(TypographyAnt.text("中间会被 spacer 撑开").build())
                .bottom(ButtonAnt.create("按钮").build())
                .padding(12, 16, 12, 16)
                .gap(8)
                .minH(220)
                .background(Background.SUBTLE)
                .borderRadius(Radius.MD)
                .build();
        String code = """
                VBarAnt.create()
                        .padding(12, 16, 12, 16)
                        .gap(8)
                        .minH(220)
                        .build();
                """;
        return Demos.sectionWithCode("5. 尺寸控制",
                "gap / padding / minH 共同决定竖向节奏。默认不钳死高度，卡片类布局更灵活。",
                code, Demos.column(demo1, demo2, demo3));
    }
}
