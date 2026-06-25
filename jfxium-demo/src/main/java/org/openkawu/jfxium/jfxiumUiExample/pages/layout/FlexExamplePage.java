package org.openkawu.jfxium.jfxiumUiExample.pages.layout;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.FlexAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;

/**
 * Flex 弹性布局 —— 行 / 列 / 两端对齐 / 换行。
 */
public class FlexExamplePage extends VBoxAnt {

    public FlexExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Flex 弹性布局")
                .description("CSS Flexbox 风格的弹性布局容器，支持方向、对齐、换行。")
                .sections(
                        rowSection(),
                        columnSection(),
                        justifySection(),
                        wrapSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node rowSection() {
        Node demo = FlexAnt.create()
                .gap(12)
                .children(
                        ButtonAnt.create("按钮 1").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("按钮 2").build(),
                        ButtonAnt.create("按钮 3").build()
                )
                .build();
        String code = """
                FlexAnt.create()
                        .gap(12)
                        .children(btn1, btn2, btn3)
                        .build();
                """;
        return Demos.sectionWithCode("1. 水平排列（默认）",
                "默认水平方向排列子元素。",
                code, demo);
    }

    private Node columnSection() {
        Node demo = FlexAnt.createVertical()
                .gap(8)
                .children(
                        ButtonAnt.create("上").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("中").build(),
                        ButtonAnt.create("下").build()
                )
                .build();
        String code = """
                FlexAnt.createVertical()
                        .gap(8)
                        .children(btn1, btn2, btn3)
                        .build();
                """;
        return Demos.sectionWithCode("2. 垂直排列",
                "createVertical() 纵向排列子元素。",
                code, demo);
    }

    private Node justifySection() {
        Node demo = FlexAnt.create()
                .gap(12)
                .justify(FlexAnt.Justify.BETWEEN)
                .children(
                        ButtonAnt.create("左侧").build(),
                        ButtonAnt.create("中间").build(),
                        ButtonAnt.create("右侧").build()
                )
                .build();
        String code = """
                FlexAnt.create()
                        .gap(12)
                        .justify(FlexAnt.Justify.BETWEEN)
                        .children(left, center, right)
                        .build();
                """;
        return Demos.sectionWithCode("3. 两端对齐",
                "justify(BETWEEN) 子元素两端对齐，中间均匀分布。",
                code, demo);
    }

    /** 5. PlayGround：实时调整 direction / justify / align / gap。 */
    private Node playgroundSection() {
        Binder<String> direction = PlayGround.binder("row");
        Binder<String> justify = PlayGround.binder("start");
        Binder<String> align = PlayGround.binder("center");
        Binder<String> gap = PlayGround.binder("12");
        return PlayGround.rebindRebuild(
                () -> buildFlex(direction.get(), justify.get(), align.get(), gap.get()),
                "方向 / 对齐 / 间距",
                PlayGround.row("方向", PlayGround.segmented(direction,
                        PlayGround.entry("row", "水平"),
                        PlayGround.entry("column", "垂直"))),
                PlayGround.row("主轴对齐", PlayGround.segmented(justify,
                        PlayGround.entry("start", "起点"),
                        PlayGround.entry("center", "居中"),
                        PlayGround.entry("end", "终点"),
                        PlayGround.entry("between", "两端"))),
                PlayGround.row("交叉轴对齐", PlayGround.segmented(align,
                        PlayGround.entry("start", "起点"),
                        PlayGround.entry("center", "居中"),
                        PlayGround.entry("end", "终点"),
                        PlayGround.entry("stretch", "拉伸"))),
                PlayGround.row("间距 gap", PlayGround.textField(gap, "12", "子元素间 px")));
    }

    private Node buildFlex(String direction, String justify, String align, String gapStr) {
        FlexAnt.Direction dir = "column".equals(direction)
                ? FlexAnt.Direction.COLUMN : FlexAnt.Direction.ROW;
        FlexAnt.Justify j = switch (justify) {
            case "center"  -> FlexAnt.Justify.CENTER;
            case "end"     -> FlexAnt.Justify.END;
            case "between" -> FlexAnt.Justify.BETWEEN;
            default        -> FlexAnt.Justify.START;
        };
        FlexAnt.Align a = switch (align) {
            case "start"   -> FlexAnt.Align.START;
            case "end"     -> FlexAnt.Align.END;
            case "stretch" -> FlexAnt.Align.STRETCH;
            default        -> FlexAnt.Align.CENTER;
        };
        double g;
        try { g = Math.max(0, Double.parseDouble(gapStr)); }
        catch (NumberFormatException e) { g = 12.0; }
        return FlexAnt.create()
                .direction(dir)
                .justify(j)
                .align(a)
                .gap(g)
                .children(
                        ButtonAnt.create("按钮 1").type(ButtonAnt.Type.PRIMARY).build(),
                        ButtonAnt.create("按钮 2").build(),
                        ButtonAnt.create("按钮 3").build()
                )
                .build();
    }

    private Node wrapSection() {
        Node demo = FlexAnt.create()
                .wrap(true)
                .columnGap(12)
                .rowGap(8)
                .children(
                        ButtonAnt.create("标签 1").build(),
                        ButtonAnt.create("标签 2").build(),
                        ButtonAnt.create("标签 3").build(),
                        ButtonAnt.create("标签 4").build(),
                        ButtonAnt.create("标签 5").build(),
                        ButtonAnt.create("标签 6").build()
                )
                .build();
        String code = """
                FlexAnt.create()
                        .wrap(true)
                        .columnGap(12)
                        .rowGap(8)
                        .children(btn1, btn2, btn3, btn4, btn5, btn6)
                        .build();
                """;
        return Demos.sectionWithCode("4. 自动换行",
                "wrap(true) 超出容器宽度时自动换行；columnGap/rowGap 分别控制列/行间距。",
                code, demo);
    }
}
