package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.IconAnt;
import org.openkawu.jfxium.component.TooltipAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Tooltip 展示页（M19.9）。
 */
public class TooltipPage implements ShowcasePage {

    @Override public String   key()      { return "tooltip"; }
    @Override public String   title()    { return "Tooltip 文字提示"; }
    @Override public Category category() { return Category.FEEDBACK; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Tooltip 文字提示");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("鼠标悬停时显示的轻量文字提示——补充说明、解释操作语义首选。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionDelay(),
                        sectionOnIcon(),
                        sectionOnLabel()
                )
                .build();
    }

    private Node sectionBasic() {
        Node btn = ButtonAnt.create("悬停我").build();
        TooltipAnt.create("这是一个简单的提示").install(btn);

        return ShowcaseSection.create()
                .title("场景 1：基础用法（install 到任意 Node）")
                .description("TooltipAnt.create(...).install(node) —— 默认 1 秒延迟显示，5 秒自动隐藏")
                .demo(btn)
                .code("""
                        Button btn = ButtonAnt.create("悬停我").build();
                        TooltipAnt.create("这是一个简单的提示").install(btn);
                        """)
                .build();
    }

    private Node sectionDelay() {
        Node fast = ButtonAnt.create("快（200ms）").build();
        TooltipAnt.create("快速显示").delay(Duration.millis(200)).install(fast);

        Node normal = ButtonAnt.create("默认（1s）").build();
        TooltipAnt.create("默认延迟").install(normal);

        Node slow = ButtonAnt.create("慢（2s）").build();
        TooltipAnt.create("慢速显示").delay(Duration.seconds(2)).install(slow);

        HBox row = HBoxBuilder.create().spacing(8).children(fast, normal, slow).build();

        return ShowcaseSection.create()
                .title("场景 2：自定义延迟")
                .description(".delay(Duration) 控制悬停多久后弹出 —— 操作密集场景调小，说明类调大避免打扰")
                .demo(row)
                .code("""
                        TooltipAnt.create("快速").delay(Duration.millis(200)).install(node);
                        TooltipAnt.create("默认").install(node);
                        TooltipAnt.create("慢速").delay(Duration.seconds(2)).install(node);
                        """)
                .build();
    }

    private Node sectionOnIcon() {
        // 没有专用 ? 图标，用 Symbol.INFO 字符代替
        Label info = new Label(IconAnt.Symbol.INFO.getChar());
        info.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px; -fx-cursor: help;");
        TooltipAnt.create("这个字段必填，长度 6–18 字符，包含字母数字").install(info);

        Label withIcon = new Label("API Key");
        withIcon.setGraphic(info);
        withIcon.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        withIcon.setGraphicTextGap(6);

        return ShowcaseSection.create()
                .title("场景 3：挂在帮助图标上（admin 高频）")
                .description("字段标签后跟一个 ⓘ 图标，悬停看详细解释 —— 不抢屏幕空间")
                .demo(withIcon)
                .code("""
                        Label info = new Label(IconAnt.Symbol.INFO.getChar());
                        TooltipAnt.create("详细说明...").install(info);

                        Label withIcon = new Label("API Key");
                        withIcon.setGraphic(info);
                        """)
                .build();
    }

    private Node sectionOnLabel() {
        Label longText = new Label("张三 · 高级管理员 · 2026/05/24 02:18 创建");
        longText.setMaxWidth(180);
        longText.setStyle("-fx-text-overrun: ellipsis;");
        TooltipAnt.create("张三 · 高级管理员 · 2026/05/24 02:18 创建（完整内容）").install(longText);

        return ShowcaseSection.create()
                .title("场景 4：截断文字 + 完整 Tooltip（admin 表格列高频）")
                .description("窄列里文字被截断，悬停查看完整内容 —— 表格 / 卡片次要信息常用")
                .demo(longText)
                .code("""
                        Label cell = new Label(longText);
                        cell.setMaxWidth(180);
                        cell.setStyle("-fx-text-overrun: ellipsis;");
                        TooltipAnt.create(longText).install(cell);
                        """)
                .build();
    }
}
