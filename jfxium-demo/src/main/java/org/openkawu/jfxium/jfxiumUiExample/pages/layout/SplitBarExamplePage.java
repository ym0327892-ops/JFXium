package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.SplitBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * SplitBar 三段式 —— 两段 / 三段 / 自定义间距。
 */
public class SplitBarExamplePage extends VBoxAnt {

    public SplitBarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("SplitBar 三段式")
                .description("左-中-右三段式布局容器，中间自动填充，常用于 Header / Toolbar。")
                .sections(
                        twoSegmentSection(),
                        threeSegmentSection(),
                        customGapSection()
                )
                .padding(24)
                .build());
    }

    private Node twoSegmentSection() {
        Node demo = SplitBarAnt.create()
                .left(new Label("左侧标题"))
                .right(ButtonAnt.create("操作").type(ButtonAnt.Type.PRIMARY).build())
                .build();
        String code = """
                SplitBarAnt.create()
                        .left(new Label("左侧标题"))
                        .right(ButtonAnt.create("操作").type(ButtonAnt.Type.PRIMARY).build())
                        .build();
                """;
        return Demos.sectionWithCode("1. 两段式（左+右）",
                "只设 left + right，中间自动撑开。",
                code, demo);
    }

    private Node threeSegmentSection() {
        Node demo = SplitBarAnt.create()
                .left(new Label("品牌"))
                .center(new Label("居中内容"))
                .right(ButtonAnt.create("设置").build())
                .build();
        String code = """
                SplitBarAnt.create()
                        .left(new Label("品牌"))
                        .center(new Label("居中内容"))
                        .right(ButtonAnt.create("设置").build())
                        .build();
                """;
        return Demos.sectionWithCode("2. 三段式（左+中+右）",
                "left + center + right 完整三段布局。",
                code, demo);
    }

    private Node customGapSection() {
        Node demo = SplitBarAnt.create()
                .left(new Label("Logo"))
                .right(
                        ButtonAnt.create("帮助").build(),
                        ButtonAnt.create("退出").type(ButtonAnt.Type.DANGER).build()
                )
                .gap(24)
                .build();
        String code = """
                SplitBarAnt.create()
                        .left(new Label("Logo"))
                        .right(btn1, btn2)
                        .gap(24)
                        .build();
                """;
        return Demos.sectionWithCode("3. 自定义间距",
                "gap(24) 设置各段之间的间距。",
                code, demo);
    }
}
