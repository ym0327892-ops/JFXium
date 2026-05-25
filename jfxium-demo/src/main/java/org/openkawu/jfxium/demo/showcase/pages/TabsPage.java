package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.ButtonAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.component.TabsAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Tabs 标签页展示页（M19.10）。
 */
public class TabsPage implements ShowcasePage {

    @Override public String   key()      { return "tabs"; }
    @Override public String   title()    { return "Tabs 标签页"; }
    @Override public Category category() { return Category.NAVIGATION; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Tabs 标签页");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("两种类型（LINE / CARD）+ 三档尺寸 + 4 个位置 + 居中 + 切换回调。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionTypes(),
                        sectionSizes(),
                        sectionPlacement(),
                        sectionWithExtra()
                )
                .build();
    }

    private Node panel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-padding: 16; -fx-text-fill: -color-fg-muted;");
        return l;
    }

    private Node sectionBasic() {
        Node tabs = TabsAnt.create()
                .tab("a", "标签 A", panel("Tab A 的内容"))
                .tab("b", "标签 B", panel("Tab B 的内容"))
                .tab("c", "标签 C", panel("Tab C 的内容"))
                .onChange(key -> MessageAnt.info("切到：" + key))
                .build();

        return ShowcaseSection.create()
                .title("场景 1：基础用法")
                .description(".tab(key, label, content) + .onChange(key) —— 类似 Element Plus 风格")
                .demo(tabs)
                .code("""
                        Node tabs = TabsAnt.create()
                            .tab("a", "标签 A", contentA)
                            .tab("b", "标签 B", contentB)
                            .onChange(key -> handle(key))
                            .build();
                        """)
                .build();
    }

    private Node sectionTypes() {
        Node line = TabsAnt.create()
                .tab("a", "线条", panel("LINE 类型 - 默认（底线滑动指示）"))
                .tab("b", "默认", panel("..."))
                .type(TabsAnt.Type.LINE)
                .build();

        Node card = TabsAnt.create()
                .tab("a", "卡片", panel("CARD 类型 - 标签像卡片，选中态高亮"))
                .tab("b", "类型", panel("..."))
                .type(TabsAnt.Type.CARD)
                .build();

        VBox col = VBoxBuilder.create().spacing(16).children(
                grayLabel("LINE（默认，底线指示器）"), line,
                grayLabel("CARD（卡片式）"), card
        ).build();

        return ShowcaseSection.create()
                .title("场景 2：两种类型（LINE / CARD）")
                .description("LINE 适合内容区切换；CARD 适合管理类页面（如标签页编辑器）")
                .demo(col)
                .code("""
                        TabsAnt.create().tab("a", "...", c).type(TabsAnt.Type.LINE).build();
                        TabsAnt.create().tab("a", "...", c).type(TabsAnt.Type.CARD).build();
                        """)
                .build();
    }

    private Node sectionSizes() {
        Node small = TabsAnt.create().tab("a", "Small", panel("..."))
                .tab("b", "Tab", panel("...")).size(TabsAnt.Size.SMALL).build();
        Node middle = TabsAnt.create().tab("a", "Middle", panel("..."))
                .tab("b", "Tab", panel("...")).size(TabsAnt.Size.MIDDLE).build();
        Node large = TabsAnt.create().tab("a", "Large", panel("..."))
                .tab("b", "Tab", panel("...")).size(TabsAnt.Size.LARGE).build();

        VBox col = VBoxBuilder.create().spacing(16).children(
                grayLabel("SMALL"), small,
                grayLabel("MIDDLE（默认）"), middle,
                grayLabel("LARGE"), large
        ).build();

        return ShowcaseSection.create()
                .title("场景 3：三档尺寸")
                .description("SMALL / MIDDLE / LARGE")
                .demo(col)
                .code("""
                        TabsAnt.create().tab(...).size(TabsAnt.Size.SMALL).build();
                        TabsAnt.create().tab(...).build();   // MIDDLE 默认
                        TabsAnt.create().tab(...).size(TabsAnt.Size.LARGE).build();
                        """)
                .build();
    }

    private Node sectionPlacement() {
        Node bottom = TabsAnt.create()
                .tab("a", "底部 A", panel("..."))
                .tab("b", "底部 B", panel("..."))
                .tabPlacement(TabsAnt.TabPlacement.BOTTOM)
                .build();
        Node left = TabsAnt.create()
                .tab("a", "左 A", panel("..."))
                .tab("b", "左 B", panel("..."))
                .tabPlacement(TabsAnt.TabPlacement.LEFT)
                .build();

        VBox col = VBoxBuilder.create().spacing(16).children(
                grayLabel("TabPlacement.BOTTOM"), bottom,
                grayLabel("TabPlacement.LEFT"), left
        ).build();

        return ShowcaseSection.create()
                .title("场景 4：位置（TOP / BOTTOM / LEFT / RIGHT）")
                .description("LEFT/RIGHT 适合多个标签页时的纵向布局")
                .demo(col)
                .code("""
                        TabsAnt.create().tab(...).tabPlacement(TabsAnt.TabPlacement.BOTTOM).build();
                        TabsAnt.create().tab(...).tabPlacement(TabsAnt.TabPlacement.LEFT).build();
                        """)
                .build();
    }

    private Node sectionWithExtra() {
        Node tabs = TabsAnt.create()
                .tab("a", "概览", panel("..."))
                .tab("b", "明细", panel("..."))
                .tab("c", "日志", panel("..."))
                .extraRight(ButtonAnt.create("+ 新建").type(ButtonAnt.Type.PRIMARY).build())
                .build();

        return ShowcaseSection.create()
                .title("场景 5：右侧 extra 按钮")
                .description(".extraRight(node) —— 标签页右侧加全局操作按钮")
                .demo(tabs)
                .code("""
                        TabsAnt.create()
                            .tab("a", "概览", c)
                            .tab("b", "明细", c)
                            .extraRight(ButtonAnt.create("+ 新建").type(Type.PRIMARY).build())
                            .build();
                        """)
                .build();
    }

    private static Label grayLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
        return l;
    }
}
