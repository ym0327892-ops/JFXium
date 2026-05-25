package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.CardAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.StatisticAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Statistic 数值展示页（M19.9）—— dashboard 必用。
 */
public class StatisticPage implements ShowcasePage {

    @Override public String   key()      { return "statistic"; }
    @Override public String   title()    { return "Statistic 数值"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Statistic 数值");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("dashboard 必用 —— 标题 + 大数字 + 前/后缀。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionPrefixSuffix(),
                        sectionSizes(),
                        sectionDashboard()
                )
                .build();
    }

    private Node sectionBasic() {
        Node s1 = StatisticAnt.create().title("总用户").value(1234).build();
        Node s2 = StatisticAnt.create().title("今日订单").value(89).build();
        Node s3 = StatisticAnt.create().title("月销售额").value("¥125,432").build();

        HBox row = HBoxBuilder.create().spacing(40).children(s1, s2, s3).build();

        return ShowcaseSection.create()
                .title("场景 1：基础（标题 + 数字）")
                .description(".value() 接 int / long / double / String —— admin 概览的基本块")
                .demo(row)
                .code("""
                        StatisticAnt.create().title("总用户").value(1234).build();
                        StatisticAnt.create().title("今日订单").value(89).build();
                        StatisticAnt.create().title("月销售额").value("¥125,432").build();
                        """)
                .build();
    }

    private Node sectionPrefixSuffix() {
        Node revenue = StatisticAnt.create()
                .title("收入").value(125432).prefix("¥").build();
        Node growth = StatisticAnt.create()
                .title("月增长").value(23.4).suffix("%").build();
        Node users = StatisticAnt.create()
                .title("活跃用户").value(8742).suffix(" / 12,000").build();

        HBox row = HBoxBuilder.create().spacing(40).children(revenue, growth, users).build();

        return ShowcaseSection.create()
                .title("场景 2：前缀 / 后缀（货币、单位、百分比）")
                .description(".prefix(str) / .suffix(str) —— 展示带单位的数值")
                .demo(row)
                .code("""
                        StatisticAnt.create().title("收入").value(125432).prefix("¥").build();
                        StatisticAnt.create().title("月增长").value(23.4).suffix("%").build();
                        StatisticAnt.create().title("活跃用户").value(8742).suffix(" / 12,000").build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        Node small = StatisticAnt.create().title("Small").value(123).size(StatisticAnt.Size.SMALL).build();
        Node def = StatisticAnt.create().title("Default").value(123).build();
        Node large = StatisticAnt.create().title("Large").value(123).size(StatisticAnt.Size.LARGE).build();

        HBox row = HBoxBuilder.create().spacing(40).children(small, def, large).build();

        return ShowcaseSection.create()
                .title("场景 3：三档尺寸")
                .description("SMALL / DEFAULT / LARGE —— LARGE 适合 dashboard 头部主指标")
                .demo(row)
                .code("""
                        StatisticAnt.create().title("Small").value(123).size(StatisticAnt.Size.SMALL).build();
                        StatisticAnt.create().title("Default").value(123).build();
                        StatisticAnt.create().title("Large").value(123).size(StatisticAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionDashboard() {
        // 模拟 dashboard 4 格统计卡
        HBox row = new HBox(16);
        for (Node card : new Node[]{
                statCard("总订单", 12420, "↑ 12%", false),
                statCard("总收入", "¥528K", "↑ 8%", false),
                statCard("待处理", 86, "↓ 24%", true),
                statCard("满意度", "98.2%", "↑ 0.4%", false)
        }) {
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
        }

        return ShowcaseSection.create()
                .title("场景 4：dashboard 头部 4 格统计卡（实战）")
                .description("Statistic + CardAnt + 趋势标 —— admin dashboard 的标准头部")
                .demo(row)
                .code("""
                        // 单个卡片：CardAnt 包 StatisticAnt + 趋势 Label
                        VBox stat = StatisticAnt.create()
                            .title("总订单").value(12420).size(StatisticAnt.Size.LARGE).build();
                        Label trend = new Label("↑ 12%");
                        trend.setStyle("-fx-text-fill: -color-success-emphasis;");
                        VBox content = VBoxBuilder.create().spacing(4).children(stat, trend).build();
                        VBox card = CardAnt.create().content(content).bordered(true).build();
                        """)
                .build();
    }

    private static Node statCard(String title, Object value, String trend, boolean down) {
        Node stat = StatisticAnt.create()
                .title(title)
                .value(String.valueOf(value))
                .size(StatisticAnt.Size.LARGE)
                .build();
        Label tr = new Label(trend);
        tr.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                (down ? "-color-danger-emphasis" : "-color-success-emphasis") + ";");

        VBox content = VBoxBuilder.create().spacing(4).children(stat, tr).build();
        return CardAnt.create().content(content).bordered(true).shadow(CardAnt.Shadow.SMALL).build();
    }
}
