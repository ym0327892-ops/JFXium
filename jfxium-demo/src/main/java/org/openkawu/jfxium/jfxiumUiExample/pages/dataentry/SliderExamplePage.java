package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SliderAnt;

/**
 * Slider 滑块 —— 基础 / 范围 / 禁用。
 */
public class SliderExamplePage extends VBoxAnt {

    public SliderExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Slider 滑块")
                .description("通过拖动滑块在一个区间内选择值，支持单滑块和双滑块（范围）模式。")
                .sections(basicSection(), rangeSection(), disabledSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node slider = SliderAnt.create()
                .min(0).max(100).value(30)
                .build();
        String code = """
                SliderAnt.create()
                        .min(0).max(100).value(30)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "默认单滑块，min/max/value 设定范围与初始值。", code, slider);
    }

    private Node rangeSection() {
        Node slider = SliderAnt.create()
                .range()
                .min(0).max(100)
                .defaultValue(new double[]{20, 80})
                .build();
        String code = """
                SliderAnt.create()
                        .range()
                        .min(0).max(100)
                        .defaultValue(new double[]{20, 80})
                        .build();
                """;
        return Demos.sectionWithCode("2. 范围模式", "range() 启用双滑块，defaultValue 设定初始区间。", code, slider);
    }

    private Node disabledSection() {
        Node slider = SliderAnt.create()
                .min(0).max(100).value(50)
                .disabled(true)
                .build();
        String code = """
                SliderAnt.create()
                        .min(0).max(100).value(50)
                        .disabled(true)
                        .build();
                """;
        return Demos.sectionWithCode("3. 禁用状态", "disabled(true) 禁止拖动。", code, slider);
    }
}
