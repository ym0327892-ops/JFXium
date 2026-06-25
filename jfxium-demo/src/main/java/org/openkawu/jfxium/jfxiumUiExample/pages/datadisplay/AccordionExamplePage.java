package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.geometry.Orientation;
import javafx.scene.Node;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.control.AccordionAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TypographyAnt;

/**
 * Accordion 手风琴 —— CollapseAnt 的互斥折叠快捷入口。
 */
public class AccordionExamplePage extends VBoxAnt {

    public AccordionExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Accordion 手风琴")
                .description("CollapseAnt accordion 模式的快捷入口，始终互斥展开（同时只开一个）。内部 100% 委托 CollapseAnt。")
                .sections(
                        basicSection(),
                        advancedSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = AccordionAnt.create()
                .pane("面板一：基础用法", TypographyAnt.text("这是面板一的内容区域，可以放置任意 Node。").build())
                .pane("面板二：数据处理", VBarAnt.create()
                        .compact()
                        .gap(8)
                        .top(
                                TypographyAnt.text("面板二支持放置多个子节点。").build(),
                                TypographyAnt.text("这里展示了纵向排列的内容。").build()
                        )
                        .build())
                .pane("面板三：其他信息", TypographyAnt.text("面板三 —— 最简单的 Label 内容").build())
                .build();
        String code = """
                AccordionAnt.create()
                    .pane("面板一", TypographyAnt.text("内容一").build())
                    .pane("面板二", VBarAnt.create().compact().top(...).build())
                    .pane("面板三", TypographyAnt.text("内容三").build())
                    .build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "pane(title, content) 逐一添加折叠面板；同一时间只有一个面板展开。",
                code, demo);
    }

    private Node advancedSection() {
        Node demo = AccordionAnt.create()
                .pane("🚀 快速入门",
                        TypographyAnt.text("第一步：引入 Maven 依赖\n第二步：初始化 ThemeManager\n第三步：使用 Builder API 构建 UI")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                .pane("🎨 主题定制",
                        VBarAnt.create()
                                .compact()
                                .gap(8)
                                .top(
                                        TypographyAnt.text("支持 11 套内置主题").build(),
                                        TypographyAnt.text("颜色体系：0-9 阶色板").build(),
                                        TypographyAnt.text("密度：DEFAULT / COMPACT").build()
                                )
                                .build())
                .pane("📦 组件体系",
                        VBarAnt.create()
                                .compact()
                                .gap(8)
                                .top(
                                        TypographyAnt.text("control：29 个原生控件包装").build(),
                                        TypographyAnt.text("composite：44 个组合组件").build(),
                                        TypographyAnt.text("overlay：9 个浮层组件").build()
                                )
                                .build())
                .pane("🔧 高级配置",
                        TypographyAnt.text("自定义 LESS 变量、扩展主题颜色、注入自定义组件等。")
                                .type(TypographyAnt.TextColor.SECONDARY).build())
                .build();
        String code = """
                AccordionAnt.create()
                    .pane("🚀 快速入门", ...)
                    .pane("🎨 主题定制", ...)
                    .pane("📦 组件体系", ...)
                    .pane("🔧 高级配置", ...)
                    .build();
                """;
        return Demos.sectionWithCode("2. FAQ / 文档风格",
                "手风琴非常适合 FAQ、产品文档、设置面板等「分组折叠展开」场景。",
                code, demo);
    }

    // ============================================================
    // 3. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Accordion 的 2 个维度：面板数 / 内容类型。
     *
     * <p>AccordionAnt 是 Builder 模式（{@code build()} 返回 {@link VBox}），
     * 内部委托 {@code CollapseAnt.create().accordion(true)}，没有 Controller 暴露，
     * 所有面板内容均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— VBox 重建，开销可接受，且保证所有属性变更生效。</p>
     *
     * <p>面板数限制在 [2, 6]。内容类型：{@code text} 全部用单行 TypographyAnt；
     * {@code rich} 奇数面板用 VBarAnt 多行布局，偶数面板用单行 TypographyAnt（交错对比）。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> paneCount   = PlayGround.binder("3");
        Binder<String> contentType = PlayGround.binder("text");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            int n = clamp(parseInt(paneCount.get(), 3), 2, 6);
            boolean rich = "rich".equals(contentType.get());

            AccordionAnt.Builder b = AccordionAnt.create();
            for (int i = 0; i < n; i++) {
                String title = "面板 " + (i + 1);
                Node content = (rich && i % 2 == 1)
                        ? buildRichContent(i + 1)
                        : TypographyAnt.text("面板 " + (i + 1) + " 的内容区域。").build();
                b.pane(title, content);
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("3. 交互演示",
                "通过左侧控件实时改变 Accordion 的面板数 / 内容类型 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("面板数", PlayGround.segmented(paneCount,
                                PlayGround.entry("3", "3"),
                                PlayGround.entry("4", "4"),
                                PlayGround.entry("5", "5"))),
                        PlayGround.row("内容类型", PlayGround.segmented(contentType,
                                PlayGround.entry("text", "单行文本"),
                                PlayGround.entry("rich", "富内容（交替）")))));
    }

    /** 复合内容：3 行 TypographyAnt，纵向排列。 */
    private static Node buildRichContent(int idx) {
        return VBarAnt.create()
                .compact()
                .gap(8)
                .top(
                        TypographyAnt.text("面板 " + idx + " —— 标题行").build(),
                        TypographyAnt.text("面板 " + idx + " —— 详细说明").build(),
                        TypographyAnt.text("面板 " + idx + " —— 备注").build()
                )
                .build();
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static int parseInt(String v, int def) {
        if (v == null) return def;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
