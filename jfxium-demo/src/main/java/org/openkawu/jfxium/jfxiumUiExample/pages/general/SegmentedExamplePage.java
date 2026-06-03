package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SegmentedAnt;

/**
 * Segmented 分段器 —— 基础 / 禁用 / 块级模式。
 */
public class SegmentedExamplePage extends VBoxAnt {

    public SegmentedExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Segmented 分段器")
                .description("用于在多个选项中切换，类似 Tab 但更紧凑。")
                .sections(
                        basicSection(),
                        disabledSection(),
                        blockSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        HBox segmented = SegmentedAnt.create()
                .option("daily", "日")
                .option("weekly", "周")
                .option("monthly", "月")
                .selected("weekly")
                .onChange(val -> System.out.println("选中: " + val))
                .build();
        String code = """
                HBox segmented = SegmentedAnt.create()
                        .option("daily", "日")
                        .option("weekly", "周")
                        .option("monthly", "月")
                        .selected("weekly")
                        .onChange(val -> System.out.println("选中: " + val))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "通过 option() 添加选项，selected() 设置默认值。", code, segmented);
    }

    private Node disabledSection() {
        HBox segmented = SegmentedAnt.create()
                .option("opt1", "选项 A")
                .option("opt2", "选项 B")
                .option("opt3", "选项 C")
                .selected("opt1")
                .disabled()
                .build();
        String code = """
                HBox segmented = SegmentedAnt.create()
                        .option("opt1", "选项 A")
                        .option("opt2", "选项 B")
                        .option("opt3", "选项 C")
                        .selected("opt1")
                        .disabled()
                        .build();
                """;
        return Demos.sectionWithCode("2. 禁用状态", "disabled() 禁用整个分段器。", code, segmented);
    }

    private Node blockSection() {
        HBox segmented = SegmentedAnt.create()
                .option("map", "地图")
                .option("transit", "公交")
                .option("satellite", "卫星")
                .selected("map")
                .block()
                .build();
        String code = """
                HBox segmented = SegmentedAnt.create()
                        .option("map", "地图")
                        .option("transit", "公交")
                        .option("satellite", "卫星")
                        .selected("map")
                        .block()
                        .build();
                """;
        return Demos.sectionWithCode("3. 块级模式", "block() 让分段器撑满父容器宽度。", code, segmented);
    }
}
