package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround;
import org.openkawu.jfxium.jfxiumUiExample.util.PlayGround.Binder;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.PopoverAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

import java.util.function.Supplier;

/**
 * Popover 气泡卡片 —— 点击触发 / 悬停触发。
 *
 * <p>PopoverAnt 是浮层型组件：build() 返回 Popover，内部已在 target 上注册触发器，
 * 因此把 target 按钮放进演示区即可，无需额外 .show() 调用。</p>
 */
public class PopoverExamplePage extends VBoxAnt {

    public PopoverExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Popover 气泡卡片")
                .description("点击或悬停元素时弹出带标题的气泡卡片，承载更丰富的内容。")
                .sections(clickSection(), hoverSection(), playgroundSection())
                .padding(24)
                .build());
    }

    private Node clickSection() {
        Button trigger = ButtonAnt.create("点击查看").type(ButtonAnt.Type.PRIMARY).build();
        PopoverAnt.create()
                .title("标题")
                .content(TypographyAnt.text("这是气泡卡片的内容区域。").build())
                .target(trigger)
                .trigger(PopoverAnt.Trigger.CLICK)
                .build();
        String code = """
                Button trigger = ButtonAnt.create("点击查看").type(ButtonAnt.Type.PRIMARY).build();
                PopoverAnt.create()
                        .title("标题")
                        .content(TypographyAnt.text("这是气泡卡片的内容区域。").build())
                        .target(trigger)
                        .trigger(PopoverAnt.Trigger.CLICK)
                        .build();
                """;
        return Demos.sectionWithCode("1. 点击触发",
                "trigger(CLICK) 时点击 target 弹出气泡，再次点击关闭。", code, trigger);
    }

    private Node hoverSection() {
        Button trigger = ButtonAnt.create("悬停查看").build();
        PopoverAnt.create()
                .title("提示")
                .content(TypographyAnt.text("鼠标移入即显示，移出自动隐藏。").build())
                .target(trigger)
                .trigger(PopoverAnt.Trigger.HOVER)
                .build();
        String code = """
                PopoverAnt.create()
                        .title("提示")
                        .content(TypographyAnt.text("鼠标移入即显示，移出自动隐藏。").build())
                        .target(trigger)
                        .trigger(PopoverAnt.Trigger.HOVER)
                        .build();
                """;
        return Demos.sectionWithCode("2. 悬停触发",
                "trigger(HOVER) 时鼠标移入显示，移出自动隐藏。", code, trigger);
    }

    // ============================================================
    // 3. 交互演示 (PlayGround)
    // ============================================================

    /**
     * 可交互演示区 —— 实时改变 Popover 的 2 个维度：触发方式 / 弹出位置。
     *
     * <p>PopoverAnt 是个特殊 overlay：{@code build()} 不返回 Node，而是返回 Popover 对象
     * （内部在 target 上注册了鼠标监听器）。PlayGround 重建时会生成新的 trigger 按钮
     * + 重新创建 Popover 重新注册监听 —— 旧按钮被 GC 后其上的监听器随之失效，
     * 不会产生「旧 Popover 残留悬浮」的副作用。</p>
     */
    private Node playgroundSection() {
        Binder<String> trigger   = PlayGround.binder("click");  // click / hover
        Binder<String> placement = PlayGround.binder("top");    // top / bottom

        Supplier<Node> factory = () -> {
            Button btn = ButtonAnt.create("触发气泡").type(ButtonAnt.Type.PRIMARY).build();
            PopoverAnt.create()
                    .title("交互演示")
                    .content(TypographyAnt.text("位置 + 触发方式都可通过左侧控件实时调整。").build())
                    .target(btn)
                    .trigger("hover".equals(trigger.get())
                            ? PopoverAnt.Trigger.HOVER
                            : PopoverAnt.Trigger.CLICK)
                    .placement("top".equals(placement.get()) ? Pos.TOP_CENTER : Pos.BOTTOM_CENTER)
                    .build();
            return btn;
        };

        return Demos.section("3. 交互演示",
                "通过左侧控件实时改变 Popover 的触发方式（点击/悬停）和弹出位置（上/下） —— 每次切换都重新生成 trigger 按钮 + 重新注册监听器。",
                PlayGround.rebindRebuild(factory, "触发 / 位置",
                        PlayGround.row("触发方式", PlayGround.segmented(trigger,
                                PlayGround.entry("click", "点击"),
                                PlayGround.entry("hover", "悬停"))),
                        PlayGround.row("弹出位置", PlayGround.segmented(placement,
                                PlayGround.entry("top",    "上方"),
                                PlayGround.entry("bottom", "下方")))));
    }
}
