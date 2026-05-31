package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.openkawu.jfxium.component.RadioButtonAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Radio 单选框 —— 基础 / 形状 / 禁用 / 分组。
 */
public class RadioExamplePage extends VBoxAnt {

    public RadioExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Radio 单选框")
                .description("在一组选项中选择一个，使用 ToggleGroup 实现互斥。")
                .sections(basicSection(), shapeSection(), disabledSection(), groupSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        ToggleGroup group = new ToggleGroup();
        RadioButton r1 = RadioButtonAnt.create("选项 A").toggleGroup(group).selected(true).build();
        RadioButton r2 = RadioButtonAnt.create("选项 B").toggleGroup(group).build();
        Node demo = Demos.row(r1, r2);
        String code = """
                ToggleGroup group = new ToggleGroup();
                RadioButton r1 = RadioButtonAnt.create("选项 A")
                        .toggleGroup(group).selected(true).build();
                RadioButton r2 = RadioButtonAnt.create("选项 B")
                        .toggleGroup(group).build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "通过 ToggleGroup 实现单选互斥。", code, demo);
    }

    private Node shapeSection() {
        ToggleGroup g1 = new ToggleGroup();
        RadioButton circle = RadioButtonAnt.create("圆形（默认）")
                .toggleGroup(g1).selected(true).build();
        ToggleGroup g2 = new ToggleGroup();
        RadioButton square = RadioButtonAnt.create("方形 SQUARE")
                .toggleGroup(g2).shape(RadioButtonAnt.Shape.SQUARE).selected(true).build();
        ToggleGroup g3 = new ToggleGroup();
        RadioButton rounded = RadioButtonAnt.create("圆角 ROUNDED")
                .toggleGroup(g3).shape(RadioButtonAnt.Shape.ROUNDED).selected(true).build();
        Node demo = Demos.row(circle, square, rounded);
        String code = """
                // shape 控制选择框外形
                RadioButtonAnt.create("圆形（默认）").toggleGroup(g1).build();
                RadioButtonAnt.create("方形 SQUARE")
                        .shape(RadioButtonAnt.Shape.SQUARE).build();
                RadioButtonAnt.create("圆角 ROUNDED")
                        .shape(RadioButtonAnt.Shape.ROUNDED).build();
                """;
        return Demos.sectionWithCode("2. 形状 Shape",
                "DEFAULT（圆形）/ SQUARE（方形）/ ROUNDED（圆角方形）。",
                code, demo);
    }

    private Node disabledSection() {
        RadioButton r1 = RadioButtonAnt.create("禁用未选").disabled(true).build();
        RadioButton r2 = RadioButtonAnt.create("禁用已选").selected(true).disabled(true).build();
        Node demo = Demos.row(r1, r2);
        String code = """
                RadioButtonAnt.create("禁用未选").disabled(true).build();
                RadioButtonAnt.create("禁用已选").selected(true).disabled(true).build();
                """;
        return Demos.sectionWithCode("3. 禁用状态", "disabled(true) 禁用交互。", code, demo);
    }

    private Node groupSection() {
        ToggleGroup sizeGroup = new ToggleGroup();
        RadioButton s = RadioButtonAnt.create("Small").toggleGroup(sizeGroup).build();
        RadioButton m = RadioButtonAnt.create("Medium").toggleGroup(sizeGroup).selected(true).build();
        RadioButton l = RadioButtonAnt.create("Large").toggleGroup(sizeGroup).build();
        Node demo = Demos.row(s, m, l);
        String code = """
                ToggleGroup sizeGroup = new ToggleGroup();
                RadioButton s = RadioButtonAnt.create("Small").toggleGroup(sizeGroup).build();
                RadioButton m = RadioButtonAnt.create("Medium")
                        .toggleGroup(sizeGroup).selected(true).build();
                RadioButton l = RadioButtonAnt.create("Large").toggleGroup(sizeGroup).build();
                """;
        return Demos.sectionWithCode("4. 单选组", "多个 RadioButton 共享同一个 ToggleGroup。", code, demo);
    }
}
