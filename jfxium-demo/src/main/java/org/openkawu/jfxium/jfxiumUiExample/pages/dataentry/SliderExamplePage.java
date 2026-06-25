package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SliderAnt;

/**
 * Slider 滑块 —— 基础 / 范围 / 禁用 / 交互演示。
 */
public class SliderExamplePage extends VBoxAnt {

    public SliderExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Slider 滑块")
                .description("通过拖动滑块在一个区间内选择值，支持单滑块和双滑块（范围）模式。")
                .sections(basicSection(), rangeSection(), disabledSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node slider = SliderAnt.create()
                .min(0).max(100).value(30)
                .build();
        String code = """
                SliderAnt.create()
                        .min(0).max(100).value(30)
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "默认单滑块，min/max/value 设定范围与初始值。", code, slider);
    }

    private Node rangeSection() {
        Node slider = SliderAnt.create()
                .range()
                .min(0).max(100)
                .defaultValue(new double[]{20, 80})
                .build();
        String code = """
                SliderAnt.create()
                        .range()
                        .min(0).max(100)
                        .defaultValue(new double[]{20, 80})
                        .build();
                """;
        return Demos.sectionWithCode("2. 范围模式", "range() 启用双滑块，defaultValue 设定初始区间。", code, slider);
    }

    private Node disabledSection() {
        Node slider = SliderAnt.create()
                .min(0).max(100).value(50)
                .disabled(true)
                .build();
        String code = """
                SliderAnt.create()
                        .min(0).max(100).value(50)
                        .disabled(true)
                        .build();
                """;
        return Demos.sectionWithCode("3. 禁用状态", "disabled(true) 禁止拖动。", code, slider);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Slider 的 4 个维度：最小值 / 最大值 / 当前值 / 方向。
     *
     * <p>SliderAnt 没有 Controller（没有 controllerOf 入口），且 Builder 字段
     * （min / max / value / vertical）都是 build-time。所以采用
     * {@link PlayGround#rebuildRebuild}：每次值变更都重新 {@code build()}，
     * 重建整个 Slider 节点 —— JavaFX 的 Slider 内部结构简单（HBox + VBox 等几个
     * 容器 + Slider 自身），重建开销极低，且这是唯一能完整覆盖 4 个维度的方式。</p>
     *
     * <p>说明：为了避免 free-form 数字输入的解析复杂度，playground 用「预设值」
     * segmented（如 最小值=0/10/50）而非自由文本框；预设组合保证 min &lt; max
     * 且 value 在 [min, max] 内（SliderAnt.normalizeRange() 也会兜底 clamp）。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> min     = PlayGround.binder("0");
        Binder<String> max     = PlayGround.binder("100");
        Binder<String> value   = PlayGround.binder("50");
        Binder<String> orient  = PlayGround.binder("horizontal");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            double minV = parseNum(min.get(), 0);
            double maxV = parseNum(max.get(), 100);
            if (maxV < minV) maxV = minV;
            double valueV = parseNum(value.get(), 50);
            if (valueV < minV) valueV = minV;
            if (valueV > maxV) valueV = maxV;
            return SliderAnt.create()
                    .min(minV).max(maxV).value(valueV)
                    .vertical("vertical".equals(orient.get()))
                    .build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Slider 的最小值 / 最大值 / 当前值 / 方向 —— SliderAnt 无 Controller，所有属性变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("最小值", PlayGround.segmented(min,
                                PlayGround.entry("0",   "0"),
                                PlayGround.entry("10",  "10"),
                                PlayGround.entry("50",  "50"))),
                        PlayGround.row("最大值", PlayGround.segmented(max,
                                PlayGround.entry("50",   "50"),
                                PlayGround.entry("100",  "100"),
                                PlayGround.entry("200",  "200"),
                                PlayGround.entry("500",  "500"))),
                        PlayGround.row("当前值", PlayGround.segmented(value,
                                PlayGround.entry("0",   "0"),
                                PlayGround.entry("25",  "25"),
                                PlayGround.entry("50",  "50"),
                                PlayGround.entry("75",  "75"),
                                PlayGround.entry("100", "100"))),
                        PlayGround.row("方向", PlayGround.segmented(orient,
                                PlayGround.entry("horizontal", "水平"),
                                PlayGround.entry("vertical",   "垂直")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static double parseNum(String v, double fallback) {
        if (v == null) return fallback;
        try { return Double.parseDouble(v); }
        catch (NumberFormatException e) { return fallback; }
    }
}
