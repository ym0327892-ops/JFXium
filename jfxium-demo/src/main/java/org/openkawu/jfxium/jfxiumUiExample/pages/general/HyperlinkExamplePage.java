package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.HyperlinkAnt;

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
                        underlineSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.column(
                HyperlinkAnt.create("查看文档")
                        .onClick(() -> System.out.println("点击：查看文档"))
                        .build(),
                HyperlinkAnt.create("隐私政策")
                        .onClick(() -> System.out.println("点击：隐私政策"))
                        .build(),
                HyperlinkAnt.create("用户协议")
                        .onClick(() -> System.out.println("点击：用户协议"))
                        .build()
        );
        String code = """
                HyperlinkAnt.create("查看文档")
                        .onClick(() -> System.out.println("点击跳转"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "create(text) 创建超链接；onClick() 设置点击回调。",
                code, demo);
    }

    private Node disabledSection() {
        Node demo = Demos.column(
                HyperlinkAnt.create("可用链接")
                        .onClick(() -> System.out.println("点击"))
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
                        .onClick(() -> System.out.println("点击"))
                        .build(),
                HyperlinkAnt.create("默认（hover 时显示下划线）")
                        .onClick(() -> System.out.println("点击"))
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
}
