package org.openkawu.jfxium.component.composite;

import javafx.geometry.Side;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ChartAnt")
class ChartAntTest extends JfxTestBase {

    // ============================================================
    // 1. 类型 → 原生图表类
    // ============================================================

    @Test
    @DisplayName("各工厂方法产出对应的原生图表类型")
    void factories_produceExpectedNativeType() {
        assertInstanceOf(LineChart.class, ChartAnt.line().build());
        assertInstanceOf(BarChart.class, ChartAnt.bar().build());
        assertInstanceOf(AreaChart.class, ChartAnt.area().build());
        assertInstanceOf(StackedBarChart.class, ChartAnt.stackedBar().build());
        assertInstanceOf(StackedAreaChart.class, ChartAnt.stackedArea().build());
        assertInstanceOf(ScatterChart.class, ChartAnt.scatter().build());
        assertInstanceOf(BubbleChart.class, ChartAnt.bubble().build());
        assertInstanceOf(PieChart.class, ChartAnt.pie().build());
    }

    @Test
    @DisplayName("create() 默认折线图；type(null) 回退折线图")
    void defaultType_isLine() {
        assertInstanceOf(LineChart.class, ChartAnt.create().build());
        assertInstanceOf(LineChart.class, ChartAnt.create().type(null).build());
        assertInstanceOf(LineChart.class, ChartAnt.bar().type(null).build());
    }

    @Test
    @DisplayName("build() 挂 jfx-chart 标记类且默认关闭动画")
    void build_appliesMarkerStyleClassAndAnimationDefault() {
        Chart chart = ChartAnt.line().build();
        assertTrue(chart.getStyleClass().contains(JfxStyles.JFX_CHART),
                "缺少 jfx-chart 标记类: " + chart.getStyleClass());
        assertFalse(chart.getAnimated(), "图表动画默认应为关闭");
    }

    @Test
    @DisplayName("animated(true) 打开系列动画")
    void animated_isHonored() {
        assertTrue(ChartAnt.line().animated(true).build().getAnimated());
        assertFalse(ChartAnt.line().animated(false).build().getAnimated());
    }

    // ============================================================
    // 2. 标题 / 图例 / 坐标轴
    // ============================================================

    @Test
    @DisplayName("title / legend / legendSide 落到原生图表")
    void titleAndLegend_areApplied() {
        Chart chart = ChartAnt.bar()
                .title("月度对比")
                .legend(true)
                .legendSide(Side.RIGHT)
                .build();

        assertEquals("月度对比", chart.getTitle());
        assertTrue(chart.isLegendVisible());
        assertEquals(Side.RIGHT, chart.getLegendSide());
    }

    @Test
    @DisplayName("title(null) 归空字符串，不显示 null")
    void nullTitle_becomesEmptyString() {
        assertEquals("", ChartAnt.line().title(null).build().getTitle());
        assertEquals("", ChartAnt.line().build().getTitle());
    }

    @Test
    @DisplayName("xAxisLabel/yAxisLabel 落到 CategoryAxis / NumberAxis")
    void axisLabels_areApplied() {
        LineChart<String, Number> chart = (LineChart<String, Number>) ChartAnt.line()
                .xAxisLabel("星期")
                .yAxisLabel("人次")
                .build();

        assertEquals("星期", chart.getXAxis().getLabel());
        assertEquals("人次", chart.getYAxis().getLabel());
    }

    @Test
    @DisplayName("categories() 显式声明分类轴，忽略 null 项")
    void categories_areDeclaredAndNullsSkipped() {
        CategoryAxis axis = (CategoryAxis) ((LineChart<?, ?>) ChartAnt.line()
                .categories("周一", null, "周三")
                .build()).getXAxis();

        assertEquals(List.of("周一", "周三"), axis.getCategories());
    }

    @Test
    @DisplayName("自定义坐标轴被原样采用，标签仍可覆盖")
    void customAxes_areReused() {
        CategoryAxis cx = new CategoryAxis();
        NumberAxis ny = new NumberAxis();
        LineChart<String, Number> chart = (LineChart<String, Number>) ChartAnt.line()
                .xAxis(cx)
                .yAxis(ny)
                .xAxisLabel("X")
                .build();

        assertSame(cx, chart.getXAxis());
        assertSame(ny, chart.getYAxis());
        assertEquals("X", cx.getLabel());
    }

    // ============================================================
    // 3. 数值轴图表（散点 / 气泡）
    // ============================================================

