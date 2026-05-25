package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ProgressAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Progress 进度条 / 进度环展示页（M19.9）。
 */
public class ProgressPage implements ShowcasePage {

    @Override public String   key()      { return "progress"; }
    @Override public String   title()    { return "Progress 进度"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Progress 进度");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("线性进度条 + 圆形进度环；4 种状态色（normal/success/error/warning）。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBarBasic(),
                        sectionBarSizes(),
                        sectionBarStatus(),
                        sectionCircleBasic(),
                        sectionCircleSizes(),
                        sectionCircleStatus()
                )
                .build();
    }

    // Bar
    private Node sectionBarBasic() {
        Node b1 = ProgressAnt.bar().progress(0.3).build();
        Node b2 = ProgressAnt.bar().progress(0.6).build();
        Node b3 = ProgressAnt.bar().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();

        VBox col = VBoxBuilder.create().spacing(12).children(b1, b2, b3).build();

        return ShowcaseSection.create()
                .title("场景 1：进度条基础（30% / 60% / 100%）")
                .description(".progress(0.0–1.0) 控制进度；100% 时建议状态切到 SUCCESS")
                .demo(col)
                .code("""
                        ProgressAnt.bar().progress(0.3).build();
                        ProgressAnt.bar().progress(0.6).build();
                        ProgressAnt.bar().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
                        """)
                .build();
    }

    private Node sectionBarSizes() {
        Node small = ProgressAnt.bar().progress(0.5).size(ProgressAnt.Size.SMALL).build();
        Node def = ProgressAnt.bar().progress(0.5).build();
        Node large = ProgressAnt.bar().progress(0.5).size(ProgressAnt.Size.LARGE).build();

        VBox col = VBoxBuilder.create().spacing(12).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 2：进度条三档尺寸")
                .description("SMALL / DEFAULT / LARGE —— 改高度，整体长度不变")
                .demo(col)
                .code("""
                        ProgressAnt.bar().progress(0.5).size(ProgressAnt.Size.SMALL).build();
                        ProgressAnt.bar().progress(0.5).build();
                        ProgressAnt.bar().progress(0.5).size(ProgressAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionBarStatus() {
        Node normal = ProgressAnt.bar().progress(0.5).status(ProgressAnt.Status.NORMAL).build();
        Node success = ProgressAnt.bar().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
        Node warning = ProgressAnt.bar().progress(0.4).status(ProgressAnt.Status.WARNING).build();
        Node error = ProgressAnt.bar().progress(0.7).status(ProgressAnt.Status.ERROR).build();

        VBox col = VBoxBuilder.create().spacing(12).children(normal, success, warning, error).build();

        return ShowcaseSection.create()
                .title("场景 3：进度条 4 种状态色")
                .description("NORMAL（蓝） / SUCCESS（绿，100% 用） / WARNING（橙） / ERROR（红，下载/上传失败用）")
                .demo(col)
                .code("""
                        ProgressAnt.bar().progress(0.5).status(ProgressAnt.Status.NORMAL).build();
                        ProgressAnt.bar().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
                        ProgressAnt.bar().progress(0.4).status(ProgressAnt.Status.WARNING).build();
                        ProgressAnt.bar().progress(0.7).status(ProgressAnt.Status.ERROR).build();
                        """)
                .build();
    }

    // Circle
    private Node sectionCircleBasic() {
        Node c1 = ProgressAnt.circle().progress(0.25).build();
        Node c2 = ProgressAnt.circle().progress(0.6).build();
        Node c3 = ProgressAnt.circle().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();

        HBox row = HBoxBuilder.create().spacing(20).children(c1, c2, c3).build();

        return ShowcaseSection.create()
                .title("场景 4：进度环基础（25% / 60% / 100%）")
                .description("ProgressAnt.circle() 圆形进度环 —— dashboard 数据可视化首选")
                .demo(row)
                .code("""
                        ProgressAnt.circle().progress(0.25).build();
                        ProgressAnt.circle().progress(0.6).build();
                        ProgressAnt.circle().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
                        """)
                .build();
    }

    private Node sectionCircleSizes() {
        Node small = ProgressAnt.circle().progress(0.5).size(40).build();
        Node mid = ProgressAnt.circle().progress(0.5).size(80).build();
        Node large = ProgressAnt.circle().progress(0.5).size(120).build();

        HBox row = HBoxBuilder.create().spacing(20).children(small, mid, large).build();

        return ShowcaseSection.create()
                .title("场景 5：进度环自定义尺寸")
                .description(".size(double) 直接像素 —— 40/80/120 等任意值")
                .demo(row)
                .code("""
                        ProgressAnt.circle().progress(0.5).size(40).build();
                        ProgressAnt.circle().progress(0.5).size(80).build();
                        ProgressAnt.circle().progress(0.5).size(120).build();
                        """)
                .build();
    }

    private Node sectionCircleStatus() {
        Node normal = ProgressAnt.circle().progress(0.5).status(ProgressAnt.Status.NORMAL).build();
        Node success = ProgressAnt.circle().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
        Node warning = ProgressAnt.circle().progress(0.4).status(ProgressAnt.Status.WARNING).build();
        Node error = ProgressAnt.circle().progress(0.7).status(ProgressAnt.Status.ERROR).build();

        HBox row = HBoxBuilder.create().spacing(20).children(normal, success, warning, error).build();

        return ShowcaseSection.create()
                .title("场景 6：进度环 4 种状态色")
                .description("与进度条对齐的 4 状态色 —— 用于 dashboard 关键指标可视化")
                .demo(row)
                .code("""
                        ProgressAnt.circle().progress(0.5).status(ProgressAnt.Status.NORMAL).build();
                        ProgressAnt.circle().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
                        ProgressAnt.circle().progress(0.4).status(ProgressAnt.Status.WARNING).build();
                        ProgressAnt.circle().progress(0.7).status(ProgressAnt.Status.ERROR).build();
                        """)
                .build();
    }
}
