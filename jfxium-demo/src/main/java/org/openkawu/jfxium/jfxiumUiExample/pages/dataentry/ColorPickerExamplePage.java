package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.openkawu.jfxium.component.ColorPickerAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * ColorPicker 颜色选择 —— 基础 / 带默认值。
 */
public class ColorPickerExamplePage extends VBoxAnt {

    public ColorPickerExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ColorPicker 颜色选择")
                .description("提供颜色选取的输入控件，封装 JavaFX 原生 ColorPicker。")
                .sections(basicSection(), defaultValueSection(), valueSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = ColorPickerAnt.create()
                .onChange(color -> {})
                .build();
        String code = """
                ColorPickerAnt.create()
                        .onChange(color -> System.out.println(color))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "默认白色，点击展开调色板选择颜色，onChange 回调拿到新值。",
                code, demo);
    }

    private Node defaultValueSection() {
        Node demo = Demos.row(
                ColorPickerAnt.create().value(Color.web("#1677ff")).build(),
                ColorPickerAnt.create().value(Color.web("#52c41a")).build()
        );
        String code = """
                ColorPickerAnt.create().value(Color.web("#1677ff")).build();
                ColorPickerAnt.create().value(Color.web("#52c41a")).build();
                """;
        return Demos.sectionWithCode("2. 默认值",
                "value(Color) 指定初始颜色。",
                code, demo);
    }

    /**
     * 3. 获取选中颜色 —— onChange 回调拿到 JavaFX Color，转成 hex 字符串显示。
     *
     * <p>onChange 给的是 Color 对象，业务里常需要 hex 串（如 #1677FF），
     * 这里用一个小工具把 Color 转成 hex 并实时显示在结果 Label 上。</p>
     */
    private Node valueSection() {
        Label result = new Label("当前颜色：#FFFFFF");
        Node picker = ColorPickerAnt.create()
                .value(Color.web("#1677ff"))
                .onChange(color -> result.setText("当前颜色：" + toHex(color)))
                .build();
        Node demo = Demos.column(picker, result);
        String code = """
                Label result = new Label("当前颜色：#FFFFFF");
                ColorPickerAnt.create()
                        .value(Color.web("#1677ff"))
                        .onChange(color -> result.setText("当前颜色：" + toHex(color)))
                        .build();

                // Color -> #RRGGBB
                static String toHex(Color c) {
                    return String.format("#%02X%02X%02X",
                            (int) Math.round(c.getRed() * 255),
                            (int) Math.round(c.getGreen() * 255),
                            (int) Math.round(c.getBlue() * 255));
                }
                """;
        return Demos.sectionWithCode("3. 获取选中颜色",
                "onChange(color -> ...) 给出 JavaFX Color，转成 hex 字符串后显示在结果 Label。",
                code, demo);
    }

    /** Color 转 #RRGGBB hex 字符串。 */
    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }
}