    @Test
    @DisplayName("散点/气泡的 X/Y 轴都是 NumberAxis（对比分类轴族为 CategoryAxis）")
    void numericAxisFamilies_useNumberAxes() {
        XYChart<?, ?> scatter = (XYChart<?, ?>) ChartAnt.scatter().build();
        assertInstanceOf(NumberAxis.class, scatter.getXAxis());
        assertInstanceOf(NumberAxis.class, scatter.getYAxis());

        XYChart<?, ?> bubble = (XYChart<?, ?>) ChartAnt.bubble().build();
        assertInstanceOf(NumberAxis.class, bubble.getXAxis());
        assertInstanceOf(NumberAxis.class, bubble.getYAxis());
    }

    @Test
    @DisplayName("pointSeries 原样引用，之后追加数据点即刻生效")
    void pointSeries_isReferencedAndLive() {
        XYChart.Series<Number, Number> s = ChartAnt.pointSeries("样本",
                ChartAnt.point(1, 2), ChartAnt.point(3, 4));

        ScatterChart<Number, Number> chart = (ScatterChart<Number, Number>) ChartAnt.scatter()
                .pointSeries(s)
                .build();

        assertEquals(1, chart.getData().size());
        assertSame(s, chart.getData().get(0));
        assertEquals(2, s.getData().size());

        s.getData().add(ChartAnt.point(5, 6));
        assertEquals(3, chart.getData().get(0).getData().size());
    }

    @Test
    @DisplayName("points(name, ...) 装配数值轴系列，忽略 null 点")
    void points_packagesVarargsAndSkipsNulls() {
        ScatterChart<Number, Number> chart = (ScatterChart<Number, Number>) ChartAnt.scatter()
                .points(null, ChartAnt.point(1, 1), null)
                .build();

        assertEquals(1, chart.getData().size());
        XYChart.Series<Number, Number> s = chart.getData().get(0);
        assertEquals("", s.getName());
        assertEquals(1, s.getData().size());
        assertEquals(1.0, s.getData().get(0).getXValue());
        assertEquals(1.0, s.getData().get(0).getYValue());
    }

    @Test
    @DisplayName("bubble(x,y,size) 把大小写进 extraValue；非正/非有限值归零")
    void bubbleSize_goesToExtraValue() {
        XYChart.Data<Number, Number> ok = ChartAnt.bubble(12, 5.2, 20);
        assertEquals(12.0, ok.getXValue());
        assertEquals(5.2, ok.getYValue());
        assertEquals(20.0, ok.getExtraValue());

        assertEquals(0.0, ChartAnt.bubble(1, 1, -5).getExtraValue());
        assertEquals(0.0, ChartAnt.bubble(1, 1, Double.NaN).getExtraValue());
        assertEquals(0.0, ChartAnt.bubble(1, 1, Double.POSITIVE_INFINITY).getExtraValue());
    }

    @Test
    @DisplayName("数值轴标签落到 NumberAxis")
    void numericAxisLabels_areApplied() {
        ScatterChart<Number, Number> chart = (ScatterChart<Number, Number>) ChartAnt.scatter()
                .xAxisLabel("身高")
                .yAxisLabel("体重")
                .build();

        assertEquals("身高", chart.getXAxis().getLabel());
        assertEquals("体重", chart.getYAxis().getLabel());
    }

    @Test
    @DisplayName("自定义数值 X 轴被原样采用")
    void customNumericXAxis_isReused() {
        NumberAxis nx = new NumberAxis(0, 100, 10);
        ScatterChart<Number, Number> chart = (ScatterChart<Number, Number>) ChartAnt.scatter()
                .xAxis(nx)
                .build();

        assertSame(nx, chart.getXAxis());
    }

    // ============================================================
    // 4. 数据装配
    // ============================================================

    @Test
    @DisplayName("series(...) 原样引用传入的 Series（可直接热改数据）")
    void series_areReferencedNotCopied() {
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("访问量");
        s.getData().add(ChartAnt.data("周一", 120));

        LineChart<String, Number> chart = (LineChart<String, Number>) ChartAnt.line()
                .series(s)
                .build();

        assertEquals(1, chart.getData().size());
        assertSame(s, chart.getData().get(0));

        // 构建后继续改 Series 即刻反映到图表
        s.getData().add(ChartAnt.data("周二", 200));
        assertEquals(2, chart.getData().get(0).getData().size());
    }

