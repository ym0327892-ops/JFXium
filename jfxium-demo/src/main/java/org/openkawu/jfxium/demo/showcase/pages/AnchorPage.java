package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.AnchorAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.HBoxBuilder;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Anchor 锚点导航展示页（M19.14）。
 */
public class AnchorPage implements ShowcasePage {

    @Override public String   key()      { return "anchor"; }
    @Override public String   title()    { return "Anchor 锚点"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Anchor 锚点");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("文档内导航 —— 长页面跳转章节，admin 设置/帮助页常用。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionVertical(),
                        sectionHorizontal(),
                        sectionNested(),
                        sectionWithCallback()
                )
                .build();
    }

    private Node sectionVertical() {
        Node a = AnchorAnt.create()
                .item("intro", "简介", "#intro")
                .item("install", "安装", "#install")
                .item("usage", "使用", "#usage")
                .item("api", "API", "#api")
                .activeKey("install")
                .build();
        return ShowcaseSection.create()
                .title("场景 1：垂直方向（默认）")
                .description("左侧栏样式 —— 文档内章节快速跳转")
                .demo(a)
                .code("""
                        AnchorAnt.create()
                            .item("intro", "简介", "#intro")
                            .item("install", "安装", "#install")
                            .activeKey("install")
                            .build();
                        """)
                .build();
    }

    private Node sectionHorizontal() {
        Node a = AnchorAnt.create()
                .item("overview", "概览", "#overview")
                .item("guide", "指南", "#guide")
                .item("api", "API", "#api")
                .item("examples", "示例", "#examples")
                .direction(AnchorAnt.Direction.HORIZONTAL)
                .activeKey("guide")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：水平方向")
                .description("Direction.HORIZONTAL —— 顶栏 tab 风格")
                .demo(a)
                .code("""
                        AnchorAnt.create()
                            .item("overview", "概览", "#overview")
                            .direction(AnchorAnt.Direction.HORIZONTAL)
                            .build();
                        """)
                .build();
    }

    private Node sectionNested() {
        Node a = AnchorAnt.create()
                .item("intro", "简介", "#intro", java.util.List.of(
                        new AnchorAnt.AnchorItem("what", "是什么", "#what"),
                        new AnchorAnt.AnchorItem("why", "为什么", "#why")
                ))
                .item("install", "安装", "#install", java.util.List.of(
                        new AnchorAnt.AnchorItem("npm", "NPM", "#npm"),
                        new AnchorAnt.AnchorItem("maven", "Maven", "#maven")
                ))
                .activeKey("npm")
                .build();
        return ShowcaseSection.create()
                .title("场景 3：嵌套层级")
                .description(".item(key, title, href, children) —— 子条目缩进显示")
                .demo(a)
                .code("""
                        AnchorAnt.create()
                            .item("intro", "简介", "#intro", List.of(
                                new AnchorAnt.AnchorItem("what", "是什么", "#what"),
                                new AnchorAnt.AnchorItem("why", "为什么", "#why")
                            ))
                            .build();
                        """)
                .build();
    }

    private Node sectionWithCallback() {
        Node a = AnchorAnt.create()
                .item("a", "Section A", "#a")
                .item("b", "Section B", "#b")
                .item("c", "Section C", "#c")
                .onChange(key -> MessageAnt.info("跳到：" + key))
                .build();
        return ShowcaseSection.create()
                .title("场景 4：onChange 回调")
                .description(".onChange(key) —— 点击锚点联动滚动到对应区域")
                .demo(a)
                .code("""
                        AnchorAnt.create()
                            .item("a", "Section A", "#a")
                            .onChange(key -> scrollTo(key))
                            .build();
                        """)
                .build();
    }
}
