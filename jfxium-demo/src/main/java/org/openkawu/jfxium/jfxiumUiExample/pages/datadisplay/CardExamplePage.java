package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;
import javafx.scene.control.Label;


import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.control.ButtonAnt;
import org.openkawu.jfxium.component.composite.CardAnt;

/**
 * Card 卡片 —— 基础 / 边框+阴影 / 悬停 / extra+操作。
 */
public class CardExamplePage extends VBoxAnt {

    public CardExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Card 卡片")
                .description("通用的内容容器卡片，支持标题、边框、阴影、悬停效果。")
                .sections(
                        basicSection(),
                        borderedShadowSection(),
                        hoverableSection(),
                        extraSection()
                )
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                CardAnt.create()
                        .title("卡片标题")
                        .content(new Label("这是卡片内容区域。"))
                        .build()
        );
        String code = """
                CardAnt.create()
                        .title("卡片标题")
                        .content(new Label("这是卡片内容区域。"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础卡片", "带标题的简单卡片。", code, demo);
    }

    private Node borderedShadowSection() {
        Node demo = Demos.row(
                CardAnt.create()
                        .title("有边框")
                        .content(new Label("bordered(true)"))
                        .bordered(true)
                        .build(),
                CardAnt.create()
                        .title("中等阴影")
                        .content(new Label("shadow(MEDIUM)"))
                        .shadow(CardAnt.Shadow.MEDIUM)
                        .build()
        );
        String code = """
                CardAnt.create().title("有边框")
                        .content(new Label("bordered(true)"))
                        .bordered(true).build();
                CardAnt.create().title("中等阴影")
                        .content(new Label("shadow(MEDIUM)"))
                        .shadow(CardAnt.Shadow.MEDIUM).build();
                """;
        return Demos.sectionWithCode("2. 边框与阴影",
                "bordered(true) 显示边框；shadow(MEDIUM) 添加阴影。",
                code, demo);
    }

    private Node hoverableSection() {
        Node demo = Demos.row(
                CardAnt.create()
                        .title("悬停效果")
                        .content(new Label("鼠标悬停时卡片浮起。"))
                        .hoverable(true)
                        .bordered(true)
                        .build()
        );
        String code = """
                CardAnt.create()
                        .title("悬停效果")
                        .content(new Label("鼠标悬停时卡片浮起。"))
                        .hoverable(true)
                        .bordered(true)
                        .build();
                """;
        return Demos.sectionWithCode("3. 悬停浮起",
                "hoverable(true) 鼠标悬停时卡片有浮起效果。",
                code, demo);
    }

    private Node extraSection() {
        Node extraBtn = ButtonAnt.create("更多").type(ButtonAnt.Type.LINK).build();
        Node demo = Demos.row(
                CardAnt.create()
                        .title("带操作的卡片")
                        .extra(extraBtn)
                        .content(new Label("extra(node) 在标题右侧放置操作按钮。"))
                        .bordered(true)
                        .build()
        );
        String code = """
                Node extraBtn = ButtonAnt.create("更多")
                        .type(ButtonAnt.Type.LINK).build();
                CardAnt.create()
                        .title("带操作的卡片")
                        .extra(extraBtn)
                        .content(new Label("标题右侧的操作区。"))
                        .bordered(true)
                        .build();
                """;
        return Demos.sectionWithCode("4. Extra 操作区",
                "extra(node) 在卡片标题栏右侧放置额外操作。",
                code, demo);
    }
}
