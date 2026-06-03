package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.Button;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.overlay.PopoverAnt;
import org.openkawu.jfxium.component.control.TypographyAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

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
                .sections(clickSection(), hoverSection())
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
}