    @Test
    @DisplayName("series(name, list) 包装为 Series；name(null) → 空名")
    void seriesWithName_packagesList() {
        LineChart<String, Number> chart = (LineChart<String, Number>) ChartAnt.line()
                .series(null, List.of(ChartAnt.data("周一", 10)))
                .build();

        XYChart.Series<String, Number> s = chart.getData().get(0);
        assertEquals("", s.getName());
        assertEquals(1, s.getData().size());
    }

    @Test
    @DisplayName("series(null) / series 中的 null 项被忽略")
    void nullSeries_areIgnored() {
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        LineChart<String, Number> chart = (LineChart<String, Number>) ChartAnt.line()
                .series((XYChart.Series<String, Number>[]) null)
                .series(s, null)
                .build();

        assertEquals(1, chart.getData().size());
    }

    @Test
    @DisplayName("data(category, y) 归一化 null 入参")
    void data_normalizesNulls() {
        XYChart.Data<String, Number> d = ChartAnt.data(null, null);
        assertEquals("", d.getXValue());
        assertEquals(0, d.getYValue());
    }

    @Test
    @DisplayName("pie(name, value) 装配扇区；非正值归零")
    void pieValues_areClamped() {
        PieChart chart = (PieChart) ChartAnt.pie()
                .pie("正常", 100)
                .pie("负数", -5)
                .pie("NaN", Double.NaN)
                .pie("无穷", Double.POSITIVE_INFINITY)
                .pie((String) null, 10)
                .build();

        List<PieChart.Data> data = chart.getData();
        assertEquals(5, data.size());
        assertEquals(100, data.get(0).getPieValue(), 0.001);
        assertEquals(0, data.get(1).getPieValue(), 0.001);
        assertEquals(0, data.get(2).getPieValue(), 0.001);
        assertEquals(0, data.get(3).getPieValue(), 0.001);
        assertEquals("", data.get(4).getName());
    }

    @Test
    @DisplayName("slice(null, 负值) 归一化")
    void slice_normalizesNulls() {
        PieChart.Data d = ChartAnt.slice(null, -1);
        assertEquals("", d.getName());
        assertEquals(0, d.getPieValue(), 0.001);
    }

    @Test
    @DisplayName("pie(Data...) 忽略 null 项")
    void pieDataNulls_areIgnored() {
        PieChart chart = (PieChart) ChartAnt.pie()
                .pie((PieChart.Data[]) null)
                .pie(ChartAnt.slice("A", 1), null)
                .build();
        assertEquals(1, chart.getData().size());
    }

    // ============================================================
    // 5. Controller
    // ============================================================

    @Test
    @DisplayName("controllerOf 取回 Controller；typeOf 反查类型")
    void controllerOf_and_typeOf() {
        Chart chart = ChartAnt.stackedArea().build();

        assertEquals(ChartAnt.Type.STACKED_AREA, ChartAnt.typeOf(chart));
        assertEquals(ChartAnt.Type.STACKED_AREA, ChartAnt.controllerOf(chart).getType());
        assertSame(chart, ChartAnt.controllerOf(chart).getChart());
    }

    @Test
    @DisplayName("controllerOf(null) / 非 ChartAnt 节点抛 IllegalStateException")
    void controllerOf_rejectsForeignNodes() {
        assertThrows(IllegalStateException.class, () -> ChartAnt.controllerOf(null));
        assertThrows(IllegalStateException.class, () -> ChartAnt.typeOf(null));
        assertThrows(IllegalStateException.class,
                () -> ChartAnt.controllerOf(new LineChart<>(new CategoryAxis(), new NumberAxis())));
        assertThrows(IllegalStateException.class,
                () -> ChartAnt.typeOf(new LineChart<>(new CategoryAxis(), new NumberAxis())));
    }

    @Test
    @DisplayName("Controller 改标题 / 图例")
    void controller_updatesTitleAndLegend() {
        Chart chart = ChartAnt.line().title("旧").build();
        ChartAnt.Controller ctrl = ChartAnt.controllerOf(chart);

        ctrl.setTitle("新");
        ctrl.setTitle(null);
        ctrl.setLegendVisible(true);
        ctrl.setAnimated(true);

        assertEquals("", chart.getTitle());
        assertEquals("", ctrl.getTitle());
        assertTrue(chart.isLegendVisible());
        assertTrue(chart.getAnimated());
    }

