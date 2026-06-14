package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.composite.ProgressAnt;

/**
 * Progress 进度条 —— 基础百分比 / 状态色 / 圆形。
 */
public class ProgressExamplePage extends VBoxAnt {

    public ProgressExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Progress 进度条")
                .description("展示操作的当前进度，支持条形和圆形两种形态。")
                .sections(basicSection(), statusSection(), circleSection(), dynamicSection())
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

    private Node dynamicSection() {
        HBox bar = ProgressAnt.bar().progress(0.0).build();
        VBox circle = ProgressAnt.circle().progress(0.0).size(80).build();
        ProgressAnt.Controller barController = ProgressAnt.controllerOf(bar);
        ProgressAnt.Controller circleController = ProgressAnt.controllerOf(circle);

        ButtonAnt playBtn = ButtonAnt.create("开始演示")
                .type(ButtonAnt.Type.PRIMARY)
                .build();
        ButtonAnt resetBtn = ButtonAnt.create("重置").build();

        double[] progress = {0.0};
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(30), e -> {
                    progress[0] += 0.01;
                    if (progress[0] > 1.0) progress[0] = 1.0;
                    double p = progress[0];
                    barController.setProgress(p);
                    circleController.setProgress(p);
                })
        );
        timeline.setCycleCount(100);
        timeline.setOnFinished(ev -> MessageAnt.success("演示完成"));

        playBtn.setOnAction(e -> {
            progress[0] = 0.0;
            timeline.playFromStart();
        });
        resetBtn.setOnAction(e -> {
            timeline.stop();
            progress[0] = 0.0;
            barController.setProgress(0.0);
            circleController.setProgress(0.0);
        });

        VBox barRow = Demos.column(bar, playBtn, resetBtn);
        Node demo = Demos.row(barRow, circle);
        String code = """
                HBox bar = ProgressAnt.bar().progress(0.0).build();
                ProgressAnt.Controller ctrl = ProgressAnt.controllerOf(bar);
                // ... Timeline 或业务回调
                ctrl.setProgress(p);
                """;
        return Demos.sectionWithCode("4. 动态演示",
                "点击按钮模拟进度从 0% → 100% 的动画效果，运行时通过 Controller 更新进度。",
                code, demo);
    }
}
