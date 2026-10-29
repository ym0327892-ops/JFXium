package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.TitledPaneAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

import java.util.function.Supplier;

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
                        nestedSection(),
                        arrowSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = VBarAnt.create()
                .compact()
                .gap(8)
                .top(
                        TitledPaneAnt.create()
                                .title("基础面板")
                                .content(TypographyAnt.text("这是一个默认展开的面板，内容可以是任意 Node。")
                                        .type(TypographyAnt.TextColor.SECONDARY).build())
                                .expanded(true)
                                .build(),
                        TitledPaneAnt.create()
                                .title("折叠的面板")
                                .content(TypographyAnt.text("默认折叠，点击标题展开查看内容。")
                                        .type(TypographyAnt.TextColor.SECONDARY).build())
                                .expanded(false)
                                .build()
                )
                .build();
        String code = """
                TitledPaneAnt.create()
                    .title("基础面板")
                    .content(TypographyAnt.text("内容...").build())
                    .expanded(true)
                    .build();

                TitledPaneAnt.create()
                    .title("折叠的面板")
                    .content(TypographyAnt.text("内容...").build())
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
                        .type(TypographyAnt.TextColor.SECONDARY).build())
                .expanded(false)
                .build();

        Node outer = TitledPaneAnt.create()
                .title("外层面板（展开看嵌套）")
                .content(VBarAnt.create()
                        .compact()
                        .gap(8)
                        .top(
                                TypographyAnt.text("外层面板的内容区域，下面嵌套了一个子面板：").build(),
                                inner
                        )
                        .build())
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
                    .content(VBarAnt.create()
                        .compact()
                        .top(TypographyAnt.text("...").build(), inner)
                        .build())
                    .build();
                """;
        return Demos.sectionWithCode("2. 嵌套使用",
                "TitledPane 支持多级嵌套，形成层级折叠结构。多面板互斥折叠用 CollapseAnt.create().accordion(true)。",
                code, outer);
    }

    // ============================================================
    // 3. 箭头位置与显隐
    // ============================================================

    /** 3. 箭头左 / 右 / 隐藏对照。 */
    private Node arrowSection() {
        Node demo = VBarAnt.create()
                .compact()
                .gap(8)
                .top(
                        TitledPaneAnt.create()
                                .title("箭头在左（默认）")
                                .content(TypographyAnt.text("graphicTextGap = 8，箭头与文字有间隔。")
                                        .type(TypographyAnt.TextColor.SECONDARY).build())
                                .build(),
                        TitledPaneAnt.create()
                                .title("箭头在右")
                                .expandIconPosition(TitledPaneAnt.ExpandIconPosition.RIGHT)
                                .content(TypographyAnt.text("contentDisplay=RIGHT，箭头贴标题右侧。")
                                        .type(TypographyAnt.TextColor.SECONDARY).build())
                                .build(),
                        TitledPaneAnt.create()
                                .title("隐藏箭头")
                                .showExpandIcon(false)
                                .content(TypographyAnt.text("整行仍可点击展开/折叠。")
                                        .type(TypographyAnt.TextColor.SECONDARY).build())
                                .build()
                )
                .build();
        String code = """
                // 左侧（默认）
                TitledPaneAnt.create().title("标题").content(content).build();

                // 右侧
                TitledPaneAnt.create()
                        .expandIconPosition(TitledPaneAnt.ExpandIconPosition.RIGHT)
                        .title("标题").content(content).build();

                // 隐藏箭头（仍可点击标题）
                TitledPaneAnt.create()
                        .showExpandIcon(false)
                        .title("标题").content(content).build();
                """;
        return Demos.sectionWithCode("3. 箭头位置与显隐",
                "原生 TitledPane 箭头固定左侧；这里改为自绘 graphic，支持左/右/隐藏，间距 8px。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变初始展开状态 / 折叠动画 / 是否可折叠 / 箭头位置。
     *
     * <p>TitledPaneAnt 没有暴露 modify() / Controller，所有属性都是 build-time。
     * 采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 {@code build()}，
     * 并用 {@link PlayGround#replaceDisplay} 替换展示区节点。</p>
     */
    private Node playgroundSection() {
        Binder<String> initial     = PlayGround.binder("expanded");  // expanded / collapsed
        Binder<String> animated    = PlayGround.binder("yes");       // yes / no
        Binder<String> collapsible = PlayGround.binder("yes");       // yes / no
        Binder<String> arrowPos    = PlayGround.binder("left");      // left / right / hidden

        Node innerContent = VBarAnt.create()
                .compact()
                .gap(4)
                .top(
                        TypographyAnt.text("这是面板内容区域的第一行。").build(),
                        TypographyAnt.text("可通过左侧控件实时调整初始状态、动画、是否可折叠。").build()
                )
                .build();

        Supplier<Node> factory = () -> {
            String arrow = arrowPos.get();
            TitledPaneAnt pane = TitledPaneAnt.create()
                    .title("交互演示面板")
                    .content(innerContent)
                    .expanded("expanded".equals(initial.get()))
                    .animated("yes".equals(animated.get()))
                    .collapsible("yes".equals(collapsible.get()));
            if ("hidden".equals(arrow)) {
                pane.showExpandIcon(false);
            } else {
                pane.expandIconPosition("right".equals(arrow)
                        ? TitledPaneAnt.ExpandIconPosition.RIGHT
                        : TitledPaneAnt.ExpandIconPosition.LEFT);
            }
            return pane.build();
        };

        return Demos.section("4. 交互演示",
                "实时改变初始展开状态、折叠动画、是否可折叠、箭头位置 —— 任意组合立即生效。",
                PlayGround.rebindRebuild(factory, "初始状态 / 动画 / 可折叠 / 箭头",
                        PlayGround.row("初始状态", PlayGround.segmented(initial,
                                PlayGround.entry("expanded",  "展开"),
                                PlayGround.entry("collapsed", "折叠"))),
                        PlayGround.row("折叠动画", PlayGround.segmented(animated,
                                PlayGround.entry("yes", "开"),
                                PlayGround.entry("no",  "关"))),
                        PlayGround.row("可折叠", PlayGround.segmented(collapsible,
                                PlayGround.entry("yes", "可折叠"),
                                PlayGround.entry("no",  "禁止折叠"))),
                        PlayGround.row("箭头", PlayGround.segmented(arrowPos,
                                PlayGround.entry("left",   "左侧"),
                                PlayGround.entry("right",  "右侧"),
                                PlayGround.entry("hidden", "隐藏")))));
    }
}
