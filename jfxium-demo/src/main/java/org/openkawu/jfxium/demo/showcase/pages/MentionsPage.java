package org.openkawu.jfxium.demo.showcase.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.component.MentionsAnt;
import org.openkawu.jfxium.component.MessageAnt;
import org.openkawu.jfxium.core.container.VBoxBuilder;
import org.openkawu.jfxium.demo.showcase.ShowcasePage;
import org.openkawu.jfxium.demo.showcase.ShowcaseSection;

/**
 * Mentions @提及 展示页（M19.14）。
 */
public class MentionsPage implements ShowcasePage {

    @Override public String   key()      { return "mentions"; }
    @Override public String   title()    { return "Mentions @提及"; }
    @Override public Category category() { return Category.DATA_ENTRY; }

    @Override
    public Node getView() {
        Label pageTitle = new Label("Mentions @提及");
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");
        Label pageDesc = new Label("文本框输入 @ 触发候选下拉 —— 评论 / 协作类应用必备。");
        pageDesc.setStyle("-fx-text-fill: -color-fg-muted;");

        VBox header = VBoxBuilder.create().spacing(8).children(pageTitle, pageDesc).build();

        return VBoxBuilder.create()
                .spacing(20)
                .children(
                        header,
                        sectionBasic(),
                        sectionCustomPrefix(),
                        sectionMoreOptions()
                )
                .build();
    }

    private Node sectionBasic() {
        Node m = MentionsAnt.create()
                .placeholder("输入 @ 弹出候选 —— 试试 @张")
                .option("zhangsan", "张三")
                .option("lisi", "李四")
                .option("wangwu", "王五")
                .onSelect(v -> MessageAnt.info("提及了：" + v))
                .build();
        return ShowcaseSection.create()
                .title("场景 1：基础（默认 @ 触发）")
                .description("输入 @ 弹候选下拉；选中后插入到文本里")
                .demo(m)
                .code("""
                        MentionsAnt.create()
                            .placeholder("输入 @ 弹出候选")
                            .option("zhangsan", "张三")
                            .option("lisi", "李四")
                            .onSelect(v -> notify(v))
                            .build();
                        """)
                .build();
    }

    private Node sectionCustomPrefix() {
        Node m = MentionsAnt.create()
                .placeholder("输入 # 触发话题候选")
                .prefix("#")
                .option("ant", "Ant Design")
                .option("element", "Element Plus")
                .option("mui", "Material UI")
                .build();
        return ShowcaseSection.create()
                .title("场景 2：自定义前缀（话题 # 触发）")
                .description(".prefix(\"#\") —— 微博/Twitter 风格话题输入")
                .demo(m)
                .code("""
                        MentionsAnt.create()
                            .prefix("#")
                            .option("ant", "Ant Design")
                            .option("element", "Element Plus")
                            .build();
                        """)
                .build();
    }

    private Node sectionMoreOptions() {
        var m = MentionsAnt.create()
                .placeholder("@ 触发候选；rows=5 行；选中后会插入文本");
        for (int i = 1; i <= 20; i++) {
            m.option("user" + i, "用户 " + i);
        }
        Node node = m.rows(5).build();
        return ShowcaseSection.create()
                .title("场景 3：多候选项 + 自定义行数")
                .description(".rows(5) —— 控制 TextArea 行数；适合长评论场景")
                .demo(node)
                .code("""
                        var b = MentionsAnt.create();
                        for (int i = 1; i <= 20; i++) {
                            b.option("user" + i, "用户 " + i);
                        }
                        b.rows(5).build();
                        """)
                .build();
    }
}
