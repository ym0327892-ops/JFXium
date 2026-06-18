package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.DrawerAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Drawer 抽屉 —— 4 个方向滑入 + 自定义尺寸。
 *
 * <p>跟 Modal 的边界：Modal 居中弹窗、用户必须处理；Drawer 从屏幕边缘滑入、
 * 适合"详情页"、"筛选条件"、"设置面板"等不打断主操作的辅助内容。</p>
 *
 * <p><b>API 形式</b>：跟 Modal 一样是 Result 包装型 —— build() 返回 DrawerResult，
 * 必须再调 .open(owner) 才会显示。</p>
 */
public class DrawerExamplePage extends VBoxAnt {

    public DrawerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Drawer 抽屉")
                .description("从屏幕边缘滑入的辅助面板，常用于详情查看 / 表单编辑。")
                .sections(
                        placementSection(),
                        sizeSection()
                )
                .padding(24)
                .build());
    }

    /** 1. 4 个方向的滑入。 */
    private Node placementSection() {
        Node row = Demos.row(
                ButtonAnt.create("从右侧滑入（默认）")
                        .type(ButtonAnt.Type.PRIMARY)
                        .onClick(e -> openSimple(DrawerAnt.Placement.RIGHT, "RIGHT"))
                        .build(),
                ButtonAnt.create("从左侧")
                        .onClick(e -> openSimple(DrawerAnt.Placement.LEFT, "LEFT"))
                        .build(),
                ButtonAnt.create("从顶部")
                        .onClick(e -> openSimple(DrawerAnt.Placement.TOP, "TOP"))
                        .build(),
                ButtonAnt.create("从底部")
                        .onClick(e -> openSimple(DrawerAnt.Placement.BOTTOM, "BOTTOM"))
                        .build()
        );
        String code = """
                DrawerAnt.create()
                        .title("Drawer 演示")
                        .content(contentNode)
                        .placement(DrawerAnt.Placement.RIGHT)   // LEFT / RIGHT / TOP / BOTTOM
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("1. 4 个方向",
                "placement(LEFT/RIGHT/TOP/BOTTOM) —— 决定从哪个方向滑入。",
                code, row);
    }

    /** 2. 自定义尺寸（width 影响左右抽屉，height 影响上下抽屉）。 */
    private Node sizeSection() {
        Node row = Demos.row(
                ButtonAnt.create("窄抽屉 320px")
                        .onClick(e -> DrawerAnt.create()
                                .title("窄抽屉")
                                .content("width(320) —— 适合简单详情。")
                                .placement(DrawerAnt.Placement.RIGHT)
                                .width(320)
                                .build()
                                .open((Node) e.getSource()))
                        .build(),
                ButtonAnt.create("宽抽屉 720px")
                        .onClick(e -> DrawerAnt.create()
                                .title("宽抽屉")
                                .content("width(720) —— 适合编辑表单。")
                                .placement(DrawerAnt.Placement.RIGHT)
                                .width(720)
                                .build()
                                .open((Node) e.getSource()))
                        .build()
        );
        String code = """
                // RIGHT/LEFT 时用 width 控制宽度
                DrawerAnt.create()
                        .title("窄抽屉")
                        .content("适合简单详情。")
                        .placement(DrawerAnt.Placement.RIGHT)
                        .width(320)
                        .build()
                        .open(ownerNode);

                // TOP/BOTTOM 时用 height 控制高度
                DrawerAnt.create()
                        .title("底部抽屉")
                        .content("适合筛选条件。")
                        .placement(DrawerAnt.Placement.BOTTOM)
                        .height(300)
                        .build()
                        .open(ownerNode);
                """;
        return Demos.sectionWithCode("2. 自定义宽度",
                "width(int) —— RIGHT/LEFT 时是宽度；TOP/BOTTOM 时改用 height(int)。",
                code, row);
    }

    /** 内部辅助：打开一个最朴素的 Drawer 演示某个 placement。 */
    private void openSimple(DrawerAnt.Placement placement, String label) {
        VBox content = VBarAnt.create()
                .compact()
                .gap(8)
                .top(
                        TypographyAnt.text("当前 placement = " + label).build(),
                        TypographyAnt.text("点击外部蒙层、按 ESC、或点击右上角 X 都可关闭。").build()
                )
                .build();

        // owner 传 stage 任意节点即可；这里用一个临时 Label 取 scene 即可
        // 实际项目用触发按钮自己作为 owner 更直接，见下面的 sizeSection
        Node owner = this;
        DrawerAnt.create()
                .title("Drawer 演示 - " + label)
                .content(content)
                .placement(placement)
                .build()
                .open(owner);
    }
}
