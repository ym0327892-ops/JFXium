package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.EmptyAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Empty 空状态展示页（M19.12）。
 */
public class EmptyPage implements ShowcasePage {

    @Override public String   key()      { return "empty"; }
    @Override public String   title()    { return "Empty 空状态"; }
    @Override public Category category() { return Category.DATA_DISPLAY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Empty 空状态");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("数据为空时的占位 —— 比「白屏」友好；admin 列表/搜索结果为空标配。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionDefault(),
                        sectionCustomDescription(),
                        sectionWithExtra(),
                        sectionExtraButton()
                )
                .build();
    }

    private Node sectionDefault() {
        Node e = EmptyAnt.create().build();
        return ShowcaseSection.create()
                .title("场景 1：默认（无数据）")
                .description("默认图标 + 默认文案")
                .demo(e)
                .code("""
                        VBox empty = EmptyAnt.create().build();
                        """)
                .build();
    }

    private Node sectionCustomDescription() {
        Node e = EmptyAnt.create("没有匹配的搜索结果").build();
        return ShowcaseSection.create()
                .title("场景 2：自定义文案")
                .description(".create(description) 或 .description(...)")
                .demo(e)
                .code("""
                        EmptyAnt.create("没有匹配的搜索结果").build();
                        """)
                .build();
    }

    private Node sectionWithExtra() {
        Node e = EmptyAnt.create("订单列表为空")
                .extra(ButtonAnt.create("浏览商品").type(ButtonAnt.Type.PRIMARY).build())
                .build();
        return ShowcaseSection.create()
                .title("场景 3：自定义 extra 节点")
                .description(".extra(Node) —— 任意 Node（按钮组、链接、图标 + 文字）")
                .demo(e)
                .code("""
                        EmptyAnt.create("订单列表为空")
                            .extra(ButtonAnt.create("浏览商品").type(Type.PRIMARY).build())
                            .build();
                        """)
                .build();
    }

    private Node sectionExtraButton() {
        Node e = EmptyAnt.create("还没有创建项目")
                .extraButton("立即创建", () -> MessageAnt.success("点击了立即创建"))
                .build();
        return ShowcaseSection.create()
                .title("场景 4：快捷 extra 按钮")
                .description(".extraButton(text, action) —— 一行 API 加 PRIMARY 按钮")
                .demo(e)
                .code("""
                        EmptyAnt.create("还没有创建项目")
                            .extraButton("立即创建", () -> openCreateModal())
                            .build();
                        """)
                .build();
    }
}
