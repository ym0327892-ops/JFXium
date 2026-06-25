package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.CheckBoxAnt;
import org.openkawu.jfxium.core.token.Size;

/**
 * Checkbox 复选框 —— 基础 / 禁用 / 形状。
 */
public class CheckboxExamplePage extends VBoxAnt {

    public CheckboxExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Checkbox 复选框")
                .description("多选场景下的勾选控件，支持选中、半选、禁用等状态。")
                .sections(basicSection(), disabledSection(), shapeSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        CheckBox cb1 = CheckBoxAnt.create("苹果").build();
        CheckBox cb2 = CheckBoxAnt.create("香蕉").selected(true).build();
        CheckBox cb3 = CheckBoxAnt.create("橙子").build();
        Node demo = Demos.row(cb1, cb2, cb3);
        String code = """
                CheckBoxAnt.create("苹果").build();
                CheckBoxAnt.create("香蕉").selected(true).build();
                CheckBoxAnt.create("橙子").build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "最简单的多选框。", code, demo);
    }

    private Node disabledSection() {
        CheckBox cb1 = CheckBoxAnt.create("禁用未选").disabled(true).build();
        CheckBox cb2 = CheckBoxAnt.create("禁用已选").selected(true).disabled(true).build();
        CheckBox cb3 = CheckBoxAnt.create("半选状态").indeterminate(true).disabled(true).build();
        Node demo = Demos.row(cb1, cb2, cb3);
        String code = """
                CheckBoxAnt.create("禁用未选").disabled(true).build();
                CheckBoxAnt.create("禁用已选").selected(true).disabled(true).build();
                CheckBoxAnt.create("半选状态").indeterminate(true).disabled(true).build();
                """;
        return Demos.sectionWithCode("2. 禁用状态", "disabled(true) 禁用交互；indeterminate 半选态。", code, demo);
    }

    private Node shapeSection() {
        CheckBox cb1 = CheckBoxAnt.create("默认").selected(true).build();
        CheckBox cb2 = CheckBoxAnt.create("圆形").selected(true).shape(CheckBoxAnt.Shape.CIRCLE).build();
        CheckBox cb3 = CheckBoxAnt.create("方形").selected(true).shape(CheckBoxAnt.Shape.SQUARE).build();
        CheckBox cb4 = CheckBoxAnt.create("大圆角").selected(true).shape(CheckBoxAnt.Shape.ROUNDED).build();
        Node demo = Demos.row(cb1, cb2, cb3, cb4);
        String code = """
                CheckBoxAnt.create("默认").selected(true).build();
                CheckBoxAnt.create("圆形").selected(true).shape(CheckBoxAnt.Shape.CIRCLE).build();
                CheckBoxAnt.create("方形").selected(true).shape(CheckBoxAnt.Shape.SQUARE).build();
                CheckBoxAnt.create("大圆角").selected(true).shape(CheckBoxAnt.Shape.ROUNDED).build();
                """;
        return Demos.sectionWithCode("3. 形状 Shape",
                "DEFAULT / CIRCLE / SQUARE / ROUNDED 四种外形。", code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 CheckBox 的 5 个维度：大小 / 形状 / 文字 / 状态 / 禁用。
     *
     * <p>CheckBoxAnt 是继承式 API（{@code extends CheckBox}），且没有 Controller 暴露，
     * 所有属性（size / shape / disabled / indeterminate / text）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— CheckBox 节点重建，但开销极小（一个 Node），且保证所有属性变更生效。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> size     = PlayGround.binder("default");
        Binder<String> shape    = PlayGround.binder("default");
        Binder<String> text     = PlayGround.binder("Apple");
        Binder<String> state    = PlayGround.binder("unchecked");
        Binder<String> disabled = PlayGround.binder("false");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            CheckBoxAnt cb = CheckBoxAnt.create(text.get());
            applySize(cb, size.get());
            applyShape(cb, shape.get());
            applyState(cb, state.get());
            cb.disabled(parseBool(disabled.get()));
            return cb.build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 CheckBox 的大小 / 形状 / 文字 / 状态 / 禁用 —— CheckBoxAnt 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("大小", PlayGround.segmented(size,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("形状", PlayGround.segmented(shape,
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("circle",  "圆形"),
                                PlayGround.entry("square",  "方形"),
                                PlayGround.entry("rounded", "大圆角"))),
                        PlayGround.row("文字", PlayGround.textField(text, text.get(), "CheckBox 文字")),
                        PlayGround.row("状态", PlayGround.segmented(state,
                                PlayGround.entry("unchecked",     "未选"),
                                PlayGround.entry("checked",       "已选"),
                                PlayGround.entry("indeterminate", "半选"))),
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

    private static void applySize(CheckBoxAnt cb, String v) {
        if (v == null) { cb.size(Size.DEFAULT); return; }
        switch (v) {
            case "small"  -> cb.size(Size.SMALL);
            case "large"  -> cb.size(Size.LARGE);
            default       -> cb.size(Size.DEFAULT);
        }
    }

    private static void applyShape(CheckBoxAnt cb, String v) {
        if (v == null) { cb.shape(CheckBoxAnt.Shape.DEFAULT); return; }
        switch (v) {
            case "circle"  -> cb.shape(CheckBoxAnt.Shape.CIRCLE);
            case "square"  -> cb.shape(CheckBoxAnt.Shape.SQUARE);
            case "rounded" -> cb.shape(CheckBoxAnt.Shape.ROUNDED);
            default        -> cb.shape(CheckBoxAnt.Shape.DEFAULT);
        }
    }

    private static void applyState(CheckBoxAnt cb, String v) {
        if ("checked".equals(v)) {
            cb.selected(true).indeterminate(false).allowIndeterminate(false);
        } else if ("indeterminate".equals(v)) {
            cb.selected(false).indeterminate(true).allowIndeterminate(true);
        } else {
            cb.selected(false).indeterminate(false).allowIndeterminate(false);
        }
    }
}
