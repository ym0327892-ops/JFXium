package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
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
                        customSection(),
                        dynamicSection(),
                        wrapSection()
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

    private Node dynamicSection() {
        Label content = new Label("点击下方按钮切换水印文字。");
        content.setWrapText(true);
        content.setMinHeight(150);

        String[] texts = {"JFXium", "机密文档", "内部资料", "DRAFT"};
        int[] textIdx = {0};

        StackPane watermark = WatermarkAnt.create()
                .content(content)
                .text(texts[0])
                .build();
        WatermarkAnt.Controller controller = WatermarkAnt.controllerOf(watermark);

        ButtonAnt toggleBtn = ButtonAnt.create("切换水印")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    textIdx[0] = (textIdx[0] + 1) % texts.length;
                    controller.setText(texts[textIdx[0]]);
                })
                .build();

        Node demo = Demos.column(watermark, toggleBtn);
        String code = """
                StackPane watermark = WatermarkAnt.create().content(node).text("JFXium").build();
                WatermarkAnt.Controller controller = WatermarkAnt.controllerOf(watermark);
                controller.setText("机密文档");
                """;
        return Demos.sectionWithCode("3. 动态切换",
                "点击按钮循环切换水印文字，通过 Controller 更新同一个水印层。",
                code, demo);
    }

    private Node wrapSection() {
        Label content = new Label("现有根节点也可以一行包成水印容器。");
        content.setWrapText(true);
        content.setMinHeight(150);

        StackPane root = new StackPane(content);
        root.setMinHeight(220);

        StackPane watermark = WatermarkAnt.wrap(root, builder -> builder.text("JFXium"));
        String code = """
                StackPane root = new StackPane(content);
                StackPane watermark = WatermarkAnt.wrap(root, b -> b.text("JFXium"));

                // 如果 root 已经是 Scene 的根节点，也可以直接：
                // WatermarkAnt.wrap(scene, b -> b.text("JFXium"));
                """;
        return Demos.sectionWithCode("4. 一行接入", "wrap() 可以直接把现有根节点包成水印容器；Scene 根节点也能一行替换。", code, watermark);
    }
}
