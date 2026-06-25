package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.TimelineAnt;

/**
 * Timeline 时间轴 —— 基础 / 彩色圆点 / 交替模式。
 */
public class TimelineExamplePage extends VBoxAnt {

    public TimelineExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Timeline 时间轴")
                .description("垂直展示的时间流信息，可用于记录事件历程。")
                .sections(
                        basicSection(),
                        colorSection(),
                        alternateSection(),
                        playgroundSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node timeline = TimelineAnt.create()
                .item("创建项目 2024-01-01")
                .item("完成初始化 2024-01-05")
                .item("发布 v1.0 2024-02-01")
                .item("用户突破 1000 2024-03-15")
                .build();
        String code = """
                Node timeline = TimelineAnt.create()
                        .item("创建项目 2024-01-01")
                        .item("完成初始化 2024-01-05")
                        .item("发布 v1.0 2024-02-01")
                        .item("用户突破 1000 2024-03-15")
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础用法", "简单的时间轴。", code, timeline);
    }

    private Node colorSection() {
        Node timeline = TimelineAnt.create()
                .item("成功步骤", TimelineAnt.DotColor.GREEN)
                .item("进行中", TimelineAnt.DotColor.BLUE)
                .item("警告事件", TimelineAnt.DotColor.RED)
                .item("等待中", TimelineAnt.DotColor.GRAY)
                .build();
        String code = """
                Node timeline = TimelineAnt.create()
                        .item("成功步骤", TimelineAnt.DotColor.GREEN)
                        .item("进行中", TimelineAnt.DotColor.BLUE)
                        .item("警告事件", TimelineAnt.DotColor.RED)
                        .item("等待中", TimelineAnt.DotColor.GRAY)
                        .build();
                """;
        return Demos.sectionWithCode("2. 彩色圆点", "通过 DotColor 设置不同颜色表示状态。", code, timeline);
    }

    private Node alternateSection() {
        Node timeline = TimelineAnt.create()
                .mode(TimelineAnt.Mode.ALTERNATE)
                .item("需求评审", "2024-01-10")
                .item("开发完成", "2024-02-20")
                .item("测试通过", "2024-03-01")
                .item("正式上线", "2024-03-15")
                .build();
        String code = """
                Node timeline = TimelineAnt.create()
                        .mode(TimelineAnt.Mode.ALTERNATE)
                        .item("需求评审", "2024-01-10")
                        .item("开发完成", "2024-02-20")
                        .item("测试通过", "2024-03-01")
                        .item("正式上线", "2024-03-15")
                        .build();
                """;
        return Demos.sectionWithCode("3. 交替模式", "mode(ALTERNATE) 让内容左右交替展示。", code, timeline);
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Timeline 的 4 个维度：模式 / 圆点颜色 / 待处理 / 条目数。
     *
     * <p>TimelineAnt 是 Builder 模式（{@code build()} 返回 {@link VBox}），
     * 没有 Controller 暴露，所有属性（mode / dotColor / pending / item）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— VBox 重建，开销可接受，且保证所有属性变更生效。</p>
     *
     * <p>圆点颜色用 {@code item(String, DotColor)} 重载一次性设置；条目数限制在 [2, 8]。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> mode      = PlayGround.binder("left");
        Binder<String> dotColor  = PlayGround.binder("blue");
        Binder<String> pending   = PlayGround.binder("false");
        Binder<String> itemCount = PlayGround.binder("4");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> {
            int n = clamp(parseInt(itemCount.get(), 4), 2, 8);
            TimelineAnt.DotColor color = parseDotColor(dotColor.get());

            TimelineAnt.Builder b = TimelineAnt.create().mode(parseMode(mode.get()));
            for (int i = 0; i < n; i++) {
                b.item("步骤 " + (i + 1) + " —— 时间轴事件描述", color);
            }
            if (parseBool(pending.get())) {
                b.pending("加载中...");
            }
            return b.build();
        };

        // 3. 串起来
        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Timeline 的模式 / 圆点颜色 / 待处理 / 条目数 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("模式", PlayGround.segmented(mode,
                                PlayGround.entry("left",      "左侧"),
                                PlayGround.entry("right",     "右侧"),
                                PlayGround.entry("alternate", "交替"))),
                        PlayGround.row("圆点颜色", PlayGround.segmented(dotColor,
                                PlayGround.entry("blue",  "蓝"),
                                PlayGround.entry("red",   "红"),
                                PlayGround.entry("green", "绿"),
                                PlayGround.entry("gray",  "灰"))),
                        PlayGround.row("待处理", PlayGround.segmented(pending,
                                PlayGround.entry("false", "隐藏"),
                                PlayGround.entry("true",  "显示"))),
                        PlayGround.row("条目数", PlayGround.segmented(itemCount,
                                PlayGround.entry("3", "3"),
                                PlayGround.entry("5", "5"),
                                PlayGround.entry("7", "7")))));
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

    private static TimelineAnt.Mode parseMode(String v) {
        if (v == null) return TimelineAnt.Mode.LEFT;
        return switch (v) {
            case "right"     -> TimelineAnt.Mode.RIGHT;
            case "alternate" -> TimelineAnt.Mode.ALTERNATE;
            default          -> TimelineAnt.Mode.LEFT;
        };
    }

    private static TimelineAnt.DotColor parseDotColor(String v) {
        if (v == null) return TimelineAnt.DotColor.BLUE;
        return switch (v) {
            case "red"   -> TimelineAnt.DotColor.RED;
            case "green" -> TimelineAnt.DotColor.GREEN;
            case "gray"  -> TimelineAnt.DotColor.GRAY;
            default      -> TimelineAnt.DotColor.BLUE;
        };
    }
}
