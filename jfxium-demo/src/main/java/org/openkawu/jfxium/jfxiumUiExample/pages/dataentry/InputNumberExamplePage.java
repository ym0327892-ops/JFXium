package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.InputNumberAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

/**
 * InputNumber 数字输入框 —— 步进 / 范围 / 精度 / 前后缀。
 *
 * <p>带 +/- 步进按钮的数字输入框，对标 Ant Design InputNumber。</p>
 */
public class InputNumberExamplePage extends VBoxAnt {

    public InputNumberExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("InputNumber 数字输入框")
                .description("带 +/- 步进按钮的数字输入框，支持范围限制、步长、精度、前后缀。")
                .sections(basicSection(), rangeStepSection(), precisionSection(), affixSection(), valueSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                InputNumberAnt.create().value(3).build(),
                InputNumberAnt.create().value(0).disabled().build()
        );
        String code = """
                InputNumberAnt.create().value(3).build();
                InputNumberAnt.create().value(0).disabled().build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "点击 +/- 按钮或直接输入数字；disabled() 禁用。", code, demo);
    }

    private Node rangeStepSection() {
        Node demo = Demos.row(
                InputNumberAnt.create().value(5).min(0).max(10).build(),
                InputNumberAnt.create().value(0).step(5).build()
        );
        String code = """
                // 限制范围 0~10
                InputNumberAnt.create().value(5).min(0).max(10).build();

                // 步长 5（每次 +/- 5）
                InputNumberAnt.create().value(0).step(5).build();
                """;
        return Demos.sectionWithCode("2. 范围与步长",
                "min/max 限制取值范围；step 设置每次步进量。", code, demo);
    }

    private Node precisionSection() {
        Node demo = Demos.row(
                InputNumberAnt.create().value(3.14).step(0.01).precision(2).build()
        );
        String code = """
                // 保留 2 位小数，步长 0.01
                InputNumberAnt.create()
                        .value(3.14)
                        .step(0.01)
                        .precision(2)
                        .build();
                """;
        return Demos.sectionWithCode("3. 小数精度",
                "precision(2) 保留 2 位小数。", code, demo);
    }

    private Node affixSection() {
        Node demo = Demos.row(
                InputNumberAnt.create().value(100).prefix("¥").build(),
                InputNumberAnt.create().value(80).suffix("%").build()
        );
        String code = """
                InputNumberAnt.create().value(100).prefix("¥").build();
                InputNumberAnt.create().value(80).suffix("%").build();
                """;
        return Demos.sectionWithCode("4. 前后缀",
                "prefix/suffix 添加货币符号、单位等。", code, demo);
    }

    /**
     * 5. 获取当前值 —— onChange 回调拿到的是当前数字值（double）。
     *
     * <p>点 +/- 或输入后回车/失焦，onChange 触发，结果 Label 实时显示当前数值。</p>
     */
    private Node valueSection() {
        Label result = TypographyAnt.text("当前值：3").build();
        Node input = InputNumberAnt.create()
                .value(3)
                .onChange(v -> result.setText("当前值：" + v))
                .build();
        Node demo = Demos.column(input, result);
        String code = """
                Label result = TypographyAnt.text("当前值：3").build();
                Node input = InputNumberAnt.create()
                        .value(3)
                        .onChange(v -> result.setText("当前值：" + v))
                        .build();
                Demos.column(input, result);
                """;
        return Demos.sectionWithCode("5. 获取当前值",
                "onChange(v -> ...) 回调给出当前数字值；点 +/- 或输入后回车/失焦即更新结果 Label。",
                code, demo);
    }

    /**
     * 6. 交互演示 —— 通过左侧控件实时改变 InputNumber 的尺寸 / 范围 / 步长 / 禁用状态。
     *
     * <p>InputNumberAnt 无 Controller，所有属性变更均通过 build 重建生效。</p>
     */
    private Node playgroundSection() {
        Binder<String> sizeBinder     = PlayGround.binder("default");
        Binder<String> minBinder      = PlayGround.binder("0");
        Binder<String> maxBinder      = PlayGround.binder("100");
        Binder<String> stepBinder     = PlayGround.binder("1");
        Binder<String> disabledBinder = PlayGround.binder("off");

        Supplier<Node> factory = () -> {
            double min = parseNum(minBinder.get(), 0, -Double.MAX_VALUE, Double.MAX_VALUE);
            double max = parseNum(maxBinder.get(), 100, -Double.MAX_VALUE, Double.MAX_VALUE);
            double step = parseNum(stepBinder.get(), 1, 0.0001, Double.MAX_VALUE);
            return InputNumberAnt.create()
                    .size(parseSize(sizeBinder.get()))
                    .value((min + max) / 2.0)
                    .min(min)
                    .max(max)
                    .step(step)
                    .disabled(parseBool(disabledBinder.get()))
                    .placeholder("数量")
                    .build();
        };

        return Demos.section("6. 交互演示",
                "通过左侧控件实时改变 InputNumber 的尺寸、最小/最大值、步长、禁用状态 —— InputNumberAnt 无 Controller，所有属性变更均通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("最小值", PlayGround.textField(minBinder, "0", "输入最小值")),
                        PlayGround.row("最大值", PlayGround.textField(maxBinder, "100", "输入最大值")),
                        PlayGround.row("步长", PlayGround.textField(stepBinder, "1", "输入步长")),
                        PlayGround.row("禁用", PlayGround.segmented(disabledBinder,
                                PlayGround.entry("off", "启用"),
                                PlayGround.entry("on",  "禁用")))));
    }

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
    }

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

    private static Size parseSize(String v) {
        if (v == null) return Size.DEFAULT;
        return switch (v) {
            case "small" -> Size.SMALL;
            case "large" -> Size.LARGE;
            default      -> Size.DEFAULT;
        };
    }
}
