package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.HyperlinkAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * Hyperlink 超链接 —— 基础 / 禁用 / 下划线 / 已访问。
 */
public class HyperlinkExamplePage extends VBoxAnt {

    public HyperlinkExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Hyperlink 超链接")
                .description("文本超链接控件，支持点击跳转、回调、禁用、下划线样式。")
                .sections(
                        basicSection(),
                        disabledSection(),
                        underlineSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                HyperlinkAnt.create("查看文档")
                        .onClick(() -> MessageAnt.info("点击了：查看文档"))
                        .build(),
                HyperlinkAnt.create("隐私政策")
                        .onClick(() -> MessageAnt.info("点击了：隐私政策"))
                        .build(),
                HyperlinkAnt.create("用户协议")
                        .onClick(() -> MessageAnt.info("点击了：用户协议"))
                        .build()
        );
        String code = """
                HyperlinkAnt.create("查看文档")
                        .onClick(() -> MessageAnt.info("点击了：查看文档"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "create(text) 创建超链接；onClick() 设置点击回调。",
                code, demo);
    }

    private Node disabledSection() {
        Node demo = Demos.column(
                HyperlinkAnt.create("可用链接")
                        .onClick(() -> MessageAnt.info("点击了：可用链接"))
                        .build(),
                HyperlinkAnt.create("已失效的链接")
                        .disabled(true)
                        .build()
        );
        String code = """
                HyperlinkAnt.create("可用链接")
                        .onClick(() -> ...)
                        .build();
                HyperlinkAnt.create("已失效")
                        .disabled(true)
                        .build();
                """;
        return Demos.sectionWithCode("2. 禁用状态",
                "disabled(true) 禁用超链接，视觉变灰且不可点击。",
                code, demo);
    }

    private Node underlineSection() {
        Node demo = Demos.column(
                HyperlinkAnt.create("始终显示下划线")
                        .underline(true)
                        .onClick(() -> MessageAnt.info("点击了：始终显示下划线"))
                        .build(),
                HyperlinkAnt.create("默认（hover 时显示下划线）")
                        .onClick(() -> MessageAnt.info("点击了：默认（hover 时显示下划线）"))
                        .build()
        );
        String code = """
                HyperlinkAnt.create("始终显示下划线")
                        .underline(true)
                        .build();
                HyperlinkAnt.create("默认（hover 时显示）")
                        .build();
                """;
        return Demos.sectionWithCode("3. 下划线样式",
                "underline(true) 始终显示下划线；默认仅在 hover 时显示。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Hyperlink 的 4 个维度：文本 / 禁用 / 下划线 / 已访问。
     *
     * <p>HyperlinkAnt 继承自 JavaFX {@link javafx.scene.control.Hyperlink}，
     * 所有属性（text/disabled/underline/visited）都是直接修改 this，没有独立 Controller。
     * 采用 {@link PlayGround#rebindRebuild} —— 每次 binder 变化都重新
     * {@code create() + 链式 + build()} 生成全新的 Hyperlink 节点。</p>
     */
    private Node playgroundSection() {
        Binder<String> textBinder     = PlayGround.binder("立即试用");
        Binder<String> disabledBinder = PlayGround.binder("no");    // yes/no
        Binder<String> underlineBinder = PlayGround.binder("yes");  // yes/no (always show)
        Binder<String> visitedBinder  = PlayGround.binder("no");    // yes/no

        Supplier<Node> factory = () -> {
            String textRaw = textBinder.get();
            final String text = textRaw == null || textRaw.isBlank() ? "立即试用" : textRaw;
            final boolean disabled = "yes".equals(disabledBinder.get());
            boolean underline = "yes".equals(underlineBinder.get());
            boolean visited = "yes".equals(visitedBinder.get());

            return HyperlinkAnt.create(text)
                    .disabled(disabled)
                    .underline(underline)
                    .visited(visited)
                    .onClick(() -> MessageAnt.info("点击：" + text))
                    .build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Hyperlink 的文本内容、禁用状态、是否始终显示下划线、是否标记为已访问 —— 四种状态任意组合，右侧立刻看到效果。",
                PlayGround.rebindRebuild(factory, "文本 / 禁用 / 下划线 / 已访问",
                        PlayGround.row("文本", PlayGround.textField(textBinder, "立即试用", "输入链接文本")),
                        PlayGround.row("禁用", PlayGround.segmented(disabledBinder,
                                PlayGround.entry("no",  "启用"),
                                PlayGround.entry("yes", "禁用"))),
                        PlayGround.row("下划线", PlayGround.segmented(underlineBinder,
                                PlayGround.entry("no",  "仅 hover"),
                                PlayGround.entry("yes", "始终显示"))),
                        PlayGround.row("已访问", PlayGround.segmented(visitedBinder,
                                PlayGround.entry("no",  "未访问"),
                                PlayGround.entry("yes", "已访问")))));
    }
}