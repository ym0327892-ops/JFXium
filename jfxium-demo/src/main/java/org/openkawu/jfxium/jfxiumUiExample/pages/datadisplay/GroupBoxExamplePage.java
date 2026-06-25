package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.GroupBoxAnt;

import java.util.function.Supplier;

/**
 * GroupBox 分组框 —— 基础 / 边框 / extra 操作。
 */
public class GroupBoxExamplePage extends VBoxAnt {

    public GroupBoxExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("GroupBox 分组框")
                .description("带标题边框的内容容器，用于将相关控件组织在同一视觉区域。")
                .sections(
                        basicSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                GroupBoxAnt.create()
                        .title("基本信息")
                        .content(TypographyAnt.text("这里放置一组相关输入控件。").build())
                        .bordered(true)
                        .build()
        );
        String code = """
                GroupBoxAnt.create()
                        .title("基本信息")
                        .content(TypographyAnt.text("这里放置一组相关输入控件。").build())
                        .bordered(true)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础分组框", "带边框和标题的桌面风格分组框。", code, demo);
    }

    private Node playgroundSection() {
        Binder<String> size     = PlayGround.binder("medium");
        Binder<String> type     = PlayGround.binder("default");
        Binder<String> bordered = PlayGround.binder("true");
        Binder<String> headerBg = PlayGround.binder("true");
        Binder<String> hover    = PlayGround.binder("true");

        Supplier<Node> factory = () -> {
            Node extraBtn = ButtonAnt.create("更多").type(ButtonAnt.Type.LINK).build();
            return GroupBoxAnt.create()
                    .title("配置面板")
                    .extra(extraBtn)
                    .content(TypographyAnt.text("这里放置一组相关输入控件。").build())
                    .size(parseSize(size.get()))
                    .type(parseType(type.get()))
                    .bordered(parseBool(bordered.get()))
                    .headerBackground(parseBool(headerBg.get()))
                    .hoverable(parseBool(hover.get()))
                    .build();
        };

        Node demo = Demos.row(
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("大小", PlayGround.segmented(size,
                                PlayGround.entry("medium", "中等"),
                                PlayGround.entry("small", "小"))),
                        PlayGround.row("类型", PlayGround.segmented(type,
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("inner", "内嵌"))),
                        PlayGround.row("边框", PlayGround.segmented(bordered,
                                PlayGround.entry("true", "显示边框"),
                                PlayGround.entry("false", "隐藏边框"))),
                        PlayGround.row("头部底色", PlayGround.segmented(headerBg,
                                PlayGround.entry("true", "头部底色"),
                                PlayGround.entry("false", "无底色"))),
                        PlayGround.row("悬停效果", PlayGround.segmented(hover,
                                PlayGround.entry("true", "悬停高亮"),
                                PlayGround.entry("false", "关闭悬停")))
                )
        );

        String code = """
                Node extraBtn = ButtonAnt.create("更多")
                        .type(ButtonAnt.Type.LINK).build();
                GroupBoxAnt.create()
                        .title("配置面板")
                        .extra(extraBtn)
                        .content(TypographyAnt.text("...").build())
                        .size(Size.MIDDLE)
                        .type(GroupBoxAnt.Type.DEFAULT)
                        .bordered(true)
                        .headerBackground(true)
                        .hoverable(true)
                        .build();
                """;
        return Demos.sectionWithCode("2. PlayGround 交互演示",
                "动态调整 size / type / bordered / headerBackground / hoverable 五个维度。",
                code, demo);
    }

    private static Size parseSize(String v) {
        return "small".equals(v) ? Size.SMALL : Size.MIDDLE;
    }

    private static GroupBoxAnt.Type parseType(String v) {
        return "inner".equals(v) ? GroupBoxAnt.Type.INNER : GroupBoxAnt.Type.DEFAULT;
    }

    private static boolean parseBool(String v) {
        return "true".equals(v) || "on".equals(v);
    }
}
