package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.CollapseAnt;

/**
 * Collapse 折叠面板 —— 基础 / 手风琴 / 禁用面板。
 */
public class CollapseExamplePage extends VBoxAnt {

    public CollapseExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Collapse 折叠面板")
                .description("可以折叠/展开的内容区域，用于将复杂内容分组收纳。")
                .sections(basicSection(), accordionSection(), disabledSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node collapse = CollapseAnt.create()
                .panel("1", "面板一", TypographyAnt.text("这是面板一的内容。").build())
                .panel("2", "面板二", TypographyAnt.text("这是面板二的内容。").build())
                .panel("3", "面板三", TypographyAnt.text("这是面板三的内容。").build())
                .activeKey("1")
                .build();
        String code = """
                CollapseAnt.create()
                        .panel("1", "面板一", TypographyAnt.text("这是面板一的内容。").build())
                        .panel("2", "面板二", TypographyAnt.text("这是面板二的内容。").build())
                        .panel("3", "面板三", TypographyAnt.text("这是面板三的内容。").build())
                        .activeKey("1")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "多个面板可同时展开，activeKey 设置默认展开项。", code, collapse);
    }

    private Node accordionSection() {
        Node collapse = CollapseAnt.create()
                .accordion()
                .panel("a", "手风琴 A", TypographyAnt.text("只能展开一个面板。").build())
                .panel("b", "手风琴 B", TypographyAnt.text("展开 B 时 A 自动收起。").build())
                .panel("c", "手风琴 C", TypographyAnt.text("互斥展开模式。").build())
                .activeKey("a")
                .build();
        String code = """
                CollapseAnt.create()
                        .accordion()
                        .panel("a", "手风琴 A", TypographyAnt.text("只能展开一个面板。").build())
                        .panel("b", "手风琴 B", TypographyAnt.text("展开 B 时 A 自动收起。").build())
                        .panel("c", "手风琴 C", TypographyAnt.text("互斥展开模式。").build())
                        .activeKey("a")
                        .build();
                """;
        return Demos.sectionWithCode("2. 手风琴模式",
                "accordion() 启用互斥展开，同一时间只有一个面板打开。", code, collapse);
    }

    private Node disabledSection() {
        Node collapse = CollapseAnt.create()
                .panel("1", "可用面板", TypographyAnt.text("正常交互。").build())
                .panel("2", "禁用面板", TypographyAnt.text("无法展开。").build(), true)
                .activeKey("1")
                .build();
        String code = """
                CollapseAnt.create()
                        .panel("1", "可用面板", TypographyAnt.text("正常交互。").build())
                        .panel("2", "禁用面板", TypographyAnt.text("无法展开。").build(), true)
                        .activeKey("1")
                        .build();
                """;
        return Demos.sectionWithCode("3. 禁用面板",
                "panel 第四个参数 disabled=true 禁止该面板展开/收起。", code, collapse);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Collapse 的 3 个维度：模式（普通/手风琴）/ 面板数 / 默认展开。
     *
     * <p>CollapseAnt 是 Builder 模式（{@code build()} 返回 {@link VBox}），
     * 没有 Controller 暴露，所有面板内容与属性均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— VBox 重建，开销可接受，且保证所有属性变更生效。</p>
     *
     * <p>面板数限制在 [1, 9]，默认展开索引被 clamp 到 [0, panelCount-1] 防止越界。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> mode         = PlayGround.binder("normal");
        Binder<String> panelCount   = PlayGround.binder("3");
        Binder<String> defaultActive = PlayGround.binder("0");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            int n = clamp(parseInt(panelCount.get(), 3), 1, 9);
            int activeIdx = clamp(parseInt(defaultActive.get(), 0), 0, n - 1);

            CollapseAnt.Builder b = CollapseAnt.create();
            for (int i = 0; i < n; i++) {
                b.panel(String.valueOf(i),
                        "面板 " + (i + 1),
                        TypographyAnt.text("面板 " + (i + 1) + " 的内容区域。").build());
            }
            if (parseBool(mode.get())) {
                b.accordion();
            }
            b.activeKey(String.valueOf(activeIdx));
            return b.build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Collapse 的模式 / 面板数 / 默认展开 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("模式", PlayGround.segmented(mode,
                                PlayGround.entry("normal",    "普通"),
                                PlayGround.entry("accordion", "手风琴"))),
                        PlayGround.row("面板数", PlayGround.segmented(panelCount,
                                PlayGround.entry("3", "3"),
                                PlayGround.entry("5", "5"),
                                PlayGround.entry("7", "7"))),
                        PlayGround.row("默认展开", PlayGround.segmented(defaultActive,
                                PlayGround.entry("0", "0"),
                                PlayGround.entry("1", "1"),
                                PlayGround.entry("2", "2")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return v != null && "true".equalsIgnoreCase(v);
    }

    private static int parseInt(String v, int def) {
        if (v == null) return def;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
