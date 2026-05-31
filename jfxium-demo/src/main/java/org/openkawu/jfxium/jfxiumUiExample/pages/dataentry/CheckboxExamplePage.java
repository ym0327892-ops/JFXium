package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.openkawu.jfxium.component.CheckBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Checkbox 复选框 —— 基础 / 禁用 / 形状。
 */
public class CheckboxExamplePage extends VBoxAnt {

    public CheckboxExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Checkbox 复选框")
                .description("多选场景下的勾选控件，支持选中、半选、禁用等状态。")
                .sections(basicSection(), disabledSection(), shapeSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        CheckBox cb1 = CheckBoxAnt.create("苹果").build();
        CheckBox cb2 = CheckBoxAnt.create("香蕉").selected(true).build();
        CheckBox cb3 = CheckBoxAnt.create("橙子").build();
        Node demo = Demos.row(cb1, cb2, cb3);
        String code = """
                CheckBoxAnt.create("苹果").build();
                CheckBoxAnt.create("香蕉").selected(true).build();
                CheckBoxAnt.create("橙子").build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "最简单的多选框。", code, demo);
    }

    private Node disabledSection() {
        CheckBox cb1 = CheckBoxAnt.create("禁用未选").disabled(true).build();
        CheckBox cb2 = CheckBoxAnt.create("禁用已选").selected(true).disabled(true).build();
        CheckBox cb3 = CheckBoxAnt.create("半选状态").indeterminate(true).disabled(true).build();
        Node demo = Demos.row(cb1, cb2, cb3);
        String code = """
                CheckBoxAnt.create("禁用未选").disabled(true).build();
                CheckBoxAnt.create("禁用已选").selected(true).disabled(true).build();
                CheckBoxAnt.create("半选状态").indeterminate(true).disabled(true).build();
                """;
        return Demos.sectionWithCode("2. 禁用状态", "disabled(true) 禁用交互；indeterminate 半选态。", code, demo);
    }

    private Node shapeSection() {
        CheckBox cb1 = CheckBoxAnt.create("默认").selected(true).build();
        CheckBox cb2 = CheckBoxAnt.create("圆形").selected(true).shape(CheckBoxAnt.Shape.CIRCLE).build();
        CheckBox cb3 = CheckBoxAnt.create("方形").selected(true).shape(CheckBoxAnt.Shape.SQUARE).build();
        CheckBox cb4 = CheckBoxAnt.create("大圆角").selected(true).shape(CheckBoxAnt.Shape.ROUNDED).build();
        Node demo = Demos.row(cb1, cb2, cb3, cb4);
        String code = """
                CheckBoxAnt.create("默认").selected(true).build();
                CheckBoxAnt.create("圆形").selected(true).shape(CheckBoxAnt.Shape.CIRCLE).build();
                CheckBoxAnt.create("方形").selected(true).shape(CheckBoxAnt.Shape.SQUARE).build();
                CheckBoxAnt.create("大圆角").selected(true).shape(CheckBoxAnt.Shape.ROUNDED).build();
                """;
        return Demos.sectionWithCode("3. 形状 Shape",
                "DEFAULT / CIRCLE / SQUARE / ROUNDED 四种外形。", code, demo);
    }
}
