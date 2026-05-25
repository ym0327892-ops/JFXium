package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.InputNumberAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * InputNumber 数字输入框展示页（M19.11）。
 */
public class InputNumberPage implements ShowcasePage {

    @Override public String   key()      { return "input-number"; }
    @Override public String   title()    { return "InputNumber 数字输入"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("InputNumber 数字输入");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("带 +/- 步进按钮的数字输入；min/max 边界 + step 步长 + precision 小数位 + prefix/suffix。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionRange(),
                        sectionPrecision(),
                        sectionAffix(),
                        sectionSizes()
                )
                .build();
    }

    private Node sectionBasic() {
        Node n = InputNumberAnt.create().value(10).build();
        return ShowcaseSection.create()
                .title("场景 1：基础（默认 step=1）")
                .description("点 + / - 步进；可手动输入；回车确认")
                .demo(n)
                .code("""
                        InputNumberAnt.create().value(10).build();
                        """)
                .build();
    }

    private Node sectionRange() {
        Node n = InputNumberAnt.create().value(50).min(0).max(100).step(5).build();

        return ShowcaseSection.create()
                .title("场景 2：边界 + 自定义步长")
                .description(".min(0).max(100).step(5) —— 越界自动夹紧到边界")
                .demo(n)
                .code("""
                        InputNumberAnt.create().value(50).min(0).max(100).step(5).build();
                        """)
                .build();
    }

    private Node sectionPrecision() {
        Node n = InputNumberAnt.create().value(3.14).step(0.01).precision(2).build();

        return ShowcaseSection.create()
                .title("场景 3：小数精度（precision）")
                .description(".precision(2) —— 限制小数位数；适合金额/百分比/温度")
                .demo(n)
                .code("""
                        InputNumberAnt.create().value(3.14).step(0.01).precision(2).build();
                        """)
                .build();
    }

    private Node sectionAffix() {
        Node price = InputNumberAnt.create().value(99).prefix("¥").min(0).step(10).build();
        Node percent = InputNumberAnt.create().value(80).suffix("%").min(0).max(100).step(5).build();

        HBox row = HBoxBuilder.create().spacing(12).children(price, percent).build();

        return ShowcaseSection.create()
                .title("场景 4：前缀 / 后缀（货币、百分比单位）")
                .description(".prefix(\"¥\") / .suffix(\"%\") —— 文字单位嵌在输入框里")
                .demo(row)
                .code("""
                        InputNumberAnt.create().value(99).prefix("¥").build();
                        InputNumberAnt.create().value(80).suffix("%").build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        Node small = InputNumberAnt.create().value(10).size(InputNumberAnt.Size.SMALL).build();
        Node def = InputNumberAnt.create().value(10).build();
        Node large = InputNumberAnt.create().value(10).size(InputNumberAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(8).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 5：三档尺寸")
                .description("SMALL / DEFAULT / LARGE —— 与 Input/Combo 高度对齐")
                .demo(row)
                .code("""
                        InputNumberAnt.create().value(10).size(InputNumberAnt.Size.SMALL).build();
                        InputNumberAnt.create().value(10).build();
                        InputNumberAnt.create().value(10).size(InputNumberAnt.Size.LARGE).build();
                        """)
                .build();
    }
}
