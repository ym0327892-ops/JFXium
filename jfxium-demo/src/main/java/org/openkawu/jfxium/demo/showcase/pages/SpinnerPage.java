package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.SpinnerAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Spinner 圆形进度指示器展示页（M19.14）。
 *
 * <p><b>注意</b>：与 SpinAnt 区别 —— SpinAnt 是「带文字提示的整组加载块」，
 * SpinnerAnt 是 JavaFX 原生 ProgressIndicator 的极简包装（圆形旋转），更轻量。</p>
 */
public class SpinnerPage implements ShowcasePage {

    @Override public String   key()      { return "spinner"; }
    @Override public String   title()    { return "Spinner 旋转指示器"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Spinner 旋转指示器");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("最简的圆形 loading —— 比 SpinAnt 轻量，仅一个旋转圈，按需自定义大小。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionSizes(),
                        sectionVsSpin()
                )
                .build();
    }

    private Node sectionBasic() {
        ProgressIndicator s = SpinnerAnt.create().build();
        return ShowcaseSection.create()
                .title("场景 1：默认（24×24）")
                .description("最小开销 loading；行内/小区域用")
                .demo(s)
                .code("""
                        ProgressIndicator s = SpinnerAnt.create().build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        ProgressIndicator s16 = SpinnerAnt.create().size(16).build();
        ProgressIndicator s24 = SpinnerAnt.create().size(24).build();
        ProgressIndicator s40 = SpinnerAnt.create().size(40).build();
        ProgressIndicator s60 = SpinnerAnt.create().size(60).build();

        HBox row = HBoxBuilder.create().spacing(20).children(s16, s24, s40, s60).build();

        return ShowcaseSection.create()
                .title("场景 2：自定义尺寸（16/24/40/60）")
                .description(".size(double) —— 任意像素值")
                .demo(row)
                .code("""
                        SpinnerAnt.create().size(16).build();
                        SpinnerAnt.create().size(24).build();
                        SpinnerAnt.create().size(40).build();
                        SpinnerAnt.create().size(60).build();
                        """)
                .build();
    }

    private Node sectionVsSpin() {
        Label compare = new Label("SpinnerAnt vs SpinAnt 对比：\n"
                + "• SpinnerAnt —— 极简 ProgressIndicator，仅旋转圈，行内 loading 用\n"
                + "• SpinAnt —— 带 tip 文字、3 种指示器（SPINNER/DOTS/BARS）、3 档 size，整组反馈用");
        compare.setStyle("-fx-text-fill: -color-fg-muted; -fx-padding: 16; "
                + "-fx-background-color: -color-bg-subtle; -fx-background-radius: 4;");

        return ShowcaseSection.create()
                .title("场景 3：与 SpinAnt 的区别")
                .description("两个组件并存原因：粒度不同；按场景挑选")
                .demo(compare)
                .code("""
                        // 行内 loading（小尺寸，无文字）
                        ProgressIndicator s = SpinnerAnt.create().size(20).build();

                        // 整组加载反馈（带 tip 文字 + 多种指示器样式）
                        VBox spin = SpinAnt.create().tip("加载中...").build();
                        """)
                .build();
    }
}
