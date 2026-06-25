package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.function.Supplier;

import org.openkawu.jfxium.component.composite.SpinAnt;
import org.openkawu.jfxium.component.composite.VBarAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.layout.StackPaneAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.core.css.Background;
import org.openkawu.jfxium.core.token.Size;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Spin 加载 —— 基础 / 尺寸 / 提示文字 / overlay 挂载。
 */
public class SpinExamplePage extends VBoxAnt {

    public SpinExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Spin 加载中")
                .description("用于页面或区域的加载状态指示，支持多种动画形态。overlay() 可挂载到任意节点实现区域加载覆盖。")
                .sections(basicSection(), sizeSection(), tipSection(), overlaySection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node spinner = SpinAnt.create().build();
        Node dots = SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).build();
        Node bars = SpinAnt.create().indicator(SpinAnt.Indicator.BARS).build();
        Node demo = Demos.row(spinner, dots, bars);
        String code = """
                SpinAnt.create().build();  // SPINNER（默认）
                SpinAnt.create().indicator(SpinAnt.Indicator.DOTS).build();
                SpinAnt.create().indicator(SpinAnt.Indicator.BARS).build();
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "三种动画形态：SPINNER / DOTS / BARS。", code, demo);
    }

    private Node sizeSection() {
        Node small = SpinAnt.create().size(Size.SMALL).build();
        Node normal = SpinAnt.create().build();
        Node large = SpinAnt.create().size(Size.LARGE).build();
        Node demo = Demos.row(small, normal, large);
        String code = """
                SpinAnt.create().size(Size.SMALL).build();
                SpinAnt.create().build();  // DEFAULT
                SpinAnt.create().size(Size.LARGE).build();
                """;
        return Demos.sectionWithCode("2. 尺寸",
                "SMALL / DEFAULT / LARGE 三档大小。", code, demo);
    }

    private Node tipSection() {
        Node spin = SpinAnt.create().tip("加载中...").build();
        Node spinDots = SpinAnt.create()
                .indicator(SpinAnt.Indicator.DOTS)
                .tip("请稍候")
                .size(Size.LARGE)
                .build();
        Node demo = Demos.row(spin, spinDots);
        String code = """
                SpinAnt.create().tip("加载中...").build();
                SpinAnt.create()
                        .indicator(SpinAnt.Indicator.DOTS)
                        .tip("请稍候")
                        .size(Size.LARGE)
                        .build();
                """;
        return Demos.sectionWithCode("3. 提示文字",
                "tip() 在动画下方显示文字说明。", code, demo);
    }

    private Node overlaySection() {
        VBarAnt content = VBarAnt.create()
                .compact()
                .gap(8)
                .top(
                        TypographyAnt.text("用户列表区域").build(),
                        TypographyAnt.text("这里可以是表格、表单等任意内容").build(),
                        TypographyAnt.text("点击下方按钮模拟加载状态").build()
                )
                .padding(20)
                .background(Background.DEFAULT)
                .build();
        content.setMinHeight(120);

        Node demoArea = StackPaneAnt.create().children(content).build();
        demoArea.getStyleClass().add("jfx-demo-dashed-border");

        // overlay 创建一次，可反复 show/hide
        SpinAnt.Overlay loading = SpinAnt.overlay(content);

        ButtonAnt triggerBtn = ButtonAnt.create("模拟加载").build();
        triggerBtn.setOnAction(e -> {
            loading.show("数据加载中...");
            PauseTransition pt = new PauseTransition(Duration.seconds(2));
            pt.setOnFinished(ev -> loading.hide());
            pt.play();
        });

        Node demo = VBarAnt.create()
                .compact()
                .gap(12)
                .top(demoArea, triggerBtn)
                .build();

        String code = """
                SpinAnt.Overlay loading = SpinAnt.overlay(content);
                loading.show("数据加载中...");
                // ... 异步操作 ...
                loading.hide();
                """;
        return Demos.sectionWithCode("4. overlay 挂载",
                "SpinAnt.overlay(target) 将加载遮罩挂载到任意节点上方，show()/hide() 控制显示。2 秒后自动消失。",
                code, demo);
    }

    // ============================================================
    // 5. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Spin 的 4 个维度：大小 / 指示器 / 提示文字 / 全屏。
     *
     * <p>SpinAnt 是 Builder 模式（{@code build()} 返回 {@link VBox}），
     * 没有 Controller 暴露，所有属性（size / indicator / tip / fullscreen）均为 build-time。
     * 因此采用 {@link PlayGround#rebindRebuild}：每次 binder 变化都重新 build()
     * —— VBox 重建，但开销极小，且保证所有属性变更生效。</p>
     *
     * <p>tip 文本：留空 = 不显示提示文字；非空 = 在动画下方显示。</p>
     */
    private Node playgroundSection() {
        // 1. 状态盒子
        Binder<String> size       = PlayGround.binder("default");
        Binder<String> indicator  = PlayGround.binder("spinner");
        Binder<String> tip        = PlayGround.binder("加载中...");
        Binder<String> fullscreen = PlayGround.binder("false");

        // 2. display 工厂 —— 每次都反映 binder 当前值
        Supplier<Node> factory = () -> SpinAnt.create()
                .size(parseSize(size.get()))
                .indicator(parseIndicator(indicator.get()))
                .tip(tip.get())
                .fullscreen(parseBool(fullscreen.get()))
                .build();

        // 3. 串起来
        return Demos.section("5. 交互演示",
                "通过左侧控件实时改变 Spin 的大小 / 指示器 / 提示文字 / 全屏 —— Builder 无 Controller，所有变更通过 build 重建生效。",
                PlayGround.rebindRebuild(factory, null,
                        PlayGround.row("大小", PlayGround.segmented(size,
                                PlayGround.entry("small",   "小"),
                                PlayGround.entry("default", "默认"),
                                PlayGround.entry("large",   "大"))),
                        PlayGround.row("指示器", PlayGround.segmented(indicator,
                                PlayGround.entry("spinner", "旋转"),
                                PlayGround.entry("dots",   "圆点"),
                                PlayGround.entry("bars",   "长条"))),
                        PlayGround.row("提示文字", PlayGround.textField(tip, tip.get(), "提示文字（留空不显示）")),
                        PlayGround.row("全屏", PlayGround.segmented(fullscreen,
                                PlayGround.entry("false", "普通"),
                                PlayGround.entry("true",  "全屏")))));
    }

    // ============================================================
    // 参数解析 helpers
    // ============================================================

    private static boolean parseBool(String v) {
        return v != null && "true".equalsIgnoreCase(v);
    }

    private static Size parseSize(String v) {
        if (v == null) return Size.DEFAULT;
        return switch (v) {
            case "small"  -> Size.SMALL;
            case "large"  -> Size.LARGE;
            default       -> Size.DEFAULT;
        };
    }

    private static SpinAnt.Indicator parseIndicator(String v) {
        if (v == null) return SpinAnt.Indicator.SPINNER;
        return switch (v) {
            case "dots" -> SpinAnt.Indicator.DOTS;
            case "bars" -> SpinAnt.Indicator.BARS;
            default     -> SpinAnt.Indicator.SPINNER;
        };
    }
}
