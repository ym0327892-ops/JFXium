package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.RateAnt;

/**
 * Rate 评分 —— 基础 / 半星 / 自定义数量。
 */
public class RateExamplePage extends VBoxAnt {

    public RateExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Rate 评分")
                .description("星级评分组件，支持半星与自定义星数。")
                .sections(basicSection(), halfSection(), countSection(), valueSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = RateAnt.create()
                .value(3)
                .onChange(v -> {})
                .build();
        String code = """
                RateAnt.create()
                        .value(3)
                        .onChange(v -> System.out.println("评分：" + v))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "value(...) 设置初始分值，点击星星即可改变评分。",
                code, demo);
    }

    private Node halfSection() {
        Node demo = RateAnt.create()
                .value(2.5)
                .allowHalf()
                .build();
        String code = """
                RateAnt.create()
                        .value(2.5)
                        .allowHalf()
                        .build();
                """;
        return Demos.sectionWithCode("2. 半星",
                "allowHalf() 开启半星，点击星星左半边记 0.5 分。",
                code, demo);
    }

    private Node countSection() {
        Node demo = RateAnt.create()
                .count(10)
                .value(7)
                .build();
        String code = """
                RateAnt.create()
                        .count(10)
                        .value(7)
                        .build();
                """;
        return Demos.sectionWithCode("3. 自定义星数",
                "count(...) 自定义星星总数，默认为 5。",
                code, demo);
    }

    /**
     * 4. 获取当前评分 —— onChange 回调拿到当前分值（double）。
     *
     * <p>点击星星即触发 onChange，结果 Label 实时显示当前评分。</p>
     */
    private Node valueSection() {
        Label result = new Label("当前评分：3");
        Node rate = RateAnt.create()
                .value(3)
                .allowHalf()
                .onChange(v -> result.setText("当前评分：" + v))
                .build();
        Node demo = Demos.column(rate, result);
        String code = """
                Label result = new Label("当前评分：3");
                RateAnt.create()
                        .value(3)
                        .allowHalf()
                        .onChange(v -> result.setText("当前评分：" + v))
                        .build();
                """;
        return Demos.sectionWithCode("4. 获取当前评分",
                "onChange(v -> ...) 给出当前分值（开启 allowHalf 时可为 0.5 的倍数）；点击星星即更新结果 Label。",
                code, demo);
    }
}
