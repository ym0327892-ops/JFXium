package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
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
                        wrapSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Label content = TypographyAnt.text("这里是需要添加水印的内容区域，可以放置任意节点。").build();
        content.setWrapText(true);
        content.setMinHeight(200);

        Node watermark = WatermarkAnt.create()
                .content(content)
                .text("JFXium")
                .build();
        String code = """
                Node watermark = WatermarkAnt.create()
                        .content(myContent)
                        .text("JFXium")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础文字水印", "text() 设置水印文字，默认 -22 度旋转。", code, watermark);
    }

    private Node customSection() {
        Label content = TypographyAnt.text("自定义旋转角度和透明度的水印效果展示区域。").build();
        content.setWrapText(true);
        content.setMinHeight(200);

        Node watermark = WatermarkAnt.create()
                .content(content)
                .text("机密文档")
                .rotate(-45)
                .opacity(0.08)
                .fontSize(20)
                .build();
        String code = """
                Node watermark = WatermarkAnt.create()
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
        Label content = TypographyAnt.text("点击下方按钮切换水印文字。").build();
        content.setWrapText(true);
        content.setMinHeight(150);

        String[] texts = {"JFXium", "机密文档", "内部资料", "DRAFT"};
        int[] textIdx = {0};

        Node watermark = WatermarkAnt.create()
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
                Node watermark = WatermarkAnt.create().content(node).text("JFXium").build();
                WatermarkAnt.Controller controller = WatermarkAnt.controllerOf(watermark);
                controller.setText("机密文档");
                """;
        return Demos.sectionWithCode("3. 动态切换",
                "点击按钮循环切换水印文字，通过 Controller 更新同一个水印层。",
                code, demo);
    }

    private Node wrapSection() {
        Label content = TypographyAnt.text("现有根节点也可以一行包成水印容器。").build();
        content.setWrapText(true);
        content.setMinHeight(150);

        StackPaneAnt root = StackPaneAnt.create().children(content).build();
        root.setMinHeight(220);

        Node watermark = WatermarkAnt.wrap(root, builder -> builder.text("JFXium"));
        String code = """
                StackPaneAnt root = StackPaneAnt.create().children(content).build();
                Node watermark = WatermarkAnt.wrap(root, b -> b.text("JFXium"));

                // 如果 root 已经是 Scene 的根节点，也可以直接：
                // WatermarkAnt.wrap(scene, b -> b.text("JFXium"));
                """;
        return Demos.sectionWithCode("4. 一行接入", "wrap() 可以直接把现有根节点包成水印容器；Scene 根节点也能一行替换。", code, watermark);
    }

    /**
     * 交互演示 section（M19.PlayGround）—— controller 模式样板。
     *
     * <p>WatermarkAnt 的 Controller 暴露了完整的运行时 API：{@code setText / setRotate /
     * setGap / setOpacity / setFontSize / setColor / setImage} —— 全部原地修改、无重建。
     * 这是 Controller 模式最典型的应用场景。</p>
     *
     * <p>{@link PlayGround#rebindController} 把这套流程封装成「声明式 API」：
     * 准备 Binder 状态盒子 + 写 controller apply + 串 row 即可。</p>
     */
    private Node playgroundSection() {
        // 1. 准备展示内容
        Label content = TypographyAnt.text("水印 Playground 内容区域 —— 试试切换文字 / 角度 / 间距。").build();
        content.setWrapText(true);
        content.setMinHeight(180);

        // 2. 预先 build 出水印节点
        Node watermark = WatermarkAnt.create()
                .content(content)
                .text("JFXium")
                .build();
        WatermarkAnt.Controller controller = WatermarkAnt.controllerOf(watermark);

        // 3. 状态盒子
        Binder<String> text   = PlayGround.binder("JFXium");
        Binder<String> rotate = PlayGround.binder("-22");
        Binder<String> gap    = PlayGround.binder("100");

        // 4. controller apply —— 读 binder 状态 → 原地修改水印层
        Runnable apply = () -> {
            controller.setText(text.get());
            try {
                controller.setRotate(Double.parseDouble(rotate.get()));
            } catch (NumberFormatException e) {
                controller.setRotate(-22);
            }
            try {
                double g = Double.parseDouble(gap.get());
                controller.setGap(g, g);
            } catch (NumberFormatException e) {
                controller.setGap(100, 100);
            }
        };

        // 5. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变水印的文字 / 旋转角度 / 间距 —— Controller 原地修改无重建。",
                PlayGround.rebindController(watermark, apply, null,
                        PlayGround.row("文字", PlayGround.textField(text, text.get(), "水印文字")),
                        PlayGround.row("角度", PlayGround.segmented(rotate,
                                PlayGround.entry("0",   "0°"),
                                PlayGround.entry("-22", "-22°"),
                                PlayGround.entry("-45", "-45°"),
                                PlayGround.entry("45",  "45°"))),
                        PlayGround.row("间距", PlayGround.segmented(gap,
                                PlayGround.entry("60",  "60"),
                                PlayGround.entry("100", "100"),
                                PlayGround.entry("160", "160"),
                                PlayGround.entry("220", "220")))));
    }
}
