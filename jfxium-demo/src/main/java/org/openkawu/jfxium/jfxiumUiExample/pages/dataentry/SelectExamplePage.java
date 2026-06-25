package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;
import javafx.scene.control.Label;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ComboBoxAnt;
import org.openkawu.jfxium.core.token.Size;

import java.util.function.Supplier;

/**
 * Select 选择器 —— 基础 / 可编辑 / 禁用+尺寸。
 */
public class SelectExamplePage extends VBoxAnt {

    public SelectExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ComboBox 下拉框")
                .description("下拉选择组件（ComboBoxAnt），支持搜索、多种尺寸。与 Button/Input 等高对齐。")
                .sections(
                        basicSection(),
                        editableSection(),
                        disabledSizeSection(),
                        valueSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                ComboBoxAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .placeholder("请选择")
                        .build()
        );
        String code = """
                ComboBoxAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .placeholder("请选择")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "最简单的下拉选择。", code, demo);
    }

    private Node editableSection() {
        Node demo = Demos.row(
                ComboBoxAnt.<String>create()
                        .items("北京", "上海", "广州", "深圳")
                        .placeholder("可输入搜索")
                        .editable(true)
                        .build()
        );
        String code = """
                ComboBoxAnt.<String>create()
                        .items("北京", "上海", "广州", "深圳")
                        .placeholder("可输入搜索")
                        .editable(true)
                        .build();
                """;
        return Demos.sectionWithCode("2. 可编辑（搜索）",
                "editable(true) 允许用户输入文字过滤选项。",
                code, demo);
    }

    private Node disabledSizeSection() {
        Node demo = Demos.row(
                ComboBoxAnt.<String>create()
                        .items("A", "B", "C")
                        .size(Size.SMALL)
                        .placeholder("Small")
                        .build(),
                ComboBoxAnt.<String>create()
                        .items("A", "B", "C")
                        .placeholder("Default")
                        .build(),
                ComboBoxAnt.<String>create()
                        .items("A", "B", "C")
                        .size(Size.LARGE)
                        .placeholder("Large")
                        .build(),
                ComboBoxAnt.<String>create()
                        .items("A", "B", "C")
                        .disabled(true)
                        .placeholder("禁用")
                        .build()
        );
        String code = """
                ComboBoxAnt.<String>create().items("A", "B", "C")
                        .size(Size.SMALL).placeholder("Small").build();
                ComboBoxAnt.<String>create().items("A", "B", "C")
                        .placeholder("Default").build();
                ComboBoxAnt.<String>create().items("A", "B", "C")
                        .size(Size.LARGE).placeholder("Large").build();
                ComboBoxAnt.<String>create().items("A", "B", "C")
                        .disabled(true).placeholder("禁用").build();
                """;
        return Demos.sectionWithCode("3. 尺寸与禁用",
                "三档尺寸 SMALL / DEFAULT / LARGE；disabled(true) 禁用。",
                code, demo);
    }

    /**
     * 4. 获取选中值 —— 演示 onChange 回调拿到的是用户选中的「值」本身。
     *
     * <p>ComboBoxAnt 的 onChange 回调直接给出选中项的值（这里 items 就是字符串值），
     * 用一个结果 Label 实时显示，用户一眼看到「我刚选了什么」。</p>
     */
    private Node valueSection() {
        Label result = TypographyAnt.text("当前值：(未选择)").build();
        Node cb = ComboBoxAnt.<String>create()
                .items("北京", "上海", "广州", "深圳")
                .placeholder("请选择城市")
                .onChange(v -> result.setText("当前值：" + v))
                .build();
        Node demo = Demos.column(cb, result);
        String code = """
                Label result = TypographyAnt.text("当前值：(未选择)").build();
                ComboBox<String> cb = ComboBoxAnt.<String>create()
                        .items("北京", "上海", "广州", "深圳")
                        .placeholder("请选择城市")
                        .onChange(v -> result.setText("当前值：" + v))
                        .build();
                Demos.column(cb, result);
                """;
        return Demos.sectionWithCode("4. 获取选中值",
                "onChange(v -> ...) 回调直接给出选中的值；下方结果 Label 实时显示当前值。",
                code, demo);
    }

    /**
     * 5. 交互演示 —— 通过左侧控件实时改变 ComboBox 的尺寸 / 可编辑 / 禁用 / 占位符。
     */
    private Node playgroundSection() {
        Binder<String> sizeBinder        = PlayGround.binder("default");
        Binder<String> editableBinder    = PlayGround.binder("off");
        Binder<String> disabledBinder    = PlayGround.binder("off");
        Binder<String> placeholderBinder = PlayGround.binder("请选择");

        Supplier<Node> factory = () -> ComboBoxAnt.<String>create()
                .items("北京", "上海", "广州", "深圳", "杭州", "成都")
                .size(parseSize(sizeBinder.get()))
                .editable(parseBool(editableBinder.get()))
                .disabled(parseBool(disabledBinder.get()))
                .placeholder(placeholderBinder.get().isEmpty() ? "请选择" : placeholderBinder.get())
                .build();

        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 ComboBox 的尺寸、可编辑、禁用、占位符。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("尺寸", PlayGround.segmented(sizeBinder,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("可编辑", PlayGround.segmented(editableBinder,
                                PlayGround.entry("off", "关"),
                                PlayGround.entry("on",  "开"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabledBinder,
                                PlayGround.entry("off", "启用"),
                                PlayGround.entry("on",  "禁用"))),
                        PlayGround.row("占位符", PlayGround.textField(placeholderBinder, "请选择", "输入占位符"))));
    }

    private static boolean parseBool(String v) {
        return "on".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v);
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
