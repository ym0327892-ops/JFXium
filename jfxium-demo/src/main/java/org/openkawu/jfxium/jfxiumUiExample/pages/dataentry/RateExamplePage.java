package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.RateAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;

/**
 * Rate 评分 —— 基础 / 半星 / 自定义数量 / 交互演示。
 */
public class RateExamplePage extends VBoxAnt {

    public RateExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Rate 评分")
                .description("星级评分组件，支持半星与自定义星数。")
                .sections(basicSection(), halfSection(), countSection(), valueSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = RateAnt.create()
                .value(3)
                .onChange(v -> MessageAnt.info("评分：" + v))
                .build();
        String code = """
                RateAnt.create()
                        .value(3)
                        .onChange(v -> MessageAnt.info("评分：" + v))
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
        Label result = TypographyAnt.text("当前评分：3").build();
        Node rate = RateAnt.create()
                .value(3)
                .allowHalf()
                .onChange(v -> result.setText("当前评分：" + v))
                .build();
        Node demo = Demos.column(rate, result);
        String code = """
                Label result = TypographyAnt.text("当前评分：3").build();
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

    // ============================================================
    // 5. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Rate 的 4 个维度：当前值 / 星数 / 半星 / 禁用。
     *
     * <p>RateAnt 没有 Controller（没有 controllerOf 入口），且 Builder 字段
     * （value / count / allowHalf / disabled）全部是 build-time。
     * 所以采用 {@link PlayGround#rebindRebuild}：每次值变更都重新 {@code build()}
     * —— Rate 节点本身重建，但 Rate 内部是几个 Label + Region，体积很小，
     * 重建代价低，且保证所有属性变更都生效。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> value     = PlayGround.binder("3");
        Binder<String> count     = PlayGround.binder("5");
        Binder<String> allowHalf = PlayGround.binder("off");
        Binder<String> disabled  = PlayGround.binder("off");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            int c = (int) parseNum(count.get(), 5, 1, 99);
            double v = parseNum(value.get(), 3, 0, c);
            RateAnt.Builder b = RateAnt.create()
                    .count(c)
                    .value(v)
                    .disabled(parseBool(disabled.get()));
            if (parseBool(allowHalf.get())) {
                b = b.allowHalf();
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Rate 的当前值 / 星数 / 半星 / 禁用 —— RateAnt 无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("当前值", PlayGround.segmented(value,
                                PlayGround.entry("0",  "0"),
                                PlayGround.entry("1",  "1"),
                                PlayGround.entry("2",  "2"),
                                PlayGround.entry("3",  "3"),
                                PlayGround.entry("4",  "4"),
                                PlayGround.entry("5",  "5"))),
                        PlayGround.row("星数", PlayGround.segmented(count,
                                PlayGround.entry("3",  "3"),
                                PlayGround.entry("5",  "5"),
                                PlayGround.entry("7",  "7"),
                                PlayGround.entry("10", "10"))),
                        PlayGround.row("半星", PlayGround.segmented(allowHalf,
                                PlayGround.entry("off", "关"),
                                PlayGround.entry("on",  "开"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("off", "启用"),
                                PlayGround.entry("on",  "禁用")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
    }

    /**
     * 解析数字字符串，超出 [min, max] 范围自动夹紧。NaN/Infinity 退回 fallback。
     */
    private static double parseNum(String v, double fallback, double min, double max) {
        if (v == null || v.isBlank()) return fallback;
        try {
            double d = Double.parseDouble(v.trim());
            if (Double.isNaN(d) || Double.isInfinite(d)) return fallback;
            if (d < min) return min;
            if (d > max) return max;
            return d;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
