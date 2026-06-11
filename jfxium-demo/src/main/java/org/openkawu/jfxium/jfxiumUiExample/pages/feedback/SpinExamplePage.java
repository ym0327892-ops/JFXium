package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.openkawu.jfxium.component.composite.SpinAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Spin 加载 —— 基础 / 尺寸 / 提示文字 / overlay 挂载。
 */
public class SpinExamplePage extends VBoxAnt {

    public SpinExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Spin 加载中")
                .description("用于页面或区域的加载状态指示，支持多种动画形态。overlay() 可挂载到任意节点实现区域加载覆盖。")
                .sections(basicSection(), sizeSection(), tipSection(), overlaySection())
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

    private Node overlaySection() {
        VBox content = new VBox(8,
                new Label("用户列表区域"),
                new Label("这里可以是表格、表单等任意内容"),
                new Label("点击下方按钮模拟加载状态")
        );
        content.setStyle("-fx-padding: 20px; -fx-background-color: -color-bg-container; -fx-min-height: 120px;");

        StackPane demoArea = new StackPane(content);
        demoArea.setStyle("-fx-border-color: -color-border-muted; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        // overlay 创建一次，可反复 show/hide
        SpinAnt.Overlay loading = SpinAnt.overlay(content);

        ButtonAnt triggerBtn = ButtonAnt.create("模拟加载").build();
        triggerBtn.setOnAction(e -> {
            loading.show("数据加载中...");
            PauseTransition pt = new PauseTransition(Duration.seconds(2));
            pt.setOnFinished(ev -> loading.hide());
            pt.play();
        });

        VBox demo = new VBox(12, demoArea, triggerBtn);

        String code = """
                SpinAnt.Overlay loading = SpinAnt.overlay(content);
                loading.show("数据加载中...");
                // ... 异步操作 ...
                loading.hide();
                """;
        return Demos.sectionWithCode("4. overlay 挂载",
                "SpinAnt.overlay(target) 将加载遮罩挂载到任意节点上方，show()/hide() 控制显示。2 秒后自动消失。",
                code, demo);
    }
}
