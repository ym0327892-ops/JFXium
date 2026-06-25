package org.openkawu.jfxium.jfxiumUiExample.pages.dataentry;

import javafx.scene.Node;

import org.openkawu.jfxium.component.control.ChoiceBoxAnt;
import org.openkawu.jfxium.component.overlay.MessageAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

import java.util.function.Supplier;

/**
 * ChoiceBox 选择框 —— 基础 / 尺寸 / 禁用 / 回调。
 */
public class ChoiceBoxExamplePage extends VBoxAnt {

    public ChoiceBoxExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("ChoiceBox 选择框")
                .description("轻量级下拉选择控件，比 ComboBox 更精简，适用选项较少的场景（如主题切换、语言选择）。")
                .sections(
                        basicSection(),
                        sizeSection(),
                        disabledSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                Demos.labeled("主题", ChoiceBoxAnt.<String>create()
                        .items("亮色", "暗色", "自动")
                        .value("亮色")
                        .onSelect(v -> MessageAnt.info("选中：" + v))
                        .build()),
                Demos.labeled("语言", ChoiceBoxAnt.<String>create()
                        .items("简体中文", "English", "日本語")
                        .value("简体中文")
                        .build())
        );
        String code = """
                ChoiceBoxAnt.<String>create()
                    .items("亮色", "暗色", "自动")
                    .value("亮色")
                    .onSelect(v -> MessageAnt.info("选中：" + v))
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "items() 设置选项；value() 设置默认选中；onSelect() 监听选中变更。",
                code, demo);
    }

    private Node sizeSection() {
        Node demo = Demos.row(
                ChoiceBoxAnt.<String>create()
                        .items("Small", "Option 2")
                        .value("Small")
                        .size(Size.SMALL)
                        .build(),
                ChoiceBoxAnt.<String>create()
                        .items("Default", "Option 2")
                        .value("Default")
                        .size(Size.DEFAULT)
                        .build(),
                ChoiceBoxAnt.<String>create()
                        .items("Large", "Option 2")
                        .value("Large")
                        .size(Size.LARGE)
                        .build()
        );
        String code = """
                ChoiceBoxAnt.<String>create()
                    .items("Small", "Option 2")
                    .size(Size.SMALL)
                    .build();
                ChoiceBoxAnt.<String>create()
                    .items("Default", "Option 2")
                    .size(Size.DEFAULT)
                    .build();
                ChoiceBoxAnt.<String>create()
                    .items("Large", "Option 2")
                    .size(Size.LARGE)
                    .build();
                """;
        return Demos.sectionWithCode("2. 尺寸",
                "size(SMALL/DEFAULT/LARGE) 三种尺寸，与其他控件一致。",
                code, demo);
    }

    private Node disabledSection() {
        Node demo = Demos.row(
                ChoiceBoxAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .value("选项一")
                        .build(),
                ChoiceBoxAnt.<String>create()
                        .items("选项一", "选项二", "选项三")
                        .value("选项一")
                        .disabled(true)
                        .build()
        );
        String code = """
                ChoiceBoxAnt.<String>create()
                    .items("选项一", "选项二", "选项三")
                    .value("选项一")
                    .build();
                ChoiceBoxAnt.<String>create()
                    .items("选项一", "选项二", "选项三")
                    .disabled(true)
                    .build();
                """;
        return Demos.sectionWithCode("3. 禁用状态",
                "disabled(true) 禁用选择框，单击不弹出下拉。",
                code, demo);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 ChoiceBox 的 3 个维度：尺寸 / 禁用状态 / 默认选中。
     *
     * <p>ChoiceBoxAnt 继承自 JavaFX {@link javafx.scene.control.ChoiceBox}，所有属性
     * （size/disabled/value/items）都是直接修改 this，没有独立 Controller。采用
     * {@link PlayGround#rebindRebuild} —— 每次 binder 变化都重新 {@code create() + 链式 + build()}
     * 生成全新 ChoiceBox 节点。</p>
     *
     * <p>onSelect 回调里弹 Message 提示 —— 验证 rebuild 后回调仍能正常触发。</p>
     */
    private Node playgroundSection() {
        Binder<String> size     = PlayGround.binder("default");  // small / default / large
        Binder<String> disabled = PlayGround.binder("no");       // yes / no
        Binder<String> value    = PlayGround.binder("主题");     // 主题/语言/字体

        Supplier<Node> factory = () -> {
            ChoiceBoxAnt<String> cb = ChoiceBoxAnt.<String>create()
                    .items("主题", "语言", "字体", "字号", "行距")
                    .value(value.get())
                    .size(parseSize(size.get()))
                    .disabled("yes".equals(disabled.get()))
                    .onSelect(v -> MessageAnt.info("选中：" + v));
            return cb.build();
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 ChoiceBox 的尺寸（小/默认/大）、禁用状态、默认选中值 —— 选完后立刻在右侧看到效果。",
                PlayGround.rebindRebuild(factory, "尺寸 / 禁用 / 默认值",
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("no",  "启用"),
                                PlayGround.entry("yes", "禁用"))),
                        PlayGround.row("默认值", PlayGround.segmented(value,
                                PlayGround.entry("主题", "主题"),
                                PlayGround.entry("语言", "语言"),
                                PlayGround.entry("字体", "字体")))));
    }

    private static Size parseSize(String v) {
        if ("small".equals(v)) return Size.SMALL;
        if ("large".equals(v)) return Size.LARGE;
        return Size.DEFAULT;
    }
}
