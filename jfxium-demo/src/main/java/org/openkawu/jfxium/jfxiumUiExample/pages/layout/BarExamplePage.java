package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;
import javafx.scene.control.Label;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.BarAnt;

/**
 * Bar 横向栏 —— 两段 / 三段 / 自定义间距 / 自控高度。
 *
 * <p>BarAnt 是项目最底层的「横向栏」布局原子（左 + 弹性 + 右），
 * Card/Modal/Drawer 的 header/footer 全部基于它。</p>
 */
public class BarExamplePage extends VBoxAnt {

    public BarExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Bar 横向栏")
                .description("左-中-右三段式布局原子，中间自动填充。Card/Modal/Drawer 的 header/footer 底层都用它。")
                .sections(
                        twoSegmentSection(),
                        threeSegmentSection(),
                        customGapSection(),
                        headerStyleSection()
                )
                .padding(24)
                .build());
    }

    private Node twoSegmentSection() {
        Node demo = BarAnt.create()
                .left(new Label("左侧标题"))
                .right(ButtonAnt.create("操作").type(ButtonAnt.Type.PRIMARY).build())
                .build();
        String code = """
                BarAnt.create()
                        .left(new Label("左侧标题"))
                        .right(ButtonAnt.create("操作").type(ButtonAnt.Type.PRIMARY).build())
                        .build();
                """;
        return Demos.sectionWithCode("1. 两段式（左+右）",
                "只设 left + right，中间自动撑开。",
                code, demo);
    }

    private Node threeSegmentSection() {
        Node demo = BarAnt.create()
                .left(new Label("品牌"))
                .center(new Label("居中内容"))
                .right(ButtonAnt.create("设置").build())
                .build();
        String code = """
                BarAnt.create()
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
        Node demo = BarAnt.create()
                .left(new Label("Logo"))
                .right(
                        ButtonAnt.create("帮助").build(),
                        ButtonAnt.create("退出").type(ButtonAnt.Type.DANGER).build()
                )
                .gap(24)
                .build();
        String code = """
                BarAnt.create()
                        .left(new Label("Logo"))
                        .right(btn1, btn2)
                        .gap(24)
                        .build();
                """;
        return Demos.sectionWithCode("3. 自定义间距",
                "gap(24) 设置各段之间的间距。",
                code, demo);
    }

    private Node headerStyleSection() {
        Node demo = BarAnt.create()
                .left(new Label("时间范围"))
                .right(ButtonAnt.create("设置").build())
                .padding(8, 12, 8, 12)
                .borderBottom()
                .build();
        String code = """
                // Card header 风格：自控高度 + 底部分隔线
                BarAnt.create()
                        .left(new Label("时间范围"))
                        .right(ButtonAnt.create("设置").build())
                        .padding(8, 12, 8, 12)   // 高度由 padding 自控
                        .borderBottom()           // 底部 1px 分隔线
                        .build();
                """;
        return Demos.sectionWithCode("4. Header 风格（自控高度 + 分隔线）",
                "padding() 控制栏高度，borderBottom() 加底部分隔线——这就是 Card/Modal header 的底层用法。",
                code, demo);
    }
}
