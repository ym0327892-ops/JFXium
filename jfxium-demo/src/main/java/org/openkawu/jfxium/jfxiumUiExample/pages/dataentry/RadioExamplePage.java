package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.RadioButtonAnt;
import org.openkawu.jfxium.core.token.Size;

/**
 * Radio 单选框 —— 基础 / 形状 / 禁用 / 分组。
 */
public class RadioExamplePage extends VBoxAnt {

    public RadioExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Radio 单选框")
                .description("在一组选项中选择一个，使用 ToggleGroup 实现互斥。")
                .sections(basicSection(), shapeSection(), disabledSection(), groupSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        ToggleGroup group = new ToggleGroup();
        RadioButton r1 = RadioButtonAnt.create("选项 A").toggleGroup(group).selected(true).build();
        RadioButton r2 = RadioButtonAnt.create("选项 B").toggleGroup(group).build();
        Node demo = Demos.row(r1, r2);
        String code = """
                ToggleGroup group = new ToggleGroup();
                RadioButton r1 = RadioButtonAnt.create("选项 A")
                        .toggleGroup(group).selected(true).build();
                RadioButton r2 = RadioButtonAnt.create("选项 B")
                        .toggleGroup(group).build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "通过 ToggleGroup 实现单选互斥。", code, demo);
    }

    private Node shapeSection() {
        ToggleGroup g1 = new ToggleGroup();
        RadioButton circle = RadioButtonAnt.create("圆形（默认）")
                .toggleGroup(g1).selected(true).build();
        ToggleGroup g2 = new ToggleGroup();
        RadioButton square = RadioButtonAnt.create("方形 SQUARE")
                .toggleGroup(g2).shape(RadioButtonAnt.Shape.SQUARE).selected(true).build();
        ToggleGroup g3 = new ToggleGroup();
        RadioButton rounded = RadioButtonAnt.create("圆角 ROUNDED")
                .toggleGroup(g3).shape(RadioButtonAnt.Shape.ROUNDED).selected(true).build();
        Node demo = Demos.row(circle, square, rounded);
        String code = """
                // shape 控制选择框外形
                RadioButtonAnt.create("圆形（默认）").toggleGroup(g1).build();
                RadioButtonAnt.create("方形 SQUARE")
                        .shape(RadioButtonAnt.Shape.SQUARE).build();
                RadioButtonAnt.create("圆角 ROUNDED")
                        .shape(RadioButtonAnt.Shape.ROUNDED).build();
                """;
        return Demos.sectionWithCode("2. 形状 Shape",
                "DEFAULT（圆形）/ SQUARE（方形）/ ROUNDED（圆角方形）。",
                code, demo);
    }

    private Node disabledSection() {
        RadioButton r1 = RadioButtonAnt.create("禁用未选").disabled(true).build();
        RadioButton r2 = RadioButtonAnt.create("禁用已选").selected(true).disabled(true).build();
        Node demo = Demos.row(r1, r2);
        String code = """
                RadioButtonAnt.create("禁用未选").disabled(true).build();
                RadioButtonAnt.create("禁用已选").selected(true).disabled(true).build();
                """;
        return Demos.sectionWithCode("3. 禁用状态", "disabled(true) 禁用交互。", code, demo);
    }

    private Node groupSection() {
        ToggleGroup sizeGroup = new ToggleGroup();
        RadioButton s = RadioButtonAnt.create("Small").toggleGroup(sizeGroup).build();
        RadioButton m = RadioButtonAnt.create("Medium").toggleGroup(sizeGroup).selected(true).build();
        RadioButton l = RadioButtonAnt.create("Large").toggleGroup(sizeGroup).build();
        Node demo = Demos.row(s, m, l);
        String code = """
                ToggleGroup sizeGroup = new ToggleGroup();
                RadioButton s = RadioButtonAnt.create("Small").toggleGroup(sizeGroup).build();
                RadioButton m = RadioButtonAnt.create("Medium")
                        .toggleGroup(sizeGroup).selected(true).build();
                RadioButton l = RadioButtonAnt.create("Large").toggleGroup(sizeGroup).build();
                """;
        return Demos.sectionWithCode("4. 单选组", "多个 RadioButton 共享同一个 ToggleGroup。", code, demo);
    }

    // ============================================================
    // 5. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 RadioButton 的 5 个维度：大小 / 形状 / 文字 / 状态 / 禁用。
     *
     * <p>RadioButtonAnt 是继承式 API（{@code extends RadioButton}），且没有 Controller 暴露，
     * 所有属性（size / shape / disabled / selected / text）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— RadioButton 节点重建，但开销极小（一个 Node），且保证所有属性变更生效。</p>
     *
     * <p>注：单选互斥依靠外部 ToggleGroup；此处展示单体 RadioButton 的属性变更，
     * 互斥逻辑可参考第 4 节 groupSection。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> size     = PlayGround.binder("default");
        Binder<String> shape    = PlayGround.binder("default");
        Binder<String> text     = PlayGround.binder("选项 A");
        Binder<String> state    = PlayGround.binder("unchecked");
        Binder<String> disabled = PlayGround.binder("false");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            RadioButtonAnt rb = RadioButtonAnt.create(text.get());
            applySize(rb, size.get());
            applyShape(rb, shape.get());
            rb.selected("checked".equals(state.get()));
            rb.disabled(parseBool(disabled.get()));
            return rb.build();
        };

        // 3. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 RadioButton 的大小 / 形状 / 文字 / 状态 / 禁用 —— RadioButtonAnt 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("大小", PlayGround.segmented(size,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("形状", PlayGround.segmented(shape,
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("square",  "方形"),
                                PlayGround.entry("rounded", "圆角"))),
                        PlayGround.row("文字", PlayGround.textField(text, text.get(), "Radio 文字")),
                        PlayGround.row("状态", PlayGround.segmented(state,
                                PlayGround.entry("unchecked", "未选"),
                                PlayGround.entry("checked",   "已选"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("false", "启用"),
                                PlayGround.entry("true",  "禁用")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return v != null && "true".equalsIgnoreCase(v);
    }

    private static void applySize(RadioButtonAnt rb, String v) {
        if (v == null) { rb.size(Size.DEFAULT); return; }
        switch (v) {
            case "small"  -> rb.size(Size.SMALL);
            case "large"  -> rb.size(Size.LARGE);
            default       -> rb.size(Size.DEFAULT);
        }
    }

    private static void applyShape(RadioButtonAnt rb, String v) {
        if (v == null) { rb.shape(RadioButtonAnt.Shape.DEFAULT); return; }
        switch (v) {
            case "square"  -> rb.shape(RadioButtonAnt.Shape.SQUARE);
            case "rounded" -> rb.shape(RadioButtonAnt.Shape.ROUNDED);
            default        -> rb.shape(RadioButtonAnt.Shape.DEFAULT);
        }
    }
}
