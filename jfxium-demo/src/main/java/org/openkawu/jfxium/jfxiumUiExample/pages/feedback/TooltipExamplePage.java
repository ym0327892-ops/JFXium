package org.openkawu.jfxium.jfxiumUiExample.pages.feedback;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.TooltipAnt;
import org.openkawu.jfxium.component.control.ButtonAnt;

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
                .sections(basicSection(), delaySection(), longTextSection())
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
}
