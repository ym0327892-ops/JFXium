package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.WatermarkAnt;

/**
 * Watermark 水印 —— 基础文字水印 / 自定义旋转与透明度。
 */
public class WatermarkExamplePage extends VBoxAnt {

    public WatermarkExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Watermark 水印")
                .description("给页面或区域添加背景水印，常用于敏感信息防泄漏。")
                .sections(
                        basicSection(),
                        customSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Label content = new Label("这里是需要添加水印的内容区域，可以放置任意节点。");
        content.setWrapText(true);
        content.setMinHeight(200);

        StackPane watermark = WatermarkAnt.create()
                .content(content)
                .text("JFXium")
                .build();
        String code = """
                StackPane watermark = WatermarkAnt.create()
                        .content(myContent)
                        .text("JFXium")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础文字水印", "text() 设置水印文字，默认 -22 度旋转。", code, watermark);
    }

    private Node customSection() {
        Label content = new Label("自定义旋转角度和透明度的水印效果展示区域。");
        content.setWrapText(true);
        content.setMinHeight(200);

        StackPane watermark = WatermarkAnt.create()
                .content(content)
                .text("机密文档")
                .rotate(-45)
                .opacity(0.08)
                .fontSize(20)
                .build();
        String code = """
                StackPane watermark = WatermarkAnt.create()
                        .content(myContent)
                        .text("机密文档")
                        .rotate(-45)
                        .opacity(0.08)
                        .fontSize(20)
                        .build();
                """;
        return Demos.sectionWithCode("2. 自定义旋转与透明度",
                "rotate() 设置角度，opacity() 设置透明度，fontSize() 设置字号。", code, watermark);
    }
}
