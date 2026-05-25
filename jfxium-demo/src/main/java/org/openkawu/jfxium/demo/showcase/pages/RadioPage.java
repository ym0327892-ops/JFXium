package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.RadioButtonAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * RadioButton 展示页（M19.6.2）。
 */
public class RadioPage implements ShowcasePage {

    @Override public String   key()      { return "radio"; }
    @Override public String   title()    { return "Radio 单选框"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Radio 单选框");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("配合 ToggleGroup 使用，同组内只能选中一个。M19.6 加 Size API。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionGroup(),
                        sectionSizes(),
                        sectionShapes(),
                        sectionDisabled()
                )
                .build();
    }

    private Node sectionShapes() {
        ToggleGroup g = new ToggleGroup();
        Node defShape = RadioButtonAnt.create("DEFAULT 圆形").toggleGroup(g).selected(true).build();
        Node square   = RadioButtonAnt.create("SQUARE 方形").shape(RadioButtonAnt.Shape.SQUARE).toggleGroup(g).build();
        Node rounded  = RadioButtonAnt.create("ROUNDED 圆角").shape(RadioButtonAnt.Shape.ROUNDED).toggleGroup(g).build();
        HBox row = HBoxBuilder.create().spacing(16).children(defShape, square, rounded).build();

        return ShowcaseSection.create()
                .title("场景 5：形状变体（M19.20 新增 Shape API）")
                .description(".shape(Shape.DEFAULT/SQUARE/ROUNDED) —— 方形外观仍是单选语义")
                .demo(row)
                .code("""
                        RadioButtonAnt.create("方形").shape(RadioButtonAnt.Shape.SQUARE).build();
                        RadioButtonAnt.create("圆角").shape(RadioButtonAnt.Shape.ROUNDED).build();
                        """)
                .build();
    }

    private Node sectionBasic() {
        Node r1 = RadioButtonAnt.create("选项 A").selected(true).build();
        Node r2 = RadioButtonAnt.create("选项 B").build();
        HBox row = HBoxBuilder.create().spacing(16).children(r1, r2).build();

        return ShowcaseSection.create()
                .title("场景 1：基础用法（不加 ToggleGroup 时不互斥）")
                .description("不挂 ToggleGroup 时每个 Radio 独立——通常你需要 ToggleGroup")
                .demo(row)
                .code("""
                        RadioButton r1 = RadioButtonAnt.create("选项 A").selected(true).build();
                        RadioButton r2 = RadioButtonAnt.create("选项 B").build();
                        """)
                .build();
    }

    private Node sectionGroup() {
        ToggleGroup group = new ToggleGroup();
        Node r1 = RadioButtonAnt.create("男").toggleGroup(group).selected(true).build();
        Node r2 = RadioButtonAnt.create("女").toggleGroup(group).build();
        Node r3 = RadioButtonAnt.create("其他").toggleGroup(group).build();
        HBox row = HBoxBuilder.create().spacing(16).children(r1, r2, r3).build();

        return ShowcaseSection.create()
                .title("场景 2：互斥组（ToggleGroup）")
                .description("加同一 ToggleGroup 后只能选中一个——表单「性别」等单选场景的标配")
                .demo(row)
                .code("""
                        ToggleGroup group = new ToggleGroup();
                        RadioButton r1 = RadioButtonAnt.create("男").toggleGroup(group).selected(true).build();
                        RadioButton r2 = RadioButtonAnt.create("女").toggleGroup(group).build();
                        RadioButton r3 = RadioButtonAnt.create("其他").toggleGroup(group).build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        ToggleGroup g = new ToggleGroup();
        Node small = RadioButtonAnt.create("Small").size(RadioButtonAnt.Size.SMALL).toggleGroup(g).build();
        Node def = RadioButtonAnt.create("Default").toggleGroup(g).selected(true).build();
        Node large = RadioButtonAnt.create("Large").size(RadioButtonAnt.Size.LARGE).toggleGroup(g).build();
        HBox row = HBoxBuilder.create().spacing(16).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 3：三档尺寸（M19.6 新增 Size API）")
                .description("与 Button/Input 完全一致的 SMALL / DEFAULT / LARGE 三档")
                .demo(row)
                .code("""
                        RadioButtonAnt.create("Small").size(RadioButtonAnt.Size.SMALL).build();
                        RadioButtonAnt.create("Default").build();
                        RadioButtonAnt.create("Large").size(RadioButtonAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionDisabled() {
        ToggleGroup g = new ToggleGroup();
        Node enabled = RadioButtonAnt.create("可用").toggleGroup(g).selected(true).build();
        Node disabled = RadioButtonAnt.create("禁用未选").disabled(true).toggleGroup(g).build();
        Node disabledSel = RadioButtonAnt.create("禁用已选").disabled(true).selected(true).build();
        HBox row = HBoxBuilder.create().spacing(16).children(enabled, disabled, disabledSel).build();

        return ShowcaseSection.create()
                .title("场景 4：禁用态")
                .description("disabled 状态下 hover/click 失效，视觉变浅")
                .demo(row)
                .code("""
                        RadioButtonAnt.create("禁用未选").disabled(true).build();
                        RadioButtonAnt.create("禁用已选").disabled(true).selected(true).build();
                        """)
                .build();
    }
}
