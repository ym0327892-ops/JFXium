package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;

import org.openkawu.jfxium.component.composite.ChartAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.layout.HBoxAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

import java.util.List;
import java.util.function.Supplier;

/**
 * Chart 图表 —— 折线 / 柱状 / 面积 / 堆叠 / 饼图（ChartAnt 封装）。
 */
public class ChartExamplePage extends VBoxAnt {

    public ChartExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Chart 图表")
                .description("封装 javafx.scene.chart，八种图表类型统一 Builder 入口，分类色板由主题提供。")
                .sections(lineSection(), multiBarSection(), areaSection(), stackedSection(),
                        pieSection(), numericSection(), controllerSection(), playgroundSection())
                .padding(24)
                .build());
    }

    // ============================================================
    // 1. 折线图
    // ============================================================

    private Node lineSection() {
        XYChart.Series<String, Number> pv = series("访问量",
                ChartAnt.data("周一", 120), ChartAnt.data("周二", 200),
                ChartAnt.data("周三", 150), ChartAnt.data("周四", 80),
                ChartAnt.data("周五", 70), ChartAnt.data("周六", 110),
                ChartAnt.data("周日", 130));

        XYChart.Series<String, Number> uv = series("独立访客",
                ChartAnt.data("周一", 60), ChartAnt.data("周二", 90),
                ChartAnt.data("周三", 70), ChartAnt.data("周四", 40),
                ChartAnt.data("周五", 35), ChartAnt.data("周六", 55),
                ChartAnt.data("周日", 65));

        Chart chart = ChartAnt.line()
                .title("周访问趋势")
                .xAxisLabel("星期")
                .yAxisLabel("人次")
                .series(pv, uv)
                .prefSize(560, 320)
                .build();

        String code = """
                XYChart.Series<String, Number> pv = new XYChart.Series<>();
                pv.setName("访问量");
                pv.getData().addAll(
                        ChartAnt.data("周一", 120),
                        ChartAnt.data("周二", 200));

                Chart chart = ChartAnt.line()
                        .title("周访问趋势")
                        .xAxisLabel("星期")
                        .yAxisLabel("人次")
                        .series(pv)
                        .prefSize(560, 320)
                        .build();
                """;
        return Demos.sectionWithCode("1. 折线图",
                "line() 创建折线图，series() 可传多个系列，data() 免去 XYChart.Data 泛型噪音。", code, chart);
    }

    // ============================================================
    // 2. 多系列柱状图
    // ============================================================

    private Node multiBarSection() {
        XYChart.Series<String, Number> thisWeek = series("本周",
                ChartAnt.data("一月", 40), ChartAnt.data("二月", 60),
                ChartAnt.data("三月", 50), ChartAnt.data("四月", 80));

        XYChart.Series<String, Number> lastWeek = series("上周",
                ChartAnt.data("一月", 35), ChartAnt.data("二月", 45),
                ChartAnt.data("三月", 65), ChartAnt.data("四月", 55));

        Chart chart = ChartAnt.bar()
                .title("月度对比")
                .yAxisLabel("订单数")
                .legend(true)
                .legendSide(Side.BOTTOM)
                .series(thisWeek, lastWeek)
                .prefSize(560, 320)
                .build();

        String code = """
                Chart chart = ChartAnt.bar()
                        .title("月度对比")
                        .yAxisLabel("订单数")
                        .legend(true)
                        .legendSide(Side.BOTTOM)
                        .series(thisWeek, lastWeek)
                        .build();
                """;
        return Demos.sectionWithCode("2. 柱状图（多系列 + 图例）",
                "bar() 创建柱状图；legend() / legendSide() 控制图例显隐与位置。", code, chart);
    }

    // ============================================================
    // 3. 面积图
    // ============================================================

    private Node areaSection() {
        XYChart.Series<String, Number> series = series("流量",
                ChartAnt.data("0时", 30), ChartAnt.data("4时", 12),
                ChartAnt.data("8时", 150), ChartAnt.data("12时", 220),
                ChartAnt.data("16时", 260), ChartAnt.data("20时", 180),
                ChartAnt.data("24时", 90));

        Chart chart = ChartAnt.area()
                .title("全天流量分布")
                .yAxisLabel("MB")
                .series(series)
                .prefSize(560, 320)
                .build();

        String code = """
                Chart chart = ChartAnt.area()
                        .title("全天流量分布")
                        .yAxisLabel("MB")
                        .series(traffic)
                        .build();
                """;
        return Demos.sectionWithCode("3. 面积图",
                "area() 创建面积图，适合表现连续区间上的累计量。", code, chart);
    }

    // ============================================================
    // 4. 堆叠柱状图 / 堆叠面积图
    // ============================================================

    private Node stackedSection() {
        Chart stackedBar = ChartAnt.stackedBar()
                .title("各渠道订单（堆叠）")
                .yAxisLabel("订单数")
                .series(
                        series("线上",
                                ChartAnt.data("一月", 20), ChartAnt.data("二月", 30),
                                ChartAnt.data("三月", 25), ChartAnt.data("四月", 40)),
                        series("线下",
                                ChartAnt.data("一月", 10), ChartAnt.data("二月", 15),
                                ChartAnt.data("三月", 30), ChartAnt.data("四月", 20)))
                .prefSize(460, 300)
                .build();

        Chart stackedArea = ChartAnt.stackedArea()
                .title("各端活跃（堆叠面积）")
                .series(
                        series("桌面端",
                                ChartAnt.data("周一", 40), ChartAnt.data("周二", 55),
                                ChartAnt.data("周三", 45), ChartAnt.data("周四", 60)),
                        series("移动端",
                                ChartAnt.data("周一", 70), ChartAnt.data("周二", 65),
                                ChartAnt.data("周三", 85), ChartAnt.data("周四", 90)))
                .prefSize(460, 300)
                .build();

        Node demo = Demos.row(stackedBar, stackedArea);
        String code = """
                Chart stackedBar = ChartAnt.stackedBar()
                        .title("各渠道订单（堆叠）")
                        .series(online, offline)
                        .build();

                Chart stackedArea = ChartAnt.stackedArea()
                        .title("各端活跃（堆叠面积）")
                        .series(desktop, mobile)
                        .build();
                """;
        return Demos.sectionWithCode("4. 堆叠图",
                "stackedBar() / stackedArea() 把多个系列堆叠在同一根柱 / 同一块面积上。", code, demo);
    }

    // ============================================================
    // 5. 饼图
    // ============================================================

    private Node pieSection() {
        Chart chart = ChartAnt.pie()
                .title("流量来源占比")
                .pie(ChartAnt.slice("搜索引擎", 1048))
                .pie(ChartAnt.slice("直接访问", 735))
                .pie(ChartAnt.slice("邮件营销", 580))
                .pie(ChartAnt.slice("联盟广告", 484))
                .pie(ChartAnt.slice("视频广告", 300))
                .prefSize(460, 320)
                .build();

        String code = """
                Chart chart = ChartAnt.pie()
                        .title("流量来源占比")
                        .pie(ChartAnt.slice("搜索引擎", 1048))
                        .pie(ChartAnt.slice("直接访问", 735))
                        .build();

                // 或直接传 PieChart.Data
                Chart chart2 = ChartAnt.pie()
                        .pie(new PieChart.Data("邮件营销", 580))
                        .build();
                """;
        return Demos.sectionWithCode("5. 饼图",
                "pie() 创建饼图；slice(name, value) 是扇区工厂，数值非正会被归零。", code, chart);
    }

    // ============================================================
    // 6. 散点图 / 气泡图（数值轴）
    // ============================================================

    private Node numericSection() {
        Chart scatter = ChartAnt.scatter()
                .title("身高体重分布")
                .xAxisLabel("身高(cm)")
                .yAxisLabel("体重(kg)")
                .points("样本 A",
                        ChartAnt.point(160, 52), ChartAnt.point(165, 58),
                        ChartAnt.point(170, 65), ChartAnt.point(175, 70),
                        ChartAnt.point(180, 78))
                .points("样本 B",
                        ChartAnt.point(158, 48), ChartAnt.point(163, 55),
                        ChartAnt.point(172, 68), ChartAnt.point(178, 74))
                .prefSize(460, 300)
                .build();

        Chart bubble = ChartAnt.bubble()
                .title("城市指标（气泡=人口）")
                .xAxisLabel("GDP(千亿)")
                .yAxisLabel("人均收入(万)")
                .points("城市",
                        ChartAnt.bubble(12, 5.2, 20),
                        ChartAnt.bubble(25, 7.8, 45),
                        ChartAnt.bubble(8, 4.1, 12),
                        ChartAnt.bubble(31, 9.4, 62),
                        ChartAnt.bubble(18, 6.3, 33))
                .prefSize(460, 300)
                .build();

        Node demo = Demos.row(scatter, bubble);
        String code = """
                // 散点图：X/Y 均为数值轴，数据点用 point(x, y)
                Chart scatter = ChartAnt.scatter()
                        .xAxisLabel("身高(cm)")
                        .yAxisLabel("体重(kg)")
                        .points("样本 A", ChartAnt.point(160, 52), ChartAnt.point(165, 58))
                        .build();

                // 气泡图：第三维（气泡大小）走数据点的 extraValue
                Chart bubble = ChartAnt.bubble()
                        .points("城市", ChartAnt.bubble(12, 5.2, 20))
                        .build();
                """;
        return Demos.sectionWithCode("6. 散点图 / 气泡图",
                "scatter() / bubble() 的 X 轴是数值轴，必须用 point(x,y) / bubble(x,y,size) 装配数据点"
                        + "（分类轴的 data(name, y) 在这里取不到坐标）。", code, demo);
    }

    // ============================================================
    // 7. 动态更新（Controller）
    // ============================================================

    private Node controllerSection() {
        XYChart.Series<String, Number> initial = series("今天",
                ChartAnt.data("周一", 20), ChartAnt.data("周二", 40),
                ChartAnt.data("周三", 35), ChartAnt.data("周四", 50));

        Chart chart = ChartAnt.line()
                .title("实时数据")
                .series(initial)
                .prefSize(560, 300)
                .build();

        ChartAnt.Controller ctrl = ChartAnt.controllerOf(chart);

        int[][] datasets = {
                {20, 40, 35, 50},
                {80, 30, 65, 20},
                {45, 55, 25, 70},
        };
        String[] titles = {"实时数据", "切换数据集 #1", "切换数据集 #2"};
        int[] cursor = {0};

        ButtonAnt next = ButtonAnt.create("切换数据集")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> {
                    cursor[0] = (cursor[0] + 1) % datasets.length;
                    int[] values = datasets[cursor[0]];

                    XYChart.Series<String, Number> fresh = new XYChart.Series<>();
                    fresh.setName("今天");
                    for (int i = 0; i < values.length; i++) {
                        fresh.getData().add(ChartAnt.data(
                                new String[]{"周一", "周二", "周三", "周四"}[i], values[i]));
                    }

                    ctrl.clearSeries();
                    ctrl.addSeries(fresh);
                    ctrl.setTitle(titles[cursor[0]]);
                })
                .build();

        Node demo = Demos.column(chart, HBoxAnt.create().spacing(8).children(next).build());

        String code = """
                Chart chart = ChartAnt.line().series(series).build();
                ChartAnt.Controller ctrl = ChartAnt.controllerOf(chart);

                // 运行期换数据集 / 改标题，无需重建组件
                ctrl.clearSeries();
                ctrl.addSeries(freshSeries);
                ctrl.setTitle("切换数据集 #1");
                """;
        return Demos.sectionWithCode("7. 动态更新",
                "controllerOf(chart) 取回 Controller：clearSeries / addSeries / setTitle 原地刷新，不重建节点。",
                code, demo);
    }

    // ============================================================
    // 8. 交互演示 (PlayGround)
    // ============================================================

    private Node playgroundSection() {
        Binder<String> type = PlayGround.binder("line");
        Binder<String> scale = PlayGround.binder("week");

        Supplier<Node> factory = () -> {
            String[] categories = "month".equals(scale.get())
                    ? new String[]{"一月", "二月", "三月", "四月", "五月"}
                    : new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            int[][] table = "month".equals(scale.get())
                    ? new int[][]{{40, 62, 51, 78, 66}, {28, 44, 70, 52, 61}}
                    : new int[][]{{120, 200, 150, 80, 70, 110, 130}, {60, 90, 70, 40, 35, 55, 65}};

            XYChart.Series<String, Number> a = new XYChart.Series<>();
            a.setName("访问量");
            XYChart.Series<String, Number> b = new XYChart.Series<>();
            b.setName("独立访客");
            for (int i = 0; i < categories.length; i++) {
                a.getData().add(ChartAnt.data(categories[i], table[0][i]));
                b.getData().add(ChartAnt.data(categories[i], table[1][i]));
            }

            return switch (type.get()) {
                case "bar" -> ChartAnt.bar().title("访问量对比").series(a, b).prefSize(560, 320).build();
                case "area" -> ChartAnt.area().title("访问量对比").series(a).prefSize(560, 320).build();
                case "stackedBar" -> ChartAnt.stackedBar().title("访问量对比").series(a, b).prefSize(560, 320).build();
                default -> ChartAnt.line().title("访问量对比").series(a, b).prefSize(560, 320).build();
            };
        };

        return Demos.section("8. 交互演示",
                "左侧切换图表类型与数据粒度 —— 不同类型对应不同的原生图表节点，必须重建。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("类型", PlayGround.segmented(type,
                                PlayGround.entry("line",       "折线"),
                                PlayGround.entry("bar",        "柱状"),
                                PlayGround.entry("area",       "面积"),
                                PlayGround.entry("stackedBar", "堆叠柱"))),
                        PlayGround.row("粒度", PlayGround.segmented(scale,
                                PlayGround.entry("week",  "按星期"),
                                PlayGround.entry("month", "按月")))));
    }

    // ============================================================
    // helpers
    // ============================================================

    private static XYChart.Series<String, Number> series(String name, XYChart.Data<String, Number>... data) {
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName(name);
        s.getData().addAll(List.of(data));
        return s;
    }
}
