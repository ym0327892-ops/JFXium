package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import org.openkawu.jfxium.component.StatisticAnt;
import org.openkawu.jfxium.component.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Statistic 统计数值 —— 基础数值 / 前后缀 / 趋势。
 */
public class StatisticExamplePage extends VBoxAnt {

    public StatisticExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Statistic 统计数值")
                .description("展示统计类数值，常用于仪表盘的关键指标。")
                .sections(basicSection(), affixSection(), trendSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                StatisticAnt.create().title("活跃用户").value(112893).build(),
                StatisticAnt.create().title("账户余额").value("9,999.99").precision(2).build()
        );
        String code = """
                StatisticAnt.create().title("活跃用户").value(112893).build();
                StatisticAnt.create().title("账户余额").value("9,999.99").build();
                """;
        return Demos.sectionWithCode("1. 基础数值",
                "title 标题 + value 数值，value 支持 int / long / double / String。", code, demo);
    }

    private Node affixSection() {
        Node demo = Demos.row(
                StatisticAnt.create().title("充值金额").prefix("￥").value("88,888").build(),
                StatisticAnt.create().title("反馈数").value(99).suffix("条").build()
        );
        String code = """
                StatisticAnt.create().title("充值金额").prefix("￥").value("88,888").build();
                StatisticAnt.create().title("反馈数").value(99).suffix("条").build();
                """;
        return Demos.sectionWithCode("2. 前缀与后缀",
                "prefix / suffix 在数值前后追加单位或符号。", code, demo);
    }

    private Node trendSection() {
        Node up = StatisticAnt.create()
                .title("环比增长")
                .value("11.28")
                .suffix(TypographyAnt.text("% ↑").type(TypographyAnt.Type.SUCCESS).build())
                .build();
        Node down = StatisticAnt.create()
                .title("环比下降")
                .value("9.30")
                .suffix(TypographyAnt.text("% ↓").type(TypographyAnt.Type.DANGER).build())
                .build();
        String code = """
                // 用带颜色的 Typography 文本作为 suffix 节点表达趋势
                StatisticAnt.create()
                        .title("环比增长")
                        .value("11.28")
                        .suffix(TypographyAnt.text("% ↑").type(TypographyAnt.Type.SUCCESS).build())
                        .build();
                """;
        return Demos.sectionWithCode("3. 趋势",
                "suffix 支持 Node，传入带颜色的 Typography 文本即可表达涨跌。", code, Demos.row(up, down));
    }
}
