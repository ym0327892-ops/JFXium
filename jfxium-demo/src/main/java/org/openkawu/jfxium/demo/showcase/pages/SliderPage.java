package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.SliderAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Slider 滑块展示页（M19.11）。
 */
public class SliderPage implements ShowcasePage {

    @Override public String   key()      { return "slider"; }
    @Override public String   title()    { return "Slider 滑块"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Slider 滑块");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("数值范围拖拽选择 —— 单点 / 区间 / 离散刻度 / 自定义标记 / 垂直方向。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionRange(),
                        sectionStepDots(),
                        sectionMarks(),
                        sectionVertical()
                )
                .build();
    }

    private Node sectionBasic() {
        Node s = SliderAnt.create().min(0).max(100).value(40).build();
        ((javafx.scene.layout.Region) s).setPrefWidth(360);

        return ShowcaseSection.create()
                .title("场景 1：基础滑块（0–100，初值 40）")
                .description(".min / .max / .value —— 默认连续拖动")
                .demo(s)
                .code("""
                        SliderAnt.create().min(0).max(100).value(40).build();
                        """)
                .build();
    }

    private Node sectionRange() {
        Node s = SliderAnt.create()
                .min(0).max(100)
                .range()
                .defaultValue(new double[]{20, 80})
                .build();
        ((javafx.scene.layout.Region) s).setPrefWidth(360);

        return ShowcaseSection.create()
                .title("场景 2：区间滑块（双拖把）")
                .description(".range() + .defaultValue([min, max]) —— 价格区间 / 时间段筛选")
                .demo(s)
                .code("""
                        SliderAnt.create()
                            .min(0).max(100)
                            .range()
                            .defaultValue(new double[]{20, 80})
                            .build();
                        """)
                .build();
    }

    private Node sectionStepDots() {
        Node s = SliderAnt.create()
                .min(0).max(100).value(40)
                .step(10)
                .dots(true)
                .build();
        ((javafx.scene.layout.Region) s).setPrefWidth(360);

        return ShowcaseSection.create()
                .title("场景 3：离散刻度（step + dots）")
                .description(".step(10) 跳格步进；.dots(true) 显示每个步进点 —— 评分/级别选择")
                .demo(s)
                .code("""
                        SliderAnt.create()
                            .min(0).max(100).value(40)
                            .step(10).dots(true)
                            .build();
                        """)
                .build();
    }

    private Node sectionMarks() {
        Map<Double, String> marks = new LinkedHashMap<>();
        marks.put(0.0, "0°C");
        marks.put(26.0, "26°C");
        marks.put(37.0, "37°C");
        marks.put(100.0, "100°C");

        Node s = SliderAnt.create()
                .min(0).max(100).value(37)
                .marks(marks)
                .step(1)
                .build();
        ((javafx.scene.layout.Region) s).setPrefWidth(360);

        return ShowcaseSection.create()
                .title("场景 4：自定义刻度标记（marks）")
                .description(".marks(Map<value, label>) —— 在指定位置显示自定义标签")
                .demo(s)
                .code("""
                        Map<Double, String> marks = Map.of(
                            0.0, "0°C", 26.0, "26°C", 37.0, "37°C", 100.0, "100°C"
                        );
                        SliderAnt.create()
                            .min(0).max(100).value(37)
                            .marks(marks)
                            .build();
                        """)
                .build();
    }

    private Node sectionVertical() {
        Node s1 = SliderAnt.create().min(0).max(100).value(40).vertical(true).build();
        Node s2 = SliderAnt.create().min(0).max(100).value(60).vertical(true)
                .step(20).dots(true).build();
        Node s3 = SliderAnt.create().min(0).max(100).range()
                .defaultValue(new double[]{20, 80}).vertical(true).build();

        // 给纵向 slider 一个固定高度
        ((javafx.scene.layout.Region) s1).setPrefHeight(160);
        ((javafx.scene.layout.Region) s2).setPrefHeight(160);
        ((javafx.scene.layout.Region) s3).setPrefHeight(160);

        HBox row = HBoxBuilder.create().spacing(40).children(s1, s2, s3).build();

        return ShowcaseSection.create()
                .title("场景 5：垂直方向（vertical）")
                .description(".vertical(true) —— 适合音量、温度、亮度等垂直语义控制")
                .demo(row)
                .code("""
                        SliderAnt.create().min(0).max(100).value(40).vertical(true).build();
                        """)
                .build();
    }
}
