package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
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
                .sections(basicSection(), sizeSection())
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
                .type(TypographyAnt.Type.SECONDARY).build();
        Node demo = Demos.column(big, caption);
        String code = """
                QRCodeAnt.create().value("JFXium").size(200).build();
                // 说明文字用 Typography 放在二维码下方
                TypographyAnt.text("扫码访问 JFXium")
                        .type(TypographyAnt.Type.SECONDARY).build();
                """;
        return Demos.sectionWithCode("2. 尺寸与说明文字",
                "size 调整边长，搭配下方说明文字形成完整卡片。", code, demo);
    }
}
