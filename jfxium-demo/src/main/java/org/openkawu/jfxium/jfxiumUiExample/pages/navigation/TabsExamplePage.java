package org.openkawu.jfxium.jfxiumUiExample.pages.navigation;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openkawu.jfxium.component.TabsAnt;
import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;

/**
 * Tabs 标签页 —— 基础线条 / 卡片类型 / 位置。
 */
public class TabsExamplePage extends VBoxAnt {

    public TabsExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Tabs 标签页")
                .description("选项卡切换组件，支持线条、卡片样式和多种位置。")
                .sections(
                        basicLineSection(),
                        cardTypeSection(),
                        placementSection()
                )
                .padding(24)
                .build());
    }

    private Node basicLineSection() {
        Node demo = TabsAnt.create()
                .tab("tab1", "标签一", new Label("标签一的内容"))
                .tab("tab2", "标签二", new Label("标签二的内容"))
                .tab("tab3", "标签三", new Label("标签三的内容"))
                .build();
        String code = """
                TabsAnt.create()
                        .tab("tab1", "标签一", new Label("标签一的内容"))
                        .tab("tab2", "标签二", new Label("标签二的内容"))
                        .tab("tab3", "标签三", new Label("标签三的内容"))
                        .build();
                """;
        return Demos.sectionWithCode("1. 基础线条样式",
                "默认线条（LINE）样式的标签页。",
                code, demo);
    }

    private Node cardTypeSection() {
        Node demo = TabsAnt.create()
                .type(TabsAnt.Type.CARD)
                .tab("card1", "卡片一", new Label("卡片一的内容"))
                .tab("card2", "卡片二", new Label("卡片二的内容"))
                .tab("card3", "卡片三", new Label("卡片三的内容"))
                .build();
        String code = """
                TabsAnt.create()
                        .type(TabsAnt.Type.CARD)
                        .tab("card1", "卡片一", new Label("卡片一的内容"))
                        .tab("card2", "卡片二", new Label("卡片二的内容"))
                        .build();
                """;
        return Demos.sectionWithCode("2. 卡片类型",
                "type(CARD) 卡片风格标签页。",
                code, demo);
    }

    private Node placementSection() {
        Node demo = TabsAnt.create()
                .tab("t1", "可用标签", new Label("这个标签可以正常切换"))
                .tab("t2", "禁用标签", new Label("这个标签被禁用了"), true)
                .tab("t3", "另一个标签", new Label("第三个标签的内容"))
                .onChange(key -> System.out.println("切换到: " + key))
                .build();
        String code = """
                TabsAnt.create()
                        .tab("t1", "可用标签", content1)
                        .tab("t2", "禁用标签", content2, true)  // disabled
                        .tab("t3", "另一个标签", content3)
                        .onChange(key -> System.out.println("切换到: " + key))
                        .build();
                """;
        return Demos.sectionWithCode("3. 禁用标签 + onChange",
                "tab 第 4 参数 disabled=true 禁用该标签；onChange 监听切换事件。",
                code, demo);
    }
}
