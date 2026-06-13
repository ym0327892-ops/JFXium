package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
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
                .sections(basicSection(), sizeSection(), dynamicSection())
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

    private Node dynamicSection() {
        Node[] qrHolder = new Node[1];
        qrHolder[0] = QRCodeAnt.create().value("JFXium").size(160).build();

        ButtonAnt regenBtn = ButtonAnt.create("重新生成")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    VBox parent = (VBox) qrHolder[0].getParent();
                    if (parent != null) {
                        int idx = parent.getChildren().indexOf(qrHolder[0]);
                        String newValue = "JFXium-" + System.currentTimeMillis() % 10000;
                        parent.getChildren().set(idx,
                                QRCodeAnt.create().value(newValue).size(160).build());
                        qrHolder[0] = parent.getChildren().get(idx);
                    }
                })
                .build();

        Node demo = Demos.column(qrHolder[0], regenBtn);
        String code = """
                // 初始构建
                QRCodeAnt.create().value("JFXium").size(160).build();
                // 动态更新：rebuild + replace
                parent.getChildren().set(idx,
                        QRCodeAnt.create().value(newValue).size(160).build());
                """;
        return Demos.sectionWithCode("3. 动态重新生成",
                "点击按钮更换编码内容，演示 rebuild + replace 动态更新二维码。",
                code, demo);
    }
}
