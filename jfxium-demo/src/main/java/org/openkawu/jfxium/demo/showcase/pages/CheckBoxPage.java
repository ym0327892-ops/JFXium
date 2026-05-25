package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CheckBoxAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * CheckBox 展示页（M19.6.2）。
 */
public class CheckBoxPage implements ShowcasePage {

    @Override public String   key()      { return "checkbox"; }
    @Override public String   title()    { return "CheckBox 复选框"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("CheckBox 复选框");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("支持三态：勾选 / 未勾选 / 不确定（半选）。M19.6 加 Size + allowIndeterminate。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionIndeterminate(),
                        sectionAllowIndeterminate(),
                        sectionSizes(),
                        sectionShapes(),
                        sectionDisabled()
                )
                .build();
    }

    private Node sectionShapes() {
        Node defShape = CheckBoxAnt.create("DEFAULT 方角").selected(true).build();
        Node circle  = CheckBoxAnt.create("CIRCLE 圆形").shape(CheckBoxAnt.Shape.CIRCLE).selected(true).build();
        Node square  = CheckBoxAnt.create("SQUARE 直角").shape(CheckBoxAnt.Shape.SQUARE).selected(true).build();
        Node rounded = CheckBoxAnt.create("ROUNDED 大圆角").shape(CheckBoxAnt.Shape.ROUNDED).selected(true).build();
        HBox row = HBoxBuilder.create().spacing(16)
                .children(defShape, circle, square, rounded).build();

        return ShowcaseSection.create()
                .title("场景 6：形状变体（M19.20 新增 Shape API）")
                .description(".shape(Shape.DEFAULT/CIRCLE/SQUARE/ROUNDED) —— 圆形外观但仍是 CheckBox 的多选语义")
                .demo(row)
                .code("""
                        CheckBoxAnt.create("圆形").shape(CheckBoxAnt.Shape.CIRCLE).build();
                        CheckBoxAnt.create("直角").shape(CheckBoxAnt.Shape.SQUARE).build();
                        CheckBoxAnt.create("大圆角").shape(CheckBoxAnt.Shape.ROUNDED).build();
                        """)
                .build();
    }

    private Node sectionBasic() {
        Node c1 = CheckBoxAnt.create("记住我").selected(true).build();
        Node c2 = CheckBoxAnt.create("订阅邮件").build();
        Node c3 = CheckBoxAnt.create("接受协议")
                .onChange(checked -> System.out.println("接受协议：" + checked))
                .build();
        HBox row = HBoxBuilder.create().spacing(16).children(c1, c2, c3).build();

        return ShowcaseSection.create()
                .title("场景 1：基础用法")
                .description("勾选 / 未勾选 + onChange 回调")
                .demo(row)
                .code("""
                        CheckBox c1 = CheckBoxAnt.create("记住我").selected(true).build();
                        CheckBox c2 = CheckBoxAnt.create("接受协议")
                            .onChange(checked -> handle(checked))
                            .build();
                        """)
                .build();
    }

    private Node sectionIndeterminate() {
        Node c1 = CheckBoxAnt.create("半选状态").indeterminate(true).build();
        Node c2 = CheckBoxAnt.create("已勾选").selected(true).build();
        Node c3 = CheckBoxAnt.create("未勾选").build();
        HBox row = HBoxBuilder.create().spacing(16).children(c1, c2, c3).build();

        return ShowcaseSection.create()
                .title("场景 2：三态视觉对比")
                .description("indeterminate 用于表示「子项部分被选中」（如「全选」处于半选状态）")
                .demo(row)
                .code("""
                        CheckBox c = CheckBoxAnt.create("半选状态")
                            .indeterminate(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionAllowIndeterminate() {
        Node clickable = CheckBoxAnt.create("点我循环三态：✓ → 半选 → ✗")
                .allowIndeterminate(true)
                .build();

        return ShowcaseSection.create()
                .title("场景 3：allowIndeterminate（M19.6 新增）")
                .description("启用后用户每次点击在 selected ↔ indeterminate ↔ unselected 间循环——对齐 Ant Design 三态语义")
                .demo(clickable)
                .code("""
                        CheckBox tri = CheckBoxAnt.create("点我循环三态")
                            .allowIndeterminate(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        Node small = CheckBoxAnt.create("Small").size(CheckBoxAnt.Size.SMALL).build();
        Node def = CheckBoxAnt.create("Default").selected(true).build();
        Node large = CheckBoxAnt.create("Large").size(CheckBoxAnt.Size.LARGE).build();
        HBox row = HBoxBuilder.create().spacing(16).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 4：三档尺寸（M19.6 新增 Size API）")
                .description("与 Button/Input 完全一致的 SMALL / DEFAULT / LARGE 三档")
                .demo(row)
                .code("""
                        CheckBoxAnt.create("Small").size(CheckBoxAnt.Size.SMALL).build();
                        CheckBoxAnt.create("Default").build();
                        CheckBoxAnt.create("Large").size(CheckBoxAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionDisabled() {
        Node enabled = CheckBoxAnt.create("可用").selected(true).build();
        Node disabledSel = CheckBoxAnt.create("禁用已选").disabled(true).selected(true).build();
        Node disabledIndeterminate = CheckBoxAnt.create("禁用半选").disabled(true).indeterminate(true).build();
        Node disabledUnsel = CheckBoxAnt.create("禁用未选").disabled(true).build();
        HBox row = HBoxBuilder.create().spacing(16)
                .children(enabled, disabledSel, disabledIndeterminate, disabledUnsel).build();

        return ShowcaseSection.create()
                .title("场景 5：禁用态全组合")
                .description("disabled 在三种态（已选/半选/未选）下都视觉变浅")
                .demo(row)
                .code("""
                        CheckBoxAnt.create("禁用已选").disabled(true).selected(true).build();
                        CheckBoxAnt.create("禁用半选").disabled(true).indeterminate(true).build();
                        CheckBoxAnt.create("禁用未选").disabled(true).build();
                        """)
                .build();
    }
}