    @Test
    @DisplayName("Controller 增删清空 XY 系列（含数值轴系列）")
    void controller_mutatesSeries() {
        Chart chart = ChartAnt.line().build();
        ChartAnt.Controller ctrl = ChartAnt.controllerOf(chart);

        XYChart.Series<String, Number> a = new XYChart.Series<>();
        XYChart.Series<String, Number> b = new XYChart.Series<>();
        ctrl.addSeries(a, b);
        assertEquals(2, ctrl.getSeries().size());
        assertSame(a, ctrl.getSeries().get(0));

        ctrl.removeSeries(a);
        assertEquals(List.of(b), ctrl.getSeries());

        ctrl.clearSeries();
        assertTrue(ctrl.getSeries().isEmpty());
    }

    @Test
    @DisplayName("Controller 也能管理散点图的数值轴系列")
    void controller_managesNumericSeries() {
        ChartAnt.Controller ctrl = ChartAnt.controllerOf(ChartAnt.scatter().build());

        XYChart.Series<Number, Number> s = ChartAnt.pointSeries("样本", ChartAnt.point(1, 1));
        ctrl.addSeries(s);

        assertEquals(1, ctrl.getSeries().size());
        assertSame(s, ctrl.getSeries().get(0));

        ctrl.clearSeries();
        assertTrue(ctrl.getSeries().isEmpty());
    }

    @Test
    @DisplayName("Controller 空系列入参无副作用")
    void controller_nullSeriesAreIgnored() {
        ChartAnt.Controller ctrl = ChartAnt.controllerOf(ChartAnt.line().build());
        ctrl.addSeries((XYChart.Series<String, Number>[]) null);
        ctrl.addSeries((XYChart.Series<String, Number>) null);
        ctrl.removeSeries(null);
        assertTrue(ctrl.getSeries().isEmpty());
    }

    @Test
    @DisplayName("Controller 饼图数据：putPieValue 更新同名扇区 / 追加新扇区")
    void controller_pieDataOps() {
        ChartAnt.Controller ctrl = ChartAnt.controllerOf(
                ChartAnt.pie().pie("A", 10).build());

        ctrl.putPieValue("A", 42);
        assertEquals(1, ctrl.getPieData().size(), "同名扇区应更新而非追加");
        assertEquals(42, ctrl.getPieData().get(0).getPieValue(), 0.001);

        ctrl.putPieValue("B", 7);
        assertEquals(2, ctrl.getPieData().size());

        // 非正 / null 名 归一化
        ctrl.putPieValue(null, -3);
        PieChart.Data appended = ctrl.getPieData().get(2);
        assertEquals("", appended.getName());
        assertEquals(0, appended.getPieValue(), 0.001);

        ctrl.clearPieData();
        assertTrue(ctrl.getPieData().isEmpty());
    }

    @Test
    @DisplayName("Controller 跨家族操作抛 IllegalStateException")
    void controller_rejectsCrossFamilyOps() {
        ChartAnt.Controller line = ChartAnt.controllerOf(ChartAnt.line().build());
        assertThrows(IllegalStateException.class, () -> line.addPieData(ChartAnt.slice("A", 1)));
        assertThrows(IllegalStateException.class, line::clearPieData);
        assertThrows(IllegalStateException.class, line::getPieData);

        ChartAnt.Controller pie = ChartAnt.controllerOf(ChartAnt.pie().build());
        assertThrows(IllegalStateException.class, pie::clearSeries);
        assertThrows(IllegalStateException.class, pie::getSeries);
        assertThrows(IllegalStateException.class,
                () -> pie.addSeries(new XYChart.Series<String, Number>()));
    }

    // ============================================================
    // 6. build() 语义
    // ============================================================

    @Test
    @DisplayName("build() 幂等：重复调用不累积状态，各自产出独立图表")
    void build_isIdempotent() {
        ChartAnt.Builder builder = ChartAnt.line()
                .title("T")
                .series("S", List.of(ChartAnt.data("周一", 1)));

        LineChart<String, Number> first = (LineChart<String, Number>) builder.build();
        LineChart<String, Number> second = (LineChart<String, Number>) builder.build();

        assertNotSame(first, second);
        assertEquals(1, first.getData().size());
        assertEquals(1, second.getData().size(), "重复 build 不应累积 series");
        assertEquals("T", second.getTitle());
    }

    @Test
    @DisplayName("分类轴族装配 CategoryAxis + NumberAxis（饼图不加坐标轴）")
    void axes_areCategoryAndNumberForCategoryFamilies() {
        XYChart<?, ?> xy = (XYChart<?, ?>) ChartAnt.bar().build();
        assertInstanceOf(CategoryAxis.class, xy.getXAxis());
        assertInstanceOf(NumberAxis.class, xy.getYAxis());
    }
}
