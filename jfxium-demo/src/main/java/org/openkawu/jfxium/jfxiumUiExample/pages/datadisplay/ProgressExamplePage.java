package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.ProgressAnt;

/**
 * Progress 进度条 —— 基础百分比 / 状态色 / 圆形。
 */
public class ProgressExamplePage extends VBoxAnt {

    public ProgressExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Progress 进度条")
                .description("展示操作的当前进度，支持条形和圆形两种形态。")
                .sections(basicSection(), statusSection(), circleSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        HBox p30 = ProgressAnt.bar().progress(0.3).build();
        HBox p70 = ProgressAnt.bar().progress(0.7).build();
        HBox p100 = ProgressAnt.bar().progress(1.0).build();
        Node demo = Demos.column(p30, p70, p100);
        String code = """
                ProgressAnt.bar().progress(0.3).build();
                ProgressAnt.bar().progress(0.7).build();
                ProgressAnt.bar().progress(1.0).build();
                """;
        return Demos.sectionWithCode("1. 基础百分比", "progress(0~1) 设置进度值。", code, demo);
    }

    private Node statusSection() {
        HBox success = ProgressAnt.bar().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
        HBox warning = ProgressAnt.bar().progress(0.6).status(ProgressAnt.Status.WARNING).build();
        HBox error = ProgressAnt.bar().progress(0.4).status(ProgressAnt.Status.ERROR).build();
        Node demo = Demos.column(success, warning, error);
        String code = """
                ProgressAnt.bar().progress(1.0).status(ProgressAnt.Status.SUCCESS).build();
                ProgressAnt.bar().progress(0.6).status(ProgressAnt.Status.WARNING).build();
                ProgressAnt.bar().progress(0.4).status(ProgressAnt.Status.ERROR).build();
                """;
        return Demos.sectionWithCode("2. 状态色",
                "status() 切换颜色：SUCCESS 绿 / WARNING 黄 / ERROR 红。", code, demo);
    }

    private Node circleSection() {
        VBox c1 = ProgressAnt.circle().progress(0.75).size(80).build();
        VBox c2 = ProgressAnt.circle().progress(1.0).size(80).status(ProgressAnt.Status.SUCCESS).build();
        VBox c3 = ProgressAnt.circle().progress(0.5).size(60).status(ProgressAnt.Status.ERROR).build();
        Node demo = Demos.row(c1, c2, c3);
        String code = """
                ProgressAnt.circle().progress(0.75).size(80).build();
                ProgressAnt.circle().progress(1.0).size(80).status(ProgressAnt.Status.SUCCESS).build();
                ProgressAnt.circle().progress(0.5).size(60).status(ProgressAnt.Status.ERROR).build();
                """;
        return Demos.sectionWithCode("3. 圆形进度",
                "circle() 创建圆形进度指示器，size() 控制直径。", code, demo);
    }
}
