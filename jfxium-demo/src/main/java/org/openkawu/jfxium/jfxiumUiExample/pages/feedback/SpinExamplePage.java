package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SpinAnt;

/**
 * Spin 加载 —— 基础 / 尺寸 / 提示文字。
 */
public class SpinExamplePage extends VBoxAnt {

    public SpinExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Spin 加载中")
                .description("用于页面或区域的加载状态指示，支持多种动画形态。")
                .sections(basicSection(), sizeSection(), tipSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        VBox spinner = SpinAnt.create().build();
        VBox dots = SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).build();
        VBox bars = SpinAnt.create().indicator(SpinAnt.Indicator.BARS).build();
        Node demo = Demos.row(spinner, dots, bars);
        String code = """
                SpinAnt.create().build();  // SPINNER（默认）
                SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).build();
                SpinAnt.create().indicator(SpinAnt.Indicator.BARS).build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "三种动画形态：SPINNER / DOTS / BARS。", code, demo);
    }

    private Node sizeSection() {
        VBox small = SpinAnt.create().size(SpinAnt.Size.SMALL).build();
        VBox normal = SpinAnt.create().build();
        VBox large = SpinAnt.create().size(SpinAnt.Size.LARGE).build();
        Node demo = Demos.row(small, normal, large);
        String code = """
                SpinAnt.create().size(SpinAnt.Size.SMALL).build();
                SpinAnt.create().build();  // DEFAULT
                SpinAnt.create().size(SpinAnt.Size.LARGE).build();
                """;
        return Demos.sectionWithCode("2. 尺寸",
                "SMALL / DEFAULT / LARGE 三档大小。", code, demo);
    }

    private Node tipSection() {
        VBox spin = SpinAnt.create().tip("加载中...").build();
        VBox spinDots = SpinAnt.create()
                .indicator(SpinAnt.Indicator.DOTS)
                .tip("请稍候")
                .size(SpinAnt.Size.LARGE)
                .build();
        Node demo = Demos.row(spin, spinDots);
        String code = """
                SpinAnt.create().tip("加载中...").build();
                SpinAnt.create()
                        .indicator(SpinAnt.Indicator.DOTS)
                        .tip("请稍候")
                        .size(SpinAnt.Size.LARGE)
                        .build();
                """;
        return Demos.sectionWithCode("3. 提示文字",
                "tip() 在动画下方显示文字说明。", code, demo);
    }
}
