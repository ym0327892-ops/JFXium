package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.QRCodeAnt;

/**
 * QRCode 二维码 —— 基础 / 尺寸与说明文字。
 */
public class QRCodeExamplePage extends VBoxAnt {

    public QRCodeExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("QRCode 二维码")
                .description("将文本或链接渲染为二维码（演示用简化绘制实现）。")
                .sections(basicSection(), sizeSection(), dynamicSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = QRCodeAnt.create()
                .value("https://github.com/openkawu/JFXium")
                .size(160)
                .build();
        String code = """
                QRCodeAnt.create()
                        .value("https://github.com/openkawu/JFXium")
                        .size(160)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "value 指定编码内容，默认带边框。", code, demo);
    }

    private Node sizeSection() {
        Node big = QRCodeAnt.create().value("JFXium").size(200).build();
        Node caption = TypographyAnt.text("扫码访问 JFXium")
                .type(TypographyAnt.TextColor.SECONDARY).build();
        Node demo = Demos.column(big, caption);
        String code = """
                QRCodeAnt.create().value("JFXium").size(200).build();
                // 说明文字用 Typography 放在二维码下方
                TypographyAnt.text("扫码访问 JFXium")
                        .type(TypographyAnt.TextColor.SECONDARY).build();
                """;
        return Demos.sectionWithCode("2. 尺寸与说明文字",
                "size 调整边长，搭配下方说明文字形成完整卡片。", code, demo);
    }

    private Node dynamicSection() {
        Node qr = QRCodeAnt.create().value("JFXium").size(160).build();
        QRCodeAnt.Controller controller = QRCodeAnt.controllerOf(qr);

        ButtonAnt regenBtn = ButtonAnt.create("重新生成")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    String newValue = "JFXium-" + System.currentTimeMillis() % 10000;
                    controller.setValue(newValue);
                })
                .build();

        Node demo = Demos.column(qr, regenBtn);
        String code = """
                Node qr = QRCodeAnt.create().value("JFXium").size(160).build();
                QRCodeAnt.Controller controller = QRCodeAnt.controllerOf(qr);
                controller.setValue(newValue);
                """;
        return Demos.sectionWithCode("3. 动态重新生成",
                "点击按钮更换编码内容，通过 Controller 重绘同一个二维码节点。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 QRCode 的 4 个维度：编码内容 / 尺寸 / 前景色 / 背景色。
     *
     * <p>QRCodeAnt 提供静态 {@code controllerOf(Node)}，Controller 暴露
     * {@code setValue / setSize / setColor / setBgColor}（都是 Canvas 原地重绘，不重建节点）。
     * 所以采用 {@link PlayGround#rebindController}：apply Runnable 内部调 controller.setXxx，
     * display 节点不重建、无闪烁、Canvas 引用始终有效。</p>
     *
     * <p>说明：QRCodeAnt 项目内并不存在 level（容错率）字段，此处不列为 4 个可改属性
     * 之一；为了让 playground 仍有「调色」能力，新增了 bgColor 选项与 color 配对。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> value   = PlayGround.binder("https://github.com/openkawu/JFXium");
        Binder<String> size    = PlayGround.binder("160");
        Binder<String> color   = PlayGround.binder("black");
        Binder<String> bgColor = PlayGround.binder("white");

        // 2. 预 build display 并取 controller（controllerOf 要求 node 已 build）
        Node qr = QRCodeAnt.create().value(value.get()).size(parseSize(size.get())).build();
        QRCodeAnt.Controller controller = QRCodeAnt.controllerOf(qr);

        // 3. apply Runnable —— 读 binder → 调 controller.setXxx（Canvas 原地重绘）
        Runnable apply = () -> {
            controller.setValue(value.get());
            controller.setSize(parseSize(size.get()));
            controller.setColor(parseColor(color.get()));
            controller.setBgColor(parseBgColor(bgColor.get()));
        };

        // 4. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 QRCode 的内容 / 尺寸 / 前景 / 背景 —— Controller 在原 Canvas 上重绘，不重建节点。",
                PlayGround.rebindController(qr, apply, null,
                        PlayGround.row("内容", PlayGround.textField(value, value.get(), "输入 URL 或文本")),
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("120", "120"),
                                PlayGround.entry("160", "160"),
                                PlayGround.entry("200", "200"),
                                PlayGround.entry("240", "240"))),
                        PlayGround.row("前景", PlayGround.segmented(color,
                                PlayGround.entry("black", "默认黑"),
                                PlayGround.entry("blue",  "蓝"),
                                PlayGround.entry("red",   "红"),
                                PlayGround.entry("green", "绿"))),
                        PlayGround.row("背景", PlayGround.segmented(bgColor,
                                PlayGround.entry("white", "默认白"),
                                PlayGround.entry("light",  "浅灰"),
                                PlayGround.entry("lightblue", "浅蓝")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static int parseSize(String v) {
        if (v == null) return 160;
        try { return Math.max(40, Math.min(400, Integer.parseInt(v))); }
        catch (NumberFormatException e) { return 160; }
    }

    private static Color parseColor(String v) {
        if (v == null) return Color.BLACK;
        return switch (v) {
            case "blue"  -> Color.web("#1677ff");
            case "red"   -> Color.web("#ff4d4f");
            case "green" -> Color.web("#52c41a");
            default      -> Color.BLACK;
        };
    }

    private static Color parseBgColor(String v) {
        if (v == null) return Color.WHITE;
        return switch (v) {
            case "light"      -> Color.web("#f5f5f5");
            case "lightblue"  -> Color.web("#e6f4ff");
            default           -> Color.WHITE;
        };
    }
}
