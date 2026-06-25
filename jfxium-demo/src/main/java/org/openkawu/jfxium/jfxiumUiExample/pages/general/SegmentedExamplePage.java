package org.openkawu.jfxium.jfxiumUiExample.pages.general;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SegmentedAnt;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.component.overlay.MessageAnt;

import java.util.function.Supplier;

/**
 * Segmented 分段器 —— 基础 / 禁用 / 块级模式。
 */
public class SegmentedExamplePage extends VBoxAnt {

    public SegmentedExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Segmented 分段器")
                .description("用于在多个选项中切换，类似 Tab 但更紧凑。")
                .sections(
                        basicSection(),
                        disabledSection(),
                        blockSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node segmented = SegmentedAnt.create()
                .option("daily", "日")
                .option("weekly", "周")
                .option("monthly", "月")
                .selected("weekly")
                .onChange(val -> MessageAnt.info("选中: " + val))
                .build();
        String code = """
                Node segmented = SegmentedAnt.create()
                        .option("daily", "日")
                        .option("weekly", "周")
                        .option("monthly", "月")
                        .selected("weekly")
                        .onChange(val -> MessageAnt.info("选中: " + val))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "通过 option() 添加选项，selected() 设置默认值。", code, segmented);
    }

    private Node disabledSection() {
        Node segmented = SegmentedAnt.create()
                .option("opt1", "选项 A")
                .option("opt2", "选项 B")
                .option("opt3", "选项 C")
                .selected("opt1")
                .disabled()
                .build();
        String code = """
                Node segmented = SegmentedAnt.create()
                        .option("opt1", "选项 A")
                        .option("opt2", "选项 B")
                        .option("opt3", "选项 C")
                        .selected("opt1")
                        .disabled()
                        .build();
                """;
        return Demos.sectionWithCode("2. 禁用状态", "disabled() 禁用整个分段器。", code, segmented);
    }

    private Node blockSection() {
        Node segmented = SegmentedAnt.create()
                .option("map", "地图")
                .option("transit", "公交")
                .option("satellite", "卫星")
                .selected("map")
                .block()
                .build();
        String code = """
                Node segmented = SegmentedAnt.create()
                        .option("map", "地图")
                        .option("transit", "公交")
                        .option("satellite", "卫星")
                        .selected("map")
                        .block()
                        .build();
                """;
        return Demos.sectionWithCode("3. 块级模式", "block() 让分段器撑满父容器宽度。", code, segmented);
    }

    /**
     * 交互演示 section（M19.PlayGround）—— rebuild 模式样板。
     *
     * <p>SegmentedAnt 提供了 {@link SegmentedAnt#controllerOf(Node)}，但其 Controller
     * 只暴露 {@code setSelected(String)} —— size / disabled 是 build 期决定的属性，
     * 没有对应的 setSize / setDisabled 可原地修改。故 playground 采用 rebuild 策略：
     * 任何属性变化都重新 build 一个新 Segmented 替换展示区。</p>
     *
     * <p>{@link PlayGround#rebindRebuild} 把这套流程封装成「声明式 API」：
     * 准备 Binder 状态盒子 + 写 displayFactory + 串 row 即可，
     * rebindRebuild 内部会自动提取 binder 注册监听，触发时调 factory.get() 重建。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> size     = PlayGround.binder("default");
        Binder<String> disabled = PlayGround.binder("off");
        Binder<String> selected = PlayGround.binder("daily");

        // 2. display 工厂 —— 每次 binder 变化都重 build
        Supplier<Node> factory = () -> {
            SegmentedAnt.Builder b = SegmentedAnt.create()
                    .option("daily", "日")
                    .option("weekly", "周")
                    .option("monthly", "月")
                    .selected(selected.get())
                    .onChange(val -> MessageAnt.info("选中: " + val));
            b.size(parseSize(size.get()));
            if ("on".equals(disabled.get())) b.disabled();
            return b.build();
        };

        // 3. 串起来 —— rebindRebuild 内部自动提取 binder 并注册监听
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Segmented 的尺寸 / 禁用 / 选中 —— display 整棵重建（size/disabled 是 build 期属性）。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("尺寸", PlayGround.segmented(size,
                                PlayGround.entry("small",   "Small"),
                                PlayGround.entry("default", "Default"),
                                PlayGround.entry("large",   "Large"))),
                        PlayGround.row("禁用", PlayGround.segmented(disabled,
                                PlayGround.entry("off", "正常"),
                                PlayGround.entry("on",  "禁用"))),
                        PlayGround.row("选中", PlayGround.segmented(selected,
                                PlayGround.entry("daily",   "日"),
                                PlayGround.entry("weekly",  "周"),
                                PlayGround.entry("monthly", "月")))));
    }

    // ============================================================
    // 枚举解析 helpers
    // ============================================================

    private static Size parseSize(String v) {
        if (v == null) return Size.DEFAULT;
        return switch (v) {
            case "small" -> Size.SMALL;
            case "large" -> Size.LARGE;
            default      -> Size.DEFAULT;
        };
    }
}
