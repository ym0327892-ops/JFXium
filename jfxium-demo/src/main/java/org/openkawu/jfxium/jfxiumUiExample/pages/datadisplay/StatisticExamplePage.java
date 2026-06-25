package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.Random;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.composite.StatisticAnt;

/**
 * Statistic 统计数值 —— 基础数值 / 前后缀 / 趋势。
 */
public class StatisticExamplePage extends VBoxAnt {

    public StatisticExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Statistic 统计数值")
                .description("展示统计类数值，常用于仪表盘的关键指标。")
                .sections(basicSection(), affixSection(), trendSection(), dynamicSection(), playgroundSection())
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
                .suffix(TypographyAnt.text("% ↑").type(TypographyAnt.TextColor.SUCCESS).build())
                .build();
        Node down = StatisticAnt.create()
                .title("环比下降")
                .value("9.30")
                .suffix(TypographyAnt.text("% ↓").type(TypographyAnt.TextColor.DANGER).build())
                .build();
        String code = """
                // 用带颜色的 Typography 文本作为 suffix 节点表达趋势
                StatisticAnt.create()
                        .title("环比增长")
                        .value("11.28")
                        .suffix(TypographyAnt.text("% ↑").type(TypographyAnt.TextColor.SUCCESS).build())
                        .build();
                """;
        return Demos.sectionWithCode("3. 趋势",
                "suffix 支持 Node，传入带颜色的 Typography 文本即可表达涨跌。", code, Demos.row(up, down));
    }

    private Node dynamicSection() {
        Random rand = new Random();
        Node stat = StatisticAnt.create()
                .title("实时数据")
                .value(rand.nextInt(10000, 99999))
                .suffix("条")
                .build();
        StatisticAnt.Controller controller = StatisticAnt.controllerOf(stat);

        ButtonAnt refreshBtn = ButtonAnt.create("刷新数据")
                .type(ButtonAnt.Type.PRIMARY)
                .onClick(e -> controller.setValue(rand.nextInt(10000, 99999)))
                .build();

        Node demo = Demos.column(stat, refreshBtn);
        String code = """
                Node stat = StatisticAnt.create()
                        .title("实时数据")
                        .value(12345)
                        .suffix("条")
                        .build();
                StatisticAnt.Controller ctrl = StatisticAnt.controllerOf(stat);
                ctrl.setValue(newValue);
                """;
        return Demos.sectionWithCode("4. 动态刷新",
                "点击按钮刷新数据，运行时通过 Controller 更新数值。",
                code, demo);
    }

    /**
     * 交互演示 section（M19.PlayGround）—— controller 模式样板。
     *
     * <p>StatisticAnt 的 Controller 暴露了 {@code setTitle / setValue / setPrefix /
     * setSuffix}，可原地修改 value 标签文本（无重建）。注意：{@code precision} 是 build 期
     * 字段（且框架侧并未在 build 里真正格式化数字），故 playground 通过 apply 自己按
     * precision 调 {@code controller.setValue(String.format(...))} 来模拟精度效果。</p>
     *
     * <p>{@link PlayGround#rebindController} 把这套流程封装成「声明式 API」：
     * 准备 Binder 状态盒子 + 写 controller apply + 串 row 即可，
     * 内部会自动提取 binder 注册监听。</p>
     */
    private Node playgroundSection() {
        // 1. 预先 build 出一个 Statistic 节点
        VBox stat = StatisticAnt.create()
                .title("活跃用户")
                .value(12345)
                .build();

        // 2. 状态盒子
        Binder<String> title     = PlayGround.binder("活跃用户");
        Binder<String> valueText = PlayGround.binder("12345");
        Binder<String> precision = PlayGround.binder("0");

        // 3. controller apply —— 读 binder 状态 → 原地修改 stat 节点
        StatisticAnt.Controller controller = StatisticAnt.controllerOf(stat);
        Runnable apply = () -> {
            controller.setTitle(title.get());
            int p = parsePrecision(precision.get());
            try {
                double raw = Double.parseDouble(valueText.get());
                String formatted = p > 0
                        ? String.format("%." + p + "f", raw)
                        : (raw == Math.floor(raw) && !Double.isInfinite(raw)
                                ? String.valueOf((long) raw) : String.valueOf(raw));
                controller.setValue(formatted);
            } catch (NumberFormatException e) {
                controller.setValue(valueText.get());
            }
        };

        // 4. 串起来 —— rebindController 内部自动提取 binder 并注册监听
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Statistic 的标题 / 数值 / 精度 —— 原地修改无重建。",
                PlayGround.rebindController(stat, apply, null,
                        PlayGround.row("标题", PlayGround.textField(title, title.get(), "输入统计标题")),
                        PlayGround.row("数值", PlayGround.textField(valueText, valueText.get(), "输入数字（可含小数）")),
                        PlayGround.row("精度", PlayGround.segmented(precision,
                                PlayGround.entry("0", "整数"),
                                PlayGround.entry("1", "1 位"),
                                PlayGround.entry("2", "2 位"),
                                PlayGround.entry("3", "3 位")))));
    }

    // ============================================================
    // 解析 helpers
    // ============================================================

    private static int parsePrecision(String v) {
        if (v == null) return 0;
        try {
            return Math.max(0, Math.min(6, Integer.parseInt(v)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
