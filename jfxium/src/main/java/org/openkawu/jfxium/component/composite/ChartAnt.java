package org.openkawu.jfxium.component.composite;

import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.style.JfxStyles;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Chart 图表组件 —— 封装 {@code javafx.scene.chart.*} 的 Builder 入口。
 *
 * <h2>为什么需要本类</h2>
 * <p>主题层早已为原生图表写好了完整样式（{@code components/_chart.less}，564 行，
 * 覆盖 {@code .chart} / {@code .chart-plot-background} / {@code .axis} /
 * {@code .chart-series-line} / {@code .chart-bar} / {@code .chart-pie} /
 * {@code .chart-legend} 等），分类色板由 {@code -color-chart-1..8} 提供。
 * 但业务侧此前只能 {@code new LineChart<>(...)} —— 违反红线 11「禁止业务代码 new 原生控件」，
 * 且坐标系、动画开关、系列装配全靠手写。本类是图表的<b>合规入口</b>。</p>
 *
 * <h2>已支持类型（8 种，覆盖 javafx.scene.chart 全部内置图表）</h2>
 * <p><b>分类轴族</b>（X 轴 {@link CategoryAxis}，数据形状「分类名 → 数值」，
 * 数据点用 {@link #data(String, Number)}）：</p>
 * <ul>
 *   <li>{@link Type#LINE} / {@link Type#BAR} / {@link Type#AREA}</li>
 *   <li>{@link Type#STACKED_BAR} / {@link Type#STACKED_AREA}</li>
 * </ul>
 * <p><b>数值轴族</b>（X 轴 {@link NumberAxis}，数据点是数值坐标）：</p>
 * <ul>
 *   <li>{@link Type#SCATTER} —— 散点图，用 {@link #point(double, double)} 装配数据点</li>
 *   <li>{@link Type#BUBBLE} —— 气泡图，用 {@link #bubble(double, double, double)}，
 *       第三维（气泡大小）落在数据点的 {@code extraValue}</li>
 * </ul>
 * <p><b>饼图</b>（无坐标轴）：{@link Type#PIE}，数据用 {@link #slice(String, double)}
 * 或 {@link PieChart.Data}。</p>
 * <p>两族的差别只在坐标轴类型，因此<b>数据装配方法不可混用</b> ——
 * 用 {@code data(name, y)} 建的点在散点图上取不到 X 坐标。参见
 * {@link Builder#series(XYChart.Series[])} 与 {@link Builder#pointSeries(XYChart.Series[])}。</p>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * XYChart.Series<String, Number> thisWeek = new XYChart.Series<>();
 * thisWeek.setName("本周");
 * thisWeek.getData().addAll(
 *     new XYChart.Data<>("周一", 120),
 *     new XYChart.Data<>("周二", 200));
 *
 * Chart chart = ChartAnt.line()
 *     .title("周访问量")
 *     .yAxisLabel("PV")
 *     .series(thisWeek)
 *     .prefSize(520, 300)
 *     .build();
 *
 * // 运行期改数据 / 改标题 —— 无需重建组件
 * ChartAnt.Controller ctrl = ChartAnt.controllerOf(chart);
 * ctrl.setTitle("本月访问量");
 * }</pre>
 *
 * <h2>返回值说明</h2>
 * <p>{@code build()} 返回<b>原生 {@link Chart} 本体</b>（不包不装）。所有原生能力
 * （{@code getData()} / {@code getXAxis()} 等）都可直接使用；数据的热更新走
 * {@link Controller}，避免持有控件引用。</p>
 *
 * @see <a href="https://openjfx.io/javadoc/21/javafx.controls/javafx/scene/chart/package-summary.html">javafx.scene.chart</a>
 */
public class ChartAnt {

    /** 挂在 {@link Chart#getProperties()} 上的 controller key。 */
    private static final String CONTROLLER_KEY = ChartAnt.class.getName() + ".controller";

    /** 挂在 {@link Chart#getProperties()} 上的图表类型 key。 */
    private static final String TYPE_KEY = ChartAnt.class.getName() + ".type";

    /** 图表类型。 */
    public enum Type {
        /** 折线图（{@code LineChart}）。 */
        LINE,
        /** 柱状图（{@code BarChart}）。 */
        BAR,
        /** 面积图（{@code AreaChart}）。 */
        AREA,
        /** 堆叠柱状图（{@code StackedBarChart}）。 */
        STACKED_BAR,
        /** 堆叠面积图（{@code StackedAreaChart}）。 */
        STACKED_AREA,
        /** 散点图（{@code ScatterChart}，X/Y 均为数值轴）。 */
        SCATTER,
        /** 气泡图（{@code BubbleChart}，X/Y 为数值轴，第三维走 {@code extraValue}）。 */
        BUBBLE,
        /** 饼图（{@code PieChart}，数据用 {@link #pie} 提供）。 */
        PIE
    }

    private ChartAnt() {
        // 组件入口类，禁止实例化
    }

    // ============================================================
    // 静态工厂
    // ============================================================

    /** 创建 Builder，默认类型为 {@link Type#LINE}。 */
    public static Builder create() {
        return new Builder();
    }

    /** 折线图 Builder。 */
    public static Builder line() {
        return new Builder().type(Type.LINE);
    }

    /** 柱状图 Builder。 */
    public static Builder bar() {
        return new Builder().type(Type.BAR);
    }

    /** 面积图 Builder。 */
    public static Builder area() {
        return new Builder().type(Type.AREA);
    }

    /** 堆叠柱状图 Builder。 */
    public static Builder stackedBar() {
        return new Builder().type(Type.STACKED_BAR);
    }

    /** 堆叠面积图 Builder。 */
    public static Builder stackedArea() {
        return new Builder().type(Type.STACKED_AREA);
    }

    /**
     * 散点图 Builder。
     *
     * <p>X / Y 均为数值轴，数据点用 {@link #point(double, double)} /
     * {@link #pointSeries(String, Data...)} 装配（分类轴系列的 {@code Data<String,Number>}
     * 在这里会取不到数值，必须用数值 X）。</p>
     */
    public static Builder scatter() {
        return new Builder().type(Type.SCATTER);
    }

    /**
     * 气泡图 Builder。
     *
     * <p>X / Y 为数值轴，第三维（气泡大小）走数据点的 {@code extraValue} ——
     * 用 {@link #bubble(double, double, double)} 装配。</p>
     */
    public static Builder bubble() {
        return new Builder().type(Type.BUBBLE);
    }

    /**
     * 分类轴数据点工厂（折线 / 柱状 / 面积 / 堆叠类型的通用写法）。
     *
     * <p>免去业务侧手写 {@code new XYChart.Data<>("周一", 120)} 的泛型噪音。</p>
     *
     * @param category 分类名，null → 空字符串
     * @param y        数值，null → 0
     */
    public static XYChart.Data<String, Number> data(String category, Number y) {
        return new XYChart.Data<>(category != null ? category : "", y != null ? y : 0);
    }

    /**
     * 饼图扇区工厂。
     *
     * @param name  扇区名，null → 空字符串
     * @param value 数值，负 / NaN / Infinity → 0
     */
    public static PieChart.Data slice(String name, double value) {
        double safe = (Double.isFinite(value) && value > 0) ? value : 0d;
        return new PieChart.Data(name != null ? name : "", safe);
    }

    /**
     * 散点数据点工厂（X / Y 均为数值）。
     *
     * <p>注意与分类轴系列的 {@link #data(String, Number)} 区别：散点图的 X 轴是
     * {@link NumberAxis}，只有数值 X 才会被解析出坐标。</p>
     */
    public static XYChart.Data<Number, Number> point(double x, double y) {
        return new XYChart.Data<>(x, y);
    }

    /**
     * 气泡数据点工厂（X / Y 数值 + 第三维气泡大小，走 {@code extraValue}）。
     *
     * <p>JavaFX 的 {@code BubbleChart} 由皮肤读 {@code extraValue} 决定气泡半径，
     * 因此大小不是独立属性而是数据点的一部分。</p>
     *
     * @param x    横坐标
     * @param y    纵坐标
     * @param size 气泡大小（负 / NaN / Infinity → 0）
     */
    public static XYChart.Data<Number, Number> bubble(double x, double y, double size) {
        XYChart.Data<Number, Number> d = new XYChart.Data<>(x, y);
        d.setExtraValue(Double.isFinite(size) && size > 0 ? size : 0d);
        return d;
    }

    /** 数值轴系列工厂：按名称 + 数据点建 {@link XYChart.Series}（散点 / 气泡用）。 */
    @SafeVarargs
    public static XYChart.Series<Number, Number> pointSeries(
            String name, XYChart.Data<Number, Number>... points) {
        XYChart.Series<Number, Number> s = new XYChart.Series<>();
        s.setName(name != null ? name : "");
        if (points != null) {
            for (XYChart.Data<Number, Number> p : points) {
                if (p != null) {
                    s.getData().add(p);
                }
            }
        }
        return s;
    }

    /**
     * 反查图表类型（由 {@link Builder#build()} 记录在节点属性上）。
     *
     * @throws IllegalStateException 节点不是 ChartAnt 构建的
     */
    public static Type typeOf(Node chart) {
        if (chart == null) {
            throw new IllegalStateException("typeOf(chart): chart 为 null");
        }
        Object t = chart.getProperties().get(TYPE_KEY);
        if (t instanceof Type type) {
            return type;
        }
        throw new IllegalStateException("typeOf(chart): 该节点不是 ChartAnt 构建的");
    }

    /** 饼图 Builder。 */
    public static Builder pie() {
        return new Builder().type(Type.PIE);
    }

    /**
     * 取回 {@link ChartAnt} 构建的图表 Controller，用于运行期热更新。
     *
     * @param chart {@link Builder#build()} 的返回值
     * @throws IllegalStateException chart 为 null，或不是 ChartAnt 构建的（controller 已被覆盖）
     */
    public static Controller controllerOf(Node chart) {
        if (chart == null) {
            throw new IllegalStateException("controllerOf(chart): chart 为 null");
        }
        Object controller = chart.getProperties().get(CONTROLLER_KEY);
        if (!(controller instanceof Controller c)) {
            throw new IllegalStateException(
                    "controllerOf(chart): 该节点不是 ChartAnt 构建的，或 controller 已被覆盖");
        }
        return c;
    }

    // ============================================================
    // Builder
    // ============================================================

    /**
     * ChartAnt Builder —— 链式装配图表。
     *
     * <p>继承 {@link AbstractStyleBuilder}，自带 {@code padding / prefSize / maxSize /
     * minSize / styleClass / disabled / visible / opacity / cursor / id / radius} 等通用钩子。</p>
     */
    public static class Builder extends AbstractStyleBuilder<Builder> {

        private Type type = Type.LINE;

        /** 标题，默认空字符串（JavaFX 原生默认是 null，这里统一为空串）。 */
        private String title = "";
        private String xAxisLabel = null;
        private String yAxisLabel = null;
        private Boolean legendVisible = null;
        private Side legendSide = null;

        /** 默认关闭动画 —— 桌面业务图表重绘频繁，动画是干扰；需要时显式打开。 */
        private boolean animated = false;

        /** null 表示由 build() 新建默认坐标轴。 */
        private CategoryAxis categoryAxis = null;
        private NumberAxis numberAxis = null;
        /** 散点 / 气泡的数值 X 轴。 */
        private NumberAxis xNumberAxis = null;

        private final List<String> categories = new ArrayList<>();
        /** 分类轴系列的显式类别；散点 / 气泡在 build() 时转成数值轴系列。 */
        private final List<XYChart.Series<String, Number>> categorySeries = new ArrayList<>();
        /** XY 系列（任意数据形状）——已转好的数值轴系列也并到这里。 */
        private final List<XYChart.Series<?, ?>> xySeries = new ArrayList<>();
        private final List<PieChart.Data> pieDataList = new ArrayList<>();
        private Builder() {
        }

        /** 图表类型。null → {@link Type#LINE}。 */
        public Builder type(Type type) {
            this.type = (type != null) ? type : Type.LINE;
            return this;
        }

        /** 标题。null → 空标题（不显示）。 */
        public Builder title(String title) {
            this.title = (title != null) ? title : "";
            return this;
        }

        /** X 轴标签（分类轴）。null → 不设置。 */
        public Builder xAxisLabel(String label) {
            this.xAxisLabel = label;
            return this;
        }

        /** Y 轴标签（数值轴）。null → 不设置。 */
        public Builder yAxisLabel(String label) {
            this.yAxisLabel = label;
            return this;
        }

        /**
         * 显式声明分类轴的全部分类（按顺序）。
         *
         * <p>用于「某些分类没有数据点也要出现在轴上」的场景；不设则由数据自动推导。
         * null / 空 → 忽略。</p>
         */
        public Builder categories(String... names) {
            if (names == null) {
                return this;
            }
            for (String name : names) {
                if (name != null) {
                    categories.add(name);
                }
            }
            return this;
        }

        /** 自定义分类轴。null → build() 新建默认 {@link CategoryAxis}。 */
        public Builder xAxis(CategoryAxis axis) {
            this.categoryAxis = axis;
            return this;
        }

        /** 自定义数值轴。null → build() 新建默认 {@link NumberAxis}。 */
        public Builder yAxis(NumberAxis axis) {
            this.numberAxis = axis;
            return this;
        }

        /**
         * 自定义数值 X 轴（散点 / 气泡图专用，如设置量程 / 刻度）。
         *
         * <p>null → build() 新建默认 {@link NumberAxis}。</p>
         */
        public Builder xAxis(NumberAxis axis) {
            this.xNumberAxis = axis;
            return this;
        }

        /** 图例显隐。 */
        public Builder legend(boolean visible) {
            this.legendVisible = visible;
            return this;
        }

        /** 图例位置。null → 保留 JavaFX 默认（{@link Side#BOTTOM}）。 */
        public Builder legendSide(Side side) {
            this.legendSide = side;
            return this;
        }

        /** 是否启用系列增删动画（默认 false）。 */
        public Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        // -------------------- 数据 --------------------

        /**
         * 追加 XY 系列（折线 / 柱状 / 面积 / 堆叠类型生效；饼图忽略）。
         *
         * <p>传入的 {@link XYChart.Series} 被<b>原样引用</b>（不复制），因此构建后继续向该
         * Series 增删数据点即可实时刷新图表。null 项与 null 数组被忽略。</p>
         *
         * <p>散点 / 气泡图请改用 {@link #pointSeries(String, Data...)} —— 它们的 X 轴是
         * 数值轴，分类轴系列取不到坐标。</p>
         */
        @SafeVarargs
        public final Builder series(XYChart.Series<String, Number>... series) {
            if (series == null) {
                return this;
            }
            for (XYChart.Series<String, Number> s : series) {
                if (s != null) {
                    categorySeries.add(s);
                }
            }
            return this;
        }

        /**
         * 用名称 + 数据点列表追加一个 XY 系列（内部包装为 {@link XYChart.Series}）。
         *
         * <p>name 为 null → 空字符串；data 为 null → 建空系列。</p>
         */
        public Builder series(String name, List<XYChart.Data<String, Number>> data) {
            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName(name != null ? name : "");
            if (data != null) {
                for (XYChart.Data<String, Number> d : data) {
                    if (d != null) {
                        s.getData().add(d);
                    }
                }
            }
            categorySeries.add(s);
            return this;
        }

        /**
         * 追加数值轴系列（散点 / 气泡图）。
         *
         * <p>与 {@link #series(XYChart.Series[])} 的区别只有坐标轴类型：散点 / 气泡的 X 轴是
         * {@link NumberAxis}，数据点必须是数值 X。两种方法都能收下任意 {@link XYChart.Series}，
         * 但只有与轴类型匹配的那种才会画得出点。</p>
         */
        @SafeVarargs
        public final Builder pointSeries(XYChart.Series<Number, Number>... series) {
            if (series == null) {
                return this;
            }
            for (XYChart.Series<Number, Number> s : series) {
                if (s != null) {
                    xySeries.add(s);
                }
            }
            return this;
        }

        /** 用名称 + 数据点列表追加数值轴系列。name 为 null → 空字符串。 */
        public Builder pointSeries(String name, List<XYChart.Data<Number, Number>> points) {
            XYChart.Series<Number, Number> s = new XYChart.Series<>();
            s.setName(name != null ? name : "");
            if (points != null) {
                for (XYChart.Data<Number, Number> p : points) {
                    if (p != null) {
                        s.getData().add(p);
                    }
                }
            }
            xySeries.add(s);
            return this;
        }

        /** 便捷写法：按名称 + 数据点变参追加数值轴系列（散点 / 气泡图）。null 项被忽略。 */
        @SafeVarargs
        public final Builder points(String name, XYChart.Data<Number, Number>... points) {
            XYChart.Series<Number, Number> s = new XYChart.Series<>();
            s.setName(name != null ? name : "");
            if (points != null) {
                for (XYChart.Data<Number, Number> p : points) {
                    if (p != null) {
                        s.getData().add(p);
                    }
                }
            }
            xySeries.add(s);
            return this;
        }

        /** 追加饼图扇区（饼图生效，其他类型忽略）。null 项被忽略。 */
        public Builder pie(PieChart.Data... data) {
            if (data == null) {
                return this;
            }
            for (PieChart.Data d : data) {
                if (d != null) {
                    pieDataList.add(d);
                }
            }
            return this;
        }

        /**
         * 追加一个饼图扇区（按名称 + 数值）。
         *
         * <p>name 为 null → 空字符串；value 为负 / NaN / Infinity → 归零（扇区无负面积语义）。</p>
         */
        public Builder pie(String name, double value) {
            double safe = (Double.isFinite(value) && value > 0) ? value : 0d;
            pieDataList.add(new PieChart.Data(name != null ? name : "", safe));
            return this;
        }

        // -------------------- 构建 --------------------

        /**
         * 构建图表，返回<b>原生 {@link Chart} 本体</b>。
         *
         * <p>幂等性：本方法不累积 Builder 状态（无监听器 / 无临时子节点）；每次调用都会
         * 新建一个 Chart。但传入的 {@link XYChart.Series} / {@link PieChart.Data} 是
         * 原样引用（JavaFX 中一个 Series 只能归属一个图表），因此同一 Builder 重复
         * {@code build()} 得到多个图表时，请为它们准备各自的 Series。</p>
         */
        public Chart build() {
            Chart chart = createChart();

            chart.getStyleClass().add(JfxStyles.JFX_CHART);
            chart.setTitle(title);
            if (legendVisible != null) {
                chart.setLegendVisible(legendVisible);
            }
            if (legendSide != null) {
                chart.setLegendSide(legendSide);
            }

            // 用户 style / styleClass 在内置类之后应用，便于覆盖
            applyStyles(chart);

            chart.getProperties().put(CONTROLLER_KEY, new Controller(chart));
            chart.getProperties().put(TYPE_KEY, type);
            return chart;
        }

        /** 按类型建图并装配数据。动画开关必须在加数据之前设置才能免去首帧动画。 */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private Chart createChart() {
            if (type == Type.PIE) {
                PieChart chart = new PieChart();
                chart.setAnimated(animated);
                chart.getData().addAll(pieDataList);
                return chart;
            }

            XYChart chart = (isNumericXAxis() ? createNumericXAxisChart() : createCategoryXAxisChart());
            chart.setAnimated(animated);
            chart.getData().addAll(xySeries());
            return chart;
        }

        /** 散点 / 气泡的 X 轴是数值轴。 */
        private boolean isNumericXAxis() {
            return type == Type.SCATTER || type == Type.BUBBLE;
        }

        /** 分类轴家族（折线 / 柱状 / 面积 / 堆叠）。 */
        private XYChart<String, Number> createCategoryXAxisChart() {
            CategoryAxis cx = resolveCategoryAxis();
            NumberAxis ny = resolveNumberAxis();
            return switch (type) {
                case LINE -> new LineChart<>(cx, ny);
                case BAR -> new BarChart<>(cx, ny);
                case AREA -> new AreaChart<>(cx, ny);
                case STACKED_BAR -> new StackedBarChart<>(cx, ny);
                case STACKED_AREA -> new StackedAreaChart<>(cx, ny);
                default -> throw new IllegalStateException(
                        "类型 " + type + " 不属于分类轴图表");
            };
        }

        /** 数值轴家族（散点 / 气泡）。 */
        private XYChart<Number, Number> createNumericXAxisChart() {
            NumberAxis nx = resolveNumberXAxis();
            NumberAxis ny = resolveNumberAxis();
            return switch (type) {
                case SCATTER -> new ScatterChart<>(nx, ny);
                case BUBBLE -> new BubbleChart<>(nx, ny);
                default -> throw new IllegalStateException(
                        "类型 " + type + " 不属于数值轴图表");
            };
        }

        /** 汇总本次要装配的 XY 系列（分类轴系列原样 + 数值轴系列）。 */
        private List<XYChart.Series<?, ?>> xySeries() {
            List<XYChart.Series<?, ?>> all = new ArrayList<>(xySeries);
            all.addAll(categorySeries);
            return all;
        }

        private CategoryAxis resolveCategoryAxis() {
            CategoryAxis axis = (categoryAxis != null) ? categoryAxis : new CategoryAxis();
            if (!categories.isEmpty()) {
                axis.setCategories(FXCollections.observableArrayList(categories));
            }
            if (xAxisLabel != null) {
                axis.setLabel(xAxisLabel);
            }
            return axis;
        }

        private NumberAxis resolveNumberAxis() {
            NumberAxis axis = (numberAxis != null) ? numberAxis : new NumberAxis();
            if (yAxisLabel != null) {
                axis.setLabel(yAxisLabel);
            }
            return axis;
        }

        private NumberAxis resolveNumberXAxis() {
            NumberAxis axis = (xNumberAxis != null) ? xNumberAxis : new NumberAxis();
            if (xAxisLabel != null) {
                axis.setLabel(xAxisLabel);
            }
            return axis;
        }
    }

    // ============================================================
    // Controller（运行期热更新）
    // ============================================================

    /**
     * ChartAnt Controller —— 构建后改标题 / 图例 / 数据，无需重建组件。
     *
     * <pre>{@code
     * ChartAnt.Controller ctrl = ChartAnt.controllerOf(chart);
     * ctrl.setTitle("新标题");
     * ctrl.addSeries(newSeries);
     * }</pre>
     */
    public static class Controller {

        /** {@code chart.getProperties()} 的 key，支持 {@link ChartAnt#controllerOf(Node)} 静态查找。 */
        public static final String PROPERTY_KEY = CONTROLLER_KEY;

        private final Chart chart;

        Controller(Chart chart) {
            this.chart = chart;
        }

        /** 底层原生图表。 */
        public Chart getChart() {
            return chart;
        }

        /** 当前图表类型。 */
        public Type getType() {
            return typeOf(chart);
        }

        /** 当前标题。 */
        public String getTitle() {
            return chart.getTitle();
        }

        /** 改标题。null → 空标题。 */
        public void setTitle(String title) {
            chart.setTitle(title != null ? title : "");
        }

        /** 图例显隐。 */
        public void setLegendVisible(boolean visible) {
            chart.setLegendVisible(visible);
        }

        /** 系列增删动画开关。 */
        public void setAnimated(boolean animated) {
            chart.setAnimated(animated);
        }

        // -------------------- XY 系列 --------------------

        /** 追加 XY 系列。null 项忽略。散点 / 气泡图同样适用。 */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public final void addSeries(XYChart.Series<?, ?>... series) {
            if (series == null) {
                return;
            }
            XYChart<Object, Object> xy = asXy();
            for (XYChart.Series<?, ?> s : series) {
                if (s != null) {
                    xy.getData().add((XYChart.Series) s);
                }
            }
        }

        /** 移除指定 XY 系列（不存在则无操作）。 */
        public void removeSeries(XYChart.Series<?, ?> series) {
            if (series != null) {
                asXy().getData().remove(series);
            }
        }

        /** 清空全部 XY 系列（折线 / 柱状 / 面积 / 堆叠 / 散点 / 气泡通用）。 */
        public void clearSeries() {
            asXy().getData().clear();
        }

        /** 当前全部 XY 系列（只读视图，元素类型由实际图表决定，见 {@link #getType()}）。 */
        public List<XYChart.Series<?, ?>> getSeries() {
            return List.copyOf(asXy().getData());
        }

        // -------------------- 饼图数据 --------------------

        /** 追加饼图扇区。null 项忽略。 */
        public void addPieData(PieChart.Data... data) {
            if (data == null) {
                return;
            }
            PieChart pie = asPie();
            for (PieChart.Data d : data) {
                if (d != null) {
                    pie.getData().add(d);
                }
            }
        }

        /** 清空饼图数据。 */
        public void clearPieData() {
            asPie().getData().clear();
        }

        /** 当前饼图数据（只读视图）。 */
        public List<PieChart.Data> getPieData() {
            return List.copyOf(asPie().getData());
        }

        /**
         * 按名称更新饼图扇区数值；不存在同名扇区则追加。
         *
         * <p>value 为负 / NaN / Infinity → 归零。</p>
         */
        public void putPieValue(String name, double value) {
            String key = name != null ? name : "";
            double safe = (Double.isFinite(value) && value > 0) ? value : 0d;
            PieChart pie = asPie();
            for (PieChart.Data d : pie.getData()) {
                if (key.equals(d.getName())) {
                    d.setPieValue(safe);
                    return;
                }
            }
            pie.getData().add(new PieChart.Data(key, safe));
        }

        // -------------------- 内部 --------------------

        @SuppressWarnings({"unchecked", "rawtypes"})
        private XYChart<Object, Object> asXy() {
            if (chart instanceof XYChart<?, ?> xy) {
                return (XYChart<Object, Object>) xy;
            }
            throw new IllegalStateException(
                    "当前图表是 PIE，不支持 XY 系列操作；请使用饼图相关 API");
        }

        private PieChart asPie() {
            if (chart instanceof PieChart pie) {
                return pie;
            }
            throw new IllegalStateException(
                    "当前图表不是 PIE，不支持饼图数据操作；请使用 XY 系列相关 API");
        }
    }
}
