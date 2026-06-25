package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TooltipAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

import java.util.function.Supplier;

/**
 * Tooltip 文字提示 —— 基础 / 延迟 / 长文本。
 *
 * <p>鼠标悬停在触发元素上时显示的轻量提示气泡。</p>
 */
public class TooltipExamplePage extends VBoxAnt {

    public TooltipExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Tooltip 文字提示")
                .description("鼠标悬停时显示的简短说明气泡，常用于图标按钮、省略文本的补充说明。")
                .sections(basicSection(), delaySection(), longTextSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Button btn = ButtonAnt.create("悬停看提示").type(ButtonAnt.Type.PRIMARY).build();
        TooltipAnt.create("这是一条 Tooltip 提示").install(btn);
        String code = """
                Button btn = ButtonAnt.create("悬停看提示").type(ButtonAnt.Type.PRIMARY).build();
                TooltipAnt.create("这是一条 Tooltip 提示").install(btn);
                """;
        return Demos.sectionWithCode("1. 基础用法",
                "把鼠标移到按钮上停留片刻即出现提示。install(node) 把提示绑到任意控件。",
                code, Demos.row(btn));
    }

    private Node delaySection() {
        Button fast = ButtonAnt.create("快速显示（0ms）").build();
        TooltipAnt.create("立即弹出").delay(Duration.ZERO).install(fast);

        Button slow = ButtonAnt.create("延迟显示（800ms）").build();
        TooltipAnt.create("停留 0.8 秒后弹出").delay(Duration.millis(800)).install(slow);
        String code = """
                // delay() 控制悬停多久后出现
                TooltipAnt.create("立即弹出").delay(Duration.ZERO).install(fastBtn);
                TooltipAnt.create("停留 0.8 秒后弹出")
                        .delay(Duration.millis(800))
                        .install(slowBtn);
                """;
        return Demos.sectionWithCode("2. 显示延迟",
                "delay() 设置悬停到显示的等待时间。",
                code, Demos.row(fast, slow));
    }

    private Node longTextSection() {
        Button btn = ButtonAnt.create("长文本提示").build();
        TooltipAnt.create("这是一段比较长的提示文本，用于说明 Tooltip 在内容较多时"
                + "会自动换行，气泡宽度受限于内容与最大宽度限制。").install(btn);
        String code = """
                TooltipAnt.create("这是一段比较长的提示文本……自动换行")
                        .install(btn);
                """;
        return Demos.sectionWithCode("3. 长文本",
                "内容较多时气泡自动换行。",
                code, Demos.row(btn));
    }

    // ============================================================
    // 4. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Tooltip 的 4 个维度：提示文本 / 显示延迟 / 持续时间 / 隐藏延迟。
     *
     * <p>TooltipAnt 是 Builder 模式，所有属性都是 build 时确定，build() 返回的是
     * {@link javafx.scene.control.Tooltip}（不是 Node）。采用 {@link PlayGround#rebindRebuild} 模式：
     * 每次 binder 变化都重新创建一个触发按钮 + 全新 Tooltip 并 install 到按钮上 —— 旧按钮从场景树
     * 被替换后，旧 Tooltip 关联的节点引用自然失效，不会造成事件残留。</p>
     *
     * <p><b>说明</b>：JFXium 当前 TooltipAnt 未暴露 placement / trigger 等 Ant Design 语义属性
     * （JavaFX 原生 Tooltip 由系统全局 TooltipBehavior 控制），因此演示聚焦于其真实可控的 4 个
     * 维度：文本 / 显示延迟 / 持续时间 / 隐藏延迟。</p>
     */
    private Node playgroundSection() {
        Binder<String> textBinder     = PlayGround.binder("悬停看提示");
        Binder<String> showDelayBinder = PlayGround.binder("200");  // 0/200/800
        Binder<String> showDurationBinder = PlayGround.binder("5000");  // 1500/5000/10000
        Binder<String> hideDelayBinder = PlayGround.binder("200");  // 0/200/500

        Supplier<Node> factory = () -> {
            String text = textBinder.get();
            if (text == null || text.isBlank()) text = "悬停看提示";
            Duration showDelay = parseMs(showDelayBinder.get(), 200);
            Duration showDuration = parseMs(showDurationBinder.get(), 5000);
            Duration hideDelay = parseMs(hideDelayBinder.get(), 200);

            Button trigger = ButtonAnt.create("悬停我").type(ButtonAnt.Type.PRIMARY).build();
            TooltipAnt.create(text)
                    .delay(showDelay)
                    .duration(showDuration)
                    .hideDelay(hideDelay)
                    .install(trigger);
            return trigger;
        };

        return Demos.section("4. 交互演示",
                "通过左侧控件实时改变 Tooltip 的提示文本、显示延迟（即悬停多久后弹出）、持续时间（弹出多久后自动消失）、隐藏延迟（移开后多久消失）—— 四种状态任意组合，右侧立刻看到效果。",
                PlayGround.rebindRebuild(factory, "文本 / 显示延迟 / 持续时间 / 隐藏延迟",
                        PlayGround.row("提示文本", PlayGround.textField(textBinder, "悬停看提示", "输入提示内容")),
                        PlayGround.row("显示延迟", PlayGround.segmented(showDelayBinder,
                                PlayGround.entry("0",    "立即 (0ms)"),
                                PlayGround.entry("200",  "标准 (200ms)"),
                                PlayGround.entry("800",  "迟缓 (800ms)"))),
                        PlayGround.row("持续时间", PlayGround.segmented(showDurationBinder,
                                PlayGround.entry("1500",  "短 (1.5s)"),
                                PlayGround.entry("5000",  "标准 (5s)"),
                                PlayGround.entry("10000", "长 (10s)"))),
                        PlayGround.row("隐藏延迟", PlayGround.segmented(hideDelayBinder,
                                PlayGround.entry("0",   "立即 (0ms)"),
                                PlayGround.entry("200", "标准 (200ms)"),
                                PlayGround.entry("500", "迟缓 (500ms)")))));
    }

    private static Duration parseMs(String v, int defaultMs) {
        if (v == null) return Duration.millis(defaultMs);
        try {
            int n = Integer.parseInt(v.trim());
            return Duration.millis(Math.max(0, n));
        } catch (NumberFormatException e) {
            return Duration.millis(defaultMs);
        }
    }
}