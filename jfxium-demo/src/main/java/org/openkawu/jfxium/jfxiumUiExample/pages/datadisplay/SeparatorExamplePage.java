package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.geometry.Orientation;
import javafx.scene.Node;

import org.openkawu.jfxium.component.control.SeparatorAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Separator 分隔符 —— 水平 / 垂直。
 */
public class SeparatorExamplePage extends VBoxAnt {

    public SeparatorExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Separator 分隔符")
                .description("视觉分隔线控件，JavaFX 原生 Separator 的 Builder 封装，支持水平/垂直方向。")
                .sections(
                        basicSection(),
                        verticalSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                TypographyAnt.text("上方内容区域").build(),
                SeparatorAnt.create()
                        .orientation(Orientation.HORIZONTAL)
                        .build(),
                TypographyAnt.text("下方内容区域").build()
        );
        String code = """
                SeparatorAnt.create()
                    .orientation(Orientation.HORIZONTAL)
                    .build();
                """;
        return Demos.sectionWithCode("1. 水平分隔符",
                "orientation(HORIZONTAL) 生成水平分隔线，用于纵向排列内容之间的区隔。",
                code, demo);
    }

    private Node verticalSection() {
        Node demo = Demos.row(
                TypographyAnt.text("左侧").build(),
                SeparatorAnt.create()
                        .orientation(Orientation.VERTICAL)
                        .build(),
                TypographyAnt.text("中间").build(),
                SeparatorAnt.create()
                        .orientation(Orientation.VERTICAL)
                        .build(),
                TypographyAnt.text("右侧").build()
        );
        String code = """
                SeparatorAnt.create()
                    .orientation(Orientation.VERTICAL)
                    .build();
                """;
        return Demos.sectionWithCode("2. 垂直分隔符",
                "orientation(VERTICAL) 生成垂直分隔线，用于横向排列元素之间的区隔。",
                code, demo);
    }

    /** 3. PlayGround：实时切换方向 + 内容长度。 */
    private Node playgroundSection() {
        Binder<String> orientation = PlayGround.binder("horizontal");
        Binder<String> contentLength = PlayGround.binder("short");

        return PlayGround.rebindRebuild(
                () -> {
                    Orientation o = "vertical".equals(orientation.get())
                            ? Orientation.VERTICAL : Orientation.HORIZONTAL;
                    boolean useLong = "long".equals(contentLength.get());
                    String label = useLong ? "这是一段比较长的标签文字内容用来演示效果" : "标签";
                    Node separator = SeparatorAnt.create().orientation(o).build();
                    return wrapWithContent(separator, o, label);
                },
                "方向 / 内容长度",
                PlayGround.row("方向", PlayGround.segmented(orientation,
                        PlayGround.entry("horizontal", "水平"),
                        PlayGround.entry("vertical", "垂直"))),
                PlayGround.row("内容长度", PlayGround.segmented(contentLength,
                        PlayGround.entry("short", "短"),
                        PlayGround.entry("long", "长")))
        );
    }

    /** 按方向组合「标签 + 分隔符」演示组。 */
    private Node wrapWithContent(Node separator, Orientation o, String label) {
        Node textNode = TypographyAnt.text(label).build();
        if (o == Orientation.HORIZONTAL) {
            return Demos.column(textNode, separator, textNode);
        } else {
            return Demos.row(textNode, separator, textNode);
        }
    }
}