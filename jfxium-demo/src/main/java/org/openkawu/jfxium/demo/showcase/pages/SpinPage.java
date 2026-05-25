package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.SpinAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Spin 加载中展示页（M19.12）。
 */
public class SpinPage implements ShowcasePage {

    @Override public String   key()      { return "spin"; }
    @Override public String   title()    { return "Spin 加载中"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Spin 加载中");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("加载指示器 —— 异步操作中的标准反馈。3 种样式 + 3 档尺寸 + 提示文字。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionSizes(),
                        sectionIndicators(),
                        sectionWithTip()
                )
                .build();
    }

    private Node sectionSizes() {
        Node small = SpinAnt.create().size(SpinAnt.Size.SMALL).build();
        Node def = SpinAnt.create().build();
        Node large = SpinAnt.create().size(SpinAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(40).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 1：三档尺寸")
                .description("SMALL / DEFAULT / LARGE")
                .demo(row)
                .code("""
                        SpinAnt.create().size(SpinAnt.Size.SMALL).build();
                        SpinAnt.create().build();
                        SpinAnt.create().size(SpinAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionIndicators() {
        Node spinner = SpinAnt.create().indicator(SpinAnt.Indicator.SPINNER).build();
        Node dots = SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).build();
        Node bars = SpinAnt.create().indicator(SpinAnt.Indicator.BARS).build();

        HBox row = HBoxBuilder.create().spacing(40).children(spinner, dots, bars).build();

        return ShowcaseSection.create()
                .title("场景 2：3 种指示器样式")
                .description("SPINNER（默认旋转圈）/ DOTS（三点跳动）/ BARS（条形跳动）")
                .demo(row)
                .code("""
                        SpinAnt.create().indicator(SpinAnt.Indicator.SPINNER).build();
                        SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).build();
                        SpinAnt.create().indicator(SpinAnt.Indicator.BARS).build();
                        """)
                .build();
    }

    private Node sectionWithTip() {
        Node a = SpinAnt.create().tip("加载中...").build();
        Node b = SpinAnt.create().tip("数据上传中").indicator(SpinAnt.Indicator.DOTS).build();
        Node c = SpinAnt.create().tip("正在处理").indicator(SpinAnt.Indicator.BARS).size(SpinAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(40).children(a, b, c).build();

        return ShowcaseSection.create()
                .title("场景 3：带提示文字（tip）")
                .description(".tip(text) —— 在指示器下方显示文字 —— 长任务必备")
                .demo(row)
                .code("""
                        SpinAnt.create().tip("加载中...").build();
                        SpinAnt.create().tip("数据上传中").indicator(SpinAnt.Indicator.DOTS).build();
                        """)
                .build();
    }
}
